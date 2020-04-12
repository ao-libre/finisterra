package server.network;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.minlog.Log;
import component.console.ConsoleMessage;
import component.entity.character.info.Bag;
import component.entity.world.Dialog;
import component.entity.world.Object;
import component.position.WorldPos;
import org.jetbrains.annotations.NotNull;
import server.systems.CommandSystem;
import server.systems.MeditateSystem;
import server.systems.ServerSystem;
import server.systems.account.AccountSystem;
import server.systems.combat.MagicCombatSystem;
import server.systems.combat.PhysicalCombatSystem;
import server.systems.combat.RangedCombatSystem;
import server.systems.entity.MovementSystem;
import server.systems.manager.*;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.MessageSystem;
import server.systems.network.UpdateTo;
import server.systems.user.ItemActionSystem;
import server.systems.user.PlayerActionSystem;
import server.systems.user.UserSystem;
import shared.interfaces.Intervals;
import shared.model.AttackType;
import shared.network.account.AccountCreationRequest;
import shared.network.account.AccountLoginRequest;
import shared.network.combat.AttackRequest;
import shared.network.combat.SpellCastRequest;
import shared.network.interaction.DropItem;
import shared.network.interaction.MeditateRequest;
import shared.network.interaction.TakeItemRequest;
import shared.network.interaction.TalkRequest;
import shared.network.interfaces.DefaultRequestProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.inventory.ItemActionRequest;
import shared.network.inventory.ItemActionRequest.ItemAction;
import shared.network.movement.MovementRequest;
import shared.network.notifications.EntityUpdate;
import shared.network.time.TimeSyncRequest;
import shared.network.time.TimeSyncResponse;
import shared.network.user.UserCreateRequest;
import shared.network.user.UserLoginRequest;
import shared.util.EntityUpdateBuilder;
import shared.util.Messages;

import java.util.Optional;

import static com.artemis.E.E;

/**
 * Every packet received from users will be processed here
 */
@Wire
public class ServerRequestProcessor extends DefaultRequestProcessor {

    // Injected Systems
    private ServerSystem networkManager;
    private MapManager mapManager;
    private WorldManager worldManager;
    private PhysicalCombatSystem physicalCombatSystem;
    private MagicCombatSystem magicCombatSystem;
    private ItemManager itemManager;
    private MeditateSystem meditateSystem;
    private RangedCombatSystem rangedCombatSystem;
    private CommandSystem commandSystem;
    private EntityUpdateSystem entityUpdateSystem;
    private MessageSystem messageSystem;
    private AccountSystem accountSystem;
    private UserSystem userSystem;
    private MovementSystem movementSystem;
    private PlayerActionSystem playerActionSystem;
    private ItemActionSystem itemActionSystem;

    // Accounts

    @Override
    public void processRequest(@NotNull AccountCreationRequest accountCreationRequest, int connectionId) {
        // Recibimos los datos de la cuenta del cliente.
        String username = accountCreationRequest.getUsername();
        String email = accountCreationRequest.getEmail();
        String password = accountCreationRequest.getPassword();

        accountSystem.createAccount(connectionId, username, email, password);
    }

    @Override
    public void processRequest(@NotNull AccountLoginRequest accountLoginRequest, int connectionId) {
        String email = accountLoginRequest.getEmail();
        String password = accountLoginRequest.getPassword();

        accountSystem.login(connectionId, email, password);

    }

    // Users

    @Override
    public void processRequest(@NotNull UserLoginRequest userLoginRequest, int connectionId) {
        // TODO validate connectionId corresponds to account
        userSystem.login(connectionId, userLoginRequest.getUserName());
    }

    @Override
    public void processRequest(@NotNull UserCreateRequest request, int connectionId) {
        userSystem.create(connectionId, request.getName(), request.getHeroId());
    }

    /**
     * Process {@link MovementRequest}. If it is valid, move player and notify.
     *
     * @param request      component.movement request
     * @param connectionId id
     * @see MovementRequest
     */
    @Override
    public void processRequest(MovementRequest request, int connectionId) {
        if (networkManager.connectionHasNoPlayer(connectionId)) return;
        movementSystem.move(connectionId, request.movement, request.requestNumber);
    }

    /**
     * Attack and notify, if it was effective or not, to near users
     *
     * @param attackRequest attack type
     * @param connectionId  user connection id
     */
    @Override
    public void processRequest(@NotNull AttackRequest attackRequest, int connectionId) {
        int playerId = networkManager.getPlayerByConnection(connectionId);
        E entity = E(playerId);
        AttackType type = attackRequest.type();
        if (!entity.hasAttackInterval()) {
            if (type.equals(AttackType.RANGED)) {
                rangedCombatSystem.shoot(playerId, attackRequest);
            } else {
                physicalCombatSystem.entityAttack(playerId, Optional.empty());
            }
            entity.attackIntervalValue(Intervals.ATTACK_INTERVAL);
        } else {
            messageSystem.add(playerId,
                    ConsoleMessage.error((type.equals(AttackType.RANGED) ?
                            Messages.CANT_SHOOT_THAT_FAST :
                            Messages.CANT_ATTACK_THAT_FAST)
                            .name()));
        }
    }

    /**
     * User wants to use or act over an item, do action and notify.
     *
     * @param itemAction   user slot number
     * @param connectionId user connection id
     */
    @Override
    public void processRequest(@NotNull ItemActionRequest itemAction, int connectionId) {
        itemActionSystem.useItem(connectionId, itemAction.getAction(), itemAction.getSlot());
    }

    /**
     * User wants to meditate
     *
     * @param meditateRequest request (no data)
     * @param connectionId    user connection id
     */
    @Override
    public void processRequest(MeditateRequest meditateRequest, int connectionId) {
        int playerId = networkManager.getPlayerByConnection(connectionId);
        meditateSystem.toggle(playerId);
    }

    /**
     * If the talk request starts with '/' it means is a command
     * If not, notify near users that user talked
     *
     * @param talkRequest  talk request with message
     * @param connectionId user connection id
     */
    @Override
    public void processRequest(@NotNull TalkRequest talkRequest, int connectionId) {
        int playerId = networkManager.getPlayerByConnection(connectionId);

        if (talkRequest.getMessage().startsWith("/")) {
            commandSystem.handleCommand(talkRequest.getMessage(), playerId);
        } else {
            EntityUpdate update = EntityUpdateBuilder.of(playerId).withComponents(new Dialog(talkRequest.getMessage())).build();
            entityUpdateSystem.add(update, UpdateTo.ALL);
        }
    }

    /**
     * User wants to take something from ground
     *
     * @param takeItemRequest request (no data)
     * @param connectionId    user connection id
     */
    @Override
    public void processRequest(TakeItemRequest takeItemRequest, int connectionId) {
        itemActionSystem.grabItem(connectionId);
    }

    @Override
    public void processRequest(SpellCastRequest spellCastRequest, int connectionId) {
        int playerId = networkManager.getPlayerByConnection(connectionId);
        E entity = E(playerId);
        if (!entity.hasAttackInterval()) {
            magicCombatSystem.spell(playerId, spellCastRequest);
            entity.attackIntervalValue(Intervals.MAGIC_ATTACK_INTERVAL);
        } else {
            messageSystem.add(playerId,
                    ConsoleMessage.error(Messages.CANT_MAGIC_THAT_FAST.name()));
        }
    }

    @Override
    public void processRequest(@NotNull TimeSyncRequest request, int connectionId) {
        long receiveTime = TimeUtils.millis();
        TimeSyncResponse response = new TimeSyncResponse();
        response.receiveTime = receiveTime;
        response.requestId = request.requestId;
        response.sendTime = TimeUtils.millis();
        networkManager.sendTo(connectionId, response);
    }

    @Override
    public void processRequest(@NotNull DropItem dropItem, int connectionId) {
        playerActionSystem.drop(connectionId, dropItem.getCount(), dropItem.getPosition(), dropItem.getSlot());
    }
}

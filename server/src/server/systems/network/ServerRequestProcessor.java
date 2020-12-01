package server.systems.network;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;
import server.systems.account.AccountSystem;
import server.systems.account.UserSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.item.ItemActionSystem;
import server.systems.world.entity.movement.MovementSystem;
import server.systems.world.entity.npc.NPCActionSystem;
import server.systems.world.entity.user.MeditateSystem;
import server.systems.world.entity.user.PlayerActionSystem;
import shared.network.account.AccountCreationRequest;
import shared.network.account.AccountLoginRequest;
import shared.network.combat.AttackRequest;
import shared.network.combat.SpellCastRequest;
import shared.network.interaction.*;
import shared.network.interfaces.DefaultRequestProcessor;
import shared.network.inventory.ItemActionRequest;
import shared.network.movement.MovementRequest;
import shared.network.time.TimeSyncRequest;
import shared.network.time.TimeSyncResponse;
import shared.network.user.UserCreateRequest;
import shared.network.user.UserLoginRequest;
import shared.network.user.UserLogoutRequest;

/**
 * Every packet received from users will be processed here
 */
@Wire
public class ServerRequestProcessor extends DefaultRequestProcessor {

    // Injected Systems
    private ServerSystem serverSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private MeditateSystem meditateSystem;
    private AccountSystem accountSystem;
    private UserSystem userSystem;
    private MovementSystem movementSystem;
    private PlayerActionSystem playerActionSystem;
    private ItemActionSystem itemActionSystem;
    private NPCActionSystem npcActionSystem;

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
        accountSystem.loginAccount(connectionId, email, password);
    }

    // Users
    @Override
    public void processRequest(@NotNull UserLoginRequest userLoginRequest, int connectionId) {
        // TODO validate connectionId corresponds to account
        userSystem.login(connectionId, userLoginRequest.getUserName());
    }

    @Override
    public void processRequest(UserLogoutRequest userLogoutRequest, int connectionId) {
        userSystem.userLogout(connectionId);
    }

    @Override
    public void processRequest(@NotNull UserCreateRequest request, int connectionId) {
        userSystem.create(connectionId, request.getName(), request.getHeroId(), request.getUserAcc(), request.getIndex());
    }

    /**
     * Process {@link MovementRequest}. If it is valid, move player and notify.
     *
     * @param request      {@link component.movement}
     * @param connectionID ID del de la conexión.
     */
    @Override
    public void processRequest(MovementRequest request, int connectionID) {
        if (serverSystem.connectionHasPlayer(connectionID)) {
            movementSystem.move(connectionID, request.movement, request.requestNumber);
        }
    }

    /**
     * Attack and notify, if it was effective or not, to near users
     *
     * @param attackRequest attack type
     * @param connectionId  user connection id
     */
    @Override
    public void processRequest(@NotNull AttackRequest attackRequest, int connectionId) {
        playerActionSystem.attack(connectionId, attackRequest.getTimestamp(), attackRequest.getWorldPos(), attackRequest.type());
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
        int playerId = serverSystem.getPlayerByConnection(connectionId);
        meditateSystem.toggle(playerId);
    }

    /**
     * If the talk request starts with '/' it means is a command
     * If not, notify near users that user talked
     *
     * @param talkRequest  talk request with message
     * @param connectionID user connection id
     */
    @Override
    public void processRequest(@NotNull TalkRequest talkRequest, int connectionID) {
        int playerID = serverSystem.getPlayerByConnection(connectionID);
        if (talkRequest.isValid()) playerActionSystem.talk(playerID, talkRequest.getMessage());
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
        playerActionSystem.spell(connectionId, spellCastRequest.getSpell(), spellCastRequest.getWorldPos(), spellCastRequest.getTimestamp());
    }

    @Override
    public void processRequest(@NotNull TimeSyncRequest request, int connectionId) {
        long receiveTime = TimeUtils.millis();
        TimeSyncResponse response = new TimeSyncResponse();
        response.receiveTime = receiveTime;
        response.requestId = request.requestId;
        response.sendTime = TimeUtils.millis();
        serverSystem.sendTo(connectionId, response);
    }

    @Override
    public void processRequest(NpcInteractionRequest npcInteractionRequest, int connectionId) {
        npcActionSystem.interact(connectionId, npcInteractionRequest.getTargetEntity());
    }

    @Override
    public void processRequest(@NotNull DropItem dropItem, int connectionId) {
        playerActionSystem.drop(connectionId, dropItem.getCount(), dropItem.getPosition(), dropItem.getSlot());
    }
}

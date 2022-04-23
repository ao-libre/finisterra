package server.systems.world.entity.user;

import com.artemis.ComponentMapper;
import component.console.ConsoleMessage;
import component.entity.character.info.Bag;
import component.entity.world.Dialog;
import component.entity.world.Object;
import component.graphic.FX;
import component.physics.AttackInterval;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.config.ObjectSystem;
import server.systems.network.CommandSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.MessageSystem;
import server.systems.network.ServerSystem;
import server.systems.world.MapSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.combat.MagicCombatSystem;
import server.systems.world.entity.combat.PhysicalCombatSystem;
import server.systems.world.entity.combat.RangedCombatSystem;
import server.systems.world.entity.factory.EffectEntitySystem;
import server.systems.world.entity.item.ItemUsageSystem;
import server.utils.UpdateTo;
import shared.interfaces.FXs;
import shared.interfaces.Intervals;
import shared.model.AttackType;
import shared.model.Spell;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;
import shared.util.Messages;

import java.util.Optional;

// @todo: connectionId debería estar encapsulado dentro de la parte de Network.
// Trabajar con playerId.
public class PlayerActionSystem extends PassiveSystem {

    // Injected systems.
    private ServerSystem serverSystem;
    private ItemUsageSystem itemUsageSystem;
    private ObjectSystem objectSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private RangedCombatSystem rangedCombatSystem;
    private PhysicalCombatSystem physicalCombatSystem;
    private MagicCombatSystem magicCombatSystem;
    private MessageSystem messageSystem;
    private CommandSystem commandSystem;
    private EntityUpdateSystem entityUpdateSystem;
    private EffectEntitySystem effectEntitySystem;
    private MapSystem mapSystem;

    ComponentMapper<AttackInterval> mAttackInterval;
    ComponentMapper<Bag> mBag;
    ComponentMapper<Object> mObject;
    ComponentMapper<WorldPos> mWorldPos;

    public void drop(int playerId, int count, WorldPos position, int slot) {

        // Remove item from inventory
        InventoryUpdate update = new InventoryUpdate();
        Bag bag = mBag.get(playerId);
        Bag.Item item = bag.items[slot];
        if (item == null) return;
        if (item.equipped) {
            objectSystem.getObject(item.objId).ifPresent((object) -> {
                itemUsageSystem.TAKE_OFF.accept(playerId, object);
                item.equipped = false;
            });
        }
        item.count -= count;
        if (item.count <= 0) {
            bag.remove(slot);
        }
        update.add(slot, bag.items[slot]); // should remove item if count <= 0

        serverSystem.sendByPlayerId(playerId, update);

        // Add new obj component.entity to world
        int objectId = world.create();

        WorldPos worldPos = mWorldPos.create(objectId);
        worldPos.map = position.map;
        worldPos.x = position.x;
        worldPos.y = position.y;

        Object object = mObject.create(objectId);
        object.index = item.objId;
        object.count = count;

        worldEntitiesSystem.registerEntity(objectId);
    }

    public void attack(int playerId, long timestamp, WorldPos worldPos, AttackType type) {

        if (!mAttackInterval.has(playerId)) {
            if (type.equals(AttackType.RANGED)) {
                rangedCombatSystem.shoot(playerId, worldPos, timestamp);
            } else {
                // todo use timestamp
                physicalCombatSystem.entityAttack(playerId, Optional.empty());
            }
            AttackInterval attackInterval = mAttackInterval.create(playerId);
            attackInterval.setValue(Intervals.ATTACK_INTERVAL);
        } else {
            messageSystem.add(playerId,
                    ConsoleMessage.error((type.equals(AttackType.RANGED) ?
                            Messages.CANT_SHOOT_THAT_FAST :
                            Messages.CANT_ATTACK_THAT_FAST)
                            .name()));
        }
    }

    public void talk(int playerID, String message) {
        // Limpiamos mínimamente el mensaje.
        message = message.strip();

        // Si es un comando...
        if (CommandSystem.isCommand(message)) {
            if (commandSystem.commandExists(message)) {
                commandSystem.handleCommand(message, playerID);
            } else {
                messageSystem.add(playerID, ConsoleMessage.error(Messages.INVALID_COMMAND.name()));
            }
        } else {
            // No es un comando, entonces es un dialogo.
            EntityUpdate update = EntityUpdateBuilder.of(playerID).withComponents(new Dialog(message)).build();
            entityUpdateSystem.add(update, UpdateTo.ALL);
        }
    }

    public void spell(int playerId, Spell spell, WorldPos worldPos, long timestamp) {

        if (!mAttackInterval.has(playerId)) {
            magicCombatSystem.spell(playerId, spell, worldPos, timestamp);
            AttackInterval attackInterval = mAttackInterval.create(playerId);
            attackInterval.setValue(Intervals.MAGIC_ATTACK_INTERVAL);
        } else {
            messageSystem.add(playerId,
                    ConsoleMessage.error(Messages.CANT_MAGIC_THAT_FAST.name()));
        }
    }



    public void teleport(int playerId, int targetMap, int targetX, int targetY){

        WorldPos worldPos = mWorldPos.get(playerId);
        WorldPos targetWorldPos = new WorldPos( targetX, targetY, targetMap );

        if(mapSystem.getHelper().isValid(targetWorldPos)) {
            effectEntitySystem.addFX(playerId, 1, 1);
            worldPos.setWorldPos(targetWorldPos);
            EntityUpdateBuilder resetUpdate = EntityUpdateBuilder.of(playerId);
            resetUpdate.withComponents( worldPos );
            worldEntitiesSystem.notifyUpdate( playerId, resetUpdate.build() );
        }
    }








}

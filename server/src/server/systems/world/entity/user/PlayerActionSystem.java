package server.systems.world.entity.user;

import com.artemis.E;
import com.artemis.annotations.Wire;
import component.console.ConsoleMessage;
import component.entity.character.info.Bag;
import component.entity.world.Dialog;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.config.ObjectSystem;
import server.systems.network.CommandSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.MessageSystem;
import server.systems.network.ServerSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.combat.MagicCombatSystem;
import server.systems.world.entity.combat.PhysicalCombatSystem;
import server.systems.world.entity.combat.RangedCombatSystem;
import server.systems.world.entity.item.ItemUsageSystem;
import server.utils.UpdateTo;
import shared.interfaces.Intervals;
import shared.model.AttackType;
import shared.model.Spell;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;
import shared.util.Messages;

import java.util.Optional;

@Wire
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

    public void drop(int connectionId, int count, WorldPos position, int slot) {
        int playerId = serverSystem.getPlayerByConnection(connectionId);
        E entity = E.E(playerId);

        // Remove item from inventory
        InventoryUpdate update = new InventoryUpdate();
        Bag bag = entity.getBag();
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
        serverSystem.sendTo(serverSystem.getConnectionByPlayer(playerId), update);

        // Add new obj component.entity to world
        int object = world.create();
        E.E(object).worldPos()
                .worldPosMap(position.map)
                .worldPosX(position.x)
                .worldPosY(position.y);
        E.E(object).objectIndex(item.objId);
        E.E(object).objectCount(count);
        worldEntitiesSystem.registerEntity(object);
    }

    public void attack(int connectionId, long timestamp, WorldPos worldPos, AttackType type) {
        int playerId = serverSystem.getPlayerByConnection(connectionId);
        E entity = E.E(playerId);
        if (!entity.hasAttackInterval()) {
            if (type.equals(AttackType.RANGED)) {
                rangedCombatSystem.shoot(playerId, worldPos, timestamp);
            } else {
                // todo use timestamp
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

    public void talk(int playerID, String message) {
        // Si es un comando...
        if (CommandSystem.Command.isCommand(message)) {
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

    public void spell(int connectionId, Spell spell, WorldPos worldPos, long timestamp) {
        int playerId = serverSystem.getPlayerByConnection(connectionId);
        E entity = E.E(playerId);
        if (!entity.hasAttackInterval()) {
            magicCombatSystem.spell(playerId, spell, worldPos, timestamp);
            entity.attackIntervalValue(Intervals.MAGIC_ATTACK_INTERVAL);
        } else {
            messageSystem.add(playerId,
                    ConsoleMessage.error(Messages.CANT_MAGIC_THAT_FAST.name()));
        }
    }
}

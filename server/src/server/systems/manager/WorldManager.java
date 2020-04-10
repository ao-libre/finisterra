package server.systems.manager;

import com.artemis.Component;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import component.camera.Focused;
import component.entity.character.states.CanWrite;
import component.entity.npc.OriginPos;
import component.physics.AOPhysics;
import component.position.WorldPos;
import server.systems.EntityFactorySystem;
import server.systems.ServerSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.UpdateTo;
import shared.model.npcs.NPC;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;

import java.util.List;

import static com.artemis.E.E;

@Wire
public class WorldManager extends DefaultManager {

    private MapManager mapManager;
    private ServerSystem networkManager;
    private SpellManager spellManager;
    private ObjectManager objectManager;
    private EntityFactorySystem entityFactorySystem;
    private EntityUpdateSystem entityUpdateSystem;
    private ComponentManager componentManager;

    public void registerEntity(int id) {
        mapManager.updateEntity(id);
    }

    public void registerEntity(int connectionId, int id) {
        networkManager.registerUserConnection(id, connectionId);
        registerEntity(id);
    }

    public void unregisterEntity(int entityId) {
        networkManager.unregisterUserConnection(entityId);
        mapManager.removeEntity(entityId);
        getWorld().delete(entityId);
    }

    public void sendEntityUpdate(int user, Object update) {
        if (networkManager.playerHasConnection(user)) {
            Log.debug("Sending update: " + update.toString() + " to " + user);
            networkManager.sendTo(networkManager.getConnectionByPlayer(user), update);
        }
    }

    public void notifyToNearEntities(int entityId, Object update) {
        mapManager.getNearEntities(entityId).forEach(nearPlayer -> {
            sendEntityUpdate(nearPlayer, update);
        });
    }

    public void notifyUpdate(int entityId, Object update) {
        sendEntityUpdate(entityId, update);
        notifyToNearEntities(entityId, update);
    }

    public void entityDie(int entityId) {

        final E e = E(entityId);
        if (e.hasNPC()) {
            int npcId = e.nPCId();
            NPC npc = world.getSystem(NPCManager.class).getNpcs().get(npcId);
            // TODO check if should respawn

            OriginPos originPos = e.getOriginPos();
            int npcRespawn = world.create();
            E(npcRespawn)
                    .respawnTime(5)
                    .respawnNpcId(npcId)
                    .respawnPos(originPos);

            unregisterEntity(e.id());
            npc.getDrops().forEach(itemPair -> dropItem(itemPair.getKey(), itemPair.getValue(), e.getWorldPos()));
        } else {
            // RESET USER. TODO implement ghost
            // reset health
            e.getHealth().min = e.getHealth().max;
            // reset mana
            EntityUpdateBuilder resetUpdate = EntityUpdateBuilder.of(entityId);
            resetUpdate.withComponents(e.getHealth());
            if (e.hasMana()) {
                e.getMana().min = e.getMana().max;
                resetUpdate.withComponents(e.getMana());
            }
            entityUpdateSystem.add(resetUpdate.build(), UpdateTo.ENTITY);

            EntityUpdate update = EntityUpdateBuilder.of(entityId).withComponents(e.getWorldPos()).build();
            entityUpdateSystem.add(update, UpdateTo.ALL);
        }
    }

    private void dropItem(Integer key, Integer value, WorldPos worldPos) {
        entityFactorySystem.createObject(key, value, worldPos);
    }

    public void login(int connectionId, int entity) {
        List<Component> components = componentManager.getComponents(entity, ComponentManager.Visibility.CLIENT_ALL);
        components.add(new Focused());
        components.add(new AOPhysics());
        components.add(new CanWrite());
        registerEntity(connectionId, entity);
        entityUpdateSystem.add(EntityUpdateBuilder.of(entity).withComponents(components.toArray(new Component[0])).build(), UpdateTo.ENTITY);
    }
}

package server.systems.manager;

import com.artemis.Component;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.Timer;
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
import shared.interfaces.Race;
import shared.model.lobby.Player;
import shared.model.npcs.NPC;
import shared.network.inventory.InventoryUpdate;
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

        final E entity = E(entityId);
        if (entity.hasNPC()) {
            int npcId = entity.nPCId();
            NPC npc = world.getSystem(NPCManager.class).getNpcs().get(npcId);
            // TODO check if should respawn
            OriginPos originPos = entity.getOriginPos();
            int npcRespawn = world.create();
            E(npcRespawn)
                    .respawnTime(5)
                    .respawnNpcId(npcId)
                    .respawnPos(originPos);

            unregisterEntity(entity.id());
            npc.getDrops().forEach(itemPair -> dropItem(itemPair.getKey(), itemPair.getValue(), entity.getWorldPos()));
        } else {

            // dropeo de items random al morir
            //Inventory.Item items[] = entity.getInventory().items;
            InventoryUpdate inventoryUpdate = new InventoryUpdate();
            /*
            for(int i = 0; i<20;i++) {
                if(items[i] != null) {
                    items[i].equipped = false;
                    ItemConsumers itemConsumers = getWorld().getSystem( ItemConsumers.class );
                    Obj item = objectManager.getObject( items[i].objId ).get();
                    itemConsumers.TAKE_OFF.accept( entityId, item );
                    if(!item.isNewbie() || item.isNotDrop()) {
                        Random random = new Random(  );
                        boolean dropi = random.nextBoolean();
                        Log.info("drop slot "+i +": "+ dropi);
                        if (dropi) {
                            dropItem( item.getId(), items[i].count, entity.getWorldPos() );
                            entity.getInventory().remove( i );
                            inventoryUpdate.remove( i );
                        }
                    }
                }
            }

             */
            //notifyUpdate( entityId, inventoryUpdate );
            //setea la hp a 0 porque o sino queda con hp
            entity.getHealth().min = 0;
            // cambio del cuerpo y la cabeza a fantasma
            // TODO arreglar las imagenes de los espiritus falta scalarla x2
            entity.bodyIndex(8);
            entity.headIndex(514);
            EntityUpdateBuilder resetUpdate = EntityUpdateBuilder.of(entityId);
            resetUpdate.withComponents(entity.getHealth());
            resetUpdate.withComponents(entity.getHead(), entity.getBody());
            //resetUpdate.withComponents(entity.getInventory());
            sendEntityUpdate(entityId, resetUpdate.build());
            notifyUpdate(entityId, EntityUpdateBuilder.of(entityId).withComponents(entity.getWorldPos()).build());
            //a los 20 segundos no revive automaticamente en la posision de origen del jugador
            Timer.schedule( new Timer.Task() {
                @Override
                public void run() {
                    Log.info(" pasaron 20 segundos ");
                    if (entity.hasHealth()) {
                        if(entity.healthMin() == 0) {
                            resurrect( entityId, false );
                        }
                    }
                }
            }, 20);
        }
    }

    /**
     * @param entityId player id
     * @param resurrected si fue resucitado por un jugador o npc es true
     *                   caso resurrecion automatica es false y se resucita en la ciudad
     */
    public void resurrect(int entityId, boolean resurrected){
        final E entity = E(entityId);
        Log.info( "resuscitanto player "+ entity.getName().text );
        // RESET USER.
        // reset health
        entity.getHealth().min = entity.getHealth().max;
        // reset mana
        EntityUpdateBuilder resetUpdate = EntityUpdateBuilder.of(entityId);
        resetUpdate.withComponents(entity.getHealth());
        if (entity.hasMana()) {
            entity.getMana().min = entity.getMana().max;
            resetUpdate.withComponents(entity.getMana());
        }
        //reset body and head
        //todo obtener body y head de la base de datos del jugador las cabezas actualmente son randoms
        entityFactorySystem.setNakedBody(entity, Race.of(entity));
        entityFactorySystem.setHead(entity, Race.of(entity));
        notifyUpdate(entityId, EntityUpdateBuilder.of(entityId).withComponents(entity.getBody(), entity.getHead()).build());
        //todo asignar comando para asignar la posicion de las distintas ciudades
        if (!resurrected) {
            // por si no tiene posision de origen o es la ciudad newbir y el jugador ya no es newbie
            if (entity.originPosMap() == 0 || (entity.getLevel().level>13 && entity.originPosMap()==286)){
                if (entity.getLevel().level < 13){
                    entity.originPosMap(286).originPosX(50).originPosY(60);
                }else {
                    entity.originPosMap(1).originPosX(50).originPosY(50);
                }
            }
            entity.worldPosMap(entity.originPosMap()).worldPosX(entity.originPosX()).worldPosY(entity.originPosY());
        } else {
            Timer.instance().clear();
        }
        sendEntityUpdate(entityId, resetUpdate.build());
        notifyUpdate(entityId, EntityUpdateBuilder.of(entityId).withComponents(entity.getWorldPos()).build());
    }

    private void dropItem(Integer key, Integer value, WorldPos worldPos) {
        entityFactorySystem.createObject(key, value, worldPos);
    }

    public void login(int connectionId, Player player) {
        final int entity = entityFactorySystem.createPlayer(player.getPlayerName(), player.getHero(), player.getTeam());
        List<Component> components = componentManager.getComponents(entity, ComponentManager.Visibility.CLIENT_ALL);
        components.add(new Focused());
        components.add(new AOPhysics());
        components.add(new CanWrite());
        registerEntity(connectionId, entity);
        entityUpdateSystem.add(EntityUpdateBuilder.of(entity).withComponents(components.toArray(new Component[0])).build(), UpdateTo.ENTITY);
    }
}

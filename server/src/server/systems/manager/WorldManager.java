package server.systems.manager;

import camera.Focused;
import com.artemis.Component;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.minlog.Log;
import entity.character.info.Inventory;
import entity.character.states.CanWrite;
import entity.npc.OriginPos;
import physics.AOPhysics;
import position.WorldPos;
import server.systems.EntityFactorySystem;
import server.systems.ServerSystem;
import shared.interfaces.Race;
import shared.model.lobby.Player;
import shared.model.npcs.NPC;
import shared.network.inventory.InventoryUpdate;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;
import shared.network.notifications.RemoveEntity;
import shared.objects.types.Obj;

import java.util.List;
import java.util.Random;

import static com.artemis.E.E;
import static server.utils.WorldUtils.WorldUtils;

@Wire
public class WorldManager extends DefaultManager {

    private MapManager mapManager;
    private ServerSystem networkManager;
    private SpellManager spellManager;
    private ObjectManager objectManager;
    private EntityFactorySystem entityFactorySystem;

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

    void sendEntityRemove(int user, int entity) {
        if (networkManager.playerHasConnection(user)) {
            networkManager
                    .sendTo(networkManager.getConnectionByPlayer(user), new RemoveEntity(entity));
        }
    }

    public void sendEntityUpdate(int user, Object update) {
        if (networkManager.playerHasConnection(user)) {
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
            // TODO hacer q los npc dejen de tomarnos como blanco
            // dropeo de items random al morir
            Inventory.Item items[] = entity.getInventory().items;
            InventoryUpdate inventoryUpdate = new InventoryUpdate();
            for(int i = 0; i<20;i++) {
                if(items[i] != null) {
                    items[i].equipped = false;
                    //inventoryUpdate.add( i, items[i] );
                    ItemConsumers itemConsumers = getWorld().getSystem( ItemConsumers.class );
                    Obj item = objectManager.getObject( items[i].objId ).get();
                    itemConsumers.TAKE_OFF.accept( entityId, item );
                    if(!item.isNewbie() || item.isNotDrop()) {
                        Random random = new Random(  );
                        boolean dropi = random.nextBoolean();
                        Log.info(" " + dropi);
                        if (dropi) {
                            dropItem( item.getId(), items[i].count, entity.getWorldPos() );
                            inventoryUpdate.remove( i );
                        }
                    }
                }
            }
            sendEntityUpdate( entityId, inventoryUpdate );
            //setea la hp a 0 porque o sino queda con hp
            entity.getHealth().min = 0;
            // cambio del cuerpo y la cabeza a fantasma
            // TODO arreglar las imagenes de los espiritus se ven de a 4
            entity.bodyIndex( 8 );
            entity.headIndex( 514 );
            EntityUpdateBuilder resetUpdate = EntityUpdateBuilder.of(entityId);
            resetUpdate.withComponents( entity.getHealth() );
            resetUpdate.withComponents( entity.getHead(), entity.getBody() );
            sendEntityUpdate(entityId, resetUpdate.build());
            notifyUpdate(entityId, EntityUpdateBuilder.of(entityId).withComponents(entity.getWorldPos()).build());
            //a los 20 segundos no revive automaticamente en la posision de origen del jugador
            Timer.schedule( new Timer.Task() {
                @Override
                public void run() {
                    Log.info( " pasaron 20 segundos " );
                    if (entity.healthMin() == 0) {
                        resurrect( entityId, false );
                    }
                }
            }, 20);
        }
    }

    /**
     * @param entityId player id
     * @param resurected si fue resucitado por un jugador o npc es true
     *                   caso resurrecion automatica es false y se resucita en la ciudad
     */
    public void resurrect(int entityId, boolean resurected){
        final E entity = E(entityId);
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
        if (!resurected) {
            // por si no tiene posision de origen o es la ciudad newbir y el jugador ya no es newbie
            if (entity.originPosMap() == 0 || (entity.getLevel().level>13 && entity.originPosMap()==286)){
                if (entity.getLevel().level < 13){
                    entity.originPosMap( 286 ).originPosX(50).originPosY(60);
                }else {
                    entity.originPosMap( 1 ).originPosX( 50 ).originPosY( 50 );
                }
            }
            entity.worldPosMap(entity.originPosMap()).worldPosX(entity.originPosY()).worldPosY(entity.originPosY());
        }
        sendEntityUpdate(entityId, resetUpdate.build());
        notifyUpdate(entityId, EntityUpdateBuilder.of(entityId).withComponents(entity.getWorldPos()).build());
    }

    private void dropItem(Integer key, Integer value, WorldPos worldPos) {
        entityFactorySystem.createObject(key, value, worldPos);
    }

    public void login(int connectionId, Player player) {
        final int entity = entityFactorySystem.createPlayer(player.getPlayerName(), player.getHero(), player.getTeam());
        List<Component> components = WorldUtils(getWorld()).getComponents(getWorld().getEntity(entity));
        components.add(new Focused());
        components.add(new AOPhysics());
        components.add(new CanWrite());
        networkManager.sendTo(connectionId, EntityUpdateBuilder.of(entity).withComponents(components.toArray(new Component[0])).build());
        registerEntity(connectionId, entity);
    }
}

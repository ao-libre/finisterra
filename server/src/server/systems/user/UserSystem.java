package server.systems.user;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;
import com.esotericsoftware.minlog.Log;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.EntityFactorySystem;
import server.systems.ServerSystem;
import server.systems.manager.WorldManager;
import shared.network.user.UserCreateResponse;
import shared.network.user.UserLoginResponse;

import java.io.*;
import java.util.Optional;

@Wire
public class UserSystem extends PassiveSystem {

    private ServerSystem serverSystem;
    private WorldManager worldManager;
    private EntityFactorySystem entityFactorySystem;
    private WorldSerializationManager worldSerializationManager;

    public void login(int connectionId, String userName) {
        if (userExists(userName)) {
            // login
            loadUser(userName)
                    .ifPresentOrElse(id -> {
                                serverSystem.sendTo(connectionId, UserLoginResponse.ok());
                                worldManager.login(connectionId, id);
                            },
                            () -> serverSystem.sendTo(connectionId,
                                    UserLoginResponse.failed("Hubo un problema al leer el personaje")));
        } else {
            // don't exist (should never happen)
            // TODO remove from Account ?
            serverSystem.sendTo(connectionId,
                    UserLoginResponse.failed("Este personaje no existe!"));
        }
    }

    public void create(int connectionId, String name, int heroId) {
        if (userExists(name)) {
            // send user exists
            serverSystem.sendTo(connectionId,
                    UserCreateResponse.failed("Este personaje ya existe!"));
        } else {
            // create and add to account
            int entityId = entityFactorySystem.create(name, heroId);
            saveUser(name);
            // send ok and login
            serverSystem.sendTo(connectionId,
                    UserCreateResponse.ok());
            worldManager.login(connectionId, entityId);
        }
    }

    private boolean userExists(String userName) {
        File file = new File("Charfile/" + userName + ".json");
        return file.isFile() && file.canRead();
    }

    public void saveUser(String name) {
        E user = E.withTag(name);
        if (user != null) {
            IntBag bag = new IntBag();
            bag.add(user.id());
            try (FileOutputStream outputStream = new FileOutputStream("Charfile/" + name + ".json")) {
                worldSerializationManager.save(outputStream, new SaveFileFormat(bag));
            } catch (IOException e) {
                Log.info("Couldn't save user with name:" + name);
                e.printStackTrace();
            }
        }
    }

    public Optional<Integer> loadUser(String name) {
        try (InputStream inputStream = new FileInputStream("Charfile/" + name + ".json")) {
            SaveFileFormat user = worldSerializationManager.load(inputStream, SaveFileFormat.class);
            // we expect one only entity
            return Optional.of(user.entities.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}

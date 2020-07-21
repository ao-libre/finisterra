package server.systems.world;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import component.entity.character.Character;
import component.entity.character.info.Name;
import org.jetbrains.annotations.NotNull;
import server.systems.account.UserSystem;

@Wire
public class WorldSaveSystem extends IntervalFluidIteratingSystem {

    private UserSystem userSystem;

    public WorldSaveSystem(float interval) {
        super(Aspect.all(Character.class, Name.class), interval);
    }

    @Override
    protected void process(@NotNull E e) {
        userSystem.save(e.id(), () -> {
        });
    }
}

package server.systems;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import component.console.ConsoleMessage;
import component.entity.character.states.Meditating;
import component.entity.character.status.Mana;
import component.entity.world.CombatMessage;
import component.graphic.Effect;
import server.systems.entity.EffectEntitySystem;
import server.systems.entity.SoundEntitySystem;
import server.systems.manager.WorldManager;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.MessageSystem;
import server.systems.network.UpdateTo;
import shared.interfaces.Constants;
import shared.util.EntityUpdateBuilder;
import shared.util.Messages;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;

@Wire
public class MeditateSystem extends IntervalFluidIteratingSystem {

    private static final float MANA_RECOVERY_PERCENT = 0.1f;
    private static Map<Integer, Integer> userMeditations = new HashMap<>();
    private WorldManager worldManager;
    private EntityUpdateSystem entityUpdateSystem;
    private MessageSystem messageSystem;
    private SoundEntitySystem soundEntitySystem;
    private EffectEntitySystem effectEntitySystem;

    public MeditateSystem(float timer) {
        super(Aspect.all(Meditating.class, Mana.class), timer);
    }

    @Override
    protected void process(E player) {
        Mana mana = player.getMana();
        EntityUpdateBuilder update = EntityUpdateBuilder.of(player.id());
        EntityUpdateBuilder notify = EntityUpdateBuilder.of(player.id());
        if (mana.min < mana.max) {
            int manaMin = mana.min;
            int prob = ThreadLocalRandom.current().nextInt(2);
            if (prob == 1) {
                // meditar
                mana.min += mana.max * MANA_RECOVERY_PERCENT;
                mana.min = Math.min(mana.min, mana.max);
                int recoveredMana = mana.min - manaMin;

                CombatMessage manaMessage = CombatMessage.magic("+" + recoveredMana);
                notify.withComponents(manaMessage);

                update.withComponents(mana);

                // send console message
                ConsoleMessage consoleMessage = ConsoleMessage.info(Messages.MANA_RECOVERED.name(), Integer.toString(recoveredMana));
                messageSystem.add(player.id(), consoleMessage);
            }
        }

        if (mana.min >= mana.max) {
            notify.remove(Meditating.class);
            ConsoleMessage consoleMessage = ConsoleMessage.info(Messages.MEDITATE_STOP.name());
            messageSystem.add(player.id(), consoleMessage);
            stopMeditationEffect(player.id());
        }

        if (!update.isEmpty()) {
            entityUpdateSystem.add(update.build(), UpdateTo.ENTITY);
        }

        if (!notify.isEmpty()) {
            entityUpdateSystem.add(notify.build(), UpdateTo.ALL);
        }
    }

    public void toggle(int userId) {
        E player = E(userId);
        boolean meditating = player.isMeditating();

        ConsoleMessage consoleMessage;
        EntityUpdateBuilder update = EntityUpdateBuilder.of(userId);

        if (meditating) {
            stopMeditationEffect(userId);
            consoleMessage = ConsoleMessage.info(Messages.MEDITATE_STOP.name());
            update.remove(Meditating.class);
        } else {
            E entity = E(userId);
            Mana mana = entity.getMana();
            if (mana != null && mana.min == mana.max) {
                consoleMessage = ConsoleMessage.info(Messages.MANA_FULL.name());
            } else {
                effectEntitySystem.addEffect(userId, Constants.MEDITATE_NW_FX, Effect.LOOP_INFINITE);
                soundEntitySystem.add(player.id(), 18, true);
                player.meditating();
                consoleMessage = ConsoleMessage.info(Messages.MEDITATE_START.name());
                update.withComponents(player.getMeditating());
            }
        }
        messageSystem.add(userId, consoleMessage);
        entityUpdateSystem.add(update.build(), UpdateTo.ALL);
    }

    private void stopMeditationEffect(int userId) {
        effectEntitySystem.removeEffect(userId, Constants.MEDITATE_NW_FX);
        soundEntitySystem.remove(userId, 18);
        E(userId).removeMeditating();
    }

}

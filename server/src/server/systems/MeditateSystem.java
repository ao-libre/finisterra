package server.systems;

import com.artemis.*;
import com.artemis.annotations.Wire;
import entity.character.states.Meditating;
import entity.character.status.Mana;
import entity.world.CombatMessage;
import graphics.FX;
import server.core.Server;
import shared.interfaces.Constants;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;

import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;
import static java.lang.String.format;
import static shared.util.Messages.*;

@Wire
public class MeditateSystem extends IntervalFluidIteratingSystem {

    public static final float MANA_RECOVERY_PERCENT = 0.05f;
    private Server server;
    private float timer;

    public MeditateSystem(Server server, float timer) {
        super(Aspect.all(Meditating.class, Mana.class), timer);
        this.server = server;
        this.timer = timer;
    }

    @Override
    protected void process(E e) {
        Mana mana = e.getMana();
        EntityUpdateBuilder update = EntityUpdateBuilder.of(e.id());
        EntityUpdateBuilder notify = EntityUpdateBuilder.of(e.id());
        if (mana.min < mana.max){
            int manaMin = mana.min;
            int prob = ThreadLocalRandom.current().nextInt(6);
            if (prob == 1) {
                // meditar
                mana.min += mana.max * MANA_RECOVERY_PERCENT;
                mana.min = Math.min(mana.min, mana.max);
                int recoveredMana = mana.min - manaMin;
                CombatMessage manaMessage = CombatMessage.magic("+" + recoveredMana);
                update.withComponents(mana);
                notify.withComponents(manaMessage);
                ConsoleMessage consoleMessage = ConsoleMessage.info(format(MANA_RECOVERED, recoveredMana));
                server.getWorldManager().sendEntityUpdate(e.id(), consoleMessage);
            }
        }

        if (mana.min >= mana.max) {
            e.removeFX();
            e.removeMeditating();
            notify.remove(FX.class,Meditating.class);
            ConsoleMessage consoleMessage = ConsoleMessage.info(MEDITATE_STOP);
            server.getWorldManager().sendEntityUpdate(e.id(), consoleMessage);
        }

        if (!update.isEmpty()) {
            server.getWorldManager().notifyUpdate(e.id(), update.build());
        }

        if (!notify.isEmpty()) {
            server.getWorldManager().notifyUpdate(e.id(), notify.build());
        }
    }

    public void toggle(int userId) {
        E player = E(userId);
        boolean meditating = player.isMeditating();

        ConsoleMessage consoleMessage;
        EntityUpdateBuilder update = EntityUpdateBuilder.of(userId);

        if (meditating) {
            player.removeFX();
            player.removeMeditating();
            consoleMessage = ConsoleMessage.info(MEDITATE_STOP);
            update.remove(FX.class, Meditating.class);
        } else {
            E entity = E(userId);
            Mana mana = entity.getMana();
            if (mana != null && mana.min == mana.max) {
                consoleMessage = ConsoleMessage.info(MANA_FULL);
            } else {
                player.fXAddParticleEffect(Constants.MEDITATE_NW_FX);
                player.meditating();
                consoleMessage = ConsoleMessage.info(MEDITATE_START);
                update.withComponents(player.getFX(), player.getMeditating());
            }
        }
        server.getWorldManager().sendEntityUpdate(userId, consoleMessage);
        server.getWorldManager().notifyUpdate(userId, update.build());
    }

}

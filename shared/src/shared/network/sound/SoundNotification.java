package shared.network.sound;

import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class SoundNotification implements INotification {

    private int soundNumber;

    public SoundNotification() {
    }

    public SoundNotification(int soundNumber) {
        this.soundNumber = soundNumber;
    }

    public int getSoundNumber() {
        return soundNumber;
    }

    public void setSoundNumber(int soundNumber) {
        this.soundNumber = soundNumber;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}

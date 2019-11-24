package shared.network.sound;

import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class SoundNotification implements INotification {

    private int soundNumber;
    private SoundState state;
    public SoundNotification() {
    }

    public SoundNotification(int soundNumber,SoundState state){
        this.soundNumber = soundNumber;
        this.state = state;
    }
    public SoundNotification(int soundNumber) {
        this.soundNumber = soundNumber;
        this.state = SoundState.PLAYING;
    }

    public int getSoundNumber() {
        return soundNumber;
    }

    public void setSoundNumber(int soundNumber) {
        this.soundNumber = soundNumber;
    }

    public void setState(SoundState state){
        this.state = state;
    }

    public SoundState getState(){
        return state;
    }

    public enum SoundState {
        PLAYING,
        STOPPED
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}

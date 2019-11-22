package shared.network.sound;

import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;

public class SoundNotification implements INotification {

    private String state;
    private int soundNumber;

    public SoundNotification() {
    }

    public SoundNotification(int soundNumber,String state){
        this.soundNumber = soundNumber;
        this.state = state;
    }
    public SoundNotification(int soundNumber) {
        this.soundNumber = soundNumber;
        this.state = "play";
    }

    public int getSoundNumber() {
        return soundNumber;
    }

    public void setSoundNumber(int soundNumber) {
        this.soundNumber = soundNumber;
    }

    public void setState(String state){
        this.state = state;
    }

    public String getState(){
        return state;
    }

    @Override
    public void accept(INotificationProcessor processor) {
        processor.processNotification(this);
    }
}

package Sound;

import com.artemis.Component;

public class AOSound extends Component {

    public int soundID;
    public boolean  shouldLoop;


    public AOSound(){
        soundID = -1;
        shouldLoop = false;
    }

    public AOSound(int ID, boolean loop){
        soundID = ID;
        shouldLoop = loop;
    }
}

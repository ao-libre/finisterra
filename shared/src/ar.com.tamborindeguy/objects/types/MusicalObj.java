package ar.com.tamborindeguy.objects.types;

import java.util.HashSet;
import java.util.Set;

public class MusicalObj extends Obj {

    private Set<Integer> soundIndexs = new HashSet<>();

    public MusicalObj(String name, int grhIndex) {
        super(name, grhIndex);
    }

    @Override
    public Type getType() {
        return Type.MUSICAL;
    }

    public Set<Integer> getSoundIndexs() {
        return soundIndexs;
    }

    public void addSoundIndexs(int soundIndex) {
        this.soundIndexs.add(soundIndex);
    }
}

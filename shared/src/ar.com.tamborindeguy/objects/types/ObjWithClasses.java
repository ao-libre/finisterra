package ar.com.tamborindeguy.objects.types;

import ar.com.tamborindeguy.interfaces.CharClass;
import org.ini4j.Profile;

import java.util.HashSet;
import java.util.Set;

public abstract class ObjWithClasses extends Obj {

    private Set<CharClass> forbiddenClasses = new HashSet<>();

    public ObjWithClasses(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    public void addClass(CharClass charClass) {
        forbiddenClasses.add(charClass);
    }

    public Set<CharClass> getForbiddenClasses() {
        return this.forbiddenClasses;
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        int classCount = CharClass.values().length;
        for (int i = 1; i < classCount; i++) {
            String child = section.get("CP" + i);
            if (child != null) {
                addClass(CharClass.getClass(child));
            }
        }
    }
}

package ar.com.tamborindeguy.objects.types;

import ar.com.tamborindeguy.interfaces.CharClass;
import org.ini4j.Profile;

import java.util.Set;

public abstract class ObjWithClasses extends Obj {

    private Set<CharClass> allowedClasses;

    public ObjWithClasses(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    public void addClass(CharClass charClass) {
        allowedClasses.add(charClass);
    }

    public Set<CharClass> getAllowedClasses() {
        return this.allowedClasses;
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        for (String child : section.childrenNames()) {
            if (child.startsWith("CP")) {
                String allowedClass = section.get(child);
                addClass(CharClass.getClass(allowedClass));
            }
        }
    }
}

package ar.com.tamborindeguy.objects.types;

import ar.com.tamborindeguy.interfaces.CharClass;
import ar.com.tamborindeguy.objects.factory.ObjectFactory;
import org.ini4j.Profile;

import java.util.Set;

public abstract class ObjWithClasses extends Obj {

    private Set<CharClass> allowedClasses;

    public ObjWithClasses(String name, int grhIndex) {
        super(name, grhIndex);
    }

    public void addClass(CharClass charClass){
        allowedClasses.add(charClass);
    }

    public Set<CharClass> getAllowedClasses() {
        return this.allowedClasses;
    }

    @Override
    public void fillObject(Profile.Section section) {
        super.fillObject(section);
        // TODO
        for (String child : section.childrenNames()) {
            if (child.startsWith("CP")) {
                String number = child.replace("CP", "");
                try {
                    int num = Integer.parseInt(number);
                    String allowedClass = section.get(child);
                    addClass(CharClass.getClass(allowedClass));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
    }
}

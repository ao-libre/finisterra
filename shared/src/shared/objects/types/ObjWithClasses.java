package shared.objects.types;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import org.ini4j.Profile;
import shared.interfaces.CharClass;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class ObjWithClasses extends Obj {

    private ForbiddenClasses forbiddenClasses = new ForbiddenClasses();

    public ObjWithClasses(int id, String name, int grhIndex) {
        super(id, name, grhIndex);
    }

    public ObjWithClasses() {
    }

    public void addClass(CharClass charClass) {
        forbiddenClasses.addClass(charClass);
    }

    public Set<CharClass> getForbiddenClasses() {
        return this.forbiddenClasses.getForbiddenClasses();
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

    private static class ForbiddenClasses implements Json.Serializable {

        private Set<CharClass> forbiddenClasses = new HashSet<>();

        public void addClass(CharClass charClass) {
            if (charClass == null) {
                return;
            }
            forbiddenClasses.add(charClass);
        }

        public Set<CharClass> getForbiddenClasses() {
            return forbiddenClasses;
        }

        @Override
        public void write(Json json) {
            if (forbiddenClasses == null || forbiddenClasses.isEmpty()) {
                return;
            }
            json.writeArrayStart("CP");
            forbiddenClasses.forEach(charClass -> {
                if (charClass != null) {
                    json.writeValue(charClass.name());
                }
            });
            json.writeArrayEnd();
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            final JsonValue cp = jsonData.child;
            if (cp == null) {
                return;
            }
            Arrays.stream(cp.asStringArray()).map(CharClass::getClass).forEach(this::addClass);
        }
    }
}

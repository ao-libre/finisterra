package component.entity.character.render;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import component.entity.character.states.Heading;

import java.util.Objects;

@PooledWeaver
public class CharRenderInfo extends Component {
    public static int NONE = -1;

    int body = NONE;
    int head = NONE;
    int shield = NONE;
    int helmet = NONE;
    int weapon = NONE;
    int heading = Heading.HEADING_SOUTH;

    public CharRenderInfo() {}

    public int getBody() {
        return body;
    }

    public void setBody(int body) {
        this.body = body;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }

    public int getHelmet() {
        return helmet;
    }

    public void setHelmet(int helmet) {
        this.helmet = helmet;
    }

    public int getWeapon() {
        return weapon;
    }

    public void setWeapon(int weapon) {
        this.weapon = weapon;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharRenderInfo that = (CharRenderInfo) o;
        return body == that.body &&
                head == that.head &&
                shield == that.shield &&
                helmet == that.helmet &&
                weapon == that.weapon &&
                heading == that.heading;
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, head, shield, helmet, weapon, heading);
    }
}
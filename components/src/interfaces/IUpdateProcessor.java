package interfaces;

import entity.*;
import entity.character.CanWrite;
import entity.character.Character;
import entity.character.info.Clan;
import entity.character.info.Description;
import entity.character.info.Name;
import entity.character.states.*;
import entity.character.status.*;
import interfaces.IUpdatable;

public interface IUpdateProcessor {

    void process(Clan clan);

    void process(Description description);

    void process(Name name);

    void process(Elv elv);

    void process(Exp exp);

    void process(Criminal criminal);

    void process(Immobile immobile);

    void process(Meditating meditating);

    void process(Navigating navigating);

    void process(Resting resting);

    void process(Writing writing);

    void process(GM gm);

    void process(Health health);

    void process(Hungry hungry);

    void process(Mana mana);

    void process(Level level);

    void process(Stamina stamina);

    void process(Thirst thirst);

    void process(CanWrite canWrite);

    void process(Character character);

    void process(Body body);

    void process(Dialog dialog);

    void process(Head head);

    void process(Heading heading);

    void process(Helmet helmet);

    void process(Shield shield);

    void process(Weapon weapon);
}

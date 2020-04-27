package server.database.model.modifiers;

import server.manager.ConfigurationSystem;
import shared.interfaces.CharClass;

public enum Modifiers {
    WEAPON,
    WEAPON_DAMAGE,
    PROJECTILE,
    PROJECTILE_DAMAGE,
    WRESTLING,
    WRESTLING_DAMAGE,
    EVASION,
    SHIELD,
    HEALTH;

    public float of(CharClass clazz) {
        return ConfigurationSystem.getInstance().getCharConfig().getCharClass(clazz)
                .getModifier().getValue(this);
    }
}

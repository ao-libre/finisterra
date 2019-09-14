package server.systems.battle.combat;

import com.artemis.E;
import com.badlogic.gdx.math.MathUtils;
import entity.character.attributes.Agility;
import entity.character.attributes.Attack;
import entity.character.attributes.Defense;
import entity.character.attributes.Strength;
import net.mostlyoriginal.api.system.core.PassiveSystem;

public class BattleCombatSystem extends PassiveSystem {

    public AttackResult attack(E attacker, E victim) {
        if (victim.hasShieldBlock()) {
            float probability = victim.getShieldBlock().getProbability();
            boolean blocked = MathUtils.randomBoolean(probability);
            if (blocked) {
                return AttackResult.block();
            }
        }
        if (attacker.hasAgility()) {
            Agility agility = attacker.getAgility();
            float attackerAgility = (float) agility.getCurrentValue();
            if (victim.hasAgility()) {
                float victimAgility = (float) victim.getAgility().getCurrentValue();
                float probability = attackerAgility / victimAgility;
                boolean dodged = MathUtils.randomBoolean(probability);
                if (dodged) {
                    return AttackResult.dodged();
                }
            }
        }

        Attack attack = attacker.getAttack();
        Strength strength = attacker.getStrength();

        Defense defense = victim.getDefense();
        Strength vStrength = victim.getStrength();

        int attackerStrength = strength.getCurrentValue();
        int attackerAttack = attack.getCurrentValue();
        int victimDefense = defense.getCurrentValue();
        int victimStrength = vStrength.getCurrentValue();

        int damage = (attackerStrength + Math.max(0, attackerAttack - victimDefense)) /
                (attackerStrength + victimStrength + victimDefense) *
                (attackerStrength + attackerAttack);

        return AttackResult.hit(damage);
    }

}

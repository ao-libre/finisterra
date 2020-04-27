package server.systems.world.entity.combat;

import java.util.Optional;

public interface CombatSystem {

    /*
    PoderEvasionEscudo = 100 * modShield(class) / 2
     */
    int shieldEvasionPower(int userId);

    /*
    lTemp = (100) + 3 * agility(user) * modEvasion(class)
    PoderEvasion = (lTemp + (2.5 * MaximoInt(.Stats.ELV - 12, 0)))
     */
    int evasionPower(int userId);

    /*
    PoderAtaqueTemp = (100 + 3 * agility(user)) * modWeaponAttack(class)
    PoderAtaqueArma = (PoderAtaqueTemp + (2.5 * MaximoInt(.Stats.ELV - 12, 0)))
     */
    int weaponAttackPower(int userId);

    /*
    PoderAtaqueTemp = (100 + 3 * agility(user)) * modProjectileAttack(class)
    PoderAtaqueProyectil = (PoderAtaqueTemp + (2.5 * MaximoInt(.Stats.ELV - 12, 0)))
     */
    int projectileAttackPower(int userId);

    /*
    PoderAtaqueTemp = (WrestlingSkill + 3 * .Stats.UserAtributos(eAtributos.Agilidad)) * modWrestlingAttack(class)
    PoderAtaqueWrestling = (PoderAtaqueTemp + (2.5 * MaximoInt(.Stats.ELV - 12, 0)))
     */
    int wrestlingAttackPower(int userId);

    /*
        arma = projectile | weapon | no
        modifier = projectile ? modProjectileAttack(class) : weapon ? modWeaponAttack(class) : modWrestlingAttack(class)
        weaponDamage = projectile || weapon ? random(arma.minHit. arma.maxHit) : random(4, 9)
        maxWeaponDamage = projectile || weapon ? arma.maxHit : 9
        userDamage = random(stats.minHit, stats.maxHit)
        totalDamage = (3 * weaponDamage + ((maxWeaponDamage / 5) * max(0, strenght(user) - 15) + userDamage) * modifier
     */

    int damageCalculation(int userId, int entityId); // could be npc or user

    /*
    if canAttack
        Intervalos
        hit
     */
    void entityAttack(int userId, Optional<Integer> targetId);


    /*
    i'm death, target is spirit, other team, etc
     */
    boolean canAttack(int userId, Optional<Integer> targetId);

    /*
        probability
        evasion
        shieldEvasion
     */
    void hit(int userId, int entityId);
}

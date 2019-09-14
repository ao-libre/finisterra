package server.systems.battle.combat;

public class AttackResult {

    enum Kind {
        HIT,
        MISS,
        DODGED,
        BLOCK;
    }

    private Kind kind;

    private int hit;

    public AttackResult(Kind kind) {
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    public int getHit() {
        return hit;
    }

    public static AttackResult block() {
        return new AttackResult(Kind.BLOCK);
    }

    public static AttackResult hit(int value) {
        AttackResult attackResult = new AttackResult(Kind.HIT);
        attackResult.hit = value;
        return attackResult;
    }

    public static AttackResult dodged() {
        return new AttackResult(Kind.DODGED);
    }

}

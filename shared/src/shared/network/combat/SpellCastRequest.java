package shared.network.combat;

import position.WorldPos;
import shared.model.Spell;
import shared.network.interfaces.IRequest;
import shared.network.interfaces.IRequestProcessor;

public class SpellCastRequest implements IRequest {
    private Spell spell;
    private WorldPos worldPos;

    public SpellCastRequest(){}

    public SpellCastRequest(Spell spell, WorldPos worldPos) {
        this.spell = spell;
        this.worldPos = worldPos;
    }

    public Spell getSpell() {
        return spell;
    }

    public WorldPos getWorldPos() {
        return worldPos;
    }

    @Override
    public void accept(IRequestProcessor processor, int connectionId) {
        processor.processRequest(this, connectionId);
    }
}

package ar.com.tamborindeguy.network.combat;

import ar.com.tamborindeguy.model.Spell;
import ar.com.tamborindeguy.network.interfaces.IRequest;
import ar.com.tamborindeguy.network.interfaces.IRequestProcessor;
import position.WorldPos;

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

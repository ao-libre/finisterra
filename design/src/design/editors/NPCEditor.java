package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import shared.model.npcs.NPC;

import java.util.Collections;

import static design.screens.views.View.SKIN;

public class NPCEditor extends Dialog {

    private NPC npc;

    public NPCEditor(NPC npc) {
        super("NPC Editor", SKIN);
        // TODO do npc copy
        this.npc = npc;
        addTable();
        button("Cancel", false);
        button("OK", npc);
    }

    private void addTable() {
        Table table = new Table(SKIN);
        table.add(StringEditor.simple("Name", name -> npc.setName(name), () -> npc.getName())).expandX().row();
        table.add(IntegerEditor.list("Head", Collections::emptyList, head -> npc.setHead(head), () -> npc.getHead())).expandX().row();
        table.add(IntegerEditor.list("Body", Collections::emptyList, body -> npc.setBody(body), () -> npc.getBody())).expandX().row();
        table.add(IntegerEditor.list("HP", Collections::emptyList, hp -> npc.setMaxHP(hp), () -> npc.getMaxHP())).expandX().row();
        table.add(IntegerEditor.simple("Min Hit", minHit -> npc.setMinHit(minHit), () -> npc.getMinHit())).expandX().row();
        table.add(IntegerEditor.simple("Max Hit", hit -> npc.setMaxHit(hit), () -> npc.getMaxHit())).expandX().row();
        table.add(IntegerEditor.simple("Def", def -> npc.setDef(def), () -> npc.getDef())).expandX().row();
        table.add(IntegerEditor.simple("Magic Def", defM -> npc.setDefM(defM), () -> npc.getDefM())).expandX().row();
        table.add(IntegerEditor.simple("Evasion", evasion -> npc.setEvasionPower(evasion), () -> npc.getEvasionPower())).expandX().row();
        table.add(IntegerEditor.simple("Attack", attack -> npc.setAttackPower(attack), () -> npc.getAttackPower())).expandX().row();
        table.add(IntegerEditor.simple("Gold", gold -> npc.setGiveGLD(gold), () -> npc.getGiveGLD())).expandX().row();
        table.add(IntegerEditor.simple("Exp", exp -> npc.setGiveEXP(exp), () -> npc.getGiveEXP())).expandX().row();
        table.add(BooleanEditor.simple("Hostile", hostile -> npc.setHostile(hostile), () -> npc.isHostile())).expandX().row();

        getContentTable().add(new ScrollPane(table)).prefHeight(300).prefWidth(300);
    }

}

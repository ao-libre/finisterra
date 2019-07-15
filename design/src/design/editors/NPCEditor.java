package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.NotNull;
import shared.model.npcs.NPC;

import java.util.Collections;

import static design.screens.views.View.SKIN;

public class NPCEditor extends Dialog {

    private NPC npc;

    public NPCEditor(NPC npc) {
        super("NPC Editor", SKIN);
        this.npc = npc;
        addTable();
        button("Cancel", false);
        button("OK", npc);
    }

    private void addTable() {
        getContentTable().add(new ScrollPane(getTable(npc))).prefHeight(300).prefWidth(300);
    }

    @NotNull
    public static Table getTable(NPC npc) {
        Table table = new Table(SKIN);
        table.setDebug(true);
        table.defaults().growX().uniform();
        table.add(StringEditor.simple("Name", name -> npc.setName(name), () -> npc.getName())).row();
        table.add(IntegerEditor.list("Head", Collections::emptyList, head -> npc.setHead(head), () -> npc.getHead())).row();
        table.add(IntegerEditor.list("Body", Collections::emptyList, body -> npc.setBody(body), () -> npc.getBody())).row();
        table.add(IntegerEditor.list("HP", Collections::emptyList, hp -> npc.setMaxHP(hp), () -> npc.getMaxHP())).row();
        table.add(IntegerEditor.simple("Min Hit", minHit -> npc.setMinHit(minHit), () -> npc.getMinHit())).row();
        table.add(IntegerEditor.simple("Max Hit", hit -> npc.setMaxHit(hit), () -> npc.getMaxHit())).row();
        table.add(IntegerEditor.simple("Def", def -> npc.setDef(def), () -> npc.getDef())).row();
        table.add(IntegerEditor.simple("Magic Def", defM -> npc.setDefM(defM), () -> npc.getDefM())).row();
        table.add(IntegerEditor.simple("Evasion", evasion -> npc.setEvasionPower(evasion), () -> npc.getEvasionPower())).row();
        table.add(IntegerEditor.simple("Attack", attack -> npc.setAttackPower(attack), () -> npc.getAttackPower())).row();
        table.add(IntegerEditor.simple("Gold", gold -> npc.setGiveGLD(gold), () -> npc.getGiveGLD())).row();
        table.add(IntegerEditor.simple("Exp", exp -> npc.setGiveEXP(exp), () -> npc.getGiveEXP())).row();
        table.add(BooleanEditor.simple("Hostile", hostile -> npc.setHostile(hostile), () -> npc.isHostile())).row();
        return table;
    }

}

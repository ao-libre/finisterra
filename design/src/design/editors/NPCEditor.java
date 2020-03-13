package design.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import design.editors.fields.BooleanEditor;
import design.editors.fields.FieldEditor.FieldListener;
import design.editors.fields.FieldProvider;
import design.editors.fields.IntegerEditor;
import design.editors.fields.StringEditor;
import org.jetbrains.annotations.NotNull;
import shared.model.npcs.NPC;

import static launcher.DesignCenter.SKIN;

public class NPCEditor extends Dialog {

    private NPC npc;

    public NPCEditor(NPC npc) {
        super("NPC Editor", SKIN);
        this.npc = npc;
        addTable();
        button("Cancel", false);
        button("OK", npc);
    }

    @NotNull
    public static Table getTable(NPC npc, FieldListener listener) {
        Table table = new Table(SKIN);
        table.defaults().growX().uniform();
        // common
        table.add(IntegerEditor.create("ID", npc::setId, npc::getId, listener)).row();
        table.add(StringEditor.simple("Name", npc::setName, npc::getName, listener)).row();
        table.add(StringEditor.simple("Description", npc::setDesc, npc::getDesc, listener)).row();
        table.add(IntegerEditor.create("Type", npc::setNpcType, npc::getNpcType, listener)).row();

        // image
        table.add(IntegerEditor.create("Head", FieldProvider.HEAD, npc::setHead, npc::getHead, listener)).row();
        table.add(IntegerEditor.create("Body", FieldProvider.BODY, npc::setBody, npc::getBody, listener)).row();

        // hostile
        table.add(BooleanEditor.simple("Hostile", npc::setHostile, npc::isHostile, listener)).left().row();
        table.add(IntegerEditor.create("Min HP", npc::setMinHP, npc::getMinHP, listener)).row();
        table.add(IntegerEditor.create("Max HP", npc::setMaxHP, npc::getMaxHP, listener)).row();
        table.add(IntegerEditor.create("Min Hit", npc::setMinHit, npc::getMinHit, listener)).row();
        table.add(IntegerEditor.create("Max Hit", npc::setMaxHit, npc::getMaxHit, listener)).row();
        table.add(IntegerEditor.create("Def", npc::setDef, npc::getDef, listener)).row();
        table.add(IntegerEditor.create("Magic Def", npc::setDefM, npc::getDefM, listener)).row();
        table.add(IntegerEditor.create("Evasion", npc::setEvasionPower, npc::getEvasionPower, listener)).row();
        table.add(IntegerEditor.create("Attack", npc::setAttackPower, npc::getAttackPower, listener)).row();
        table.add(IntegerEditor.create("Gold", npc::setGiveGLD, npc::getGiveGLD, listener)).row();
        table.add(IntegerEditor.create("Exp", npc::setGiveEXP, npc::getGiveEXP, listener)).row();
        table.add(IntegerEditor.create("Attack Sound N.", npc::setAttackSnd, npc::getAttackSnd, listener)).row();
        table.add(IntegerEditor.create("Get Hit Sound N.", npc::setGHitSnd, npc::getGHitSnd, listener)).row();
        table.add(IntegerEditor.create("Die Sound N.", npc::setDieSound, npc::getDieSound, listener)).row();
        table.add(IntegerEditor.create("Walk Sound N.", npc::setWalkSnd, npc::getWalkSnd, listener)).row();
        npc.getSpells();
        npc.getExpressions();
        npc.getDrops();

        npc.isRespawn();
        npc.isCommerce();
        npc.isAttackable();
        npc.isAffectParalysis();


        // trainer
        npc.getNpcSpanwer();

        // movement
        table.add(IntegerEditor.create("Movement", npc::setMovement, npc::getMovement, listener)).row();
        table.add(IntegerEditor.create("City", npc::setCity, npc::getCity, listener)).row();
        table.add(IntegerEditor.create("Item Type", npc::setItemTypes, npc::getItemTypes, listener)).row();

        // commerce
        npc.getObjs();

        return table;
    }

    private void addTable() {
        getContentTable().add(new ScrollPane(getTable(new NPC(npc), () -> {
        }))).prefHeight(300).prefWidth(300);
    }

}

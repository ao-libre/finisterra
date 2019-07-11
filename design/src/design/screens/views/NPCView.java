package design.screens.views;

import camera.Focused;
import com.artemis.E;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import design.designers.NPCDesigner;
import design.designers.NPCDesigner.NPCParameters;
import game.screens.WorldScreen;
import position.WorldPos;
import shared.model.npcs.NPC;
import shared.model.npcs.NPCToEntity;

import java.util.Comparator;

import static com.artemis.E.E;

public class NPCView extends View<NPC, NPCDesigner> implements WorldScreen {

    public NPCView() {
        super(new NPCDesigner(NPCParameters.dat()));
        createUI();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        NPC npc = getPreview().get();
        if (npc != null) {
            getPreview().show(npc);
        }
    }

    @Override
    Preview<NPC> createPreview(Table viewTable) {
        NPCPreview npcPreview = new NPCPreview();
        viewTable.add(npcPreview.getPreviewContent()).right();
        return npcPreview;
    }

    @Override
    protected void sort(Array<NPC> items) {
        items.sort(Comparator.comparingInt(NPC::getId));
    }

    @Override
    protected void keyPressed(int keyCode) {

    }

    class NPCPreview implements Preview<NPC> {

        private SpriteBatch batch = new SpriteBatch();
        private int entityId = -1;
        private final Label label;
        private final Image image;
        Table previewContent = new Table(SKIN);
        private NPC npc;

        public NPCPreview() {
            label = new Label("", SKIN);
            image = new Image();
            previewContent.add(label).row();
            previewContent.add(image);
        }

        public Table getPreviewContent() {
            return previewContent;
        }

        @Override
        public void show(NPC npc) {
            if (!npc.equals(this.npc)) {
                this.npc = npc;
                label.setText(npc.getName());
                World world = getWorld();
                E.withComponent(Focused.class).iterator().forEachRemaining(E::deleteFromWorld);
                entityId = NPCToEntity.getNpcEntity(world, npc.getId(), new WorldPos(1, 1, 1), npc);
                E(entityId).focused();
            }
        }

        @Override
        public NPC get() {
            return npc;
        }
    }
}

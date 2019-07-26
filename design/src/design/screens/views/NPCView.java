package design.screens.views;

import camera.Focused;
import com.artemis.E;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import design.designers.NPCDesigner;
import design.designers.NPCDesigner.NPCParameters;
import design.editors.NPCEditor;
import game.screens.WorldScreen;
import game.systems.camera.CameraSystem;
import game.systems.render.world.CharacterRenderingSystem;
import position.Pos2D;
import position.WorldPos;
import shared.model.npcs.NPC;
import shared.model.npcs.NPCToEntity;
import shared.util.Util;

import java.util.Comparator;
import java.util.Optional;

import static com.artemis.E.E;
import static game.systems.render.world.CharacterRenderingSystem.CharacterDrawer.createDrawer;

public class NPCView extends View<NPC, NPCDesigner> implements WorldScreen {

    public NPCView() {
        super(new NPCDesigner(NPCParameters.dat()));
    }

    @Override
    Preview<NPC> createPreview() {
        return new NPCPreview();
    }

    @Override
    Preview<NPC> createItemView() {
        return new NPCItem();
    }

    @Override
    protected void sort(Array<NPC> items) {
        items.sort(Comparator.comparingInt(NPC::getId));
    }

    @Override
    protected void keyPressed(int keyCode) {
    }

    class NPCItem extends Preview<NPC> {

        private Actor view;
        private NPC npc;

        public NPCItem() {
            super(SKIN);
        }

        @Override
        void show(NPC npc) {
            this.npc = npc;
            if (view != null) {
                clear();
            }
            add(view = NPCEditor.getTable(npc)).growX();
        }

        @Override
        NPC get() {
            return npc;
        }

    }

    class NPCPreview extends Preview<NPC> {

        private int entityId = -1;
        private final Label label;
        private final AnimationActor animationActor;
        private NPC npc;

        public NPCPreview() {
            super(SKIN);
            defaults().space(10);
            pad(20);
            label = new Label("", SKIN);
            animationActor = new AnimationActor();
            add(label).top().row();
            add(animationActor).top();
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
                animationActor.setEntityId(entityId);
            }
        }

        @Override
        public NPC get() {
            return npc;
        }

    }

    class AnimationActor extends Actor {

        private int entityId = -1;

        public AnimationActor(int entityId) {
            this.entityId = entityId;
        }

        public AnimationActor() {

        }

        public int getEntityId() {
            return entityId;
        }

        public void setEntityId(int entityId) {
            this.entityId = entityId;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (entityId >= 0) {
                createDrawer(batch, E(entityId), new Pos2D(getX() - 32, getY()), getDescriptorHandler(), getAnimationHandler(), true).draw();
            }
        }

    }
}
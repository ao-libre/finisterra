package design.screens.views;

import component.camera.Focused;
import com.artemis.E;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import design.designers.NPCDesigner;
import design.designers.NPCDesigner.NPCParameters;
import design.editors.NPCEditor;
import component.entity.character.parts.Body;
import component.entity.character.states.Heading;
import model.textures.BundledAnimation;
import org.jetbrains.annotations.NotNull;
import component.position.WorldPos;
import shared.model.map.Tile;
import shared.model.npcs.NPC;
import shared.model.npcs.NPCToEntity;

import java.util.Comparator;

import static com.artemis.E.E;
import static design.editors.fields.FieldEditor.FieldListener;
import static launcher.DesignCenter.SKIN;

public class NPCView extends View<NPC, NPCDesigner> {

    public NPCView() {
        super(new NPCDesigner(NPCParameters.dat()));
    }

    @Override
    Preview<NPC> createPreview() {
        return new NPCPreview();
    }

    @Override
    Editor<NPC> createItemView() {
        return new NPCItem();
    }

    @Override
    protected void sort(Array<NPC> items) {
        items.sort(Comparator.comparingInt(NPC::getId));
    }

    @Override
    protected void keyPressed(int keyCode) {
    }

    class NPCItem extends Editor<NPC> {

        NPCItem() {
            super(SKIN);
        }

        @NotNull
        @Override
        protected Table getTable(FieldListener listener) {
            return NPCEditor.getTable(current, listener);
        }

        @Override
        protected NPC getCopy(NPC to) {
            return new NPC(to);
        }

    }

    class NPCPreview extends Preview<NPC> {

        private int entityId = -1;
        private Label label;
        private AnimationActor animationActor;
        private NPC npc;

        NPCPreview() {
            super(SKIN);
        }

        void init() {
            Button table = new Button(SKIN, "color-base-static");
            table.defaults().space(5);
            Table buttons = new Table();
            label = new Label("", SKIN);
            Container anim = new Container();
            animationActor = new AnimationActor();
            Button move = new Button(SKIN, "switch");
            move.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    animationActor.move();
                }
            });
            buttons.add(move).right();
            Button rotate = new Button(SKIN, "colorwheel");
            rotate.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    animationActor.rotate();
                }
            });
            buttons.add(rotate).right();
            table.add(buttons).right();
            table.row();
            anim.setActor(animationActor);
            table.add(anim).row();
            table.add(label);
            add(table);
        }

        @Override
        public void show(NPC npc) {
            if (this.npc == null) {
                init();
            }
            this.npc = npc;
            label.setText(npc.getName());
            World world = getWorld();
            E.withComponent(Focused.class).iterator().forEachRemaining(E::deleteFromWorld);
            entityId = NPCToEntity.getNpcEntity(world, npc.getId(), new WorldPos(1, 1, 1), npc);
            E(entityId).focused().moving();
            animationActor.setEntityId(entityId);
        }

        @Override
        public NPC get() {
            return npc;
        }

    }

    class AnimationActor extends Actor {

        private int entityId = -1;
        private int heading = Heading.HEADING_SOUTH;

        AnimationActor() {
        }

        void setEntityId(int entityId) {
            this.entityId = entityId;
            E e = E(entityId);
            if (e.hasBody()) {
                final Body body = e.getBody();
                heading = Heading.HEADING_SOUTH;
                if (body.index <= 0) {

                    return;
                }
                BundledAnimation bodyAnimation = getAnimationHandler().getBodyAnimation(body, heading);
                if (bodyAnimation != null) {
                    TextureRegion graphic = bodyAnimation.getGraphic();
                    setSize(graphic.getRegionWidth(), graphic.getRegionHeight());
                }
            }
        }

        void move() {
            if (entityId >= 0) {
                E(entityId).moving(!E(entityId).isMoving());
            }
        }

        void rotate() {
            if (entityId >= 0) {
                heading = (heading + 1) % 4;
                E(entityId).headingCurrent(heading);
            }
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (entityId >= 0) {
                E e = E(entityId);
                if (e.hasBody()) {
                    final Body body = e.getBody();
                    BundledAnimation bodyAnimation = getAnimationHandler().getBodyAnimation(body, heading);
                    bodyAnimation.setAnimationTime(bodyAnimation.getAnimationTime() + delta);
                }
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (entityId >= 0) {
                float offset = (Tile.TILE_PIXEL_WIDTH - getWidth()) / 2;
                if (validate()) {
//                    createDrawer(batch, E(entityId), new Pos2D(getX() - offset, getY() + getHeight()), getDescriptorHandler(), getAnimationHandler(), true).draw();
                }
            }
        }

        private boolean validate() {
            boolean result = true;
            if (entityId >= 0) {
                E e = E(entityId);
                if (e.hasBody()) {
                    result = getDescriptorHandler().hasBody(e.getBody().index);
                }
                if (e.hasHead()) {
                    result &= getDescriptorHandler().hasHead(e.getHead().index);
                }
            }
            return result;
        }

    }
}

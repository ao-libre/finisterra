package design.screens.views;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import component.graphic.EffectBuilder;
import design.designers.SpellDesigner;
import design.editors.GenericEditor;
import design.editors.fields.FieldEditor;
import design.graphic.AOAnimationActor;
import design.screens.ScreenEnum;
import model.descriptors.FXDescriptor;
import model.textures.BundledAnimation;
import org.jetbrains.annotations.NotNull;
import shared.model.Spell;

import static launcher.DesignCenter.SKIN;

public class SpellView extends View<Spell, SpellDesigner> {

    public SpellView() {
        super(new SpellDesigner());
    }

    @Override
    Preview<Spell> createPreview() {
        return new SpellPreview();
    }

    @Override
    Editor<Spell> createItemView() {
        return new SpellItemView();
    }

    @Override
    protected void sort(Array<Spell> items) {
    }

    @Override
    protected void keyPressed(int keyCode) {
    }

    class SpellItemView extends Editor<Spell> {

        SpellItemView() {
            super(SKIN);
        }

        @NotNull
        @Override
        protected Table getTable(FieldEditor.FieldListener listener) {
            return GenericEditor.getTable(get(), listener);
        }

        @Override
        protected Spell getCopy(Spell to) {
            return to;
        }
    }

    class SpellPreview extends Preview<Spell> {

        private Spell spell;

        SpellPreview() {
            super(SKIN);
        }

        @Override
        void show(Spell spell) {
            clear();
            this.spell = spell;

            int fxGrh = spell.getFxGrh();
            ScreenEnum.FXS_VIEW.getScreen()
                    .getDesigner()
                    .get(fxGrh)
                    .filter(FXDescriptor.class::isInstance)
                    .ifPresent(fx -> {
                        FXDescriptor descriptor = (FXDescriptor) fx;
                        BundledAnimation anim = getAnimationHandler().getFX(EffectBuilder.create().withFX(descriptor.id).build());
                        AOAnimationActor animation = new AOAnimationActor(anim);
                        add(animation);
                    });
        }

        @Override
        Spell get() {
            return spell;
        }
    }
}

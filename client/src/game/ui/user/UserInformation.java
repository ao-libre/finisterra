package game.ui.user;

import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import game.ui.WidgetFactory;

public class UserInformation extends Table {

    private ProgressBar hp;
    private Label hpLabel;
    private ProgressBar mana;
    private Label manaLabel;
    private E e;

    // TODO refactor. Don't use E
    public UserInformation(E e) {
        Stack stack = new Stack();
        this.e = e;
        // stack add bars
        Table bars = new Table();

        bars.add(WidgetFactory.createLabel("")).growY().row();
        bars.add(createHpBar()).height(20).width(320).row();
        bars.add(createManaBar()).height(20).width(320).row();
        stack.add(bars);
        // stack add frame with name
        Table frame = new Table();
        frame.setBackground(WidgetFactory.createDrawable(WidgetFactory.Drawables.USER_FRAME.name));
        frame.add(WidgetFactory.createUserLabel(e.nameText().toUpperCase())).top().padTop(-25).height(18);
        stack.add(frame);
        add(stack);
    }

    private Stack createManaBar() {
        Stack stack = new Stack();
        mana = WidgetFactory.createProgressBar(WidgetFactory.ProgressBars.MANA);
        mana.setAnimateDuration(0.200f);
        mana.setAnimateInterpolation(Interpolation.fastSlow);
        stack.add(mana);
        manaLabel = WidgetFactory.createBarLabel("");
        manaLabel.setWidth(320);
        manaLabel.setAlignment(Align.center);
        stack.add(manaLabel);
        stack.add(WidgetFactory.createBarOverlayImage());
        return stack;
    }

    private Stack createHpBar() {
        Stack stack = new Stack();
        hp = WidgetFactory.createProgressBar(WidgetFactory.ProgressBars.HP);
        hp.setAnimateDuration(0.200f);
        hp.setAnimateInterpolation(Interpolation.fastSlow);
        stack.add(hp);
        hpLabel = WidgetFactory.createBarLabel("");
        hpLabel.setWidth(320);
        hpLabel.setAlignment(Align.center);
        stack.add(hpLabel);
        stack.add(WidgetFactory.createBarOverlayImage());
        return stack;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (e == null) return;
        if (e.hasHealth()) {
            if (hp.getMaxValue() != e.healthMax()) {
                hp.setRange(0, e.healthMax());
            }
            int hp = e.healthMin();
            if (hp != this.hp.getValue()) {
                setHp(hp);
            }
        }
        if (e.hasMana()) {
            if (mana.getMaxValue() != e.manaMax()) {
                mana.setRange(0, e.manaMax());
            }
            int mana = e.manaMin();
            if (mana != this.mana.getValue()) {
                setMana(mana);
            }
        }
    }

    private void setHp(int value) {
        hp.setValue(value);
        hpLabel.setText(e.healthMin() + "/" + e.healthMax());
    }

    private void setMana(int value) {
        mana.setValue(value);
        manaLabel.setText(e.manaMin() + "/" +e.manaMax());
    }
}

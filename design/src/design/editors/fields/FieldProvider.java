package design.editors.fields;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import design.screens.ScreenEnum;
import design.screens.ScreenManager;
import design.screens.views.View;
import design.screens.views.View.Editor;
import design.screens.views.View.State;
import model.ID;

import java.util.function.Consumer;

public enum FieldProvider {
    HEAD {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.HEADS_VIEW;
        }

    },
    BODY {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.BODIES_VIEW;
        }
    },
    SHIELD {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.SHIELDS_VIEW;
        }
    },
    WEAPON {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.WEAPONS_VIEW;
        }
    },
    ANIMATION {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.ANIMATION_VIEW;
        }
    },
    IMAGE {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.IMAGE_VIEW;
        }
    },
    TILE_SET {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.TILE_SET_VIEW;
        }
    },
    FX {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.FXS_VIEW;
        }
    },
    NONE {
        @Override
        public ScreenEnum getScreen() {
            return null;
        }
    };


    public abstract ScreenEnum getScreen();

    public Listener<? extends ID> getListener(Consumer<Integer> consumer, TextField editor, Screen current) {
        return (descriptor) -> {
            ScreenManager.getInstance().showScreen(current);
            consumer.accept(descriptor.getId());
            editor.setText(descriptor.getId() + "");
            if (current instanceof View) {
                Editor itemView = ((View) current).getItemView();
                itemView.setState(State.MODIFIED);
                ((View) current).refreshPreview();
            }
        };
    }

    public <T> void search(TextField editor, Consumer<Integer> consumer, Screen current) {
        ScreenManager.getInstance().showScreen(getScreen(), getListener(consumer, editor, current));
    }
}

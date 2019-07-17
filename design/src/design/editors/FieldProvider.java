package design.editors;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import design.screens.ScreenEnum;
import design.screens.ScreenManager;
import design.screens.views.View;
import model.ID;
import model.descriptors.HeadDescriptor;

import java.util.function.Consumer;

public enum FieldProvider {
    HEAD {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.HEADS_VIEW;
        }

        @Override
        public Listener<? extends ID> getListener(Consumer<Integer> consumer, TextField editor, Screen current) {
            return (Listener<HeadDescriptor>) headDescriptor -> {
                consumer.accept(headDescriptor.getId());
                editor.setText(headDescriptor.getId() + "");
                if (current instanceof View) {
                    ((View) current).refresh();
                }
            };
        }
    },
    BODY {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.BODIES_VIEW;
        }

        @Override
        public Listener getListener(Consumer<Integer> consumer, TextField editor, Screen current) {
            return null;
        }
    },
    SHIELD {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.SHIELDS_VIEW;
        }

        @Override
        public Listener getListener(Consumer<Integer> consumer, TextField editor, Screen current) {
            return null;
        }
    },
    WEAPON {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.WEAPONS_VIEW;
        }

        @Override
        public Listener getListener(Consumer<Integer> consumer, TextField editor, Screen current) {
            return null;
        }
    },
    ANIMATION {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.ANIMATION_VIEW;
        }

        @Override
        public Listener getListener(Consumer<Integer> consumer, TextField editor, Screen current) {
            return null;
        }
    },
    IMAGE {
        @Override
        public ScreenEnum getScreen() {
            return ScreenEnum.IMAGE_VIEW;
        }

        @Override
        public Listener getListener(Consumer<Integer> consumer, TextField editor, Screen current) {
            return null;
        }
    },
    NONE {
        @Override
        public ScreenEnum getScreen() {
            return null;
        }

        @Override
        public Listener getListener(Consumer<Integer> consumer, TextField editor, Screen current) {
            return null;
        }
    };


    public abstract ScreenEnum getScreen();

    public abstract Listener<? extends ID> getListener(Consumer<Integer> consumer, TextField editor, Screen current);

    public <T> void search(TextField editor, Consumer<Integer> consumer, Screen current) {
        ScreenManager.getInstance().showScreen(getScreen(), getListener(consumer, editor, current));
    }
}

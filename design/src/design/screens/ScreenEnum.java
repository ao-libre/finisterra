package design.screens;

import com.badlogic.gdx.Screen;

import design.screens.views.AnimationView;
import design.screens.views.ImageView;
import design.screens.views.NPCView;

public enum ScreenEnum {
    NPC_VIEW {
        private NPCView npcView;

        public Screen getScreen(Object... params) {
            if (npcView == null) {
                npcView = new NPCView();
            }
            return npcView;
        }
    },
    ANIMATION_VIEW {
        private AnimationView animationView;

        public Screen getScreen(Object... params) {
            if (animationView == null) {
                animationView = new AnimationView();
            }
            return animationView;
        }
    },
    IMAGE_VIEW {
        private ImageView imageView;

        public Screen getScreen(Object... params) {
            if (imageView == null) {
                imageView = new ImageView();
            }
            return imageView;
        }
    };

    public abstract Screen getScreen(Object... params);
}

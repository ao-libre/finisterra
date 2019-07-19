package design.screens;

import design.editors.fields.Listener;
import design.screens.views.*;

public enum ScreenEnum {
    IMAGE_VIEW("Images") {
        private ImageView imageView;

        public View getScreen(Object... params) {
            if (imageView == null) {
                imageView = new ImageView();
            }
            readParams(imageView, params);
            return imageView;
        }
    },
    ANIMATION_VIEW("Animations") {
        private AnimationView animationView;

        public View getScreen(Object... params) {
            if (animationView == null) {
                animationView = new AnimationView();
            }
            readParams(animationView, params);
            return animationView;
        }
    },
    NPC_VIEW("NPCs") {
        private NPCView npcView;

        public View getScreen(Object... params) {
            if (npcView == null) {
                npcView = new NPCView();
            }
            readParams(npcView, params);
            return npcView;
        }
    },
    BODIES_VIEW("Bodies") {
        private View view;

        public View getScreen(Object... params) {
            if (view == null) {
                view = new BodiesView();
            }
            readParams(view, params);
            return view;
        }
    },
    HEADS_VIEW("Heads") {
        private View headView;

        public View getScreen(Object... params) {
            if (headView == null) {
                headView = new HeadsView();
            }
            readParams(headView, params);
            return headView;
        }
    },
    SHIELDS_VIEW("Shields") {
        private View view;

        public View getScreen(Object... params) {
            if (view == null) {
                view = new ShieldsView();
            }
            readParams(view, params);
            return view;
        }
    },
    WEAPONS_VIEW("Weapons") {
        private View view;

        public View getScreen(Object... params) {
            if (view == null) {
                view = new WeaponsView();
            }
            readParams(view, params);
            return view;
        }
    },
    HELMETS_VIEW ("Helmets"){
        private View view;

        public View getScreen(Object... params) {
            if (view == null) {
                view = new HelmetsView();
            }
            readParams(view, params);
            return view;
        }
    },
    FXS_VIEW("FXs") {
        private View view;

        public View getScreen(Object... params) {
            if (view == null) {
                view = new FXsView();
            }
            readParams(view, params);
            return view;
        }
    },;

    private String title;

    ScreenEnum(String title) {
        this.title = title;
    }

    public abstract View getScreen(Object... params);

    void readParams(View view, Object... params) {
        if (params.length > 0) {
            if (params[0] instanceof Listener) {
                view.setListener((Listener) params[0]);
            }
        }
    }

    public String getTitle() {
        return title;
    }
}

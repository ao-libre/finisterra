package design.screens;

import design.editors.fields.Listener;
import design.screens.views.*;

public enum ScreenEnum {
    NPC_VIEW {
        private NPCView npcView;

        public View getScreen(Object... params) {
            if (npcView == null) {
                npcView = new NPCView();
            }
            readParams(npcView, params);
            return npcView;
        }
    },
    ANIMATION_VIEW {
        private AnimationView animationView;

        public View getScreen(Object... params) {
            if (animationView == null) {
                animationView = new AnimationView();
            }
            readParams(animationView, params);
            return animationView;
        }
    },
    IMAGE_VIEW {
        private ImageView imageView;

        public View getScreen(Object... params) {
            if (imageView == null) {
                imageView = new ImageView();
            }
            readParams(imageView, params);
            return imageView;
        }
    },
    HEADS_VIEW {
        private View headView;

        public View getScreen(Object... params) {
            if (headView == null) {
                headView = new HeadsView();
            }
            readParams(headView, params);
            return headView;
        }
    },
    BODIES_VIEW {
        private View view;

        public View getScreen(Object... params) {
            if (view == null) {
                view = new BodiesView();
            }
            readParams(view, params);
            return view;
        }
    },
    SHIELDS_VIEW {
        private View view;

        public View getScreen(Object... params) {
            if (view == null) {
                view = new ShieldsView();
            }
            readParams(view, params);
            return view;
        }
    }, WEAPONS_VIEW {
        private View view;

        public View getScreen(Object... params) {
            if (view == null) {
                view = new WeaponsView();
            }
            readParams(view, params);
            return view;
        }
    }, FXS_VIEW {
        private View view;

        public View getScreen(Object... params) {
            if (view == null) {
                view = new FXsView();
            }
            readParams(view, params);
            return view;
        }
    }, HELMETS_VIEW {
        private View view;

        public View getScreen(Object... params) {
            if (view == null) {
                view = new HelmetsView();
            }
            readParams(view, params);
            return view;
        }
    };

    public abstract View getScreen(Object... params);

    void readParams(View view, Object... params) {
        if (params.length > 0) {
            if (params[0] instanceof Listener) {
                view.setListener((Listener) params[0]);
            }
        }
    }
}

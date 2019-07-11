package design.screens;

import com.badlogic.gdx.Screen;
import design.screens.views.GraphicView;
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
    GRAPHIC_VIEW {
        private GraphicView graphicView;

        public Screen getScreen(Object... params) {
            if (graphicView == null) {
                graphicView = new GraphicView();
            }
            return graphicView;
        }
    };

    public abstract Screen getScreen(Object... params);
}

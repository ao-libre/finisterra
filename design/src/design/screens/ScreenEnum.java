package design.screens;

import com.esotericsoftware.minlog.Log;
import design.editors.fields.Listener;
import design.screens.views.*;

import java.lang.reflect.InvocationTargetException;

public enum ScreenEnum {
    IMAGE_VIEW("Images", ImageView.class),
    ANIMATION_VIEW("Animations", AnimationView.class),
    NPC_VIEW("NPCs", NPCView.class),
    OBJ_VIEW("Objects", ObjectView.class),
    BODIES_VIEW("Bodies", BodiesView.class),
    HEADS_VIEW("Heads", HeadsView.class),
    SHIELDS_VIEW("Shields", ShieldsView.class),
    WEAPONS_VIEW("Weapons", WeaponsView.class),
    HELMETS_VIEW("Helmets", HelmetsView.class),
    FXS_VIEW("FXs", FXsView.class),
    SPELL_VIEW("Spells", SpellView.class),
    TILE_SET_VIEW("Tile Set", TileSetView.class);

    private final String title;
    private final Class<? extends View> type;
    private View view;

    ScreenEnum(String title, Class<? extends View> type) {
        this.title = title;
        this.type = type;
    }

    public View getScreen(Object... params) {
        if (view == null) {
            try {
                view = type.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                Log.error(this.toString(), "View not implemented", e);
            }
        }
        readParams(view, params);
        return view;
    }

    void readParams(View view, Object... params) {
        if (params.length > 0) {
            if (params[0] instanceof Listener) {
                view.setListener((Listener) params[0]);
            }
        }
    }

    public Class getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}

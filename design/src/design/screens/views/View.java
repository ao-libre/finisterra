package design.screens.views;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import design.designers.IDesigner;
import design.screens.DesignScreen;
import game.handlers.AnimationHandler;
import game.handlers.DescriptorHandler;
import game.handlers.ObjectHandler;
import game.systems.render.world.CharacterRenderingSystem;
import game.utils.Skins;

public abstract class View<T, P extends IDesigner<T, ? extends IDesigner.Parameters<T>>> extends DesignScreen {

    public final static Skin SKIN = new Skins.AOSkin(Gdx.files.internal("skin/skin-composer-ui.json"));

    private AnimationHandler animationHandler;
    private DescriptorHandler descriptorHandler;

    private P designer;
    private TextButton modify;
    private TextButton delete;
    private List<T> list;
    private Preview<T> preview;
    private Preview<T> itemView;

    public View(P designer) {
        this.designer = designer;
        Log.info("View Created: " + getClass().getName());
        animationHandler = new AnimationHandler();
        descriptorHandler = new DescriptorHandler();
        createUI();
        getMainTable().setBackground(SKIN.getDrawable("white"));
    }

    @Override
    protected Table createContent() {
        Table leftPane = new Table();
        leftPane.pad(10);
        Table left = new Table();
        Table buttons = createButtons();
        left.add(buttons).row();
        List<T> list = createList();
        left.add(new ScrollPane(list));
        leftPane.add(left).left().top().expandX();

        preview = createPreview();
        itemView = createItemView();
        ScrollPane topRight = new ScrollPane(preview);
        topRight.setFadeScrollBars(false);
        topRight.setForceScroll(true, true);
        topRight.setFlickScroll(false);
        ScrollPane bottomRight = new ScrollPane(itemView);
        bottomRight.setFadeScrollBars(false);
        bottomRight.setFlickScroll(false);
        SplitPane rightPane = new SplitPane(topRight, bottomRight, true, SKIN);
        SplitPane splitPane = new SplitPane(leftPane, rightPane, false, SKIN);
        Table content = new Table();
        content.add(splitPane).grow();
        return content;
    }

    public List<T> getList() {
        return list;
    }

    public Preview<T> getPreview() {
        return preview;
    }

    protected World createWorld() {
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        SpriteBatch batch = new SpriteBatch();
        builder
                .with(new SuperMapper())
                .with(new ObjectHandler())
                .with(animationHandler)
                .with(descriptorHandler);
        WorldConfiguration config = builder.build();
        return new World(config);
    }

    public AnimationHandler getAnimationHandler() {
        return animationHandler;
    }

    public DescriptorHandler getDescriptorHandler() {
        return descriptorHandler;
    }

    public P getDesigner() {
        return designer;
    }

    abstract Preview<T> createPreview();

    abstract Preview<T> createItemView();

    private Table createButtons() {
        Table buttons = new Table(SKIN);
        buttons.defaults().space(5);
        TextButton create = new TextButton("Create", SKIN);
        create.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                designer.create();
            }
        });
        modify = new TextButton("Modify", SKIN);
        modify.setDisabled(true);
        modify.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onModify(list.getSelected());
            }
        });
        delete = new TextButton("Delete", SKIN);
        delete.setDisabled(true);
        delete.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onDelete(list.getSelected());
            }
        });
        TextButton save = new TextButton("Save", SKIN);
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                designer.save();
            }
        });

        buttons.add(create);
        buttons.add(delete);
        buttons.add(save);
        return buttons;
    }

    private void onModify(T selected) {
        designer.modify(selected, getStage());
    }

    private void onDelete(T selected) {
        designer.delete(selected);
    }

    private List<T> createList() {
        list = new List<>(SKIN);
        Array<T> items = new Array<>();
        designer.get().forEach(items::add);

        sort(items);
        list.setItems(items);
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                modify.setDisabled(list.getSelected() == null);
                delete.setDisabled(list.getSelected() == null);
                preview.show(list.getSelected());
                itemView.show(list.getSelected());
            }
        });
        return list;
    }

    protected abstract void sort(Array<T> items);

    abstract class Preview<T> extends Table {

        public Preview(Skin skin) {
            super(skin);
        }

        abstract void show(T t);

        abstract T get();
    }
}

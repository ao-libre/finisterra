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
import design.screens.DesignScreen;
import design.designers.IDesigner;
import design.systems.PreviewRenderingSystem;
import game.handlers.AnimationHandler;
import game.handlers.DescriptorHandler;
import game.handlers.ObjectHandler;
import game.screens.WorldScreen;
import game.systems.camera.CameraFocusSystem;
import game.systems.camera.CameraSystem;
import game.systems.render.world.CharacterRenderingSystem;
import game.utils.Skins;

public abstract class View<T, P extends IDesigner<T, ? extends IDesigner.Parameters<T>>> extends DesignScreen implements WorldScreen {

    public final static Skin SKIN = new Skins.AOSkin(Gdx.files.internal("skin/uiskin.json"));

    private AnimationHandler animationHandler;
    private DescriptorHandler descriptorHandler;

    private P designer;
    private TextButton modify;
    private TextButton delete;
    private List<T> list;
    private Preview<T> preview;

    public View(P designer) {
        this.designer = designer;
        Log.info("View Created: " + getClass().getName());
    }

    @Override
    protected void createContent() {
        Table viewTable = new Table(SKIN);
        createList(viewTable);
        createButtons(viewTable);
        preview = createPreview(viewTable);
        getMainTable().add(viewTable);
    }

    public List<T> getList() {
        return list;
    }

    public Preview<T> getPreview() {
        return preview;
    }

    protected World createWorld() {
        animationHandler = new AnimationHandler();
        descriptorHandler = new DescriptorHandler();
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder();
        SpriteBatch batch = new SpriteBatch();
        builder
                .with(new SuperMapper())
                .with(new ObjectHandler())
                .with(new CameraFocusSystem())
                .with(new CameraSystem(2))
                .with(new CharacterRenderingSystem(batch))
                .with(new PreviewRenderingSystem(batch))
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

    abstract Preview<T> createPreview(Table viewTable);

    private void createButtons(Table viewTable) {
        Table buttons = new Table(SKIN);
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

        buttons.add(create).expandX().row();
        buttons.add(modify).expandX().row();
        buttons.add(delete).expandX().row();
        buttons.add(save).expandX().row();
        viewTable.add(buttons).right().row();
    }

    private void onModify(T selected) {
        designer.modify(selected);
    }

    private void onDelete(T selected) {
        designer.delete(selected);
    }

    private void createList(Table viewTable) {
        list = new List<>(SKIN);
        Array<T> items = new Array<>();
        designer.get().values().forEach(items::add);
        viewTable.add(new ScrollPane(list)).expandY().left();
        sort(items);
        list.setItems(items);
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                modify.setDisabled(list.getSelected() == null);
                delete.setDisabled(list.getSelected() == null);
                preview.show(list.getSelected());
            }
        });
    }

    protected abstract void sort(Array<T> items);

    interface Preview<T> {
        void show(T t);

        T get();
    }
}

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

        buttons.add(create).left();
        buttons.add(delete).left();
        buttons.add(save).expandX().right();
        return buttons;
    }

    private void onSelect(T select) {
        listenerList.forEach(listener -> {
            // ask
            Dialog dialog = new Dialog("Select", SKIN) {
                public void result(Object obj) {
                    if ((Boolean) obj && select instanceof ID) {
                        listener.select((ID) select);
                    }
                }
            };
            dialog.text("Are you sure you want to select " + select.toString() + "?");
            dialog.button("Yes", true); //sends "true" as the result
            dialog.button("No", false);  //sends "false" as the result
            dialog.key(Input.Keys.ENTER, true); //sends "true" when the ENTER key is pressed
            dialog.show(getStage());
        });
    }
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

    public void refreshSelection() {
        modify.setDisabled(list.getSelected() == null);
        delete.setDisabled(list.getSelected() == null);
        if (list.getSelected() != null) {
            preview.show(list.getSelected());
            itemView.show(list.getSelected());
        }
    }

    public void loadItems(Optional<T> selection) {
        Array<T> items = new Array<>();
        designer.get().forEach(items::add);
        sort(items);
        list.setItems(items);
        selection.ifPresent(sel -> {
            list.setSelected(sel);
            onSelect(sel);
            refreshPreview();
        });
    }

    protected abstract void sort(Array<T> items);

    public void update(int width, int height) {
        getStage().getViewport().update(width, height, true);
    }

    protected abstract void sort(Array<T> items);

    abstract class Preview<T> extends Table {

        public Preview(Skin skin) {
            super(skin);
        }

        abstract void show(T t);

        abstract T get();
    }

    public enum State {
        MODIFIED,
        SAVED
    }

    public abstract class Editor<T> extends Preview<T> {

        T original;
        T current;
        Actor view;
        private Button restore;
        private Button save;
        private State state;
        private FieldListener listener;

        public Editor(Skin skin) {
            super(skin);
            listener = () -> setState(MODIFIED);
            addButtons();
            setState(SAVED);
        }

        public void addButtons() {
            restore = new TextButton("Restore", SKIN, "file");
            restore.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    restore();
                }
            });
            add(restore).left().pad(4).growX();
            save = new TextButton("Save", SKIN, "file");
            save.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    save();
                }
            });
            add(save).left().pad(4).growX().row();
        }

        @Override
        void show(T to) {
            if (state == MODIFIED) {
                // ASK TO SAVE
                Dialog saveDialog = new Dialog("Save Changes", SKIN) {
                    @Override
                    protected void result(Object object) {
                        if ((Boolean) object) {
                            save();
                        }
                        set(to);
                    }
                };
                saveDialog.text("Do you want to save changes before?");
                saveDialog.button("NO", false);
                saveDialog.button("YES", true);
                Vector2 coord = localToScreenCoordinates(new Vector2(getX(), getY()));
                float x = coord.x + ((view.getWidth() - saveDialog.getWidth()) / 2) - 20;
                float y = (this.getY() + this.getHeight() - saveDialog.getHeight()) / 2;
                saveDialog.show(View.this.getStage(), sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));
                saveDialog.setPosition(x, y);
                return;
            }
            set(to);
        }

        private void set(T to) {
            this.original = to;
            this.current = getCopy(to);
            if (view != null) {
                clear();
                addButtons();
            }
            view = getTable(listener);
            add(view).pad(20).growX().colspan(2);
            setState(SAVED);
        }

        @NotNull
        protected abstract Table getTable(FieldListener listener);

        protected abstract T getCopy(T to);

        T getOriginal() {
            return original;
        }

        @Override
        T get() {
            return current;
        }

        void save() {
            setState(SAVED);
            saveEdition();
        }

        void restore() {
            setState(SAVED);
            set(getOriginal());
            refreshPreview();
        }

        public void setState(State state) {
            this.state = state;
            refreshButtons();
        }

        private void refreshButtons() {
            save.setDisabled(state.equals(SAVED));
            restore.setDisabled(state.equals(SAVED));
        }


    }
}

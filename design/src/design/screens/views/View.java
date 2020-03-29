package design.screens.views;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import design.designers.IDesigner;
import design.editors.fields.FieldEditor.FieldListener;
import design.editors.fields.Listener;
import design.screens.DesignScreen;
import design.screens.ScreenEnum;
import design.screens.ScreenManager;
import game.systems.resources.AnimationsSystem;
import game.systems.resources.DescriptorsSystem;
import game.screens.WorldScreen;
import launcher.DesignCenter;
import model.ID;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static design.screens.views.View.State.MODIFIED;
import static design.screens.views.View.State.SAVED;
import static launcher.DesignCenter.SKIN;

public abstract class View<T, P extends IDesigner<T, ? extends IDesigner.Parameters<T>>> extends DesignScreen implements WorldScreen, FieldListener {

    private P designer;
    private Button modify;
    private Button delete;
    private List<T> list;
    private Preview<T> preview;
    private Editor<T> itemView;
    private ArrayList<Listener> listenerList = new ArrayList<>();

    public View(P designer) {
        this.designer = designer;
        Log.info("View Created: " + getClass().getName());
        createUI();
        getMainTable().setBackground(SKIN.getDrawable("white"));
    }

    public void setListener(Listener listener) {
        listenerList.clear();
        listenerList.add(listener);
    }

    public void clearListener() {
        listenerList.clear();
    }

    public ArrayList<Listener> getListenerList() {
        return listenerList;
    }

    @Override
    protected Table createContent() {
        Table leftPane = new Table();
        leftPane.pad(10);
        Table left = new Table();
        left.add(createButtons()).right().growX().row();
        List<T> list = createList();
        list.setTouchable(Touchable.enabled);
        ScrollPane listScroll = new ScrollPane(list, SKIN);
        listScroll.setFlickScroll(false);
        listScroll.setFadeScrollBars(false);
        listScroll.setScrollbarsVisible(true);
        left.add(listScroll).left().grow();
        leftPane.add(left).left().grow();

        preview = createPreview();
        itemView = createItemView();
        ScrollPane topRight = new ScrollPane(preview, SKIN);
        ScrollPane bottomRight = new ScrollPane(itemView, SKIN);
        bottomRight.setFadeScrollBars(false);
        bottomRight.setForceScroll(false, true);
        bottomRight.setFlickScroll(false);
        SplitPane rightPane = new SplitPane(topRight, bottomRight, true, SKIN);
        SplitPane splitPane = new SplitPane(leftPane, rightPane, false, SKIN);
        splitPane.setSplitAmount(0.3f);
        Table content = new Table();
        content.add(splitPane).grow();
        if (list.getItems().size > 0) {
            T item = list.getItems().get(0);
            list.setSelected(item);
            refreshSelection();
        }
        return content;
    }

    @Override
    protected Table createMenuButtons() {
        Table table = new Table();
        table.setBackground(SKIN.getDrawable("white"));
        table.setColor(SKIN.getColor("button"));
        table.pad(3);
        table.defaults().space(5);
        for (ScreenEnum screen : ScreenEnum.values()) {
            boolean mainButton = screen.getType().equals(getClass());
            TextButton button = new TextButton(screen.getTitle(), SKIN, mainButton ? "menu-button-selected" : "menu-button");
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.postRunnable(() -> ScreenManager.getInstance().showScreen(screen));
                }
            });
            table.add(button);
        }
        Button mapButton = new TextButton("Map Editor", SKIN, "menu-button");
        mapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.postRunnable(() -> ScreenManager.getInstance().toMapEditorScreen());
            }
        });
        table.add(mapButton);
        return table;
    }

    public void refreshPreview() {
        if (getItemView() == null) return;
        T t = getItemView().get();
        getPreview().show(t);
        clearListener();
    }

    public void saveEdition() {
        T t = getItemView().get();
        getDesigner().add(t);
        int selectedIndex = list.getSelectedIndex();
        loadItems(Optional.empty());
        list.setSelectedIndex(selectedIndex);
    }

    public List<T> getList() {
        return list;
    }

    public Preview<T> getPreview() {
        return preview;
    }

    public Editor<T> getItemView() {
        return itemView;
    }

    public AnimationsSystem getAnimationHandler() {
        return ((DesignCenter) Gdx.app.getApplicationListener()).getAnimationsSystem();
    }

    public DescriptorsSystem getDescriptorHandler() {
        return ((DesignCenter) Gdx.app.getApplicationListener()).getDescriptorsSystem();
    }

    @Override
    public World getWorld() {
        return ((DesignCenter) Gdx.app.getApplicationListener()).getWorld();
    }

    public P getDesigner() {
        return designer;
    }

    public void filesDropped(java.util.List<FileHandle> files) {
    }

    abstract Preview<T> createPreview();

    abstract Editor<T> createItemView();

    private Table createButtons() {
        Table buttons = new Table(SKIN);
        buttons.defaults().space(5);
        addButtons(buttons);
        return buttons;
    }

    protected void addButtons(Table buttons) {
        Button create = new Button(SKIN, "new");
        create.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Optional<T> newItem = designer.create();
                newItem.ifPresent(item -> getItemView().set(item));
                loadItems(newItem);
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
        delete = new Button(SKIN, "delete");
        delete.setDisabled(true);
        delete.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onDelete(list.getSelected());
            }
        });
        TextButton save = new TextButton("Save", SKIN, "file");
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                designer.save();
            }
        });

        buttons.add(create).left();
        buttons.add(delete).left();
        buttons.add(save).expandX().right();
    }

    private void onSelect(T select) {
        listenerList.forEach(listener -> {
            // ask
            Dialog dialog = new Dialog("Select", SKIN) {
                public void result(Object obj) {
                    if ((Boolean) obj && select instanceof ID) {
                        listener.select((ID) select);
                        clearListener();
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

    @Override
    public void onModify() {
        refreshPreview();
    }

    private void onModify(T selected) {
        designer.modify(selected, getStage());
    }

    private void onDelete(T selected) {
        designer.delete(selected);
        int selectedIndex = list.getSelectedIndex();
        loadItems(Optional.empty());
        if (list.getItems().size > 0) {
            list.setSelectedIndex(Math.min(selectedIndex, list.getItems().size - 1));
        }
    }

    private List<T> createList() {
        list = new List<>(SKIN);
        loadItems(Optional.empty());
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshSelection();
            }
        });
        list.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() >= 2) {
                    onSelect(list.getSelected());
                }
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
        designer.get().values().forEach(items::add);
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

    public enum State {
        MODIFIED,
        SAVED
    }

    abstract class Preview<T> extends Table {

        public Preview(Skin skin) {
            super(skin);
        }

        abstract void show(T t);

        abstract T get();
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

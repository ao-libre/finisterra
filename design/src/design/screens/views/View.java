package design.screens.views;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import design.designers.IDesigner;
import design.editors.Listener;
import design.screens.DesignScreen;
import design.screens.ScreenEnum;
import design.screens.ScreenManager;
import game.handlers.AnimationHandler;
import game.handlers.DescriptorHandler;
import game.screens.WorldScreen;
import launcher.DesignCenter;
import model.ID;

import java.util.ArrayList;

import static launcher.DesignCenter.SKIN;

public abstract class View<T, P extends IDesigner<T, ? extends IDesigner.Parameters<T>>> extends DesignScreen implements WorldScreen {

    private P designer;
    private TextButton modify;
    private TextButton delete;
    private List<T> list;
    private Preview<T> preview;
    private Preview<T> itemView;
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

    @Override
    protected Table createContent() {
        Table leftPane = new Table();
        leftPane.pad(10);
        Table left = new Table();
        left.add(createButtons()).growX().row();
        List<T> list = createList();
        list.setTouchable(Touchable.enabled);
        ScrollPane listScroll = new ScrollPane(list);
        listScroll.setFlickScroll(false);
        listScroll.setFadeScrollBars(false);
        listScroll.setScrollbarsVisible(true);
        left.add(listScroll).left().growX();
        leftPane.add(left).left().growX();

        preview = createPreview();
        itemView = createItemView();
        ScrollPane topRight = new ScrollPane(preview, SKIN);
        ScrollPane bottomRight = new ScrollPane(itemView, SKIN);
        bottomRight.setFadeScrollBars(false);
        bottomRight.setForceScroll(false, true);
        bottomRight.setFlickScroll(false);
        SplitPane rightPane = new SplitPane(topRight, bottomRight, true, SKIN);
        SplitPane splitPane = new SplitPane(leftPane, rightPane, false, SKIN);
        Table content = new Table();
        content.add(splitPane).grow();
        if (list.getItems().size > 0) {
            list.setSelected(list.getItems().get(0));
        }
        return content;
    }

    @Override
    protected Table createMenuButtons() {
        Table table = new Table();
        table.defaults().space(5);
        for (ScreenEnum screen : ScreenEnum.values()) {
            Button button = new TextButton(screen.name(), SKIN);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.postRunnable(() -> ScreenManager.getInstance().showScreen(screen));
                }
            });
            table.add(button);
        }

        return table;
    }

    public void refresh() {
        getPreview().show(getPreview().get());
        getItemView().show(getItemView().get());
        clearListener();
    }

    public List<T> getList() {
        return list;
    }

    public Preview<T> getPreview() {
        return preview;
    }

    public Preview<T> getItemView() {
        return itemView;
    }

    public AnimationHandler getAnimationHandler() {
        return ((DesignCenter) Gdx.app.getApplicationListener()).getAnimationHandler();
    }

    public DescriptorHandler getDescriptorHandler() {
        return ((DesignCenter) Gdx.app.getApplicationListener()).getDescriptorHandler();
    }

    @Override
    public World getWorld() {
        return ((DesignCenter) Gdx.app.getApplicationListener()).getWorld();
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
                onSelect(list.getSelected());
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

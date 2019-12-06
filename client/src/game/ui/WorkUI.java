package game.ui;


import com.artemis.E;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import entity.character.info.Inventory;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.handlers.ObjectHandler;
import game.screens.GameScreen;
import game.utils.Skins;
import shared.network.interaction.AddItem;
import shared.objects.types.Obj;
import shared.objects.types.WorkKind;
import shared.util.Messages;


public class WorkUI extends Table {

    private final ClickListener mouseListener;
    private WorkKind workKind;
    private AOAssetManager assetManager;
    private int needObjID = 0, needCount = 0, resultObjID = 0 , resultCount = 0;
    private String recipeTitle;
    private SelectBox<SawRecipes> sawRecipesSelect;
    //private SelectBox<ForgeRecipes> forgeRecipesSelect;

    public WorkUI() {
        super( Skins.COMODORE_SKIN );
        setBackground( "simple-window" );
        this.mouseListener = new ClickListener();
        this.assetManager = AOGame.getGlobalAssetManager();
        add( sawWork() ).prefSize( 400, 400 ).row();
    }

    public void notify(WorkKind workKind){
        switch(workKind){
            case SAW:
                clear();
                setSkin( Skins.COMODORE_SKIN );
                setBackground( "simple-window");
                add(sawWork()).prefSize( 400,400 ).row();
                break;
            case FORGE:
                clear();
                setSkin( Skins.COMODORE_SKIN );
                setBackground( "simple-window" );
                add(forgeWork()).prefSize( 400,400 ).row();
                break;
        }
    }
    //crea la UI de herreria
    private Table forgeWork() {
        Table forgeTable = new Table(  );
        //forgeTable.add( forgeRecipesSelect );
        //todo crear recetas y ver como obtener los campos need y result
        needCount = 0;
        needObjID = 0;
        resultCount = 0;
        resultObjID = 0;
        forgeTable.add( createContent( needCount, needObjID, resultObjID, resultCount ) );
        return forgeTable;
    }
    //crea la UI de carpinteria
    private Table sawWork() {
        Table sawTable = new Table( Skins.COMODORE_SKIN );

        sawRecipesSelect = new SelectBox<>(getSkin());
        final Array<SawRecipes> recipes = new Array<>();
        SawRecipes.getSawRecipes().forEach(recipes::add);
        sawRecipesSelect.setItems(recipes);
        sawRecipesSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sawTable.clearChildren();
                sawTable.add( sawRecipesSelect ).top().row();
                SawRecipes sawRecipes = sawRecipesSelect.getSelected();

                needCount = sawRecipes.getNeedCount();
                needObjID = sawRecipes.getNeedObjID();
                resultCount = sawRecipes.getResultCount();
                resultObjID = sawRecipes.getResultObjID();

                sawTable.add(createContent( needCount, needObjID, resultObjID, resultCount ) )
                        .prefSize( 400,400 ).top().row();
                sawTable.add( createButton() ).bottom();
            }
        });
        sawTable.add( sawRecipesSelect ).row();
        sawTable.add(createContent( needCount, needObjID, resultObjID, resultCount ) )
                .prefSize( 400,400 ).top().row();
        sawTable.add( createButton() ).bottom();
        return sawTable;
    }

    private Table createContent(int needCount, int needObjID, int resultObjID, int resultCount){

        Table content = new Table( Skins.COMODORE_SKIN );
        Label labelNeed = new Label("Recursos requeridos: ", Skins.COMODORE_SKIN  );
        Label labelNeedObj = new Label( " ", Skins.COMODORE_SKIN );
        if (needObjID > 0) {
            Obj needObj = GameScreen.world.getSystem( ObjectHandler.class ).getObject( needObjID ).get();
            labelNeedObj = new Label( needCount + " " + needObj.getName(), Skins.COMODORE_SKIN );
        }
        Label labelResult = new Label( "Resultado: ", Skins.COMODORE_SKIN );
        Label labelResultObj = new Label( "", Skins.COMODORE_SKIN );
        if (resultObjID > 0) {
            Obj resultObj = GameScreen.world.getSystem( ObjectHandler.class ).getObject( resultObjID ).get();
            labelResultObj = new Label( resultCount + " " + resultObj.getName(), Skins.COMODORE_SKIN );
        }

        content.add(" ").top().row();
        content.add( labelNeed ).center().row();
        content.add( labelNeedObj ).center().row();
        content.add(" ").row();
        content.add( labelResult ).center().row();
        content.add( labelResultObj ).center().row();
        return content;
    }
    private Button createButton(){
        TextButton create = new TextButton( "Craft", getSkin() );
        create.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(hasRequestItems(needCount, needObjID)) {
                    //remueve los items necesarios
                    GameScreen.getClient().sendToAll( new AddItem( E.E( GameScreen.getPlayer() )
                            .getNetwork().id, needObjID,  -needCount ) );
                    //agrega el resultado de la creacion
                    GameScreen.getClient().sendToAll( new AddItem( E.E( GameScreen.getPlayer() )
                            .getNetwork().id, resultObjID, resultCount ) );
                } else {
                    GameScreen.world.getSystem( GUI.class ).getConsole().addInfo(assetManager.getMessages(
                            Messages.ADD_OR_REMOVE_ITEMS, "No tienes los recursos necesarios" ));
                }
            }
        } );
        return create;
    }

    public WorkKind getWorkKind() {
        return workKind;
    }

    public void setWorkKind(WorkKind workKind){
        notify( workKind );
    }

    //chequea que poseas los items necesarios para la creacion
    private boolean hasRequestItems(int needCount, int needObjID){
        E player = E.E (GameScreen.getPlayer());
        Inventory.Item[] items = player.getInventory().items;
        for (int i = 0; i <20 ; i++) {
            if (items[i] != null){
                if (items[i].objId == needObjID){
                  if(items[i].count >= needCount){
                      return true;
                  }
                }
            }

        }
        return false;
    }
    public void isOver(){
        mouseListener.isOver();
    }
}

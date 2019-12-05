package game.ui;


import com.artemis.E;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import entity.character.info.Inventory;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.screens.GameScreen;
import game.utils.Skins;
import shared.network.interaction.AddItem;
import shared.objects.types.WorkKind;
import shared.util.Messages;


public class WorkUI extends Table {

    private final ClickListener mouseListener;
    private WorkKind workKind;
    private AOAssetManager assetManager;

    public WorkUI(WorkKind workKind){
        super( Skins.COMODORE_SKIN);
        this.mouseListener = new ClickListener();
        this.workKind = workKind;
        this.assetManager = AOGame.getGlobalAssetManager();


        Table mainTable = new Window("Craft",Skins.COMODORE_SKIN );
        switch(workKind){
            case SAW:
                mainTable.add(sawWork());
                break;
            case FORGE:
                mainTable.add(forgeWork());
                break;
        }
        add(mainTable);
    }

    private Table forgeWork() {
        Table forgeTable = new Table(  );

        return forgeTable;
    }
    //crea la UI de carpinteria
    private Table sawWork() {
        Table sawTable = new Table( Skins.COMODORE_SKIN );
        //todo crear recetas

        Label labelNeed = new Label("Recursos requeridos: " + "10 ramitas", Skins.COMODORE_SKIN  );
        Label labelResult = new Label("Resultado: " + " 10 flecha newbie", Skins.COMODORE_SKIN  );
        TextButton create = new TextButton( "Craft",Skins.COMODORE_SKIN );
        create.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(hasRequestItems()) {
                    //remueve los items necesarios
                    GameScreen.getClient().sendToAll( new AddItem( E.E( GameScreen.getPlayer() ).getNetwork().id, 136, -10 ) );
                    //agrega el resultado de la creacion
                    GameScreen.getClient().sendToAll( new AddItem( E.E( GameScreen.getPlayer() ).getNetwork().id, 860, 10 ) );
                } else {
                    GameScreen.world.getSystem( GUI.class ).getConsole().addInfo(assetManager.getMessages( Messages.ADD_OR_REMOVE_ITEMS, "No tienes los recursos necesarios" ));
                }
            }
        } );
        sawTable.add( labelNeed ).row();
        sawTable.add( labelResult ).row();
        sawTable.add().row();
        sawTable.add( create ).row();

        return sawTable;
    }

    public WorkKind getWorkKind() {
        return workKind;
    }
    //chequea que poseas los items necesarios para la creacion
    private boolean hasRequestItems(){
        E player = E.E (GameScreen.getPlayer());
        Inventory.Item[] items = player.getInventory().items;
        for (int i = 0; i <20 ; i++) {
            if (items[i] != null){
                if (items[i].objId == 136){
                  if(items[i].count > 10){
                      return true;
                  }
                }
            }

        }
        return false;
    }
}

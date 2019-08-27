package game.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import game.systems.network.ClientSystem;
import shared.interfaces.Hero;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.model.lobby.Team;
import shared.network.lobby.StartGameRequest;
import shared.network.lobby.player.ChangeHeroRequest;
import shared.network.lobby.player.ChangeReadyStateRequest;
import shared.network.lobby.player.ChangeTeamRequest;

public class RoomScreen extends AbstractScreen {
    private ClientSystem clientSystem;
    private Room room;
    private Player me;
    private List<Player> criminalList;
    private List<Player> armyList;
    private TextButton start;

    public RoomScreen(ClientSystem clientSystem, Room room, Player me) {
        super();
        this.clientSystem = clientSystem;
        this.room = room;
        this.me = me;
        updatePlayers();
        checkStart();
    }

    public Player getPlayer() {
        return me;
    }

    public void updatePlayers() {
        criminalList.setItems(room.getPlayers().stream().filter(player -> player.getTeam().equals(Team.CAOS_ARMY)).toArray(Player[]::new));
        armyList.setItems(room.getPlayers().stream().filter(player -> player.getTeam().equals(Team.REAL_ARMY)).toArray(Player[]::new));
    }

    public Room getRoom() {
        return room;
    }

    @Override
    protected void keyPressed(int keyCode) {

    }

    @Override
    void createContent() {
        Window table = new Window("", getSkin());
        table.setColor(1, 1, 1, 0.8f);
        Table teams = new Table(getSkin());
        teams.defaults().space(5);

        Table army = new Table(getSkin());
        Label armyLabel = new Label("REAL ARMY", getSkin());
        armyList = new List<>(getSkin());
        army.add(armyLabel).growX().row();
        army.add(armyList).minHeight(150).growX().row();
        teams.add(army).pad(20).grow().row();

        Table chaos = new Table(getSkin());
        Label chaosLabel = new Label("CHAOS ARMY", getSkin());
        criminalList = new List<>(getSkin());
        chaos.add(chaosLabel).growX().row();
        chaos.add(criminalList).minHeight(150).growX().row();
        teams.add(chaos).pad(20).grow();

        start = new TextButton("START", getSkin());
        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientSystem.getKryonetClient().sendToAll(new StartGameRequest(room.getId()));
            }
        });

        Button changeTeam = new TextButton("Change Team", getSkin());
        changeTeam.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientSystem.getKryonetClient().sendToAll(new ChangeTeamRequest());
            }
        });

        Button readyButton = new CheckBox("Ready", getSkin());
        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientSystem.getKryonetClient().sendToAll(new ChangeReadyStateRequest());
            }
        });


        SelectBox<Hero> heroSelect = new SelectBox<>(getSkin());
        final Array<Hero> heroes = new Array<>();
        Hero.getHeroes().forEach(heroes::add);
        heroSelect.setItems(heroes);
        heroSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Hero hero = heroSelect.getSelected();
                clientSystem.getKryonetClient().sendToAll(new ChangeHeroRequest(hero));
            }
        });

        Table topMenu = new Table(getSkin());
        topMenu.defaults().space(5);
        topMenu.add(changeTeam);
        topMenu.add(heroSelect);
        topMenu.add(readyButton);

        table.add(topMenu).center().expandX().row();
        table.add(teams).growX().row();
        table.add(start).expandX().right();
        getMainTable().add(table).pad(30).grow();
    }

    public void checkStart() {
        start.setDisabled(!room.getPlayers().stream().allMatch(Player::isReady));
    }

    @Override
    public void dispose() {
        clientSystem.stop();
        super.dispose();
    }
}

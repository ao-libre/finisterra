package game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import game.AOGame;
import game.handlers.AOAssetManager;
import game.handlers.MusicHandler;
import game.systems.network.ClientSystem;
import shared.interfaces.Hero;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.model.lobby.Team;
import shared.network.lobby.ExitRoomRequest;
import shared.network.lobby.JoinLobbyRequest;
import shared.network.lobby.StartGameRequest;
import shared.network.lobby.player.ChangeHeroRequest;
import shared.network.lobby.player.ChangeReadyStateRequest;
import shared.network.lobby.player.ChangeTeamRequest;
import shared.util.Messages;

public class RoomScreen extends AbstractScreen {
    private final ClientSystem clientSystem;
    private final Room room;
    private Player me;
    private List<Player> criminalList;
    private List<Player> armyList;
    private TextButton start;
    private SelectBox<Hero> heroSelect;

    public RoomScreen(ClientSystem clientSystem, Room room, Player me) {
        super();
        this.clientSystem = clientSystem;
        this.room = room;
        this.me = me;
        updateHero(me);
        updatePlayers();
        checkStart();
    }

    public Player getPlayer() {
        return me;
    }

    public void setPlayer(Player me) {
        this.me = me;
        updateHero(me);
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
        Table table = new Table(getSkin());

        Table teams = new Table(getSkin());
        teams.defaults().space(5);
        Button changeTeam = new TextButton("Change Team", getSkin(), "ui");
        changeTeam.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientSystem.getKryonetClient().sendToAll(new ChangeTeamRequest());
            }
        });

        Table army = new Window("", getSkin(), "ui");
        armyList = new List<>(getSkin(), "ui");
        army.add(new Label("Army", getSkin(), "ui")).left().row();
        army.add(new ScrollPane(armyList, getSkin(), "ui")).grow().row();

        Table chaos = new Window("", getSkin(), "ui");
        criminalList = new List<>(getSkin(), "ui");
        chaos.add(new Label("Chaos", getSkin(), "ui")).left().row();
        chaos.add(new ScrollPane(criminalList, getSkin(), "ui")).grow().row();

        teams.add(army).pad(20).minHeight(100).growX().row();
        teams.add(changeTeam).left().growX().row();
        teams.add(chaos).pad(20).minHeight(100).grow();

        start = new TextButton("Start", getSkin(), "ui");
        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientSystem.getKryonetClient().sendToAll(new StartGameRequest(room.getId()));
            }
        });

        Button exit = new TextButton("Back", getSkin(), "ui");
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientSystem.getKryonetClient().sendToAll(new ExitRoomRequest());
                clientSystem.getKryonetClient().sendToAll(new JoinLobbyRequest(me.getPlayerName()));
            }
        });

        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        AOAssetManager assetManager = game.getAssetManager();
        Button readyButton = new CheckBox(assetManager.getMessages(Messages.READY), getSkin());
        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientSystem.getKryonetClient().sendToAll(new ChangeReadyStateRequest());
            }
        });

        heroSelect = new SelectBox<>(getSkin(), "ui");
        final Array<Hero> heroes = new Array<>();
        Hero.getHeroes().forEach(heroes::add);
        heroSelect.setItems(heroes);

        heroSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Hero hero = heroSelect.getSelected();
                me.setHero(hero);
                clientSystem.getKryonetClient().sendToAll(new ChangeHeroRequest(hero));
            }
        });

        Table bottomMenu = new Table(getSkin());
        bottomMenu.pad(20).defaults().space(5);
        bottomMenu.add(exit).left();
        bottomMenu.add(heroSelect).expandX().center();
        bottomMenu.add(readyButton).expandX().center();
        bottomMenu.add(start).expandX().right();

        table.add(teams).growX().row();
        table.add(bottomMenu).growX();

        getMainTable().add(table).grow();
    }

    public void checkStart() {
        start.setDisabled(!room.getPlayers().stream().allMatch(Player::isReady));
    }

    private void updateHero(Player player) {
        heroSelect.setSelected(player.getHero());
    }
    @Override
    public void dispose() {
        clientSystem.stop();
        MusicHandler.FIRSTBGM.stop();
        super.dispose();
    }
}

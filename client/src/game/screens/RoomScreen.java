package game.screens;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import game.handlers.AOAssetManager;
import game.systems.lobby.LobbySystem;
import game.systems.resources.MusicSystem;
import game.systems.network.ClientSystem;
import shared.interfaces.Hero;
import shared.model.lobby.Player;
import shared.model.lobby.Team;
import shared.network.lobby.StartGameRequest;
import shared.network.lobby.player.ChangeHeroRequest;
import shared.network.lobby.player.ChangeReadyStateRequest;
import shared.network.lobby.player.ChangeTeamRequest;
import shared.util.Messages;

@Wire
public class RoomScreen extends AbstractScreen {

    private AOAssetManager assetManager;
    private ClientSystem clientSystem;
    private LobbySystem lobbySystem;

    private List<Player> criminalList;
    private List<Player> armyList;
    private TextButton start;
    private SelectBox<Hero> heroSelect;

    public RoomScreen() {
        selectRandomHero();
    }

    @Override
    protected void createUI() {
        Window table = new Window("", getSkin());
        table.setColor(1, 1, 1, 0.8f);
        Table teams = new Table(getSkin());
        teams.defaults().space(5);
        Table army = new Table(getSkin());
        Label armyLabel = new Label(assetManager.getMessages(Messages.LEGION_ARMY), getSkin());
        armyList = new List<>(getSkin());
        army.add(armyLabel).growX().row();
        army.add(armyList).minHeight(150).growX().row();
        teams.add(army).pad(20).grow().row();

        Table chaos = new Table(getSkin());
        Label chaosLabel = new Label(assetManager.getMessages(Messages.LEGION_CHAOS), getSkin());
        criminalList = new List<>(getSkin());
        chaos.add(chaosLabel).growX().row();
        chaos.add(criminalList).minHeight(150).growX().row();
        teams.add(chaos).pad(20).grow();

        start = new TextButton(assetManager.getMessages(Messages.START), getSkin());
        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientSystem.send(new StartGameRequest(lobbySystem.getCurrentRoom().getId()));
            }
        });

        Button changeTeam = new TextButton(assetManager.getMessages(Messages.CHANGE_TEAM), getSkin());
        changeTeam.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientSystem.send(new ChangeTeamRequest());
            }
        });

        Button readyButton = new CheckBox(assetManager.getMessages(Messages.READY), getSkin());
        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clientSystem.send(new ChangeReadyStateRequest());
            }
        });

        heroSelect = new SelectBox<>(getSkin());
        final Array<Hero> heroes = new Array<>();
        Hero.getHeroes().forEach(heroes::add);
        heroSelect.setItems(heroes);

        heroSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Hero hero = heroSelect.getSelected();
                lobbySystem.getPlayer().setHero(hero);
                clientSystem.send(new ChangeHeroRequest(hero));
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

    public void updatePlayers() {
        criminalList.setItems(lobbySystem.getCurrentRoom().getPlayers().stream().filter(player -> player.getTeam().equals(Team.CAOS_ARMY)).toArray(Player[]::new));
        armyList.setItems(lobbySystem.getCurrentRoom().getPlayers().stream().filter(player -> player.getTeam().equals(Team.REAL_ARMY)).toArray(Player[]::new));
    }

    public void checkStart() {
        start.setDisabled(!lobbySystem.getCurrentRoom().getPlayers().stream().allMatch(Player::isReady));
    }

    private void selectRandomHero() {
        Hero defaultHero = Hero.getRandom();
        heroSelect.setSelected(defaultHero);
        lobbySystem.getPlayer().setHero(defaultHero);
        clientSystem.send(new ChangeHeroRequest(defaultHero));
    }

    @Override
    public void render(float delta) {
        //@todo no hace falta actualizar en todos los frames
        updatePlayers();
        checkStart();

        super.render(delta);
    }

    @Override
    public void dispose() {
        MusicSystem.FIRSTBGM.stop(); //@todo mover esto a screen.hide() o screen.pause()
        super.dispose();
    }
}

package shared.network.interfaces;

import shared.network.account.AccountCreationRequest;
import shared.network.account.AccountLoginRequest;
import shared.network.combat.AttackRequest;
import shared.network.combat.SpellCastRequest;
import shared.network.interaction.DropItem;
import shared.network.interaction.MeditateRequest;
import shared.network.interaction.TakeItemRequest;
import shared.network.interaction.TalkRequest;
import shared.network.inventory.ItemActionRequest;
import shared.network.lobby.*;
import shared.network.lobby.player.ChangeHeroRequest;
import shared.network.lobby.player.ChangeReadyStateRequest;
import shared.network.lobby.player.ChangeTeamRequest;
import shared.network.lobby.player.PlayerLoginRequest;
import shared.network.movement.MovementRequest;
import shared.network.time.TimeSyncRequest;

public interface IRequestProcessor {

    void processRequest(MovementRequest request, int connectionId);

    void processRequest(AttackRequest attackRequest, int connectionId);

    void processRequest(ItemActionRequest itemAction, int connectionId);

    void processRequest(MeditateRequest meditateRequest, int connectionId);

    void processRequest(TalkRequest talkRequest, int connectionId);

    void processRequest(TakeItemRequest takeItemRequest, int connectionId);

    void processRequest(SpellCastRequest spellCastRequest, int connectionId);

    void processRequest(JoinRoomRequest joinRoomRequest, int connectionId);

    void processRequest(ExitRoomRequest exitRoomRequest, int connectionId);

    void processRequest(CreateRoomRequest createRoomRequest, int connectionId);

    void processRequest(JoinLobbyRequest joinLobbyRequest, int connectionId);

    void processRequest(StartGameRequest startGameRequest, int connectionId);

    void processRequest(PlayerLoginRequest playerLoginRequest, int connectionId);

    void processRequest(TimeSyncRequest timeSyncRequest, int connectionId);

    void processRequest(ChangeTeamRequest changeTeamRequest, int connectionId);

    void processRequest(ChangeReadyStateRequest changeReadyStateRequest, int connectionId);

    void processRequest(ChangeHeroRequest changeHeroRequest, int connectionId);

    void processRequest(AccountCreationRequest accountCreationRequest, int connectionId);

    void processRequest(AccountLoginRequest accountLoginRequest, int connectionId);

    void processRequest(DropItem dropItem, int connectionId);
}

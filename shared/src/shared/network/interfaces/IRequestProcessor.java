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
import shared.network.movement.MovementRequest;
import shared.network.time.TimeSyncRequest;
import shared.network.user.UserCreateRequest;
import shared.network.user.UserLoginRequest;

public interface IRequestProcessor {

    void processRequest(MovementRequest request, int connectionId);

    void processRequest(AttackRequest attackRequest, int connectionId);

    void processRequest(ItemActionRequest itemAction, int connectionId);

    void processRequest(MeditateRequest meditateRequest, int connectionId);

    void processRequest(TalkRequest talkRequest, int connectionId);

    void processRequest(TakeItemRequest takeItemRequest, int connectionId);

    void processRequest(SpellCastRequest spellCastRequest, int connectionId);

    void processRequest(TimeSyncRequest timeSyncRequest, int connectionId);

    void processRequest(AccountCreationRequest accountCreationRequest, int connectionId);

    void processRequest(AccountLoginRequest accountLoginRequest, int connectionId);

    void processRequest(DropItem dropItem, int connectionId);

    void processRequest(UserCreateRequest userCreateRequest, int connectionId);

    void processRequest(UserLoginRequest userLoginRequest, int connectionId);
}

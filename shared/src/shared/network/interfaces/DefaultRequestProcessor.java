package shared.network.interfaces;

import net.mostlyoriginal.api.system.core.PassiveSystem;
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

public class DefaultRequestProcessor extends PassiveSystem implements IRequestProcessor {

    @Override
    public void processRequest(MovementRequest request, int connectionId) {

    }

    @Override
    public void processRequest(AttackRequest attackRequest, int connectionId) {

    }

    @Override
    public void processRequest(ItemActionRequest itemAction, int connectionId) {

    }

    @Override
    public void processRequest(MeditateRequest meditateRequest, int connectionId) {

    }

    @Override
    public void processRequest(TalkRequest talkRequest, int connectionId) {

    }

    @Override
    public void processRequest(TakeItemRequest takeItemRequest, int connectionId) {

    }

    @Override
    public void processRequest(SpellCastRequest spellCastRequest, int connectionId) {

    }

    @Override
    public void processRequest(TimeSyncRequest timeSyncRequest, int connectionId) {

    }

    @Override
    public void processRequest(AccountCreationRequest accountCreationRequest, int connectionId) {

    }

    @Override
    public void processRequest(AccountLoginRequest accountLoginRequest, int connectionId) {

    }

    @Override
    public void processRequest(DropItem dropItem, int connectionId) {

    }

    @Override
    public void processRequest(UserCreateRequest userCreateRequest, int connectionId) {
        
    }

    @Override
    public void processRequest(UserLoginRequest userLoginRequest, int connectionId) {

    }

}

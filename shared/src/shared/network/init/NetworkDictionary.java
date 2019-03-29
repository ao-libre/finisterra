package shared.network.init;

import camera.Focused;
import com.artemis.Component;
import entity.*;
import entity.Object;
import entity.character.CanWrite;
import entity.character.info.*;
import entity.character.states.*;
import entity.character.status.*;
import graphics.FX;
import map.Map;
import movement.Destination;
import movement.Moving;
import movement.RandomMovement;
import net.mostlyoriginal.api.network.marshal.common.MarshalDictionary;
import network.Network;
import physics.AOPhysics;
import physics.Attack;
import physics.AttackAnimation;
import position.Pos2D;
import position.WorldPos;
import shared.interfaces.CharClass;
import shared.interfaces.Constants;
import shared.interfaces.Hero;
import shared.model.AttackType;
import shared.model.Spell;
import shared.network.combat.AttackRequest;
import shared.network.combat.AttackResponse;
import shared.network.combat.SpellCastRequest;
import shared.network.interaction.DropItem;
import shared.network.interaction.MeditateRequest;
import shared.network.interaction.TakeItemRequest;
import shared.network.interaction.TalkRequest;
import shared.network.interfaces.INotificationProcessor;
import shared.network.interfaces.IResponseProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.inventory.ItemActionRequest;
import shared.network.login.LoginFailed;
import shared.network.login.LoginOK;
import shared.network.login.LoginRequest;
import shared.network.movement.MovementNotification;
import shared.network.movement.MovementRequest;
import shared.network.movement.MovementResponse;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.FXNotification;
import shared.network.notifications.RemoveEntity;
import shared.objects.types.PotionKind;
import shared.util.MapUtils;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;


public class NetworkDictionary extends MarshalDictionary {

    public NetworkDictionary() {
        registerAll(
                // Requests
                LoginRequest.class,
                LoginFailed.class,
                MovementRequest.class,
                AttackRequest.class,
                MeditateRequest.class,
                TalkRequest.class,
                ItemActionRequest.class,
                TakeItemRequest.class,
                SpellCastRequest.class,

                // Responses
                LoginOK.class,
                MovementResponse.class,
                AttackResponse.class,

                // Notifications
                EntityUpdate.class,
                RemoveEntity.class,
                MovementNotification.class,
                InventoryUpdate.class,
                IResponseProcessor.class,
                INotificationProcessor.class,
                DropItem.class,
                FXNotification.class,

                // Other
                int[][].class,
                int[].class,
                HashMap.class,
                MapUtils.class,
                ConcurrentLinkedDeque.class,
                Component.class,
                Component[].class,
                Class.class,
                Class[].class,
                Spell.class,
                CharClass.class,
                CharHero.class,
                Hero.class,

                // Components
                Map.class,
                Inventory.class,
                Inventory.Item.class,
                Inventory.Item[].class,
                Optional.class,
                Pos2D.class,
                AOPhysics.class,
                AOPhysics.Movement.class,
                CombatMessage.class,
                CombatMessage.Kind.class,
                AttackType.class,
                Constants.Heading.class,
                Name.class,
                WorldPos.class,
                Focused.class,
                Clan.class,
                Description.class,
                Immobile.class,
                Meditating.class,
                Navigating.class,
                Resting.class,
                Writing.class,
                Object.class,
                Ground.class,
                PotionKind.class,
                Criminal.class,
                Elv.class,
                Exp.class,
                GM.class,
                Health.class,
                Hungry.class,
                Level.class,
                Mana.class,
                Stamina.class,
                Thirst.class,
                CanWrite.class,
                entity.character.Character.class,
                Body.class,
                Dialog.class,
                Dialog.Kind.class,
                Head.class,
                Heading.class,
                Helmet.class,
                Shield.class,
                Weapon.class,
                FX.class,
                Destination.class,
                Moving.class,
                RandomMovement.class,
                Network.class,
                Attack.class,
                AttackAnimation.class
        );
    }

    private void registerAll(Class... classes) {
        topId = 40;
        for (Class clazz : classes) {
            register(topId++, clazz);
        }
    }
}

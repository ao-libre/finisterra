package ar.com.tamborindeguy.network.init;

import ar.com.tamborindeguy.interfaces.Constants;
import ar.com.tamborindeguy.model.AttackType;
import ar.com.tamborindeguy.model.Spell;
import ar.com.tamborindeguy.network.combat.AttackRequest;
import ar.com.tamborindeguy.network.combat.AttackResponse;
import ar.com.tamborindeguy.network.combat.SpellCastRequest;
import ar.com.tamborindeguy.network.interaction.DropItem;
import ar.com.tamborindeguy.network.interaction.MeditateRequest;
import ar.com.tamborindeguy.network.interaction.TakeItemRequest;
import ar.com.tamborindeguy.network.interaction.TalkRequest;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;
import ar.com.tamborindeguy.network.interfaces.IResponseProcessor;
import ar.com.tamborindeguy.network.inventory.InventoryUpdate;
import ar.com.tamborindeguy.network.inventory.ItemActionRequest;
import ar.com.tamborindeguy.network.login.LoginFailed;
import ar.com.tamborindeguy.network.login.LoginOK;
import ar.com.tamborindeguy.network.login.LoginRequest;
import ar.com.tamborindeguy.network.movement.MovementNotification;
import ar.com.tamborindeguy.network.movement.MovementRequest;
import ar.com.tamborindeguy.network.movement.MovementResponse;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.network.notifications.RemoveEntity;
import ar.com.tamborindeguy.util.MapUtils;
import camera.Focused;
import com.artemis.Component;
import entity.*;
import entity.Object;
import entity.character.CanWrite;
import entity.character.info.Clan;
import entity.character.info.Description;
import entity.character.info.Inventory;
import entity.character.info.Name;
import entity.character.states.*;
import entity.character.status.*;
import graphics.FX;
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

                // Other
                HashMap.class,
                MapUtils.class,
                ConcurrentLinkedDeque.class,
                Component.class,
                Component[].class,
                Class.class,
                Class[].class,
                Spell.class,

                // Components
                Inventory.class,
                Inventory.Item.class,
                Inventory.Item[].class,
                Optional.class,
                Pos2D.class,
                AOPhysics.class,
                AOPhysics.Movement.class,
                CombatMessage.class,
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

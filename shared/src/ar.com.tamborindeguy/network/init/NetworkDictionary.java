package ar.com.tamborindeguy.network.init;

import ar.com.tamborindeguy.interfaces.Constants;
import ar.com.tamborindeguy.model.AttackType;
import ar.com.tamborindeguy.model.Player;
import ar.com.tamborindeguy.model.map.Map;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.model.map.WorldPosition;
import ar.com.tamborindeguy.network.combat.AttackRequest;
import ar.com.tamborindeguy.network.combat.AttackResponse;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;
import ar.com.tamborindeguy.network.interfaces.IResponseProcessor;
import ar.com.tamborindeguy.network.inventory.InventoryUpdate;
import ar.com.tamborindeguy.network.login.LoginFailed;
import ar.com.tamborindeguy.network.login.LoginOK;
import ar.com.tamborindeguy.network.login.LoginRequest;
import ar.com.tamborindeguy.network.movement.MovementRequest;
import ar.com.tamborindeguy.network.movement.MovementResponse;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.network.notifications.RemoveEntity;
import ar.com.tamborindeguy.util.MapUtils;
import camera.Focused;
import entity.*;
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
import position.Pos2D;
import position.WorldPos;

import java.util.ArrayList;
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

                // Responses
                LoginOK.class,
                MovementResponse.class,
                AttackResponse.class,

                // Notifications
                MapUtils.class, //why?
                INotificationProcessor.class, //why?
                IResponseProcessor.class, //why?
                EntityUpdate.class,
                RemoveEntity.class,
                Player.class,
                Map.class,
                Tile.class,
                int[].class,
                Tile[].class,
                Tile[][].class,
                WorldPosition.class,
                ArrayList.class,
                ConcurrentLinkedDeque.class,
                InventoryUpdate.class,
                Inventory.class,
                Inventory.Item.class,
                Inventory.Item[].class,
                HashMap.class,

                // Components
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
                Attack.class
        );
    }

    private void registerAll(Class... classes) {
        topId = 40;
        for (Class clazz : classes) {
            register(topId++, clazz);
        }
    }
}

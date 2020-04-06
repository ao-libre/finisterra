package shared.network.init;

import component.sound.AOSound;
import component.camera.Focused;
import com.artemis.Component;
import com.artemis.FluidIteratingSystem;
import component.console.ConsoleMessage;
import component.entity.Description;
import component.entity.Ref;
import component.entity.character.attributes.*;
import component.entity.character.equipment.Armor;
import component.entity.character.equipment.Helmet;
import component.entity.character.equipment.Shield;
import component.entity.character.equipment.Weapon;
import component.entity.character.info.*;
import component.entity.character.parts.Body;
import component.entity.character.parts.Head;
import component.entity.character.states.*;
import component.entity.character.status.*;
import component.entity.combat.AttackPower;
import component.entity.combat.EvasionPower;
import component.entity.npc.*;
import component.entity.world.CombatMessage;
import component.entity.world.Dialog;
import component.entity.world.Footprint;
import component.entity.world.Ground;
import component.graphic.Effect;
import component.graphic.EffectBuilder;
import component.graphic.FX;
import component.graphic.RenderBefore;
import component.movement.Destination;
import component.movement.Moving;
import component.movement.RandomMovement;
import net.mostlyoriginal.api.network.marshal.common.MarshalDictionary;
import component.network.Network;
import component.physics.AOPhysics;
import component.physics.AttackInterval;
import component.physics.AttackAnimation;
import component.position.WorldPos;
import component.position.WorldPosOffsets;
import shared.interfaces.CharClass;
import shared.interfaces.Constants;
import shared.interfaces.Hero;
import shared.model.AttackType;
import shared.model.Spell;
import shared.model.loaders.ObjectLoader;
import shared.model.loaders.SpellLoader;
import shared.model.lobby.Lobby;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.model.lobby.Team;
import shared.model.readers.DescriptorsReader;
import shared.model.readers.Loader;
import shared.model.readers.Reader;
import shared.network.account.AccountCreationRequest;
import shared.network.account.AccountCreationResponse;
import shared.network.account.AccountLoginRequest;
import shared.network.account.AccountLoginResponse;
import shared.network.combat.AttackRequest;
import shared.network.combat.SpellCastRequest;
import shared.network.interaction.DropItem;
import shared.network.interaction.MeditateRequest;
import shared.network.interaction.TakeItemRequest;
import shared.network.interaction.TalkRequest;
import shared.network.interfaces.DefaultNotificationProcessor;
import shared.network.interfaces.INotificationProcessor;
import shared.network.interfaces.IResponseProcessor;
import shared.network.inventory.InventoryUpdate;
import shared.network.inventory.ItemActionRequest;
import shared.network.lobby.*;
import shared.network.lobby.player.*;
import shared.network.movement.MovementNotification;
import shared.network.movement.MovementRequest;
import shared.network.movement.MovementResponse;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.RemoveEntity;
import shared.network.time.TimeSyncRequest;
import shared.network.time.TimeSyncResponse;
import shared.objects.factory.ObjectFactory;
import shared.objects.types.*;
import shared.objects.types.common.*;
import shared.util.EntityUpdateBuilder;
import shared.util.MapHelper;
import shared.util.Messages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


public class NetworkDictionary extends MarshalDictionary {

    public NetworkDictionary() {
        registerAll(
                // Game Requests
                MovementRequest.class,
                AttackRequest.class,
                MeditateRequest.class,
                TalkRequest.class,
                ItemActionRequest.class,
                TakeItemRequest.class,
                SpellCastRequest.class,
                TimeSyncRequest.class,

                // Game Responses
                MovementResponse.class,
                TimeSyncResponse.class,

                // Game Notifications
                EntityUpdate.class,
                EntityUpdate[].class,
                EntityUpdateBuilder.class,
                RemoveEntity.class,
                MovementNotification.class,
                InventoryUpdate.class,
                IResponseProcessor.class,
                INotificationProcessor.class,
                DefaultNotificationProcessor.class,
                DropItem.class,
                ConsoleMessage.class,
                ConsoleMessage.Kind.class,
                Messages.class,

                // Login
                AccountCreationRequest.class,
                AccountLoginRequest.class,
                AccountCreationResponse.class,
                AccountLoginResponse.class,

                // Lobby
                Lobby.class,
                Player.class,
                Room.class,
                Room[].class,
                Team.class,

                // Lobby Player
                ChangeHeroRequest.class,
                ChangeReadyStateRequest.class,
                ChangeTeamRequest.class,
                ChangePlayerNotification.class,

                // Lobby Requests
                CreateRoomRequest.class,
                ExitRoomRequest.class,
                JoinLobbyRequest.class,
                JoinRoomRequest.class,
                StartGameRequest.class,
                PlayerLoginRequest.class,

                // Lobby Responses
                CreateRoomResponse.class,
                CreateRoomResponse.Status.class,
                JoinLobbyResponse.class,
                JoinRoomResponse.class,
                StartGameResponse.class,

                // Lobby Notifications
                JoinRoomNotification.class,
                NewRoomNotification.class,

                // Other
                boolean[][].class,
                boolean[].class,
                int[][].class,
                int[].class,
                Integer[].class,
                java.lang.String[].class,
                ConcurrentHashMap.class,
                HashMap.class,
                HashSet.class,
                MapHelper.class,
                ConcurrentLinkedDeque.class,
                Component.class,
                Component[].class,
                Class.class,
                Class[].class,
                Spell.class,
                CharClass.class,
                CharHero.class,
                Hero.class,
                Loader.class,
                Reader.class,
                FluidIteratingSystem.class,

                ObjectLoader.class,
                SpellLoader.class,
                SpellLoader.SpellSetter.class,
                DescriptorsReader.class,
                Type.class,
                ObjectFactory.class,
                ArmorObj.class,
                ArrowObj.class,
                BoatObj.class,
                DepositObj.class,
                DoorObj.class,
                DrinkObj.class,
                Food.class,
                HelmetObj.class,
                IEquipable.class,
                IFillObject.class,
                KeyObj.class,
                MagicObj.class,
                MineralObj.class,
                MusicalObj.class,
                ObjWithClasses.class,
                PosterObj.class,
                PotionKind.class,
                PotionObj.class,
                ShieldObj.class,
                SpellObj.class,
                WeaponObj.class,
                AnvilObj.class,
                BonfireObj.class,
                BookObj.class,
                ContainerObj.class,
                FlowerObj.class,
                ForgeObj.class,
                ForumObj.class,
                FurnitureObj.class,
                GemObj.class,
                GoldObj.class,
                JewelObj.class,
                StainObj.class,
                TeleportObj.class,
                TreeObj.class,
                WoodObj.class,

                // Components
                AOSound.class,
                Ref.class,
                OriginPos.class,
                Effect.class,
                EffectBuilder.class,
                RenderBefore.class,
                Effect.Type.class,
                EvasionPower.class,
                AttackPower.class,
                NPC.class,
                SpellBook.class,
                Obj.class,
                Bag.class,
                Bag.Item.class,
                Bag.Item[].class,
                Optional.class,
                WorldPosOffsets.class,
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
                component.entity.world.Object.class,
                Ground.class,
                PotionKind.class,
                Criminal.class,
                Gold.class,
                GM.class,
                Health.class,
                Hungry.class,
                Level.class,
                Mana.class,
                Stamina.class,
                Thirst.class,
                CanWrite.class,
                component.entity.character.Character.class,
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
                AttackInterval.class,
                Agility.class,
                Strength.class,
                Intelligence.class,
                Charisma.class,
                Constitution.class,
                Hit.class,
                Armor.class,
                Footprint.class,
                AttackAnimation.class,
                Buff.class,
                AIMovement.class,
                Attackable.class,
                Commerce.class,
                Domable.class,
                Hostile.class,
                Respawn.class
        );
    }

    private void registerAll(Class... classes) {
        topId = 40;
        for (Class clazz : classes) {
            register(topId++, clazz);
        }
    }
}

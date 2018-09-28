/*******************************************************************************
 * Copyright (C) 2014  Rodrigo Troncoso
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ar.com.tamborindeguy.client.systems.physics;

import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.model.map.Tile;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import movement.Destination;
import physics.AOPhysics;
import position.Pos2D;
import position.WorldPos;

import static com.artemis.E.E;

@Wire
public class MovementSystem extends IteratingSystem {

    public MovementSystem() {
        super(Aspect.all(Destination.class,
                WorldPos.class, Pos2D.class));
    }

    @Override
    protected void process(int entity) {
        E player = E(entity);
        player.moving();
        WorldPos pos = player.getWorldPos();
        if (entity == GameScreen.getPlayer()) {
            pos = MovementProcessorSystem.getPosition(pos);
        }
        if (player.hasDestination()) {
            if (movePlayer(player)) {
                if (entity != GameScreen.getPlayer()) {
                    WorldPos dest = player.getDestination().worldPos;
                    player.getWorldPos().x = dest.x;
                    player.getWorldPos().y = dest.y;
                    player.getWorldPos().map = dest.map;
                }
                player.removeDestination();
                player.removeMoving();
            }
        }

    }

    private boolean movePlayer(E player) {
        Destination destination = player.getDestination();
        Pos2D pos2D = player.getPos2D();
        float delta = world.getDelta() * AOPhysics.WALKING_VELOCITY / Tile.TILE_PIXEL_HEIGHT;
        switch (destination.dir) {
            default:
            case DOWN:
                pos2D.y += delta;
                break;
            case LEFT:
                pos2D.x -= delta;
                break;
            case RIGHT:
                pos2D.x += delta;
                break;
            case UP:
                pos2D.y -= delta;
                break;
        }

        adjustPossiblePos(pos2D, destination.worldPos, destination.dir);
        return pos2D.x % 1 == 0 && pos2D.y % 1 == 0;
    }

    private void adjustPossiblePos(Pos2D possiblePos, WorldPos destination, AOPhysics.Movement dir) {
        int newY = (int) possiblePos.y;
        int newX = (int) possiblePos.x;
        switch (dir) {
            case LEFT:
                if (newX < destination.x) {
                    possiblePos.x = destination.x;
                }
                break;
            case RIGHT:
                if (newX == destination.x) {
                    possiblePos.x = destination.x;
                }
                break;
            case UP:
                if (newY < destination.y) {
                    possiblePos.y = destination.y;
                }
                break;
            case DOWN:
                if (newY == destination.y) {
                    possiblePos.y = destination.y;
                }
                break;
        }
    }

}

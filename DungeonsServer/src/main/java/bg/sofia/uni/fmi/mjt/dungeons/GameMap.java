package bg.sofia.uni.fmi.mjt.dungeons;

import bg.sofia.uni.fmi.mjt.dungeons.enums.Direction;
import bg.sofia.uni.fmi.mjt.dungeons.lib.Position2D;
import bg.sofia.uni.fmi.mjt.dungeons.lib.actors.Actor;
import bg.sofia.uni.fmi.mjt.dungeons.lib.actors.Minion;
import bg.sofia.uni.fmi.mjt.dungeons.lib.actors.Player;
import bg.sofia.uni.fmi.mjt.dungeons.lib.actors.Treasure;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static bg.sofia.uni.fmi.mjt.dungeons.lib.GameConfigurator.*;

public class GameMap {

    private static final Random generator = new Random();

    private Position2D[][] fields;

    public GameMap() {
        fields = new Position2D[MAP_DIMENSIONS][MAP_DIMENSIONS];
        constructGameMap();
    }

    public List<Position2D> getPositionsWithActors() {
        List<Position2D> positions = new LinkedList<>();
        for (Position2D[] row : fields) {
            for (Position2D position2D : row) {
                if (!position2D.actors().isEmpty()) {
                    positions.add(position2D);
                }
            }
        }
        return positions;
    }

    // Spawns the player at a random free position
    public void spawnPlayer(Player player) {
        Position2D randomPos = getRandomSpawnablePosition();
        player.setPosition(randomPos);
        randomPos.addActor(player);
    }

    // Spawns a minion with a random level at a random free position
    public void spawnMinion() {
        Position2D randomPos = getRandomSpawnablePosition();
        Minion minion = new Minion(generator.nextInt(Minion.MAX_MINION_LEVEL)+1);
        minion.setPosition(randomPos);
        randomPos.addActor(minion);
    }

    public void spawnTreasure() {
        Position2D randomPos = getRandomSpawnablePosition();
        Treasure treasure = new Treasure(randomPos);
        randomPos.addActor(treasure);
    }

    // Moves an already spawned player (if the direction leads to a free position)
    public void movePlayer(Player player, Direction direction) {
        Position2D previousPosition = player.position();
        int oldX = previousPosition.x();
        int oldY = previousPosition.y();

        Position2D newPosition = switch (direction) {
            case LEFT -> fields[oldX - 1][oldY];
            case RIGHT -> fields[oldX + 1][oldY];
            case UP -> fields[oldX][oldY - 1];
            case DOWN -> fields[oldX][oldY + 1];
        }; // The start of the coordinate system is the upper left corner of the window
        if (canMovePlayerTo(newPosition)) {
            player.setPosition(newPosition);
            previousPosition.removeActor(player);
            newPosition.addActor(player);
        }
    }

    public void despawnActor(Actor actor) {
        actor.position().removeActor(actor);
        switch (actor.type()) {
            case MINION -> spawnMinion();
            case TREASURE -> spawnTreasure();
        }
    }


    private void constructGameMap() {
        for (int i = 0; i < MAP_DIMENSIONS; i++) {
            for (int j = 0; j < MAP_DIMENSIONS; j++) {
                fields[i][j] = new Position2D(i, j);
            }
        }
        setObstacles();
        spawnInitialMinions();
        spawnInitialTreasures();
    }

    private void setObstacles() {
        for (int position : OBSTACLE_POSITIONS) {
            fields[position / MAP_DIMENSIONS][position % MAP_DIMENSIONS].markAsObstacle();
        }
    }

    private void spawnInitialMinions() {
        for (int i = 1; i <= MINIONS_COUNT; i++) {
            spawnMinion();
        }
    }

    private void spawnInitialTreasures() {
        for (int i = 1; i <= TREASURES_COUNT; i++) {
            spawnTreasure();
        }
    }

    // A spawnable position is one that has no actors
    private Position2D getRandomSpawnablePosition() {
        int randomInt = generator.nextInt(MAP_DIMENSIONS * MAP_DIMENSIONS);
        Position2D randomPos = fields[randomInt / MAP_DIMENSIONS][randomInt % MAP_DIMENSIONS];
        while (!randomPos.isSpawnable()) {
            randomInt = generator.nextInt(MAP_DIMENSIONS * MAP_DIMENSIONS);
            randomPos = fields[randomInt / MAP_DIMENSIONS][randomInt % MAP_DIMENSIONS];
        }
        return randomPos;
    }

    private boolean canMovePlayerTo(Position2D pos) {
        return pos.x() < MAP_DIMENSIONS && pos.y() < MAP_DIMENSIONS && pos.containsFreeSpace();
    }

}

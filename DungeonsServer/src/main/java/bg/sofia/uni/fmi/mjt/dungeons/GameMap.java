package bg.sofia.uni.fmi.mjt.dungeons;

import bg.sofia.uni.fmi.mjt.dungeons.lib.GameConfigurator;
import bg.sofia.uni.fmi.mjt.dungeons.lib.actors.Actor;
import bg.sofia.uni.fmi.mjt.dungeons.lib.actors.ActorRepository;
import bg.sofia.uni.fmi.mjt.dungeons.lib.actors.Minion;
import bg.sofia.uni.fmi.mjt.dungeons.lib.actors.Player;
import bg.sofia.uni.fmi.mjt.dungeons.enums.Direction;
import bg.sofia.uni.fmi.mjt.dungeons.lib.Position2D;

import java.util.Random;

import static bg.sofia.uni.fmi.mjt.dungeons.lib.GameConfigurator.*;

public class GameMap {

    private Random generator;

    private Position2D[][] fields;
    private int[] obstaclePositions;
    private ActorRepository actorRepository;

    public GameMap(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
        generator = new Random();
        fields = new Position2D[MAP_DIMENSIONS][MAP_DIMENSIONS];
        obstaclePositions = new int[OBSTACLES_COUNT];
        constructGameMap();
    }

    public int[] getObstaclePositions() {
        return obstaclePositions;
    }

    // Spawns the player at a random free position
    public Player spawnPlayer(Player player) {
        Position2D randomPos = getRandomSpawnablePosition();
        player.setPosition(randomPos);
        randomPos.addActor(player);
        actorRepository.addActor(player);
        return player;
    }

    // Spawns a minion with a random level at a random free position
    public void spawnMinion() {
        Position2D randomPos = getRandomSpawnablePosition();
        Minion minion = new Minion();
        minion.setPosition(randomPos);
        randomPos.addActor(minion);
        actorRepository.addActor(minion);
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
        Position2D actorPosition = actor.position();
        actorPosition.removeActor(actor);
        actorRepository.removeActor(actor);
    }


    private void constructGameMap() {
        buildBarrier();
        setObstacles();
        spawnInitialMinions();
    }

    private void buildBarrier() {
        for (int i = 0; i < MAP_DIMENSIONS; i++) {
            for (int j = 0; j < MAP_DIMENSIONS; j++) {
                fields[i][j] = new Position2D(i, j);
                if (i == 0 || j == 0 || i == MAP_DIMENSIONS - 1 || j == MAP_DIMENSIONS - 1) {
                    fields[i][j].markAsObstacle();
                }
            }
        }
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
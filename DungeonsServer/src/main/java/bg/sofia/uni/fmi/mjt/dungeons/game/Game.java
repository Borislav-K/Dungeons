package bg.sofia.uni.fmi.mjt.dungeons.game;

import bg.sofia.uni.fmi.mjt.dungeons.game.action.PlayerActionHandler;
import bg.sofia.uni.fmi.mjt.dungeons.game.map.GameMap;
import bg.sofia.uni.fmi.mjt.dungeons.network.GameServer;
import bg.sofia.uni.fmi.mjt.dungeons.network.MapDistributor;


public class Game {

    private static final double FRAME_NANOS = 17000000.0;

    private GameServer server;
    private GameMap gameMap;
    private PlayerManager playerManager;
    private MapDistributor mapDistributor;
    private PlayerActionHandler playerActionHandler;

    public Game() {
        this.playerManager = new PlayerManager();
        this.gameMap = new GameMap();
        this.playerActionHandler = new PlayerActionHandler(playerManager, gameMap);
        this.server = new GameServer(playerActionHandler);
        this.mapDistributor = new MapDistributor(playerManager, gameMap);
    }

    public void start() {
        server.start();
        startLoop();
    }

    private void startLoop() {
        double framesElapsed = 0;
        long lastMoment = System.nanoTime();
        while (true) {
            long now = System.nanoTime();
            framesElapsed += (now - lastMoment) / FRAME_NANOS;
            lastMoment = now;
            while (framesElapsed >= 1) { // Catch up with the frames if there was a delay
                tick();
                framesElapsed--;
            }

        }
    }

    private void tick() {
        server.fetchPlayerActions();
        playerActionHandler.handleAll();
        mapDistributor.distributeMap();
    }
}

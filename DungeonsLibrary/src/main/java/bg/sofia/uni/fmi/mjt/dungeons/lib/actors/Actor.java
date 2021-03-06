package bg.sofia.uni.fmi.mjt.dungeons.lib.actors;

import bg.sofia.uni.fmi.mjt.dungeons.lib.enums.ActorType;
import bg.sofia.uni.fmi.mjt.dungeons.lib.network.Transmissible;
import bg.sofia.uni.fmi.mjt.dungeons.lib.Position2D;

public interface Actor extends Transmissible {
    ActorType type();

    Position2D position();

    void setPosition(Position2D position);
}

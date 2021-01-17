package bg.sofia.uni.fmi.mjt.dungeons.lib.actors;

import bg.sofia.uni.fmi.mjt.dungeons.lib.enums.ActorType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ActorRepository {

    private Set<Actor> actors;

    public ActorRepository() {
        actors = new HashSet<>();
    }

    public void addActor(Actor actor) {
        actors.add(actor);
    }

    public void removeActor(Actor actor) {
        actors.remove(actor);
    }

    public Player getPlayerData(int playerId) {
        return (Player) actors.stream()
                .filter(actor -> actor.type().equals(ActorType.PLAYER))
                .filter(actor -> ((Player) actor).id() == playerId)
                .findFirst().orElseThrow(RuntimeException::new);

    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(actors.size());
        for (Actor actor : actors) {
            out.writeInt(actor.type().ordinal());
            actor.serialize(out);
        }
    }

    public void deserialize(DataInputStream in) throws IOException {
        int size = in.readInt();
        for (int i = 1; i <= size; i++) {
            ActorType actorType = ActorType.values()[in.readInt()];
            Actor actor;
            actor = switch (actorType) {
                case PLAYER -> new Player();
                case MINION -> new Minion();
                default -> throw new RuntimeException("Unknown actor type received from server");
            };
            actor.deserialize(in);
            actors.add(actor);
        }
    }
}
package shared.map.model;

import java.util.Set;
import java.util.TreeSet;

// Helper class for managing terrain transitions
public class TerrainType {
    private final byte id;
    private final Set<Byte> transitions;

    public TerrainType(byte id) {
        this.id = id;
        this.transitions = new TreeSet<>();
    }

    public byte getId() {
        return id;
    }

    public Set<Byte> getTransitions() {
        return transitions;
    }
}
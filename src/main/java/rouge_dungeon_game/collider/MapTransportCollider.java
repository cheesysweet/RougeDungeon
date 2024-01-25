package rouge_dungeon_game.collider;

import java.awt.Color;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;

public class MapTransportCollider extends Collider {

    static final long serialVersionUID = 20003;

    String loadMap;
    SpawnCollider spawnCollider;

    public MapTransportCollider(Point pos, Point size, String selected, SpawnCollider collider) {
        super(pos, size);
        this.loadMap = selected;
        this.spawnCollider = collider;
    }

    public MapTransportCollider(Rectangle size, String selected) {
        super(size);
        this.loadMap = selected;
    }

    public String getLoadMap() {
        return loadMap;
    }

    public SpawnCollider getSpawnCollider() {
        return spawnCollider;
    }

    @Override
    public MapTransportCollider copy() {
        return new MapTransportCollider(this.size, this.loadMap);
    }

    @Override
    public Color getColor() {
        return (Color.BLUE);
    }

    @Override
    public boolean collideable() {
        return false;
    }
}

package rouge_dungeon_game.collider;

import java.awt.Color;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;

public class TerrainCollider extends Collider {

    static final long serialVersionUID = 20005;

    public TerrainCollider(Point pos, Point size) {
        super(pos, size);
    }

    public TerrainCollider(Rectangle size) {
        super(size);
    }

    @Override
    public TerrainCollider copy() {
        return new TerrainCollider(this.size);
    }

    @Override
    public Color getColor() {
        return (Color.RED);
    }
}

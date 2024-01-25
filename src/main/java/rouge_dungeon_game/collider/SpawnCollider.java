package rouge_dungeon_game.collider;

import java.awt.Color;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;

public class SpawnCollider extends Collider {

    static final long serialVersionUID = 20004;

    public SpawnCollider(Point pos, Point size) {
        super(pos, size);
    }

    public SpawnCollider(Rectangle size) {
        super(size);
    }

    @Override
    public Color getColor() {
        return (Color.CYAN);
    }

    @Override
    public SpawnCollider copy() {
        return new SpawnCollider(this.size);
    }

    @Override
    public boolean collideable() {
        return false;
    }
}

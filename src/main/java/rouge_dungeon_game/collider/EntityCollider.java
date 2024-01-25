package rouge_dungeon_game.collider;

import java.awt.Color;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;

public class EntityCollider extends Collider {

    static final long serialVersionUID = 20006;

    public EntityCollider(Point start, Point size) {
        super(start, size);
    }

    public EntityCollider(Rectangle size) {
        super(size);
    }

    @Override
    public EntityCollider copy() {
        return new EntityCollider(this.size);
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }
}

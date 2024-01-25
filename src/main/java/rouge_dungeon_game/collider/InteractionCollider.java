package rouge_dungeon_game.collider;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.entity.Player;

import java.awt.*;

public class InteractionCollider extends Collider {

    static final long serialVersionUID = 20001;

    public InteractionCollider(Point pos, Point size) {
        super(pos, size);
    }

    public InteractionCollider(Rectangle size) {
        super(size);
    }

    @Override
    public InteractionCollider copy() {
        return new InteractionCollider(this.size);
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    /**
     * Do some interactions here?
     */
    public void interact(Player entity) {
    }
}


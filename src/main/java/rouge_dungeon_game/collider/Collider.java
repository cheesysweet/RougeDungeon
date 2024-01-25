package rouge_dungeon_game.collider;

import java.io.Serializable;
import java.awt.Color;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.Options.Directions;

/**
 * @author Sven Englsperger Raswill
 */
public abstract class Collider implements Serializable {
    public Rectangle size;

    private boolean remove = false;

    static final long serialVersionUID = 20000;

    public Collider(Point position, Point size) {
        this.size = new Rectangle(position.x(), position.y(), size.x(), size.y());
    }

    public Collider(Rectangle size) {
        this.size = size;
    }

    /**
     * Does a copy each override should use their class
     */
    public abstract Collider copy();

    /**
     * Can a char pass through
     */
    public boolean collideable() {
        return true;
    }

    /**
     * Used when printing the collider
     */
    public Color getColor() {
        return Color.BLUE;
    }

    public void remove() {
        this.remove = true;
    }

    public boolean shouldRemove() {
        return this.remove;
    }

    /**
     * Moves the internal collider
     */
    public void moveCollider(Point newPos) {
        this.size = new Rectangle(newPos.x(), newPos.y(), this.size.w(), this.size.h());
    }

    public Directions collide(Rectangle other) {
        if (other == null)
            return Directions.NONE;

        // [AABB](https://stackoverflow.com/questions/22512319/what-is-aabb-collision-detection)
        boolean right = this.size.x() + this.size.w() >= other.x();
        boolean left = other.x() + other.w() >= this.size.x();
        boolean up = this.size.y() + this.size.h() >= other.y();
        boolean down = other.y() + other.h() >= this.size.y();

        // if ALL of those are true, then the two are colliding
        if (right && left && up && down) {
            int ymid = (this.size.y() + (this.size.y() + this.size.h())) / 2;
            if (other.y() <= ymid) {
                if ((other.y() + other.h()) > ymid) {
                    return this.checkX(other);
                } else {
                    return Directions.UP;
                }
            } else {
                return Directions.DOWN;
            }
        }

        return Directions.NONE;
    }

    private Directions checkX(Rectangle other) {
        int xmid = (this.size.x() + (this.size.x() + this.size.w())) / 2;
        if (other.x() <= xmid) {
            if (other.x() + other.w() > xmid) {
                return Directions.RIGHT;
            }
        }
        return Directions.LEFT;
    }

    /**
     * Handles the colission, and has the possibility to return which direction
     * the collision came from
     */
    public Directions collide(Collider other) {
        return this.collide(other.size);
    }
}

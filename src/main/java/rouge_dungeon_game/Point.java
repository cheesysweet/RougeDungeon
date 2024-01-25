package rouge_dungeon_game;

import java.io.Serializable;

public record Point(int x, int y) implements Serializable {
    public Point add(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }

    public Point reverse() {
        return new Point(this.x * -1, this.y * -1);
    }

    public static Point zero() {
        return new Point(0, 0);
    }

    public int dot(Point other) {
        return this.x * other.x + this.y * other.y;
    } 

    public Point minus(Point other) {
        return new Point(this.x - other.x, this.y - other.y);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(!(o instanceof Point)) {
            return false;
        }
        var other = (Point) o;
        return other.x() == this.x() && other.y() == this.y();
    }
}

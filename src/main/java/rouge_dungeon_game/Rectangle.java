package rouge_dungeon_game;

import java.io.Serializable;

public record Rectangle(int x, int y, int w, int h) implements Serializable {
    static final long serialVersionUID = 10L;

    public Point getPos() {
        return new Point(this.x, this.y);
    }

    public Point getMaxPos() {
        return new Point(this.x + this.w, this.y + this.h);
    }
}

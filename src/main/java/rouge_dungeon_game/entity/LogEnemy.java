package rouge_dungeon_game.entity;

import java.util.List;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.animations.LogAnim;

public class LogEnemy extends Enemy {

    public LogEnemy(Rectangle initialNSize, int speed, String texture, List<Point> walkPath) {
        super(initialNSize, speed, texture, walkPath);
        this.animation = LogAnim.animations.get("Walk");
    }
}

package rouge_dungeon_game.entity;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.Options.Directions;
import rouge_dungeon_game.animations.Animations;
import rouge_dungeon_game.collider.DamageCollider;

import java.util.List;

public class Enemy extends Character {
    protected final String type = "Enemy";
    private final List<Point> walkPath;
    private boolean upwards = false;
    private boolean left = false;
    private int timer = 0;

    protected Animations animation;

    /**
     * Takes a rectangle instead of two points
     */
    public Enemy(Rectangle initialNSize, int speed, String texture, List<Point> walkPath) {
        super(initialNSize, speed, texture);
        this.walkPath = walkPath;
        this.damageCollider = new DamageCollider(
                new Rectangle(this.character.x() + 6, this.character.y() + 3, this.character.w() - 12,
                        this.character.h() - 6),
                10);
    }

    @Override
    public void render(long ticks) {
        this.render_handler(animation.getFrame(this.character, this.direction, ticks), false);
    }

    @Override
    public void handle() {
        timer++;
        int x1 = walkPath.get(0).x();
        int x2 = walkPath.get(1).x();
        int y1 = walkPath.get(0).y();
        int y2 = walkPath.get(1).y();

        if (timer >= 3) {
            if (x1 == x2) {
                if (upwards) {
                    this.velocity = new Point(0, -1);
                } else {
                    this.velocity = new Point(0, +1);
                }
                if (this.character.y() <= y1 - this.character.h() / 2) {
                    upwards = false;
                }
                if (this.character.y() >= y2 - this.character.h()) {
                    upwards = true;
                }
            }
            if (y1 == y2) {
                if (left) {
                    this.velocity = new Point(-1, 0);
                } else {
                    this.velocity = new Point(+1, 0);
                }
                if (this.character.x() <= x1 - this.character.w() / 2) {
                    left = false;
                }
                if (this.character.x() >= x2 - this.character.w() / 2) {
                    left = true;
                }
            }
            timer = 0;
        }
        super.handle();
    }

    @Override
    public void update() {
        super.update();
        this.damageCollider.moveCollider(new Point(this.character.x() + 6,
                this.character.y() + 6));
    }
}

package rouge_dungeon_game.animations;

import java.util.Map;

import rouge_dungeon_game.Options.Directions;

public class LogAnim extends Animations {

    /**
     * Contains the currently set up animations
     * Simply do animations.get(<animname>)
     */
    public static final Map<String, LogAnim> animations = Map.of(
            "Idle", new LogAnim(0, 0, 1, 4),
            "Walk", new LogAnim(0, 0, 4, 6));

    public LogAnim(int x, int y, int frames, int speed) {
        super(x, y, frames, speed);
    }

    public LogAnim(int x, int y, int frames, int speed, boolean isStatic, int animLength) {
        super(x, y, frames, speed, isStatic, animLength);
    }

    /**
     * Overriden here
     * {@inheritDoc}
     */
    @Override
    protected int getDirection(Directions direction) {
        switch (direction) {
            case DOWN:
                return 0;
            case RIGHT:
                return 2;
            case UP:
                return 1;
            case LEFT:
                return 3;
            case NONE:
            default:
                return -1;
        }
    }
}

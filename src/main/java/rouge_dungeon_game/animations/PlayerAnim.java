package rouge_dungeon_game.animations;

import java.util.Map;

import rouge_dungeon_game.Options.Directions;

public class PlayerAnim extends Animations {

    /**
     * Contains the currently set up animations
     * Simply do animations.get(<animname>)
     */
    public static final Map<String, PlayerAnim> animations = Map.of(
            "Idle", new PlayerAnim(0, 0, 1, 5),
            "Walk", new PlayerAnim(0, 0, 4, 7),
            "Pickup", new PlayerAnim(4, 0, 2, true, 800),
            "Attack", new PlayerAnim(1, 3, 1, true, 250)
            );

    public PlayerAnim(int x, int y, int frames, int speed) {
        super(x, y, frames, speed);
    }

    /**
     * manually set the time per frame
     * Aka speed = speed
     */
    public PlayerAnim(int x, int y, int frames, int speed, boolean isStatic, int animLength) {
        super(x, y, frames, speed, isStatic, animLength);
    }

    /**
     * Calculate the time per frame
     * Aka speed = animLength/frames
     */
    public PlayerAnim(int x, int y, int frames, boolean isStatic, int animLength) {
        super(x, y, frames, animLength/frames, isStatic, animLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getDirection(Directions direction) {
        switch (direction) {
            case DOWN:
                return 0;
            case RIGHT:
                return 1;
            case UP:
                return 2;
            case LEFT:
                return 1;
            case NONE:
            default:
                return -1;
        }
    }
}

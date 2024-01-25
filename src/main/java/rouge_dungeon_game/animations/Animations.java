package rouge_dungeon_game.animations;

import rouge_dungeon_game.App;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.Options.Directions;
import rouge_dungeon_game.Options.States;

/**
 * Contains information in regards to an animation
 * Does not handle the animation, just has information about it
 */
public abstract class Animations {

    // Start-x on the texture
    public final int x;
    // start-y on the texture
    public final int y;
    // How long per frame do you want to spend
    public final int speed;
    // How many frames are there
    public final int frames;
    // Should the character be static during the animation
    public final boolean isStatic;
    // How long is the animation overall
    public final int animLength;

    public Animations(int x, int y, int frames, int speed) {
        this.x = x;
        this.y = y;
        this.frames = frames;
        this.speed = speed;
        this.isStatic = false;
        this.animLength = 0;
    }

    public Animations(int x, int y, int frames, int speed, boolean isStatic, int animLength) {
        this.x = x;
        this.y = y;
        this.frames = frames;
        this.speed = speed;
        this.isStatic = isStatic;
        this.animLength = animLength;
    }

    // DOWN      X X X X * * * *
    // RIGHT     X X X X * * * *
    // UP        X X X X * * * *
    // y = 3 bla X X X X * * * *
    // UP        X X X X * * * *

    /**
     * Provides the next frame based on the current one.
     * @param rect - The character-rectangle (which has x, y, w and h of the character)
     * @param dirr - The current direction
     * @param ticks - The current time to use in milliseconds
     * @return Can then be fed to the drawing-function
     */
    public Rectangle getFrame(Rectangle rect, Directions dirr, long ticks) {
        if (App.currState == States.PLAY) {
            int frame = (int) ((ticks / this.speed) % this.frames);
            return new Rectangle(
                    frame + this.x,
                    this.y + getDirection(dirr),
                    rect.w(),
                    rect.h());
        }
        return new Rectangle(
                this.x,
                this.y + getDirection(dirr),
                rect.w(),
                rect.h());
    }

    /**
     * Decides what each {@link Directions} has as index-number into the 
     * texture
     */
    protected abstract int getDirection(Directions direction);
}

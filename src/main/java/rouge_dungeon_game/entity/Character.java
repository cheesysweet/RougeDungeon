package rouge_dungeon_game.entity;

import java.io.IOException;
import java.util.function.Consumer;
import java.awt.Color;
import java.awt.Graphics;

import rouge_dungeon_game.App;
import rouge_dungeon_game.Options;
import rouge_dungeon_game.Pair;
import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.Utils;
import rouge_dungeon_game.Options.Directions;
import rouge_dungeon_game.Texture.Texture;
import rouge_dungeon_game.Texture.TextureHandler;
import rouge_dungeon_game.collider.DamageCollider;
import rouge_dungeon_game.collider.EntityCollider;
import rouge_dungeon_game.items.Items;
import rouge_dungeon_game.items.Weapons;

/**
 * Represents a character in the game
 */
public abstract class Character extends Entity {

    protected Point velocity;
    protected Rectangle character;
    protected int speed;
    protected Texture texture;
    protected Directions direction;
    protected Rectangle imgPos;
    protected boolean isRunning = false;

    private int changeX = 0;
    private int changeY = 0;

    protected int hp = 5;
    protected int max_hp = 5;
    protected boolean dead = false;
    protected final int counter = 500;
    protected long damageTimer = -1;
    protected long damageIndicatorTimer = -1;

    protected final String type = "Character";

    protected Items activeItemOne = null;

    public final EntityCollider collider;
    public DamageCollider damageCollider;

    /**
     * @param startChar - Where the char starts, with w and h set
     * @param speed     - the speed of the character
     * @param texture   - which texture to use
     */
    public Character(Rectangle startChar, int speed, String texture) {
        this.character = startChar;
        this.velocity = new Point(0, 0);
        this.speed = speed;

        this.changeX = (this.character.w() - Options.CHAR_COLLIDER_SIZE) / 2;
        this.changeY = this.character.h() - Options.CHAR_COLLIDER_SIZE;

        this.collider = new EntityCollider(
                new Point(this.character.x() + this.changeX, this.character.y() + this.changeY),
                new Point(Options.CHAR_COLLIDER_SIZE, Options.CHAR_COLLIDER_SIZE));

        this.damageCollider = new DamageCollider(this.character, 10);

        this.imgPos = new Rectangle(0, 0, startChar.w(), startChar.h());
        try {
            this.texture = TextureHandler.INSTANCE.getTexture(texture);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                this.texture = TextureHandler.INSTANCE.getTexture("character");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        this.direction = Options.Directions.DOWN;
    }

    /**
     * @param startChar - Where the char starts, with w and h set
     * @param speed     - the speed of the character
     *                  Does not set the texture
     */
    public Character(Rectangle startChar, int speed) {
        this.character = startChar;
        this.velocity = new Point(0, 0);
        this.speed = speed;

        this.changeX = (this.character.w() - Options.CHAR_COLLIDER_SIZE) / 2;
        this.changeY = this.character.h() - Options.CHAR_COLLIDER_SIZE;

        this.collider = new EntityCollider(
                new Point(this.character.x() + this.changeX, this.character.y() + this.changeY),
                new Point(Options.CHAR_COLLIDER_SIZE, Options.CHAR_COLLIDER_SIZE));

        this.damageCollider = new DamageCollider(this.character, 10);

        this.imgPos = new Rectangle(0, 0, startChar.w(), startChar.h());
        this.direction = Options.Directions.DOWN;
    }

    /**
     * Changes the position of the char
     * Does new Rectangle(pos.x, pos.y, currChar.w, currChar.h)
     * 
     * @param position - the new position to move to
     */
    public void setPosition(Point position) {
        this.character = new Rectangle(position.x(), position.y(), this.character.w(), this.character.h());
    }

    public void damage(int damage) {
        if (this.hp > 0) {
            if (this.damageTimer == -1) {
                this.damageTimer = Utils.getTime() + this.counter;
                this.damageIndicatorTimer = Utils.getTime() + 10;
                var potential = this.hp - damage;
                if (potential >= 0) {
                    this.hp = potential;
                } else {
                    this.hp = 0;
                }
            }
        } else {
            this.dead = true;
        }
    }

    protected int damage = 1;

    public int getDamage() {
        if (this.activeItemOne != null) {
            if (this.activeItemOne instanceof Weapons) {
                return ((Weapons) this.activeItemOne).damage;
            }
        }
        return this.damage;
    }

    /**
     * Returns current_hp, max_hp, in that order
     */
    public Pair<Integer, Integer> getHP() {
        return new Pair<>(this.hp, this.max_hp);
    }

    public boolean isDead() {
        return this.dead;
    }

    /**
     * {@inheritDoc}
     */
    public abstract void render(long ticks);

    /**
     * Helper-method to use when rendering the character
     * 
     * @param imagePosition - the position in the texture
     * @param isFlipped     - if the image should be flipped
     */
    protected void render_handler(Rectangle imagePosition, boolean isFlipped) {
        if (this.damageIndicatorTimer != -1) {
            App.game.getGameWindow().addTexture(
                    this.texture,
                    imagePosition,
                    this.getRect(),
                    isFlipped,
                    Color.RED);
            // Reset the damageIndicatorTimer
            if (Utils.getTime() >= this.damageIndicatorTimer) {
                this.damageIndicatorTimer = -1;
            }
        } else {
            App.game.getGameWindow().addTexture(
                    this.texture,
                    imagePosition,
                    this.getRect(),
                    isFlipped);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle() {
        this.direction = Utils.set_direction(this.velocity, this.direction);

        if(this.damageTimer != -1) {
            if (Utils.getTime() >= this.damageTimer) {
                this.damageTimer = -1;
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        // If the velocity is non-zero
        if (this.velocity.x() != 0 || this.velocity.y() != 0) {
            // Move the char
            int x = this.character.x() + (this.velocity.x() * ((isRunning)? this.speed * 2: this.speed));
            int y = this.character.y() + (this.velocity.y() * ((isRunning)? this.speed * 2: this.speed));
            // Not outside the window
            {
                if (x <= 0) {
                    x = 0;
                }
                if (x > Options.WINDOW_WIDTH) {
                    x = Options.WINDOW_WIDTH - this.character.w();
                }
                if (y <= 0) {
                    y = 0;
                }
                if (y > Options.WINDOW_HEIGHT) {
                    y = Options.WINDOW_HEIGHT - this.character.h();
                }
            }
            this.character = new Rectangle(x, y, this.character.w(), this.character.h());
        }

        // move the colliders
        this.collider.moveCollider(new Point(this.character.x() + this.changeX,
                this.character.y() + this.changeY));
        this.damageCollider.moveCollider(new Point(this.character.x(),
                this.character.y()));
    }

    @Override
    public String toString() {
        StringBuilder bs = new StringBuilder();
        return bs.append(type + ": ")
                .append("Speed: ")
                .append(this.velocity)
                .append("Pos: ")
                .append(this.character)
                .append("HP: ")
                .append(this.hp)
                .toString();
    }

    public Point getVelocity() {
        return this.velocity;
    }

    public Rectangle getRect() {
        return this.character;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Directions getDirection() {
        return this.direction;
    }

    public EntityCollider getCollider() {
        return this.collider;
    }

    public DamageCollider getDmgCollider() {
        return this.damageCollider;
    }

    public Consumer<Graphics> renderColliders() {
        var outer = this;
        return new Consumer<Graphics>() {
            @Override
            public void accept(Graphics graphics) {
                Utils.PaintCollider(outer.collider, graphics);
                Utils.PaintCollider(outer.damageCollider, graphics);
            }
        };
    }

    public Items getActiveWeapon() {
        return activeItemOne;
    }
}

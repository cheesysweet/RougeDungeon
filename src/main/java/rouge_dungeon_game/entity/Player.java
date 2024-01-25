package rouge_dungeon_game.entity;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import rouge_dungeon_game.App;
import rouge_dungeon_game.Options;
import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.Utils;
import rouge_dungeon_game.Options.Directions;
import rouge_dungeon_game.Options.Actions;
import rouge_dungeon_game.collider.LootCollider;
import rouge_dungeon_game.items.CopperWeapon;
import rouge_dungeon_game.items.Weapons;
import rouge_dungeon_game.socket.ClientAction;
import rouge_dungeon_game.animations.PlayerAnim;

/**
 * Represents the player
 *
 * @author Sven Englsperger Raswill
 */
public class Player extends Character {

    protected final String type = "Player";

    // Current action that char is to do
    private Actions interact = Actions.NONE;

    private Subject<ClientAction> obv = BehaviorSubject.create();

    // Handles different parts of the animation
    private PlayerAnim playerAnim = PlayerAnim.animations.get("Idle");
    // Is one in one atm
    private boolean inAnim = false;
    // When does it end?
    private long animEnd = -1;
    // When did it start?
    private long animStart = -1;
    // Player coin amount
    private int coins = 0;

    /**
     * Takes a rectangle instead of two points
     */
    public Player(Rectangle initialNSize, int speed) {
        super(initialNSize, speed, Options.charImage);

        this.setup_keyboard();
        this.activeItemOne = new CopperWeapon();

        this.hp = Options.HEART_SIZE * 4;
        this.max_hp = this.hp;
    }

    @Override
    public void damage(int damage) {
        super.damage(damage);
    }

    /**
     * Does what it says on the tin
     */
    private void setup_keyboard() {
        var outer = this;
        App.getGameWindow().addKeyListener(new KeyAdapter() {
            int x = 0;
            int y = 0;

            @Override
            public void keyPressed(KeyEvent e) {
                if (App.currState != Options.States.PLAY)
                    return;
                if (outer.inAnim)
                    return;
                switch (e.getKeyCode()) {
                    case Options.Key_UP -> {
                        y = -1;
                    }
                    case Options.Key_LEFT -> {
                        x = -1;
                    }
                    case Options.Key_DOWN -> {
                        y = 1;
                    }
                    case Options.Key_RIGHT -> {
                        x = 1;
                    }
                    case Options.Key_INTER -> {
                        // If interacting with something, get that animation
                        outer.playerAnim = PlayerAnim.animations.get("Pickup");
                        outer.interact = Actions.INTERACTION;
                    }
                    case Options.Key_ATTACK -> {
                        // If interacting with something, get that animation
                        outer.playerAnim = PlayerAnim.animations.get("Attack");
                        outer.interact = Actions.ATTACK;
                    }
                }
                if (e.isShiftDown()) {
                    outer.isRunning = true;
                }
                // If moving and not doing anything else, change animation
                if (outer.walkingOrNone()) {
                    if (x != 0 || y != 0) {
                        outer.playerAnim = PlayerAnim.animations.get("Walk");
                        outer.interact = Actions.WALKING;
                    }
                } else {
                    x = 0;
                    y = 0;
                }
                outer.velocity = new Point(x, y);
                super.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (App.currState != Options.States.PLAY)
                    return;
                if (outer.inAnim)
                    return;
                switch (e.getKeyCode()) {
                    case Options.Key_UP:
                    case Options.Key_DOWN: {
                        y = 0;
                        break;
                    }
                    case Options.Key_LEFT:
                    case Options.Key_RIGHT: {
                        x = 0;
                        break;
                    }
                }
                if (!e.isShiftDown()) {
                    outer.isRunning = false;
                }
                // If not moving, change animation
                if (outer.walkingOrNone() && (x == 0 && y == 0)) {
                    outer.playerAnim = PlayerAnim.animations.get("Idle");
                    outer.interact = Actions.NONE;
                }
                outer.velocity = new Point(x, y);
                super.keyPressed(e);
            }
        });
    }

    private boolean walkingOrNone() {
        return this.interact == Actions.WALKING || this.interact == Actions.NONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle() {
        switch (this.interact) {
            case ATTACK -> {
                if (this.activeItemOne != null) {
                    if(this.activeItemOne instanceof Weapons) {
                        ((Weapons) this.activeItemOne).attack(this);
                    }
                }
            }
            case INTERACTION -> {
                var int_rect = Utils.getDirrs(this);
                App.game.e_handler.oColliders.stream()
                        .filter(coll -> coll.collide(int_rect) != Directions.NONE)
                        .forEach(coll -> {
                            if (coll instanceof LootCollider col) {
                                col.interact(this);
                            } else {
                                coll.interact(this);
                            }
                        });
            }
            default -> {
            }
        }
        // Handle the setting of animations and similar
        {
            // If the animation is static, and the player is not in an animation
            if (this.playerAnim.isStatic && !this.inAnim) {
                this.inAnim = true;
                this.animStart = Utils.getTime();
                this.animEnd = this.animStart + this.playerAnim.animLength;
            }
            // Check if the animation should end
            if (this.inAnim && this.animEnd <= Utils.getTime()) {
                this.inAnim = false;
                this.animEnd = -1;
                this.playerAnim = PlayerAnim.animations.get("Idle");
            }
        }
        super.handle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        if (this.interact == Actions.INTERACTION) {
            this.interact = Actions.NONE;
        }
        if (!this.inAnim && this.interact == Actions.ATTACK) {
            this.interact = Actions.NONE;
        }
        super.update();

        obv.onNext(new ClientAction(this.interact, this.direction, this.character, this.velocity));
    }

    /**
     * Emits the current position
     */
    public Observable<ClientAction> getObservable() {
        return obv;
    }

    @Override
    public String toString() {
        return this.type + ": " +
                this.velocity +
                this.character;
    }

    /**
     * Prints both the sword, but also the swing to the screen
     */
    private void handle_attacking_animation(long ticks) {
        if ((this.animStart + this.playerAnim.animLength - this.playerAnim.frames) >= Utils.getTime()) {
            activeItemOne.render(ticks, this.direction, this.collider.size);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(long ticks) {
        Rectangle currFrame;
        // If in an animation
        if (this.inAnim) {
            if (activeItemOne == null)
                return;
            // Send in the elapsed time for the animation
            currFrame = this.playerAnim.getFrame(this.character,
                    this.direction,
                    Utils.getTime() - this.animStart);
            if (this.interact == Actions.ATTACK) {
                handle_attacking_animation(ticks);
            }
        } else {
            // Just send in the current time
            currFrame = this.playerAnim.getFrame(this.character, this.direction, ticks);
        }

        this.render_handler(currFrame, this.direction == Directions.LEFT);
    }

    public void addCoins(int coins) {
        if (this.coins + coins >= Options.MAX_COINS) {
            this.coins = Options.MAX_COINS;
        } else {
            this.coins += coins;
        }
    }

    public int getCoins() {
        return coins;
    }

    public void addHp(int amount) {
        if (this.hp + amount >= this.max_hp) {
            this.hp = this.max_hp;
        } else {
            this.hp += amount;
        }
    }

    public void setActiveItem(Weapons weapon) {
        this.activeItemOne = weapon;
    }
}

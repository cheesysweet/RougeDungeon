package rouge_dungeon_game.entity;

import java.awt.Color;

import rouge_dungeon_game.App;
import rouge_dungeon_game.Options;
import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.Utils;
import rouge_dungeon_game.Options.Actions;
import rouge_dungeon_game.Options.Directions;
import rouge_dungeon_game.animations.PlayerAnim;
import rouge_dungeon_game.items.DiamondWeapon;
import rouge_dungeon_game.items.Weapons;
import rouge_dungeon_game.socket.ClientAction;

public class ClientEntity extends Character {

    private PlayerAnim playerAnim = PlayerAnim.animations.get("Idle");
    // Is one in one atm
    private boolean inAnim = false;
    // When does it end?
    private long animEnd = -1;
    // When did it start?
    private long animStart = -1;

    protected final String type = "ClientEntity";

    private Actions interact = Actions.NONE;

    public ClientEntity(Rectangle initialNSize, int speed, String texture) {
        super(initialNSize, speed, texture);

        this.activeItemOne = new DiamondWeapon();
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
            currFrame = this.playerAnim.getFrame(this.character, this.direction, Utils.getTime() - this.animStart);
            if (this.interact == Actions.ATTACK) {
                handle_attacking_animation(ticks);
            }
        } else {
            // Just send in the current time
            currFrame = this.playerAnim.getFrame(this.character, this.direction, ticks);
        }

        this.render_handler(currFrame, this.direction == Directions.LEFT);
    }

    @Override
    public void handle() {
        switch (this.interact) {
            case ATTACK -> {
                if (this.activeItemOne != null) {
                    Rectangle int_rect = Utils.getDirrs(this,
                            new Rectangle(Options.TILESIZE, Options.TILESIZE, Options.TILESIZE, Options.TILESIZE * 3));
                    App.game.e_handler.characterList.parallelStream()
                            .filter(character -> character.getDmgCollider().collide(int_rect) != Directions.NONE)
                            .forEach(character -> {
                                if (this.activeItemOne instanceof Weapons) {
                                    ((Weapons) this.activeItemOne).attack(character);
                                }
                            });
                }
            }
            default -> {
            }
        }
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

    private void handle_attacking_animation(long ticks) {
        if ((this.animStart + this.playerAnim.animLength - this.playerAnim.frames) >= Utils.getTime()) {
            activeItemOne.render(ticks, this.direction, this.collider.size);
        }
    }

    @Override
    public void update() {
        super.update();
    }

    public void do_action(ClientAction a) {
        switch (a.action()) {
            case ATTACK -> {
                this.playerAnim = PlayerAnim.animations.get("Attack");
                this.interact = a.action();
            }
            case WALKING -> {
                assert(a.dirr() != null);
                assert(a.position() != null);
                this.playerAnim = PlayerAnim.animations.get("Walk");
                this.character = a.position();
                this.direction = a.dirr();
            }
            default -> {
                this.playerAnim = PlayerAnim.animations.get("Idle");
            }
        }
    }
}

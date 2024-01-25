package rouge_dungeon_game.collider;

import java.awt.Color;

import rouge_dungeon_game.Rectangle;

public class DamageCollider extends Collider {

    static final long serialVersionUID = 20007;

    public final int damage;

    public DamageCollider(Rectangle size, int damage) {
        super(size);
        this.damage = damage;
    }

    @Override
    public DamageCollider copy() {
        return new DamageCollider(this.size, this.damage);
    }

    @Override
    public Color getColor() {
        return Color.PINK;
    }

    @Override
    public boolean collideable() {
        return false;
    }
}

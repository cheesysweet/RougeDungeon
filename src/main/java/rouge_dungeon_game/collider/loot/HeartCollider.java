package rouge_dungeon_game.collider.loot;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.collider.Collider;
import rouge_dungeon_game.collider.MobDroppable;
import rouge_dungeon_game.lootChest.MobDrop;

/**
 * Collider for heart drops
 */
public class HeartCollider extends Collider implements MobDroppable {
    static final long serialVersionUID = 20009;
    private final int amount;

    private MobDrop mobDrop;
    public HeartCollider(Point position, Point size, int amount) {
        super(position, size);
        this.amount = amount;
    }

    public HeartCollider(Rectangle size, int amount) {
        super(size);
        this.amount = amount;
    }

    @Override
    public HeartCollider copy() {return new HeartCollider(this.size, this.amount);}

    @Override
    public boolean collideable() {
        return false;
    }

    public int getAmount() {
        return this.amount;
    }

    /**
     * sets current mobDrop
     * @param mobDrop current
     */
    public void setMobDrop(MobDrop mobDrop) {
        this.mobDrop = mobDrop;
    }

    /**
     * returns mobDrop which mob drop it is
     * @return mobDrop
     */
    public MobDrop getMobDrop() {
        return mobDrop;
    }
}

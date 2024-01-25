package rouge_dungeon_game.collider;

import java.awt.Color;
import java.util.Random;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.entity.Player;
import rouge_dungeon_game.lootChest.Loot;

public class LootCollider extends InteractionCollider {

    static final long serialVersionUID = 20002;

    private final Loot loot;

    public LootCollider(Point pos, Point size, Loot loot) {
        super(pos, size);
        this.loot = loot;
    }

    public LootCollider(Rectangle size, Loot loot) {
        super(size);
        this.loot = loot;
    }

    public Loot getLoot() {
        return this.loot;
    }

    @Override
    public LootCollider copy() {
        return new LootCollider(this.size, this.loot);
    }

    @Override
    public Color getColor() {
        return this.loot.getColor();
    }

    /**
     * Change how the interactions are handled here?
     */
    @Override
    public void interact(Player entity) {
        // gives a coin 50% of the time

        if (new Random().nextInt(100) < 50) {
            entity.addCoins(getLoot().coin());
        } else {
            entity.setActiveItem(getLoot().weapon());
        }


        this.remove();
        super.interact(entity);
    }
}

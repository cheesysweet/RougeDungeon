package rouge_dungeon_game.lootChest;

import rouge_dungeon_game.items.DiamondWeapon;

import java.awt.*;
import java.util.Random;

public class LegendaryLoot extends Loot{
    public LegendaryLoot() {
        super(Color.decode("#ff8000"));
    }

    /**
     * gives random amount of coins
     * @return amount of coins
     */
    public int coin() {
        return new Random().nextInt(5,13);
    }

    /**
     * gives a diamond weapon
     * @return diamond weapon
     */
    public DiamondWeapon weapon() {
        return new DiamondWeapon();
    }
}

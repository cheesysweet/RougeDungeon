package rouge_dungeon_game.lootChest;

import rouge_dungeon_game.items.CopperWeapon;

import java.awt.*;
import java.util.Random;

public class CommonLoot extends Loot{
    public CommonLoot() {
        super(Color.decode("#0066ff"));
    }

    /**
     * gives random amount of coins
     * @return amount of coins
     */
    public int coin() {
        return new Random().nextInt(1,3);
    }

    /**
     * gives a copper weapon
     * @return copper weapon
     */
    public CopperWeapon weapon() {
        return new CopperWeapon();
    }

}

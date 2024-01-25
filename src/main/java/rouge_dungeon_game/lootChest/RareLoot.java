package rouge_dungeon_game.lootChest;

import rouge_dungeon_game.items.GoldWeapon;

import java.awt.*;
import java.util.Random;

public class RareLoot extends Loot{
    public RareLoot() {
        super(Color.decode("#cc3399"));
    }

    /**
     * gives random amount of coins
     * @return amount of coins
     */
    public int coin() {
        return new Random().nextInt(3,7);
    }

    /**
     * gives a gold weapon
     * @return gold weapon
     */
    public GoldWeapon weapon() {
        return new GoldWeapon();
    }
}

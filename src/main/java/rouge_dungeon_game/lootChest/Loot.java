package rouge_dungeon_game.lootChest;

import rouge_dungeon_game.items.Weapons;

import java.awt.*;
import java.io.Serializable;

public abstract class Loot implements Serializable {
    static final long serialVersionUID = 201000;
    private final Color color;
    public Loot(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    /**
     * gives coins
     * @return coin amount
     */
    public int coin() {
        return 1;
    }

    /**
     * gives a copper weapon
     * @return weapon
     */
    public Weapons weapon() {
        return null;
    }

}

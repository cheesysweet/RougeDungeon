package rouge_dungeon_game.lootChest;

import rouge_dungeon_game.App;
import rouge_dungeon_game.Options;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.Texture.Texture;
import rouge_dungeon_game.Texture.TextureHandler;
import rouge_dungeon_game.collider.Collider;
import rouge_dungeon_game.collider.loot.CoinCollider;
import rouge_dungeon_game.collider.loot.HeartCollider;

import java.io.IOException;
import java.util.Random;

/**
 * items that mobs can drop
 */
public class MobDrop {
    private final Rectangle position;
    private Texture texture;
    private Rectangle drop;
    private Collider collider;

    public MobDrop(Rectangle pos) {
        this.position = new Rectangle(pos.x() + (Options.TILESIZE/2), pos.y() + (Options.TILESIZE/2), Options.TILESIZE, Options.TILESIZE);
        try {
            this.texture = TextureHandler.INSTANCE.getTexture("Objects");
        } catch (IOException e) {

        }

        // drop a coin 90% of the time
        if (new Random().nextInt(100) < 80) {
            dropCoin();
        } else {
            dropHeart();
        }
    }

    /**
     * drops a coin
     */
    private void dropCoin() {
        drop = new Rectangle(0, 4, Options.TILESIZE, Options.TILESIZE);
        this.collider = new CoinCollider(position, 1);
        ((CoinCollider)this.collider).setMobDrop(this);
    }

    /**
     * drops a quarter of a heart
     */
    private void dropHeart() {
        drop = new Rectangle(7, 0, Options.TILESIZE, Options.TILESIZE);
        this.collider = new HeartCollider(position, 1);
        ((HeartCollider)this.collider).setMobDrop(this);
    }


    /**
     * renders texture
     * @param time tick time
     */
    public void render(long time) {
        App.getGameWindow().addTexture(
                texture,
                drop,
                position,
                false);
    }

    /**
     * gets collider class
     * @return collider class
     */
    public Collider getCollider() {
        return this.collider;
    }
}

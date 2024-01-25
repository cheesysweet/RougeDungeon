package rouge_dungeon_game;

import java.io.IOException;

import rouge_dungeon_game.Texture.Texture;
import rouge_dungeon_game.Texture.TextureHandler;
import rouge_dungeon_game.entity.Player;
import rouge_dungeon_game.items.Weapons;

public class Inventory {
    private Texture textures;
    private final Player player;

    public Inventory(Player player) {
        this.player = player;
        try {
            this.textures = TextureHandler.INSTANCE.getTexture("Objects");
        } catch (IOException e) {

        }
    }

    /**
     * renders the inventory
     * 
     * @param ticks game ticks
     */
    public void render(long ticks) {
        displayHP();
        displayWeapon();
        displayCoins();
    }

    /**
     * displays the border for the coins
     */
    private void displayCoins() {
        App.getGameWindow().addTexture(
                textures,
                new Rectangle(1, 9, 4 * Options.TILESIZE, 2 * Options.TILESIZE),
                new Rectangle(0, 13 * Options.TILESIZE, 4 * Options.TILESIZE, 2 * Options.TILESIZE),
                false);

        displayCoinAmount(player.getCoins());
    }

    /**
     * displays the amount of coins the player has
     * 
     * @param amount amount of coins
     */
    private void displayCoinAmount(Integer amount) {
        App.getGameWindow().addTexture(
                textures,
                getCoin((amount / 100) % 10),
                new Rectangle(25, 13 * Options.TILESIZE + 7, Options.TILESIZE, Options.TILESIZE),
                false);
        App.getGameWindow().addTexture(
                textures,
                getCoin((amount / 10) % 10),
                new Rectangle(25 + 10, 13 * Options.TILESIZE + 7, Options.TILESIZE, Options.TILESIZE),
                false);
        App.getGameWindow().addTexture(
                textures,
                getCoin(amount % 10),
                new Rectangle(25 + 20, 13 * Options.TILESIZE + 7, Options.TILESIZE, Options.TILESIZE),
                false);
    }

    /**
     * switch case to get number texture
     * 
     * @param i int
     * @return Rectangle of number to fetch from texture
     */
    private Rectangle getCoin(int i) {

        return switch (i) {
            case 1 -> new Rectangle(5, 14, Options.TILESIZE, Options.TILESIZE);
            case 2 -> new Rectangle(6, 14, Options.TILESIZE, Options.TILESIZE);
            case 3 -> new Rectangle(7, 14, Options.TILESIZE, Options.TILESIZE);
            case 4 -> new Rectangle(8, 14, Options.TILESIZE, Options.TILESIZE);
            case 5 -> new Rectangle(5, 15, Options.TILESIZE, Options.TILESIZE);
            case 6 -> new Rectangle(6, 15, Options.TILESIZE, Options.TILESIZE);
            case 7 -> new Rectangle(7, 15, Options.TILESIZE, Options.TILESIZE);
            case 8 -> new Rectangle(8, 15, Options.TILESIZE, Options.TILESIZE);
            case 9 -> new Rectangle(5, 16, Options.TILESIZE, Options.TILESIZE);
            default -> new Rectangle(6, 16, Options.TILESIZE, Options.TILESIZE);
        };
    }

    /**
     * displays current weapon
     */
    private void displayWeapon() {
        App.getGameWindow().addTexture(
                textures,
                new Rectangle(0, 3, 2 * Options.TILESIZE, 2 * Options.TILESIZE),
                new Rectangle(0, 1, 2 * Options.TILESIZE, 2 * Options.TILESIZE),
                false);
        player.getActiveWeapon().inv_render(0);
    }

    private void displayHP() {
        final int EMPTY_HEART_INDEX = 4;
        var hp = this.player.getHP();

        // Get the number of hearts to display
        int max_num_hearts = hp.second() / Options.HEART_SIZE;
        // How many of them are full?
        int current_full_hearts = hp.first() / Options.HEART_SIZE;
        // How much are partially damaged?
        int current_damage = hp.first() % Options.HEART_SIZE;

        // For each total hearts
        for (int pos = 0; pos < max_num_hearts; pos++) {
            // if pos < current_full_hearts
            if (pos < current_full_hearts) {
                renderHP(pos, 0);
                continue;
            }
            // If it is on the current heart
            if (pos == current_full_hearts) {
                renderHP(pos, (EMPTY_HEART_INDEX - current_damage));
                continue;
            }
            // The rest
            renderHP(pos, EMPTY_HEART_INDEX);
        }
    }

    /**
     * Renders a given heart-container
     */
    private void renderHP(int pos, int origin) {
        int basex = 4;
        int basey = 0;
        App.getGameWindow().addTexture(
                textures,
                basex + origin, basey,
                2 + pos, 0);
    }
}

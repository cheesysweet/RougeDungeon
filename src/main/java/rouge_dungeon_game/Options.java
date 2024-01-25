package rouge_dungeon_game;

import java.awt.event.KeyEvent;

public interface Options {
    public int TILESIZE = 16;
    public int SCALE = 2;
    public int S_TILESIZE = TILESIZE * SCALE;

    public int CHAR_COLLIDER_SIZE = TILESIZE - 6;

    public String WINDOW_NAME = "rogue-dungeon";
    public int WINDOW_WIDTH = 1620;
    public int WINDOW_HEIGHT = 1030;

    public String BASE_MAP = "garden_with_marketStall";

    // number of boxes
    public int roomHeight = 15;
    // number of boxes
    public int roomWidth = 25;

    public int FRAMES_PER_SECOND = 30;
    public int FRAMES_PER_MILLIS = 30 * 1000;
    public int NANO_TO_SECONDS = 1_000_000_000;
    public int NANO_TO_MILLI = 1_000_000;
    public long TIME_BETWEEN_UPDATES = NANO_TO_MILLI / FRAMES_PER_MILLIS;
    public int MAX_UPDATES_BETWEEN_RENDER = 1;
    public long START = System.nanoTime();

    public int SOCKET = 12345;

    public Rectangle charSize = new Rectangle(10, 10 * Options.TILESIZE, Options.TILESIZE, 22);
    public Rectangle logSize = new Rectangle(10,10, Options.TILESIZE*2, 32);
    public int charDamage = 2;
    public String charImage = "blue_character";
    public String clientImage = "character";

    public int HEART_SIZE = 5;
    public int MAX_COINS = 999;

    public Integer enemyLayer = 350;
    public Integer terrainLayer = 600;
    public Integer interactionLayer = 601;
    public Integer borderLayer = 599;

    // true = display debug-info
    public boolean DEBUG = false;

    enum States {
        // Right when the game is starting
        START,
        // The game is running
        PLAY,
        // The game is paused
        PAUSE,
        PLAYER_DEAD,
        // Used to know when the game is to close
        CLOSE
    };

    public enum Directions {
        DOWN,
        RIGHT,
        UP,
        LEFT,
        NONE,
        ANY
    }

    public enum Actions {
        NONE,
        WALKING,
        INTERACTION,
        ATTACK
    }

    public final char Key_UP = KeyEvent.VK_W; // 'w';
    public final char Key_DOWN = KeyEvent.VK_S; // 's';
    public final char Key_LEFT = KeyEvent.VK_A; // 'a';
    public final char Key_RIGHT = KeyEvent.VK_D; //'d';
    public final char Key_INTER = KeyEvent.VK_E; // 'e';
    public final char Key_ATTACK = KeyEvent.VK_SPACE; // ' ';
}

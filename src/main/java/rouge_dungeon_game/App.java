package rouge_dungeon_game;

import java.awt.Color;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import rouge_dungeon_game.CreateWorld.MapHandling;
import rouge_dungeon_game.CreateWorld.SaveMap;
import rouge_dungeon_game.Options.States;
import rouge_dungeon_game.Texture.Texture;
import rouge_dungeon_game.Texture.TextureHandler;
import rouge_dungeon_game.window.GameWindow;

/**
 * Hello world!
 *
 */
public class App {
    private static Window g_window;
    public static Game game;
    public static SaveMap map;
    public static SwingWorker<Void, Void> gameThread;

    public static States currState = States.START;

    static boolean quit = false;

    public static GameWindow getGameWindow() {
        return g_window.game_window();
    }

    /**
     * Add in the textures to be used
     */
    private static void setup_texture_handler() {
        TextureHandler.INSTANCE.addTexture("Overworld", 40, 36);
        TextureHandler.INSTANCE.addTexture("Cave", 40, 10);
        TextureHandler.INSTANCE.addTexture("Inner", 40, 18);
        TextureHandler.INSTANCE.addTexture("Objects");
        TextureHandler.INSTANCE.addTexture("Terrain");
        TextureHandler.INSTANCE.addTexture("Vegetation");
        TextureHandler.INSTANCE.addTexture("character", Options.TILESIZE, 22, true);
        TextureHandler.INSTANCE.addTexture("log", Options.TILESIZE, 22, true);
        TextureHandler.INSTANCE.addTexture("weapons", Options.TILESIZE, Options.TILESIZE, true);
        TextureHandler.INSTANCE.addTexture("slash", Options.TILESIZE * 3, Options.TILESIZE, true);
        TextureHandler.INSTANCE.addTexture("slash_90deg", Options.TILESIZE, Options.TILESIZE * 3, true);
        try {
            // Flip the slash 90 deg
            {
                Texture text = TextureHandler.INSTANCE.getTexture("slash");
                TextureHandler.INSTANCE.addTexture("slash_flip",
                        Utils.flipHorizontal(Utils.flipVertical(text.getImage())));
            }
            // Flip the slash 90 deg
            {
                Texture text = TextureHandler.INSTANCE.getTexture("slash_90deg");
                TextureHandler.INSTANCE.addTexture("slash_90deg_flip",
                        Utils.flipVertical(Utils.flipHorizontal(text.getImage())));
            }

            // add a blue character
            {
                Texture chartext = TextureHandler.INSTANCE.getTexture("character");
                TextureHandler.INSTANCE.addTexture("blue_character",
                        Utils.changePixels(chartext.getImage(),
                                new Color[] { new Color(196, 60, 60), new Color(136, 46, 46) },
                                new Color[] { Color.BLUE, new Color(42, 42, 42) }));
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Thread to handle the main game-loop
     */
    public static SwingWorker<Void, Void> create_loopThread() {
        return new SwingWorker<Void, Void>() {

            @Override
            protected void done() {
                System.err.println("Thread Done");
                super.done();
            }

            private boolean check_done() {
                return currState == States.CLOSE || this.isCancelled();
            }

            @Override
            protected Void doInBackground() throws Exception {

                game.changeMap(MapHandling.load(Options.BASE_MAP));

                long lastUpdateTime = Utils.getElapsedTimeNano();

                outerloop: while (!App.quit) {
                    if (check_done()) {
                        break outerloop;
                    }
                    long start = Utils.getElapsedTimeNano();

                    int updateCount = 0;
                    // do as many game updates as we need to, potentially playing catchup.
                    while (start - lastUpdateTime >= Options.TIME_BETWEEN_UPDATES
                            && updateCount < Options.MAX_UPDATES_BETWEEN_RENDER) {
                        // The game-loop itself
                        // NOTE: Order is important
                        if (currState == States.PLAY) {
                            try {
                                game.handle_input();
                                game.update();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (currState == States.PLAYER_DEAD) {
                            // TODO: do some things
                            g_window.show_dead();
                            App.quit = true;
                        }

                        lastUpdateTime += Options.TIME_BETWEEN_UPDATES;
                        updateCount++;
                    }

                    if (start - lastUpdateTime >= Options.TIME_BETWEEN_UPDATES) {
                        lastUpdateTime = start - Options.TIME_BETWEEN_UPDATES;
                    }

                    // In the case of a render-bug, crash the game to find out why
                    try {
                        // Give the current "ticks"
                        game.render(lastUpdateTime / Options.TIME_BETWEEN_UPDATES);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                    }

                    // Sleep for the rest of the time, to ensure that the game
                    // has the correct number of steps
                    long lastRenderTime = start;
                    while (start - lastRenderTime < Options.TIME_BETWEEN_UPDATES
                            && start - lastUpdateTime < Options.TIME_BETWEEN_UPDATES) {
                        if (check_done()) {
                            break outerloop;
                        }

                        Thread.yield();
                        start = Utils.getElapsedTimeNano();
                    }
                }
                // NOTE: Place things here to close down stuff
                return null;
            }
        };
    }

    /**
     * Start the game and gamethread
     */
    public static void start_game() {
        App.currState = States.START;
        App.quit = false;
        gameThread.execute();
    }

    /**
     * Close the game and gamethread
     * Does not reset the game-state
     */
    public static void kill_game() {
        App.currState = States.CLOSE;
        App.quit = true;
        game = new Game(g_window);
        gameThread = create_loopThread();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setup_texture_handler();
                g_window = new Window(Options.WINDOW_WIDTH, Options.WINDOW_HEIGHT);
                game = new Game(g_window);
                gameThread = create_loopThread();
            }
        });
    }
}

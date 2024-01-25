package rouge_dungeon_game.CreateWorld;

import rouge_dungeon_game.Options;
import rouge_dungeon_game.Texture.TextureHandler;


/**
 * Create World!
 *
 */
public class CreateWorld{


    public static void main(String[] args) {
        TextureHandler t_handler = TextureHandler.INSTANCE;
        // t_handler.addTexture(name, cols, rows);
        t_handler.addTexture("Overworld", 40, 36);
        t_handler.addTexture("Cave", 40, 10);
        t_handler.addTexture("Inner", 40, 18);
        t_handler.addTexture("Objects");
        t_handler.addTexture("Terrain");
        t_handler.addTexture("Vegetation");
        t_handler.addTexture("character", Options.TILESIZE, 22, true);
        t_handler.addTexture("log", Options.TILESIZE, 22, true);


        new CreateWorldFrame().start();

    }
}

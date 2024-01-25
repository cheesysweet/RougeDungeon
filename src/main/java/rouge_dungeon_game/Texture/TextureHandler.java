package rouge_dungeon_game.Texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import rouge_dungeon_game.Options;

public class TextureHandler {
    public static TextureHandler INSTANCE = new TextureHandler();

    private final HashMap<String, Texture> textureMap;

    private TextureHandler() {
        this.textureMap = new HashMap<>();
        this.textureMap.put("red", new Texture(Color.RED, Options.TILESIZE * 2, Options.TILESIZE * 2));
    }

    /**
     * Inser a texture with specified cols and rows
     */
    public void addTexture(String name, int cols, int rows) {
        this.textureMap.put(name, new Texture(name, cols, rows));
    }

    /**
     * Insert a texture and let the texture calculate itself
     */
    public void addTexture(String name) {
        this.textureMap.put(name, new Texture(name));
    }

    /**
     * Insert a texture and let the texture calculate itself
     */
    public void addTexture(String name, BufferedImage image) {
        this.textureMap.put(name, new Texture(name, image));
    }

    /**
     * Insert a texture and let the texture calculate itself
     */
    public void addTexture(String name, int sprite_w, int sprite_h, boolean spritesize) {
        this.textureMap.put(name, new Texture(name, sprite_w, sprite_h, spritesize));
    }

    /**
     * Get a texture with a specified name
     * @throws IOException
     */
    public Texture getTexture(String name) throws IOException {
        var texture = this.textureMap.get(name);
        if(texture == null) {
            throw new IOException("No such texture");
        }
        return texture;
    }
}

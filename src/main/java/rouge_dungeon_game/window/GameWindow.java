package rouge_dungeon_game.window;

import javax.swing.JLabel;

import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Consumer;

import rouge_dungeon_game.Options;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.Utils;
import rouge_dungeon_game.Texture.Texture;
import rouge_dungeon_game.Texture.TextureHandler;
import rouge_dungeon_game.entity.Tile;

/**
 * Class that handles and abstract some things about the game window
 * 
 * @author Sven Englsperge Raswill
 */
public class GameWindow extends JLabel {
    private BufferedImage image;
    private Graphics2D g2d;
    public int width;
    public int height;
    public int s_width;
    public int s_height;
    private Texture base;

    private BufferedImage background;
    private BufferedImage middleground;
    private BufferedImage foreground;
    // private Tile[][] background;
    // private Tile[][] middleground;
    // private Tile[][] foreground;

    /**
     * Set the initial width and height
     * Sets base to black
     */
    public GameWindow(int width, int height) {
        this.setSize(width, height);
        this.width = width;
        this.height = height;
        this.base = new Texture(Color.BLACK, width, height);
    }

    /**
     * Set the initial width and height
     * Takes a texture as well to set the base to
     */
    public GameWindow(Texture texture, int width, int height) {
        this.setSize(width, height);
        this.width = width;
        this.height = height;
        this.base = texture;
    }

    /**
     * Change the size of the window as well as the internal image
     */
    @Override
    public void setSize(int width, int height) {
        super.setSize(width * Options.SCALE, height * Options.SCALE);
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.g2d = (Graphics2D) this.image.getGraphics();
        this.width = width;
        this.height = height;
        this.s_width = width / Options.SCALE;
        this.s_height = height / Options.SCALE;
    }

    /**
     * Set the image to the base-texture
     */
    public void clear() {
        g2d.drawImage(this.base.getImage(),
                0, 0, this.width, this.height,
                null);
    }

    public void addTiles(Tile[][] background, Tile[][] middleground, Tile[][] foreground, Boolean scale) {
        this.background = drawTiles(background, scale);
        this.middleground = drawTiles(middleground, scale);
        this.foreground = drawTiles(foreground, scale);
    }

    public void drawBackground() {
        g2d.drawImage(this.background,
                0, 0,
                null);
    }

    public void drawMiddleground() {
        g2d.drawImage(this.middleground,
                0, 0,
                null);
    }

    public void drawForeground() {
        g2d.drawImage(this.foreground,
                0, 0,
                null);
    }

    private BufferedImage drawTiles(Tile[][] tiles, Boolean scale) {
        BufferedImage nImage;
        if (scale) {
            nImage = new BufferedImage(tiles.length * Options.S_TILESIZE,
                    tiles[0].length * Options.S_TILESIZE,
                    BufferedImage.TYPE_INT_ARGB);
        } else {
            nImage = new BufferedImage(tiles.length * Options.TILESIZE,
                    tiles[0].length * Options.TILESIZE,
                    BufferedImage.TYPE_INT_ARGB);
        }

        var g = nImage.getGraphics();
        for (var a : tiles) {
            for (var b : a) {
                if (b != null) {
                    try {
                        var text = TextureHandler.INSTANCE.getTexture(b.textureName())
                                .getSubImage(b.imgX(), b.imgY());
                        if (scale) {
                            g.drawImage(text,
                                    b.posX() * Options.S_TILESIZE, b.posY() * Options.S_TILESIZE,
                                    Options.S_TILESIZE, Options.S_TILESIZE,
                                    null);
                        } else {
                            g.drawImage(text,
                                    b.posX() * Options.TILESIZE, b.posY() * Options.TILESIZE,
                                    Options.TILESIZE, Options.TILESIZE,
                                    null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        g.drawImage(nImage, 0, 0, null);
        g.dispose();
        return nImage;

    }

    /**
     * Takes in a texture, the point from the texture to take from and the
     * point on the screen to draw to
     * 
     * @param origin          - the origin from the texture
     * @param pos             - the position on the canvas to place it
     * @param horizontal_flip - if the image should be flipped or not
     */
    public void addTexture(Texture texture,
            Rectangle origin,
            Rectangle pos,
            boolean horizontal_flip) {

        if (origin == null) {
            origin = new Rectangle(0, 0, texture.getRect().w(), texture.getRect().h());
        }

        var text = texture.getSubImage(origin);

        if (horizontal_flip) {
            text = Utils.flipHorizontal(text);
        }

        g2d.drawImage(text,
                pos.x() * Options.SCALE, pos.y() * Options.SCALE,
                pos.w() * Options.SCALE, pos.h() * Options.SCALE,
                null);
    }

    /**
     * Change to the given color as well
     */
    public void addTexture(Texture texture,
            Rectangle origin,
            Rectangle pos,
            boolean horizontal_flip,
            Color color) {

        if (origin == null) {
            origin = new Rectangle(0, 0, texture.getRect().w(), texture.getRect().h());
        }

        var text = texture.getSubImage(origin);

        text = Utils.changeColor(text, color);

        if (horizontal_flip) {
            text = Utils.flipHorizontal(text);
        }

        g2d.drawImage(text,
                pos.x() * Options.SCALE, pos.y() * Options.SCALE,
                pos.w() * Options.SCALE, pos.h() * Options.SCALE,
                null);
    }

    public void drawImage(BufferedImage image, Rectangle pos) {
        g2d.drawImage(image,
                pos.x() * Options.SCALE, pos.y() * Options.SCALE,
                pos.w() * Options.SCALE, pos.h() * Options.SCALE,
                null);
    }

    /**
     * Takes in a texture, the point from the texture to take from and the
     * point on the screen to draw to
     * 
     * @param origin - the origin from the texture
     * @param pos    - the position on the canvas to place it
     */
    public void addTexture(Texture texture,
            int originX, int originY,
            int posX, int posY) {
        var text = texture.getSubImage(originX, originY);

        g2d.drawImage(text,
                posX * Options.S_TILESIZE, posY * Options.S_TILESIZE,
                Options.S_TILESIZE, Options.S_TILESIZE,
                null);
    }

    public void drawOthers(Consumer<Graphics> f) {
        f.accept(g2d);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.image,
                0, 0,
                this.width * Options.SCALE, this.height * Options.SCALE, null);
        g.dispose();
    }
}

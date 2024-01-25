package rouge_dungeon_game.Texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics;

import javax.imageio.ImageIO;

import rouge_dungeon_game.Options;
import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.Utils;

public class Texture {

    private final String name;
    private final int columns;
    private final int rows;
    private final int spriteWidth;
    private final int spriteHeight;

    private BufferedImage image;
    private int image_width;
    private int image_height;

    /**
     * @param name - the name of the texture
     * @param columns - How many columns total is the image
     * @param rows - How many rows total is the image
     * Sets sprite w & h to TILESIZE
     */
    public Texture(String name,
            int columns,
            int rows) {
        this.name = name;
        this.columns = columns;
        this.rows = rows;

        this.image = loadImage(this.name);
        this.image_height = this.image.getHeight();
        this.image_width = this.image.getWidth();
        this.spriteWidth = Options.TILESIZE;
        this.spriteHeight = Options.TILESIZE;
    }

    /**
     * @param name - the name of the texture
     * @param sprite_w - the width of each sprite
     * @param sprite_h - the height of each sprite
     * @param spritesize - To know to use this one
     */
    public Texture(String name,
            int sprite_w,
            int sprite_h,
            boolean spritesize) {
        this.name = name;
        this.image = loadImage(this.name);

        this.image_height = this.image.getHeight();
        this.image_width = this.image.getWidth();

        this.columns = this.image_width / sprite_w;
        this.rows = this.image_height / sprite_h;

        this.spriteWidth = sprite_w;
        this.spriteHeight = sprite_h;
    }

    /**
     * Creates a texture from a color
     * @param name - the color to use
     * @param columns - How many columns total is the image
     * @param rows - How many rows total is the image
     * Sets sprite w & h to TILESIZE
     */
    public Texture(Color name,
            int columns,
            int rows) {
        this.name = name.toString();
        this.columns = columns;
        this.rows = rows;

        this.image = new BufferedImage(this.rows, this.columns, BufferedImage.TYPE_INT_ARGB);
        for(int y = 0; y < this.columns; ++y) {
            for(int x = 0; x < this.rows; ++x) {
                this.image.setRGB(x, y, name.getRGB());
            }
        }
        this.image_height = this.rows;
        this.image_width = this.columns;

        this.spriteWidth = Options.TILESIZE;
        this.spriteHeight = Options.TILESIZE;
    }

    /**
     * Simply creates an image from the name
     * columns = image width / TILESIZE
     * rows = image height / TILESIZE
     * sprite w & h = TILESIZE
     */
    public Texture(String name) {
        this.name = name;

        this.image = loadImage(this.name);
        this.columns = image.getWidth() / Options.TILESIZE;
        this.rows = image.getHeight() / Options.TILESIZE;
        this.image_height = this.image.getHeight();
        this.image_width = this.image.getWidth();

        this.spriteWidth = Options.TILESIZE;
        this.spriteHeight = Options.TILESIZE;
    }

    /**
     * Creates an image from a given image
     * @param name - the name of the image
     * @param image - the image to use
     * columns = image width / TILESIZE
     * rows = image height / TILESIZE
     * sprite w & h = TILESIZE
     */
    public Texture(String name, BufferedImage image) {
        this.name = name;

        this.image = image;
        this.columns = image.getWidth() / Options.TILESIZE;
        this.rows = image.getHeight() / Options.TILESIZE;
        this.image_height = this.image.getHeight();
        this.image_width = this.image.getWidth();

        this.spriteWidth = Options.TILESIZE;
        this.spriteHeight = Options.TILESIZE;
    }

    /**
     * Loads an image from memory
     */
    private BufferedImage loadImage(String name) {
        try {
            System.err.println(name);
            return ImageIO.read(this.getClass()
                    .getResource("/" + name + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * Return a resized image, but does not change the original one
     */
    public BufferedImage getResized(int width, int height) {
        return Utils.resize(this.image, width, height);
    }

    /**
     * Get a subimage from the image
     * @param posX is the topleft x-position
     * @param posY is the topleft y-position
     * both are multiplied with Tilessize so with a 20*20 grid of 16*16 squares
     * only the coords 10,10 are needed
     */
    public BufferedImage getSubImage(int posX, int posY) {
        return this.image.getSubimage(
                posX * this.spriteWidth,
                posY * this.spriteHeight,
                this.spriteWidth,
                this.spriteHeight);
    }

    /**
     * Get a subimage from the image
     * @param origin is the Rectangle that you want to get
     */
    public BufferedImage getSubImage(Rectangle origin) {
        return this.image.getSubimage(
                origin.x() * origin.w(),
                origin.y() * origin.h(),
                origin.w(),
                origin.h());
    }

    /**
     * Get a subimage but resized
     * @param posX is the topleft x-position
     * @param posY is the topleft y-position
     * both are multiplied with Tilessize so with a 20*20 grid of 16*16 squares
     * only the coords 10,10 are needed
     */
    public BufferedImage getSubImageResized(int posX,
                                            int posY,
                                            int width,
                                            int height) {
        BufferedImage bi = this.getSubImage(posX, posY);
        return Utils.resize(bi, width, height);
    }

    /**
     * Returns a point with x = width and y = height
     */
    public Point getWidthHeight() {
        return new Point(this.image_width, this.image_height);
    }

    /**
     * Returns a point with x = rows and y = columns
     */
    public Point getRowCol() {
        return new Point(this.rows, this.columns);
    }

    public Rectangle getRect() {
        return new Rectangle(this.rows, this.columns, this.image_width, this.image_height);
    }
}

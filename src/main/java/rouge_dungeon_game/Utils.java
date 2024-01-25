package rouge_dungeon_game;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.image.RescaleOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.io.File;
import java.net.URISyntaxException;

import rouge_dungeon_game.entity.Character;
import rouge_dungeon_game.Options.Directions;
import rouge_dungeon_game.collider.*;

public class Utils {
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }

    /**
     * Get a better view of the current time in milliseconds
     */
    public static long getTime() {
        return (System.nanoTime() / Options.NANO_TO_MILLI);
    }

    /**
     * Get the elapsed time from the start in nanoseconds
     * Slightly easier to handle
     */
    public static long getElapsedTimeNano() {
        return (System.nanoTime() - Options.START) / Options.NANO_TO_MILLI;
    }

    /**
     * Get a file using the resourceloader
     */
    public static File getFile(String name) {
        try {
            return new File(
                    Utils.class
                            .getResource(name)
                            .toURI());
        } catch (URISyntaxException e) {
            System.err.println(e);
        }
        return null;
    }

    /**
     * Helper to flip images vertically
     */
    public static BufferedImage flipVertical(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        var op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    /**
     * Helper to flip images horizontally
     */
    public static BufferedImage flipHorizontal(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0));
        var op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    /**
     * Change the color of the image using the given color
     * returns a new image with that color highlighted
     */
    public static BufferedImage changeColor(BufferedImage image, Color color) {
        var first = new float[] { color.getRed() / 255,
                color.getGreen() / 255,
                color.getBlue() / 255,
                color.getAlpha() / 255 };
        var second = new float[4];
        var res = new RescaleOp(first, second, null);
        return res.filter(image, null);
    }

    private enum Colors {
        blue(1, 0),
        green(2, 8),
        red(3, 16),
        alpha(0, 24);

        int num;
        int offset;

        Colors(int num, int offset) {
            this.num = num;
            this.offset = offset;
        }
    }

    public static BufferedImage changePixels(BufferedImage image, Color fromColor, Color toColor) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                var color = image.getRGB(row, col);
                if (color != 0) {
                    if (color == fromColor.getRGB()) {
                        newImage.setRGB(row, col, toColor.getRGB());
                        continue;
                    }
                }
                newImage.setRGB(row, col, color);
            }
        }

        return newImage;
    }

    public static BufferedImage changePixels(BufferedImage image, Color[] fromColor, Color[] toColor) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // , "From and to have to be the same length"
        assert (fromColor.length == toColor.length);
        for (int row = 0; row < width; row++) {
            outer: for (int col = 0; col < height; col++) {
                var color = image.getRGB(row, col);
                if (color != 0) {
                    for (int i = 0; i < fromColor.length; i++) {
                        if (color == fromColor[i].getRGB()) {
                            newImage.setRGB(row, col, toColor[i].getRGB());
                            continue outer;
                        }
                    }
                }
                newImage.setRGB(row, col, color);
            }
        }

        return newImage;
    }

    /**
     * Rotates an image. Actually rotates a new copy of the image.
     * [from
     * here](https://stackoverflow.com/questions/15927014/rotating-an-image-90-degrees-in-java)
     * 
     * @param img   The image to be rotated
     * @param angle The angle in degrees
     * @return The rotated image
     */
    public static BufferedImage rotate(BufferedImage img, double angle) {
        double sin = Math.abs(Math.sin(Math.toRadians(angle))),
                cos = Math.abs(Math.cos(Math.toRadians(angle)));

        int w = img.getWidth(null), h = img.getHeight(null);

        int neww = (int) Math.floor(w * cos + h * sin),
                newh = (int) Math.floor(h * cos + w * sin);

        BufferedImage bimg = new BufferedImage(neww, newh, img.getType());
        Graphics2D g = bimg.createGraphics();

        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(Math.toRadians(angle), w / 2, h / 2);
        g.drawRenderedImage(img, null);
        g.dispose();

        return bimg;
    }

    /**
     * Helper to paint a collider
     */
    public static void PaintCollider(Collider collider, Graphics graphics) {
        graphics.setColor(collider.getColor());
        graphics.drawRect(
                collider.size.x() * Options.SCALE, collider.size.y() * Options.SCALE,
                collider.size.w() * Options.SCALE, collider.size.h() * Options.SCALE);
    }

    /**
     * Get the 16x16 squares around the given char
     * ..x
     * x c x
     * ..x
     * It looks at the chars collider, so if the collider is placed at the lower
     * of two squares like this:
     * ..h
     * ..c
     * Then the top box will overlap with the "head". This to simulate "proper"
     * interaction (which this is currently used for)
     */
    public static Rectangle getDirrs(Character chara) {
        switch (chara.getDirection()) {
            case UP -> {
                return new Rectangle(
                        chara.getCollider().size.x(),
                        chara.getCollider().size.y() - Options.TILESIZE,
                        Options.TILESIZE, Options.TILESIZE);
            }
            case LEFT -> {
                return new Rectangle(
                        chara.getCollider().size.x() - Options.TILESIZE,
                        chara.getCollider().size.y(),
                        Options.TILESIZE, Options.TILESIZE);
            }
            case DOWN -> {
                return new Rectangle(
                        chara.getCollider().size.x(),
                        chara.getCollider().size.y() + chara.getCollider().size.h(),
                        Options.TILESIZE, Options.TILESIZE);
            }
            case RIGHT -> {
                return new Rectangle(
                        chara.getCollider().size.x() + chara.getCollider().size.w(),
                        chara.getCollider().size.y(),
                        Options.TILESIZE, Options.TILESIZE);
            }
            default -> {
                return null;
            }
        }
    }

    public static Rectangle getDirrs(Rectangle chara, Directions dirr) {
        switch (dirr) {
            case UP -> {
                return new Rectangle(
                        chara.x(),
                        chara.y() - Options.TILESIZE - 2,
                        Options.TILESIZE, Options.TILESIZE);
            }
            case LEFT -> {
                return new Rectangle(
                        chara.x() - Options.TILESIZE,
                        chara.y() - Options.TILESIZE / 2 - 2,
                        Options.TILESIZE, Options.TILESIZE);
            }
            case DOWN -> {
                return new Rectangle(
                        chara.x(),
                        chara.y() + chara.h(),
                        Options.TILESIZE, Options.TILESIZE);
            }
            case RIGHT -> {
                return new Rectangle(
                        chara.x() + chara.w(),
                        chara.y() - Options.TILESIZE / 2 - 2,
                        Options.TILESIZE, Options.TILESIZE);
            }
            default -> {
                return null;
            }
        }
    }

    public static Rectangle getDirrs(Character chara, Directions dirr) {
        switch (dirr) {
            case UP -> {
                return new Rectangle(
                        chara.getCollider().size.x(),
                        chara.getCollider().size.y() - Options.TILESIZE - 2,
                        Options.TILESIZE, Options.TILESIZE);
            }
            case LEFT -> {
                return new Rectangle(
                        chara.getCollider().size.x() - Options.TILESIZE,
                        chara.getCollider().size.y() - Options.TILESIZE / 2 - 2,
                        Options.TILESIZE, Options.TILESIZE);
            }
            case DOWN -> {
                return new Rectangle(
                        chara.getCollider().size.x(),
                        chara.getCollider().size.y() + chara.getCollider().size.h(),
                        Options.TILESIZE, Options.TILESIZE);
            }
            case RIGHT -> {
                return new Rectangle(
                        chara.getCollider().size.x() + chara.getCollider().size.w(),
                        chara.getCollider().size.y() - Options.TILESIZE / 2 - 2,
                        Options.TILESIZE, Options.TILESIZE);
            }
            default -> {
                return null;
            }
        }
    }

        /**
     * Get the 16x16 squares around the given char
     * ..x
     * x c x
     * ..x
     * It looks at the chars collider, so if the collider is placed at the lower
     * of two squares like this:
     * ..h
     * ..c
     * Then the top box will overlap with the "head". This to simulate "proper"
     * interaction (which this is currently used for)
     * 
     * @param size - the size of the box you want to place
     *             w and h are used for the size
     *             x and y are used for how far away it should be placed
     */
    public static Rectangle getDirrs(Character chara, Rectangle size) {
        switch (chara.getDirection()) {
            case UP -> {
                return new Rectangle(
                        chara.getCollider().size.x() - size.x(),
                        chara.getCollider().size.y() - Options.TILESIZE,
                        size.h(), size.w());
            }
            case LEFT -> {
                return new Rectangle(
                        chara.getCollider().size.x() - Options.TILESIZE,
                        chara.getCollider().size.y() - size.y() - size.y() / 2,
                        size.w(), size.h());
            }
            case DOWN -> {
                return new Rectangle(
                        chara.getCollider().size.x() - size.x(),
                        chara.getCollider().size.y() + chara.getCollider().size.h(),
                        size.h(), size.w());
            }
            case RIGHT -> {
                return new Rectangle(
                        chara.getCollider().size.x() + chara.getCollider().size.w(),
                        chara.getCollider().size.y() - size.y() - size.y() / 2,
                        size.w(), size.h());
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * @param size - how much to change the positioning with
     */
    public static Rectangle getDirrs(Rectangle chara, Directions dirr, Rectangle size) {
        switch (dirr) {
            case UP -> {
                return new Rectangle(
                        chara.x() - size.w(),
                        chara.y() - size.y(),
                        size.h(), size.w());
            }
            case LEFT -> {
                return new Rectangle(
                        chara.x() - chara.w() - size.x(),
                        chara.y() - size.y(),
                        size.w(), size.h());
            }
            case DOWN -> {
                return new Rectangle(
                        chara.x() - size.w(),
                        chara.y() + chara.h(),
                        size.h(), size.w());
            }
            case RIGHT -> {
                return new Rectangle(
                        chara.x() + chara.w() + size.x(),
                        chara.y() - size.y(),
                        size.w(), size.h());
            }
            default -> {
                return null;
            }
        }
    }

    public static Rectangle getDirrs(Character chara, Rectangle size, Directions dirr) {
        switch (dirr) {
            case UP -> {
                return new Rectangle(
                        chara.getCollider().size.x() - size.x(),
                        chara.getCollider().size.y() - Options.TILESIZE,
                        size.h(), size.w());
            }
            case LEFT -> {
                return new Rectangle(
                        chara.getCollider().size.x() - Options.TILESIZE,
                        chara.getCollider().size.y() - size.y() - size.y() / 2,
                        size.w(), size.h());
            }
            case DOWN -> {
                return new Rectangle(
                        chara.getCollider().size.x() - size.x(),
                        chara.getCollider().size.y() + chara.getCollider().size.h(),
                        size.h(), size.w());
            }
            case RIGHT -> {
                return new Rectangle(
                        chara.getCollider().size.x() + chara.getCollider().size.w(),
                        chara.getCollider().size.y() - size.y() - size.y() / 2,
                        size.w(), size.h());
            }
            default -> {
                return null;
            }
        }
    }

    public static Directions get_next_clockwise(Directions dirr) {
        switch (dirr) {
            case DOWN:
                return Directions.LEFT;
            case LEFT:
                return Directions.UP;
            case UP:
                return Directions.RIGHT;
            case RIGHT:
                return Directions.DOWN;
            default:
                return Directions.RIGHT;
        }
    }

    /**
     * Get {@link Directions} based on the velocity
     */
    public static Directions set_direction(Point velocity, Directions prev) {
        if (velocity.y() < 0) {
            return Directions.UP;
        }
        if (velocity.x() < 0) {
            return Directions.LEFT;
        }
        if (velocity.y() > 0) {
            return Directions.DOWN;
        }
        if (velocity.x() > 0) {
            return Directions.RIGHT;
        }
        return prev;
    }
}

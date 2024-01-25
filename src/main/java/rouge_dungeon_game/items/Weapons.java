package rouge_dungeon_game.items;

import java.io.IOException;

import rouge_dungeon_game.*;
import rouge_dungeon_game.Options.Directions;
import rouge_dungeon_game.Texture.Texture;
import rouge_dungeon_game.Texture.TextureHandler;
import rouge_dungeon_game.entity.Entity;
import rouge_dungeon_game.entity.Character;

public class Weapons implements Items {

    public Texture swordtexture;
    public int damage;
    public Point swordPos;

    protected Rectangle size = new Rectangle(Options.TILESIZE / 4, Options.TILESIZE,
            Options.TILESIZE / 2, Options.TILESIZE * 2);

    public Weapons(Point point, int damage) {
        this.swordPos = point;
        this.damage = damage;
        try {
            this.swordtexture = TextureHandler.INSTANCE.getTexture("weapons");
        } catch (IOException e) {
            System.err.printf("%s - %s\n", e.getCause(), e.getMessage());
        }
    }

    /**
     * @param ent - the Character that did the attack
     */
    public void attack(Character ent) {
        Rectangle int_rect = Utils.getDirrs(ent,
                this.size);
        App.game.e_handler.characterList.parallelStream()
                .filter(character -> character.getDmgCollider().collide(int_rect) != Directions.NONE)
                .forEach(character -> {
                    character.damage(this.damage);
                });
    }

    @Override
    public void interact(Entity ent) {
        System.out.println("HELLO: entity");
    }

    @Override
    public void inv_render(long ticks) {
        Point swordPoint = this.swordPos;
        App.getGameWindow().addTexture(
                this.swordtexture,
                new Rectangle(swordPoint.x(), swordPoint.y(), Options.TILESIZE, Options.TILESIZE),
                new Rectangle(10, 9, Options.TILESIZE, Options.TILESIZE),
                false);
    }


    @Override
    public void render(long ticks, Directions direction, Rectangle user) {
        Directions dirr = Utils.get_next_clockwise(direction);
        var text = swordtexture.getImage();
        if (dirr == Directions.UP || dirr == Directions.DOWN) {
            text = swordtexture
                    .getSubImage(new Rectangle(swordPos.x(), swordPos.y(), Options.TILESIZE, Options.TILESIZE));
            if (dirr == Directions.DOWN) {
                text = Utils.flipVertical(text);
            }
        }
        if (dirr == Directions.RIGHT || dirr == Directions.LEFT) {
            text = swordtexture.getSubImage(new Rectangle(3, swordPos.y(), Options.TILESIZE, Options.TILESIZE));
            if (dirr == Directions.LEFT) {
                text = Utils.flipHorizontal(text);
            }
        }
        App.game.getGameWindow().drawImage(
                text,
                Utils.getDirrs(user, dirr));
        this.render_slash(Utils.getDirrs(user,
                direction,
                this.size),
                direction);
    }

    private void render_slash(Rectangle position, Directions direction) {
        var dirr = "slash";
        switch (direction) {
            case DOWN -> dirr = "slash_flip";
            case LEFT -> dirr = "slash_90deg_flip";
            case RIGHT -> dirr = "slash_90deg";
            case UP -> dirr = "slash";
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        }
        try {
            App.game.getGameWindow().addTexture(
                    TextureHandler.INSTANCE.getTexture(dirr),
                    null,
                    position,
                    false);
        } catch (IOException e) {
            System.err.printf("%d - %d\n", e.getCause(), e.getMessage());
        }
    }
}

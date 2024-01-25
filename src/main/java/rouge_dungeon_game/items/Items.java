package rouge_dungeon_game.items;

import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.Options.Directions;
import rouge_dungeon_game.entity.Entity;

/**
 * Base class for items
 * @author Sven Englsperger Raswill
 */
public interface Items {
    /**
     * What the item is supposed to do
     * @param direction - the current direction of the player
     * @param user - the size of the user
     */
    public void interact(Entity ent);

    /**
     * Renders the item in the inventory
     */
    public void inv_render(long ticks);

    /**
     * Rendering the item in the world
     */
    public void render(long ticks, Directions dirr, Rectangle user);
}

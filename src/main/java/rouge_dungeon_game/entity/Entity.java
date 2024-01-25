package rouge_dungeon_game.entity;

/**
 * Base class for all entities
 * The order is somewhat important 
 * (best to handle before update, update before render, and finish with render)
 */
public abstract class Entity {
    /**
     * Used for things that are to be handled, done before updating information
     */
    public void handle() {}

    /**
     * Updates the characters information
     */
    public void update() {}

    /**
     * Used to render the Entity to the screen
     * @param ticks - time in milliseconds
     */
    public void render(long ticks) {}
}

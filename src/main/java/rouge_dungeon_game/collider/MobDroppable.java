package rouge_dungeon_game.collider;

import rouge_dungeon_game.lootChest.MobDrop;

/**
 * Used for when a removable thing should be removable
 */
public interface MobDroppable {
    public MobDrop getMobDrop();
    public void setMobDrop(MobDrop mobDrop);
}

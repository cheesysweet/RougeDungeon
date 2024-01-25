package rouge_dungeon_game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.awt.Graphics;

import rouge_dungeon_game.App;
import rouge_dungeon_game.Options;
import rouge_dungeon_game.Point;
import rouge_dungeon_game.Utils;
import rouge_dungeon_game.CreateWorld.MapHandling;
import rouge_dungeon_game.Options.Directions;
import rouge_dungeon_game.collider.*;
import rouge_dungeon_game.collider.loot.CoinCollider;
import rouge_dungeon_game.collider.loot.HeartCollider;
import rouge_dungeon_game.lootChest.MobDrop;

/**
 * Handles all entities, and ensures that their handle, update and render are
 * called
 */
public class EntityHandler {

    // All entities
    public List<Entity> entityList = new ArrayList<>();
    // Playerentities
    public List<Character> playerList = new ArrayList<>();
    // Other entities
    public List<Character> characterList = new ArrayList<>();

    // All non-entity-colliders
    private List<Collider> colliders = new ArrayList<>();

    // All InteractionColliders
    public List<InteractionCollider> oColliders = new ArrayList<>();
    private List<MobDrop> mobDrops = new ArrayList<>();

    public EntityHandler() {

    }

    /**
     * Add an entity from the handler
     */
    public void addEntity(Entity ent) {
        if (ent instanceof Player || ent instanceof ClientEntity) {
            this.playerList.add((Character) ent);
        } else {
            if (ent instanceof Character) {
                this.characterList.add((Character) ent);
            }
        }
        this.entityList.add(ent);
    }

    /**
     * Add a list of entitites
     */
    public void addEntities(List<Entity> ent) {
        for (var each : ent) {
            addEntity(each);
        }
    }

    /**
     * Removes an entity from the handler
     */
    public void removeEntity(Character ent) {
        if (ent == null)
            return;
        if (ent instanceof Player || ent instanceof ClientEntity) {
            this.playerList.remove(ent);
        } else {
            if (ent instanceof Character) {
                this.characterList.remove(ent);
            }
        }
        MobDrop drop = new MobDrop(ent.getRect());
        this.mobDrops.add(drop);
        this.colliders.add(drop.getCollider());
        this.entityList.remove(ent);
    }

    /**
     * Remove inplace for the removeCharacter-operation
     */
    public void removeEntity(Character ent, boolean other) {
        if (ent == null)
            return;
        if (ent instanceof Player || ent instanceof ClientEntity) {
            this.playerList.remove(ent);
        }
        MobDrop drop = new MobDrop(ent.getRect());
        this.mobDrops.add(drop);
        this.colliders.add(drop.getCollider());
        this.entityList.remove(ent);
    }

    /**
     * Add a list of colliders to the handler
     */
    public void addColliders(List<Collider> colliders) {
        this.colliders.addAll(colliders);
        this.oColliders.addAll(
                colliders.parallelStream()
                        .filter(a -> a instanceof InteractionCollider)
                        .map(a -> (InteractionCollider) a)
                        .collect(Collectors.toList()));
    }

    /**
     * Reset the colliders
     */
    public void cleanColliders() {
        this.colliders = new ArrayList<>();
        this.oColliders = new ArrayList<>();
    }

    /**
     * Get all interactible colliders
     */
    public void checkInteractionColliders(Player player) {
        for (var each : this.oColliders) {
            if (each instanceof InteractionCollider) {
                ((InteractionCollider) each).interact(player);
            }
        }
    }

    /**
     * This iterates through all entities that need to be handled
     */
    public void handle() {
        // Handle colission for other entities
        for (var each : this.characterList) {
            assert (each != null);
            for (var collider : this.colliders) {
                assert (collider != null);
                var direction = Collisions.collide(each, collider);
                if (direction != Directions.NONE) {
                    if (collider.collideable()) {
                        each.velocity = new Point(0, 0);
                    }
                }
            }
        }

        // Handle colission for players
        for (var each : this.playerList) {
            assert (each != null);
            for (var collider : this.colliders) {
                assert (collider != null);
                if (each instanceof Player) {
                    this.player_interactions(each, collider);
                }
            }
            for (var o_entity : this.characterList) {
                var direction = Collisions.damageCollide(each, o_entity.damageCollider);
                if (direction == Directions.NONE)
                    continue;
                each.damage(o_entity.getDamage());
            }
        }

        // Update entities
        for (var each : this.entityList) {
            assert (each != null);
            each.handle();
        }
    }

    private void player_interactions(Character player, Collider collider) {
        Player player1 = (Player) player;
        if (Collisions.damageCollide(player, collider) != Directions.NONE
                && collider instanceof MapTransportCollider mapCol) {
            // checks for map transition collider
            var map = MapHandling.load(mapCol.getLoadMap());
            App.game.changeMap(map);
            App.game.setSpawn(mapCol.getSpawnCollider());
        }
        var direction = Collisions.collide(player, collider);
        if (direction == Directions.NONE) {
            return;
        }
        if (collider.collideable()) {
            player.velocity = new Point(0, 0);
        }
        if (collider instanceof LootCollider) {
            // TODO: Add actually interacting
        }
        if (collider instanceof CoinCollider col) {
            player1.addCoins(col.getAmount());
            col.remove();
            return;
        }
        if (collider instanceof HeartCollider col) {
            player1.addHp(col.getAmount());
            col.remove();
            return;
        }
    }

    /**
     * This iterates through all entities that need to be updated
     */
    public void update() {
        for (var each : this.entityList) {
            assert (each != null);
            each.update();
        }

        // Remove the colliders that should be removed
        this.colliders = this.colliders
                .stream()
                .map(col -> {
                    if (col.shouldRemove()) {
                        if (col instanceof MobDroppable) {
                            this.mobDrops.remove(((MobDroppable) col).getMobDrop());
                        }
                        if (col instanceof InteractionCollider coll) {
                            this.oColliders.remove(coll);
                            return col;
                        }
                        return null;
                    } else {
                        return col;
                    }
                })
                .filter(col -> col != null)
                .collect(Collectors.toList());

        this.characterList = this.characterList
                .stream()
                .map(col -> {
                    if (col.isDead()) {
                        this.removeEntity(col, true);
                        return null;
                    } else {
                        return col;
                    }
                }).filter(col -> col != null).collect(Collectors.toList());

    }

    /**
     * This iterates through all entities that need to be rendered
     */
    public void render(long time) {
        for (final var each : this.entityList) {
            assert (each != null);
            each.render(time);
            // Print the colliders if in debug
            if (Options.DEBUG && each instanceof Character) {
                App.game.getGameWindow().drawOthers(((Character) each).renderColliders());
            }
        }

        // renders mob drops
        for (var each : this.mobDrops) {
            assert (each != null);
            each.render(time);
        }
    }

    /**
     * Prints all colliders
     */
    public Consumer<Graphics> renderColliders() {
        var outer = this;
        return new Consumer<Graphics>() {
            @Override
            public void accept(Graphics graphics) {
                for (var each : outer.colliders) {
                    Utils.PaintCollider(each, graphics);
                }
            }
        };
    }
}

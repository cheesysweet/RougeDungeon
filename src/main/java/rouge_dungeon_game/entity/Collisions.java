package rouge_dungeon_game.entity;

import rouge_dungeon_game.Point;
import rouge_dungeon_game.Options.Directions;
import rouge_dungeon_game.collider.*;

public class Collisions {
    /**
     * Handles the colission between an entity and a collider
     * Including moving the entity
     */
    public static Directions collide(Character entity, Collider collider) {
        if (entity.velocity.equals(Point.zero())) {
            var eCollider = entity.collider;
            return eCollider.collide(collider);
        }
        var eCollider = entity.collider.copy();
        eCollider.moveCollider(new Point(
                eCollider.size.x() + entity.velocity.x() * entity.speed,
                eCollider.size.y() + entity.velocity.y() * entity.speed));
        return eCollider.collide(collider);
    }

    /**
     * Interacting with the damage-collider rather than the entityCollider
     */
    public static Directions damageCollide(Character entity, Collider collider) {
        if (entity.velocity.equals(Point.zero())) {
            var dCollider = entity.damageCollider;
            return dCollider.collide(collider);
        }
        var dCollider = entity.damageCollider.copy();
        dCollider.moveCollider(new Point(
                dCollider.size.x() + entity.velocity.x() * entity.speed,
                dCollider.size.y() + entity.velocity.y() * entity.speed));
        return dCollider.collide(collider);
    }
}

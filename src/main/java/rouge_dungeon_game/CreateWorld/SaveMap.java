package rouge_dungeon_game.CreateWorld;

import java.io.Serializable;
import java.util.ArrayList;

import rouge_dungeon_game.collider.Collider;
import rouge_dungeon_game.entity.Tile;

public record SaveMap(Tile[][] background,
                      Tile[][] middleGround,
                      Tile[][] foreground,
                      int width,
                      int height,
                      ArrayList<Collider> colliders,
                      ArrayList<Collider> interactions,
                      ArrayList<Collider> enemies) implements Serializable {
}

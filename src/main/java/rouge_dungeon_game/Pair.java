package rouge_dungeon_game;

import java.io.Serializable;

public record Pair<T, I>(T first, I second) implements Serializable {
}

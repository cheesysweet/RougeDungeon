package rouge_dungeon_game.socket;

import java.io.Serializable;

import rouge_dungeon_game.CreateWorld.SaveMap;

public record NewMap(SaveMap map) implements Sendable, Serializable {
    static final long serialVersionUID = 1L;
}

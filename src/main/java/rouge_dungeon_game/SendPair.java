package rouge_dungeon_game;

import java.io.Serializable;

import rouge_dungeon_game.socket.Sendable;

public record SendPair(Integer first, Sendable second) implements Serializable {
}

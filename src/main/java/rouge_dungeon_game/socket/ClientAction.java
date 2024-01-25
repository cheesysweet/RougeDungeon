package rouge_dungeon_game.socket;

import java.io.Serializable;

import rouge_dungeon_game.Options;
import rouge_dungeon_game.Point;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.Options.Directions;

public record ClientAction(Options.Actions action, Directions dirr, Rectangle position, Point velocity) implements Serializable, Sendable {
    static final long serialVersionUID = 11L;
}

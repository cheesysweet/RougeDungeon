package rouge_dungeon_game.entity;

import java.io.Serializable;

public record Tile(int posX, int posY, int imgX, int imgY, String textureName) implements Serializable {
    public void removeTexture() {

    }
}

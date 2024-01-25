package rouge_dungeon_game.CreateWorld;

import javax.swing.*;
import java.awt.*;

public class CreateWorldPanel extends JPanel {

    private CreateRoom world;

    public CreateWorldPanel() {
        this.setPreferredSize(new Dimension(1200,820));
    }

    public CreateWorldPanel start() {
        world = new CreateRoom();
        this.add(world);
        this.setVisible(true);
        return this;
    }

    public CreateWorldPanel close() {
        this.remove(world);
        world = null;
        this.setVisible(true);
        return this;
    }
}

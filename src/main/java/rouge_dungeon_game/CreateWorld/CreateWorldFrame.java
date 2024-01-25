package rouge_dungeon_game.CreateWorld;

import javax.swing.*;
import java.awt.*;

public class CreateWorldFrame extends JFrame {

    public CreateWorldFrame() {
        super("CreateWorld");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(1200,820));
    }

    public void start() {
        this.changeMain(new CreateRoom());
        this.pack();
        this.setVisible(true);
    }

    private void changeMain(JPanel pane) {
        this.add(pane);
        this.pack();
        this.setVisible(true);
    }
}

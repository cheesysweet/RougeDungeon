package rouge_dungeon_game.CreateWorld;

import io.reactivex.rxjava3.core.Observable;
import rouge_dungeon_game.Options;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.collider.Collider;
import rouge_dungeon_game.collider.SpawnCollider;
import rouge_dungeon_game.entity.EntityHandler;
import rouge_dungeon_game.window.GameWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.stream.Collectors;

public class SelectSpawnPoint extends JFrame{

    private GameWindow game_panel;
    private EntityHandler e_handler;
    private Observable<SpawnCollider> spawnCollider;



    public SelectSpawnPoint(SaveMap room) {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(810,520));

        this.game_panel = new GameWindow(850, 550);
        this.e_handler = new EntityHandler();


        printRoom(room);

        this.add(game_panel);
        this.pack();
        this.setVisible(true);
    }

    public Observable<SpawnCollider> getSpawnCollider() {
        return this.spawnCollider;
    }

    private void printRoom(SaveMap currentMap) {


        game_panel.setVisible(true);
        game_panel.setFocusable(true);

        spawnCollider = Observable.create(subscribe -> {
            game_panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    currentMap.interactions().stream()
                            .filter(c -> checkColliderPosition(e.getX(), e.getY(), c))
                            .filter(SpawnCollider.class::isInstance)
                            .map(c -> (SpawnCollider) c)
                            .forEach(subscribe::onNext);
                }
            });
        });

        game_panel.setSize(currentMap.width() * Options.TILESIZE,
                currentMap.height() * Options.TILESIZE);



        // Load in the tiles from the samemap
        game_panel.addTiles(currentMap.background(), currentMap.middleGround(),
                currentMap.foreground(), false);

        this.e_handler.addColliders(currentMap.colliders().stream()
                .peek(a -> a.size = new rouge_dungeon_game.Rectangle(a.size.x() / 4, a.size.y() /
                        4, a.size.w() / 4, a.size.h() / 4))
                .collect(Collectors.toList()));
        this.e_handler.addColliders(currentMap.interactions().stream()
                .peek(a -> a.size = new rouge_dungeon_game.Rectangle(a.size.x() / 4, a.size.y() /
                        4, a.size.w() / 4, a.size.h() / 4))
                .collect(Collectors.toList()));
        this.e_handler.addColliders(currentMap.enemies().stream()
                .peek(a -> a.size = new Rectangle(a.size.x() / 4, a.size.y() /
                        4, a.size.w() / 4, a.size.h() / 4))
                .collect(Collectors.toList()));


        // Clear the screen
        game_panel.clear();

        // Draw the background
        game_panel.drawBackground();

        // Draw the middleground
        game_panel.drawMiddleground();


        // Draw anything in the foreground
        game_panel.drawForeground();

        game_panel.drawOthers(this.e_handler.renderColliders());

        game_panel.repaint();

    }

    private Boolean checkColliderPosition(int pX, int pY,  Collider c) {
        int cX = c.size.x()*4;
        int cY = c.size.y()*4;
        int sX = c.size.w()*4;
        int sY = c.size.h()*4;
        return pX >= cX && pY >= cY && pX < cX + sX && pY < cY + sY;
    }

    public void closeFrame() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

}

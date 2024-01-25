package rouge_dungeon_game;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import rouge_dungeon_game.Options.States;
import rouge_dungeon_game.CreateWorld.CreateWorldPanel;
import rouge_dungeon_game.socket.Client;
import rouge_dungeon_game.socket.Server;
import rouge_dungeon_game.socket.SocketThread;
import rouge_dungeon_game.window.GameWindow;

public class Window extends JFrame {
    private GameWindow game_panel;
    private JPanel control_panel = new JPanel();
    private JMenuBar bar = new JMenuBar();

    public SocketThread socket;

    private boolean inGame = false;
    private final int width;
    private final int height;

    public Window(int width, int height) {
        super(Options.WINDOW_NAME);
        this.width = width;
        this.height = height;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(width, height));

        this.bar.add(create_pause_menu());
        this.bar.add(create_network_menu());
        this.bar.add(create_close_menu());

        this.setJMenuBar(bar);

        this.start();
    }

    public void setKeys() {
        var outer = this;
        this.game_panel
                .addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (inGame) {
                            handle_game(e);
                        }
                    }

                    private void handle_game(KeyEvent e) {
                        // Currently quitting
                        if (e.getKeyChar() == 'q') {
                            App.kill_game();
                            if (socket != null) {
                                socket.shutdown();
                            }
                            outer.game_panel.setVisible(false);
                            outer.control_panel.setVisible(true);
                            outer.repaint();
                        }
                        // Pausing
                        if (e.getKeyChar() == 'p') {
                            switch (App.currState) {
                                case PAUSE -> {
                                    App.currState = States.PLAY;
                                }
                                case PLAY -> {
                                    App.currState = States.PAUSE;
                                }
                                // Currently does nothing
                                case START -> {
                                }
                                default -> {
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Items related to the pause-menu
     */
    private JMenu create_pause_menu() {
        JMenu menu;
        menu = new JMenu("THING");
        menu.setMnemonic('a');
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");

        var item = new JMenuItem("START");
        item.setMnemonic('n');
        item.addActionListener(a -> {
            App.currState = States.PLAY;
        });
        menu.add(item);

        item = new JMenuItem("STOP");
        item.setMnemonic('n');
        item.addActionListener(a -> {
            App.currState = States.PAUSE;
        });
        menu.add(item);
        return menu;
    }

    /**
     * Items related to networking
     */
    private JMenu create_network_menu() {
        JMenu menu;
        menu = new JMenu("NETWORK");
        menu.setMnemonic('n');
        menu.getAccessibleContext().setAccessibleDescription(
                "Choose network");

        var item = new JMenuItem("CLIENT");
        item.setMnemonic('C');
        item.addActionListener(a -> {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Client();
                        if(socket != null) {
                            menu.setVisible(false);
                            start_game();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
        });
        menu.add(item);
        item = new JMenuItem("HOST");
        item.setMnemonic('C');
        item.addActionListener(a -> {

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    socket = new Server();
                    menu.setVisible(false);
                    start_game();
                }

            });
        });
        menu.add(item);
        return menu;
    }

    /**
     * Handles things like closing the window
     */
    private JMenu create_close_menu() {
        JMenu menu;
        menu = new JMenu("CLOSE");
        menu.setMnemonic('q');
        menu.getAccessibleContext().setAccessibleDescription(
                "Close the game");

        var item = new JMenuItem("CLOSE");
        item.setMnemonic('x');
        item.addActionListener(a -> {
            App.kill_game();
            if(socket != null) {
                socket.shutdown();
            }
            this.dispose();
            System.exit(0);
        });
        menu.add(item);
        return menu;
    }

    /**
     * Handles creating the world-panel
     */
    private void CreateWorld() {
        var panel = new CreateWorldPanel();
        JMenu menu;
        menu = new JMenu("BACK MENU");
        menu.setMnemonic('b');
        menu.getAccessibleContext().setAccessibleDescription(
                "Back to main");

        var item = new JMenuItem("BACK");
        item.setMnemonic('x');
        item.addActionListener(a -> {
            menu.setVisible(false);
            this.control_panel.setVisible(true);
            this.remove(panel.close());
            this.pack();
            this.setVisible(true);
        });
        menu.add(item);
        menu.setVisible(false);
        this.bar.add(menu);

        var tempButton = new JButton("Create World");
        tempButton.addActionListener(a -> {
            menu.setVisible(true);
            this.add(panel.start());
            this.control_panel.setVisible(false);
            this.pack();
            this.setVisible(true);
        });
        this.control_panel.add(tempButton);
    }

    public void show_dead() {
        App.kill_game();
        JOptionPane.showMessageDialog(this, "You died!");
        this.game_panel.setVisible(false);
        this.control_panel.setVisible(true);
    }

    /**
     * The things relating to starting the game
     */
    private void StartGame() {
        this.game_panel = new GameWindow(this.width, this.height);
        setKeys();

        this.add(this.game_panel);
        this.game_panel.setVisible(false);

        var tempButton = new JButton("NEW GAME");
        tempButton.addActionListener(a -> {
            this.start_game();
            App.start_game();
        });
        this.control_panel.add(tempButton);
    }

    private void start_game() {
        this.inGame = true;
        game_panel.setVisible(true);
        game_panel.setFocusable(true);
        this.control_panel.setVisible(false);
        App.start_game();
    }

    public Window start() {
        StartGame();
        CreateWorld();

        this.add(this.control_panel);

        this.pack();
        this.setVisible(true);
        return this;
    }

    public GameWindow game_window() {
        return this.game_panel;
    }
}

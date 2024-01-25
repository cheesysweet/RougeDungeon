package rouge_dungeon_game.CreateWorld;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;

import io.reactivex.rxjava3.core.Observable;
import rouge_dungeon_game.Options;
import rouge_dungeon_game.Point;
import rouge_dungeon_game.Texture.Texture;
import rouge_dungeon_game.Texture.TextureHandler;

import rouge_dungeon_game.collider.Collider;

import rouge_dungeon_game.entity.Tile;

/**
 * Class used to handle the room creator
 *
 * @author Anton Byström and Sven Englsperger Raswill
 */
public class CreateRoom extends JPanel {

    /**
     * Cols = the number of X
     * Rows = the number of Y
     */
    private CollisionHandling collisionHandling;
    private String currentFile;
    private String currentImage;
    private ArrayList<Point> clickedLabel;
    private JLayeredPane room;
    private JPanel buttonMenu;
    private JScrollPane scrollPane;

    private JLabel[][] imageGrid;
    private JLabel[][] background_grid;
    private JLabel[][] middle_grid;
    private JLabel[][] foreground_grid;
    private Tile[][] background;
    private Tile[][] middleGround;
    private Tile[][] foreground;

    // number of boxes
    private final int roomHeight = Options.roomHeight;
    // number of boxes
    private final int roomWidth = Options.roomWidth;

    private int numCols = 0;
    private int numRows = 0;

    private final Integer layer = JLayeredPane.DEFAULT_LAYER;
    private final Integer m_layer = JLayeredPane.POPUP_LAYER;
    private final Integer i_layer = JLayeredPane.DRAG_LAYER;

    private boolean rightClick;
    private boolean middleClick;
    private boolean deleteTile = false;
    private boolean colliderBoolean = false;

    private boolean interactionBoolean = false;
    private boolean enemiesBoolean = false;

    public CreateRoom() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Background-grid
        this.background_grid = new JLabel[roomWidth][roomHeight];
        this.background = new Tile[roomWidth][roomHeight];

        // middle-grid
        this.middle_grid = new JLabel[roomWidth][roomHeight];
        this.middleGround = new Tile[roomWidth][roomHeight];

        // Foreground-grid
        this.foreground_grid = new JLabel[roomWidth][roomHeight];
        this.foreground = new Tile[roomWidth][roomHeight];

        this.buttonMenu = new JPanel();
        this.buttonMenu.setLayout(new BoxLayout(buttonMenu, BoxLayout.X_AXIS));
        this.buttonMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.buttonMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.scrollPane = new JScrollPane();
        this.scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.scrollPane.setPreferredSize(new Dimension((int) (this.getWidth() * 0.8), 200));

        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.X_AXIS));
        roomPanel.setPreferredSize(new Dimension(this.getWidth(), roomHeight*Options.S_TILESIZE));

        this.room = new JLayeredPane();
        this.room.setMinimumSize(new Dimension(roomWidth*Options.S_TILESIZE, roomHeight*Options.S_TILESIZE));
        this.room.setMaximumSize(new Dimension(roomWidth*Options.S_TILESIZE, roomHeight*Options.S_TILESIZE));
        this.room.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomPanel.add(this.room);

        // initiate mouse events
        Observable<List<Point>> observable = getMouseEvent(this.room);
        Observable<JLabel> selectedPanels = checkLocation(observable, this.room);
        selectedPanels.subscribe(this::paintGrid, err -> System.err.println(err.getMessage()));

        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setText(
                "Left-click to add foreground | Middle-click to add middleground | Right-click to add background | Drag to select a larger item among the items | Drag to place a single item in a large grid");

        JPanel savePanel = new JPanel();
        savePanel.setLayout(new BoxLayout(savePanel, BoxLayout.X_AXIS));
        savePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton collider = new JButton("terrain");
        JButton interaction = new JButton("interaction");
        JButton enemies = new JButton("enemies");

        Observable<Map<String, Boolean>> collisions = Observable.create(subscribe -> {
            collider.addActionListener(e -> {
                colliderBoolean = !colliderBoolean;
                subscribe.onNext(Map.of(e.getActionCommand(), colliderBoolean));
                collider.setForeground((colliderBoolean)? Color.RED: Color.BLACK);
                interaction.setEnabled(!colliderBoolean);
                enemies.setEnabled(!colliderBoolean);
            });
            interaction.addActionListener(e -> {
                interactionBoolean = !interactionBoolean;
                subscribe.onNext(Map.of(e.getActionCommand(), interactionBoolean));
                interaction.setForeground((interactionBoolean)? Color.RED: Color.BLACK);
                collider.setEnabled(!interactionBoolean);
                enemies.setEnabled(!interactionBoolean);
            });
            enemies.addActionListener(e -> {
                enemiesBoolean = !enemiesBoolean;
                subscribe.onNext(Map.of(e.getActionCommand(), enemiesBoolean));
                enemies.setForeground((enemiesBoolean)? Color.RED: Color.BLACK);
                collider.setEnabled(!enemiesBoolean);
                interaction.setEnabled(!enemiesBoolean);

            });
        });

        this.collisionHandling = new CollisionHandling(observable, roomPanel, collisions);

        JButton deleteIcon = new JButton("Delete tile");
        deleteIcon.addActionListener(e -> {
            this.deleteTile = !this.deleteTile;
            deleteIcon.setForeground((deleteTile)? Color.RED: Color.BLACK);
        });

        JButton clear = new JButton("Clear map");
        clear.addActionListener(e -> {
            // clears panel
            clearRoom();
            grid();
        });

        JButton save = new JButton("Save map");
        save.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter name..", currentFile);
            if (input != null) {
                MapHandling.save(input, background, middleGround, foreground, currentImage, roomWidth, roomHeight,
                        this.collisionHandling.terrain, this.collisionHandling.interactions, this.collisionHandling.enemies);
            }
        });

        JButton load = new JButton("Load map");
        var outer = this;
        load.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("src/main/resources/mapSections/");
            int u = fileChooser.showOpenDialog(this);
            if (u == JFileChooser.APPROVE_OPTION) {
                File selected = fileChooser.getSelectedFile();
                this.currentFile = selected.getName().replace(".ser", "");
                SaveMap loaded = MapHandling.load(selected);

                // clears panel
                clearRoom();

                this.background = loaded.background();
                this.middleGround = loaded.middleGround();
                this.foreground = loaded.foreground();

                int row = this.background.length;
                int col = this.background[0].length;

                try{

                for (int xnum = 0; xnum < row; xnum++) {
                    for (int ynum = 0; ynum < col; ynum++) {
                        Tile b = this.background[xnum][ynum];

                        JLabel nlabel = createGrid(xnum, ynum, true);
                        this.background_grid[xnum][ynum] = nlabel;
                        this.room.add(nlabel, layer);

                        if (b != null) {
                            var texture = TextureHandler.INSTANCE.getTexture(b.textureName());
                            this.background_grid[xnum][ynum].setIcon(new ImageIcon(texture.getSubImageResized(b.imgX(), b.imgY(),
                                    Options.S_TILESIZE, Options.S_TILESIZE)));
                        }

                        Tile m = this.middleGround[xnum][ynum];

                        JLabel m_label = createGrid(xnum, ynum, false);
                        this.middle_grid[xnum][ynum] = m_label;
                        this.room.add(m_label, m_layer);

                        if (m != null) {
                            var texture = TextureHandler.INSTANCE.getTexture(m.textureName());
                            this.middle_grid[xnum][ynum].setIcon(new ImageIcon(texture.getSubImageResized(m.imgX(), m.imgY(),
                                    Options.S_TILESIZE, Options.S_TILESIZE)));
                            this.middle_grid[xnum][ynum].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                        }

                        JLabel i_label = createGrid(xnum, ynum, false);
                        this.foreground_grid[xnum][ynum] = i_label;
                        this.room.add(i_label, i_layer);

                        Tile f = this.foreground[xnum][ynum];
                        if (f != null) {
                            var texture = TextureHandler.INSTANCE.getTexture(f.textureName());
                            this.foreground_grid[xnum][ynum].setIcon(new ImageIcon(texture.getSubImageResized(f.imgX(), f.imgY(),
                                    Options.S_TILESIZE, Options.S_TILESIZE)));
                            this.foreground_grid[xnum][ynum].setBorder(BorderFactory.createLineBorder(Color.yellow));
                        }
                    }
                }
                } catch (IOException error) {
                    System.err.println(error.getMessage());
                }

                if (loaded.colliders() != null) {
                    for (Collider coll: loaded.colliders()) {
                        this.collisionHandling.printTerrain(coll);

                    }
                }
                if (loaded.interactions() != null) {
                    for (Collider coll: loaded.interactions()) {
                        this.collisionHandling.printInteractions(coll);
                    }
                }
                if (loaded.enemies() != null) {
                    for (Collider coll: loaded.enemies()) {
                        this.collisionHandling.printEnemy(coll);
                    }
                }

            }
            this.room.setVisible(true);
            this.room.repaint();
            outer.setVisible(true);
        });

        savePanel.add(deleteIcon);
        savePanel.add(clear);
        savePanel.add(save);
        savePanel.add(load);
        savePanel.add(collider);
        savePanel.add(interaction);
        savePanel.add(enemies);

        this.add(roomPanel);
        this.add(this.buttonMenu);
        this.add(this.scrollPane);
        this.add(label);
        this.add(savePanel);

        buttonMenu();
        grid();
    }

    /**
     * creates buttons for the button menu
     */
    private void buttonMenu() {
        createButton("Overworld");
        createButton("Terrain");
        createButton("Vegetation");
        createButton("Cave");
        createButton("Inner");
        createButton("Objects");

        // loads the Overworld as default image selector
        loadImage("Overworld");
    }

    /**
     * clears room panel of all components and Colliders
     */
    private void clearRoom() {
        this.room.removeAll();
        this.room.revalidate();
        this.room.repaint();
        // Background-grid
        this.background_grid = new JLabel[roomWidth][roomHeight];
        this.background = new Tile[roomWidth][roomHeight];

        // middle-grid
        this.middle_grid = new JLabel[roomWidth][roomHeight];
        this.middleGround = new Tile[roomWidth][roomHeight];

        // Foreground-grid
        this.foreground_grid = new JLabel[roomWidth][roomHeight];
        this.foreground = new Tile[roomWidth][roomHeight];
        this.collisionHandling.terrain = new ArrayList<>();
        this.collisionHandling.interactions = new ArrayList<>();
        this.collisionHandling.enemies = new ArrayList<>();
        this.collisionHandling.createBorder();
    }

    /**
     * creates a button with action listener and sets a border
     * 
     * @param name name of button / image
     */
    private void createButton(String name) {
        JButton button = new JButton(name);
        button.addActionListener(e -> loadImage(e.getActionCommand()));
        this.buttonMenu.add(Box.createRigidArea(new Dimension(10, 0)));
        this.buttonMenu.add(button);
    }

    /**
     * loads image as texture atlas
     * 
     * @param name of image
     * @throws IOException
     */
    private void loadImage(String name) {
        try {
            texture(TextureHandler.INSTANCE.getTexture(name));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        this.currentImage = name;
        this.repaint();
    }

    /**
     * Sets up the texture into the clickable images in the bottom
     */
    private void texture(Texture atlasImage) {
        Point rowCols = atlasImage.getRowCol();
        int row = rowCols.x();
        int col = rowCols.y();

        Point widthHeight = atlasImage.getWidthHeight();

        clickedLabel = new ArrayList<>();

        JPanel options = new JPanel();
        GridLayout grid = new GridLayout(row, col, 0, 0);
        options.setLayout(grid);
        options.setMaximumSize(
                new Dimension(
                        widthHeight.x() * Options.SCALE,
                        widthHeight.y() * Options.SCALE));

        this.imageGrid = new JLabel[col][row];

        for (int ynum = 0; ynum < row; ynum++) {
            for (int xnum = 0; xnum < col; xnum++) {
                final BufferedImage image = atlasImage.getSubImageResized(
                        xnum,
                        ynum,
                        Options.S_TILESIZE,
                        Options.S_TILESIZE);

                final JLabel img = new JLabel(
                        new ImageIcon(image));

                final int finalxnum = xnum;
                final int finalynum = ynum;

                img.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                img.addMouseListener(new MouseAdapter() {
                    static int startX = -1;
                    static int startY = -1;
                    static int posX = -1;
                    static int posY = -1;
                    static boolean held = false;

                    @Override
                    public void mousePressed(MouseEvent e) {
                        held = true;
                        startX = finalxnum;
                        startY = finalynum;
                        super.mousePressed(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        held = false;
                        int i_startX, i_posX, i_startY, i_posY = 0;

                        if (posX == -1 && posY == -1) {
                            posY = finalynum;
                            posX = finalxnum;
                        }

                        // Switch places on start and stop
                        if (startX <= posX) {
                            i_startX = startX;
                            i_posX = posX;
                        } else {
                            i_startX = posX;
                            i_posX = startX;
                        }

                        if (startY <= posY) {
                            i_startY = startY;
                            i_posY = posY;
                        } else {
                            i_startY = posY;
                            i_posY = startY;
                        }

                        // Set the number of chosen rows and columns
                        numCols = (i_posX - i_startX) + 1;
                        numRows = (i_posY - i_startY) + 1;

                        if (clickedLabel != null) {
                            clickedLabel.forEach(a -> imageGrid[a.x()][a.y()]
                                    .setBorder(BorderFactory.createLineBorder(Color.BLACK)));
                            clickedLabel.clear();
                        }

                        for (int i_y = 0; i_y < numRows; i_y++) {
                            for (int i_x = 0; i_x < numCols; i_x++) {
                                int currX = i_x + i_startX;
                                int currY = i_y + i_startY;
                                var label = imageGrid[currX][currY];
                                label.setBorder(BorderFactory.createLineBorder(Color.RED));
                                clickedLabel.add(new Point(currX, currY));
                            }
                        }

                        // clickedLabel = (JLabel) e.getSource();
                        // clickedLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
                        super.mouseReleased(e);
                        startX = -1;
                        startY = -1;
                        posX = -1;
                        posY = -1;
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (!held)
                            return;
                        posX = finalxnum;
                        posY = finalynum;
                        // System.out.println("Entered");
                        // System.out.println("X: " + x + " Y: " + y);
                        super.mouseEntered(e);
                    }
                });
                imageGrid[finalxnum][finalynum] = img;
                options.add(img);
            }
            this.revalidate();
            this.repaint();
        }

        // scrollPane = new JScrollPane(options,
        // ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        // ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setViewportView(options);
        this.scrollPane.setMaximumSize(new Dimension(widthHeight.x() * Options.SCALE, widthHeight.y()));
    }

    /**
     * Sets up the clickable grid at the top
     */
    private void grid() {
        int row = roomWidth;
        int col = roomHeight;

        for (int xnum = 0; xnum < row; xnum++) {
            for (int ynum = 0; ynum < col; ynum++) {

                JLabel label = createGrid(xnum, ynum, true);
                background_grid[xnum][ynum] = label;

                this.room.add(label, layer);

                JLabel m_label = createGrid(xnum, ynum, false);
                middle_grid[xnum][ynum] = m_label;

                this.room.add(m_label, m_layer);

                JLabel i_label = createGrid(xnum, ynum, false);
                foreground_grid[xnum][ynum] = i_label;

                this.room.add(i_label, i_layer);
            }
        }
    }

    private JLabel createGrid(final int xnum, final int ynum, boolean background) {
        JLabel label = new JLabel();
        if (background) {
            label.setBorder(BorderFactory.createLineBorder(Color.black));
        }
        label.setBounds(xnum * Options.S_TILESIZE,
                ynum * Options.S_TILESIZE,
                Options.S_TILESIZE,
                Options.S_TILESIZE);

        return label;
    }

    private void paintGrid(JLabel label) {
        int xnum = label.getX()/Options.S_TILESIZE;
        int ynum = label.getY()/Options.S_TILESIZE;

        if (!colliderBoolean && !interactionBoolean && !enemiesBoolean &&
                !clickedLabel.isEmpty() || deleteTile) {

            if (!deleteTile) {

                var it = clickedLabel.iterator();
                var each = it.next();
                if (numRows > 1 || numCols > 1) {
                    for (int posY = ynum; posY < ynum + numRows; posY++) {
                        for (int posX = xnum; posX < xnum + numCols; posX++) {
                            int x = each.x();
                            int y = each.y();
                            setIcon(x, y, posY, posX);
                            if (it.hasNext()) {
                                each = it.next();
                            }
                        }
                    }

                } else {
                    int x = each.x();
                    int y = each.y();

                    for (int posY = ynum; posY <= ynum; posY++) {
                        for (int posX = xnum; posX <= xnum; posX++) {
                            setIcon(x, y, posY, posX);
                        }
                    }
                }
            } else {
                System.out.println(label);
                setIcon(0, 0, ynum, xnum);
            }
        }
        this.room.revalidate();
        this.room.repaint();
    }

    private void setIcon(int x, int y, int posY, int posX) {
        if (rightClick) {
            if (deleteTile) {
                background_grid[posX][posY].setIcon(null);
                background_grid[posX][posY].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                background[posX][posY] = null;
            } else {
                background_grid[posX][posY].setIcon(imageGrid[x][y].getIcon());
                background_grid[posX][posY].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                background[posX][posY] = new Tile(posX, posY, x, y, currentImage);
            }
        } else if (middleClick) {
            if (deleteTile) {
                middle_grid[posX][posY].setIcon(null);
                middle_grid[posX][posY].setBorder(null);
                middleGround[posX][posY] = null;
            } else {
                middle_grid[posX][posY].setIcon(imageGrid[x][y].getIcon());
                middle_grid[posX][posY].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                middleGround[posX][posY] = new Tile(posX, posY, x, y, currentImage);
            }
        } else {
            if (deleteTile) {
                foreground_grid[posX][posY].setIcon(null);
                foreground_grid[posX][posY].setBorder(null);
                foreground[posX][posY] = null;
            } else {
                foreground_grid[posX][posY].setIcon(imageGrid[x][y].getIcon());
                foreground_grid[posX][posY].setBorder(BorderFactory.createLineBorder(Color.YELLOW));
                foreground[posX][posY] = new Tile(posX, posY, x, y, currentImage);
            }
        }
    }

    private Observable<List<Point>> getMouseEvent(JLayeredPane room) {
        return Observable.create(subscribe -> {
            MouseAdapter mouseAdapter = new MouseAdapter() {
                Point point;
                @Override
                public void mousePressed(MouseEvent e) {
                    point = new Point(e.getX(), e.getY());
                    super.mousePressed(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    rightClick = e.getButton() == MouseEvent.BUTTON3;
                    middleClick = e.getButton() == MouseEvent.BUTTON2;
                    subscribe.onNext(Arrays.asList(point, new Point(e.getX(), e.getY())));
                    super.mouseReleased(e);
                }
            };
            room.addMouseListener(mouseAdapter);
        });
    }

    private Observable<JLabel> checkLocation(Observable<List<Point>> ob, JLayeredPane roomPanel) {
        return Observable.create(subscribe -> {
            ob.subscribe(event -> {
                for (Component com : roomPanel.getComponentsInLayer(0)) {
                    JLabel label = getLabelPositions(Objects.requireNonNull(generatePoint(event.get(0), event.get(1))), (JLabel) com);
                    if (label != null) {
                        subscribe.onNext(label);
                    }
                }
            }, err -> System.err.println(err.getMessage()));
        });
    }


    private JLabel getLabelPositions(Point[] points, JLabel label) {
        int cX = label.getX();
        int cY = label.getY();
        int sX = label.getWidth();
        int sY = label.getHeight();
        if (points[0].x()-sX <= cX && points[0].y()-sY <= cY && points[1].x() > cX && points[1].y() > cY) {
            return label;
        }

        return null;
    }

    /**
     *
     * @param pressed pressed point
     * @param released release point
     * @return Points of start pos, size
     */
    private Point[] generatePoint(Point pressed, Point released) {
        int pX = pressed.x();
        int pY = pressed.y();
        int rX = released.x();
        int rY = released.y();

        int i_startX, i_stopX, i_startY, i_stopY;

        // Switch places on start and stop
        if (pX <= rX) {
            i_startX = pX;
            i_stopX = rX;
        } else {
            i_startX = rX;
            i_stopX = pX;
        }

        if (pY <= rY) {
            i_startY = pY;
            i_stopY = rY;
        } else {
            i_startY = rY;
            i_stopY = pY;
        }

        Point topLeft = new Point(
                i_startX,
                i_startY);
        Point size = new Point(
                i_stopX,
                i_stopY);

        if (size.x() != 0 || size.y() != 0) {
            Point[] points = new Point[2];
            points[0] = topLeft;
            points[1] = size;
            return points;
        } else {
            Point[] points = new Point[2];
            points[0] = pressed;
            points[1] = released;
            return points;
        }
    }
}

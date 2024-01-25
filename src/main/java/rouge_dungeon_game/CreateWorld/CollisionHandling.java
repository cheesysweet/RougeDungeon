package rouge_dungeon_game.CreateWorld;

import io.reactivex.rxjava3.core.Observable;
import rouge_dungeon_game.Options;
import rouge_dungeon_game.Point;
import rouge_dungeon_game.collider.*;
import rouge_dungeon_game.lootChest.CommonLoot;
import rouge_dungeon_game.lootChest.LegendaryLoot;
import rouge_dungeon_game.lootChest.Loot;
import rouge_dungeon_game.lootChest.RareLoot;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Handles the different collisions that could happen
 */
public class CollisionHandling {
    private JLayeredPane room;
    private final Observable<List<Point>> observable;

    public ArrayList<Collider> terrain = new ArrayList<>();
    public ArrayList<Collider> interactions = new ArrayList<>();
    public ArrayList<Collider> enemies = new ArrayList<>();


    ButtonGroup interactionButtonGroup;
    ButtonGroup terrainButtonGroup;
    ButtonGroup lootButtonGroup;
    ButtonGroup enemyButtonGroup;

    JRadioButton addTerrain;
    JRadioButton removeTerrain;

    JRadioButton addInteraction;
    JRadioButton removeInteraction;
    JRadioButton setLoot;
    JRadioButton setSpawn;
    JRadioButton addTransportMap;


    JRadioButton legendaryLoot;
    JRadioButton rareLoot;
    JRadioButton commonLoot;


    JRadioButton addEnemy;
    JRadioButton removeEnemy;



    public CollisionHandling(Observable<List<Point>> observable, JPanel roomPanel, Observable<Map<String, Boolean>> collisions) {
        this.observable = observable;


        Arrays.stream(roomPanel.getComponents()).findFirst().ifPresent(room -> this.room = (JLayeredPane) room);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        createRadioButtons(buttonPanel);

        // subscribes to the "terrain" and "interaction" buttons
        colliderInteractions(collisions, buttonPanel);

        // room collider
        createBorder();


        roomPanel.add(buttonPanel);
    }

    /**
     * creates room border
     */
    public void createBorder() {
        printBorder(new TerrainCollider(
                new Point(0,Options.roomHeight * Options.S_TILESIZE),
                new Point(Options.roomWidth * Options.S_TILESIZE, Options.roomHeight * Options.S_TILESIZE)));
        printBorder(new TerrainCollider(
                new Point(Options.roomWidth * Options.S_TILESIZE,0),
                new Point(Options.roomWidth * Options.S_TILESIZE, Options.roomHeight * Options.S_TILESIZE)));
    }

    /**
     * creates all the radio buttons
     * @param buttonPanel JPanel for buttons
     */
    private void createRadioButtons(JPanel buttonPanel) {

        addInteraction = new JRadioButton("addInteraction");
        removeInteraction = new JRadioButton("removeInteraction");
        setLoot = new JRadioButton("setLoot");
        setSpawn = new JRadioButton("setSpawn");
        addTransportMap = new JRadioButton("addTransportMap");

        interactionButtonGroup = new ButtonGroup();
        interactionButtonGroup.add(addInteraction);
        interactionButtonGroup.add(removeInteraction);
        interactionButtonGroup.add(setSpawn);
        interactionButtonGroup.add(setLoot);
        interactionButtonGroup.add(addTransportMap);

        addTerrain = new JRadioButton("addTerrain");
        removeTerrain = new JRadioButton("removeTerrain");

        terrainButtonGroup = new ButtonGroup();
        terrainButtonGroup.add(addTerrain);
        terrainButtonGroup.add(removeTerrain);

        legendaryLoot = new JRadioButton("legendary");
        rareLoot = new JRadioButton("rare");
        commonLoot = new JRadioButton("common");

        lootButtonGroup = new ButtonGroup();
        lootButtonGroup.add(legendaryLoot);
        lootButtonGroup.add(rareLoot);
        lootButtonGroup.add(commonLoot);

        addEnemy = new JRadioButton("addEnemy");
        removeEnemy = new JRadioButton("removeEnemy");

        enemyButtonGroup = new ButtonGroup();
        enemyButtonGroup.add(addEnemy);
        enemyButtonGroup.add(removeEnemy);

        addInteraction.setName("interaction");
        removeInteraction.setName("interaction");
        setLoot.setName("interaction");
        setSpawn.setName("interaction");
        addTransportMap.setName("interaction");

        addTerrain.setName("terrain");
        removeTerrain.setName("terrain");

        legendaryLoot.setName("loot");
        legendaryLoot.setForeground(new LegendaryLoot().getColor());
        rareLoot.setName("loot");
        rareLoot.setForeground(new RareLoot().getColor());
        commonLoot.setName("loot");
        commonLoot.setForeground(new CommonLoot().getColor());

        addEnemy.setName("enemies");
        removeEnemy.setName("enemies");

        addTerrain.setVisible(false);
        removeTerrain.setVisible(false);
        addInteraction.setVisible(false);
        removeInteraction.setVisible(false);
        setLoot.setVisible(false);
        setSpawn.setVisible(false);
        addTransportMap.setVisible(false);
        legendaryLoot.setVisible(false);
        rareLoot.setVisible(false);
        commonLoot.setVisible(false);
        addEnemy.setVisible(false);
        removeEnemy.setVisible(false);

        buttonPanel.add(addInteraction);
        buttonPanel.add(setLoot);
        buttonPanel.add(setSpawn);
        buttonPanel.add(addTransportMap);
        buttonPanel.add(removeInteraction);

        buttonPanel.add(addTerrain);
        buttonPanel.add(removeTerrain);

        buttonPanel.add(legendaryLoot);
        buttonPanel.add(rareLoot);
        buttonPanel.add(commonLoot);

        buttonPanel.add(addEnemy);
        buttonPanel.add(removeEnemy);
    }


    /**
     * handles the different colliders and uses the observable to the screen point location
     * @param buttonPanel jpanel for buttons
     * @param collisions map for terrain / interaction button to se with one is pressed
     */
    private void colliderInteractions(Observable<Map<String, Boolean>> collisions, JPanel buttonPanel) {

        collisions.subscribe(map -> {
            terrainButtonGroup.clearSelection();
            interactionButtonGroup.clearSelection();
            lootButtonGroup.clearSelection();
            enemyButtonGroup.clearSelection();
            Arrays.stream(buttonPanel.getComponents())
                    .map(comp -> (JRadioButton) comp)
                    .filter(component -> map.containsKey(component.getName()))
                    .forEach(component -> {
                        component.setVisible(map.get(component.getName()));
                        if (component.getName().equals("interaction")) {
                            showLootOptions(buttonPanel, map.get("interaction"));
                        }
                    });
        });

        this.observable.subscribe(point -> {
            if (addTerrain.isSelected()) {
                createBorder();
                Point[] points = generatePoint(point.get(0), point.get(1));
                if (points != null) {
                    printTerrain(new TerrainCollider(points[0], points[1]));
                }
            }
            if (removeTerrain.isSelected()) {
                removeTerrain(point.get(0));
            }
            if (addInteraction.isSelected()) {
                Point[] points = generatePoint(point.get(0), point.get(1));
                if (points != null) {
                    printInteractions(new InteractionCollider(points[0], points[1]));
                }
            }
            if (removeInteraction.isSelected()) {
                removeInteraction(point.get(0));
            }
            if (setLoot.isSelected()) {
                addLoot(point.get(0), buttonPanel);
            }
            if (setSpawn.isSelected()) {
                addSpawn(point.get(0));
            }
            if (addTransportMap.isSelected()) {
                Point[] points = generatePoint(point.get(0), point.get(1));
                if (points != null) {
                    JFileChooser fileChooser = new JFileChooser("src/main/resources/mapSections/");
                    int u = fileChooser.showOpenDialog(room);
                    if (u == JFileChooser.APPROVE_OPTION) {
                        File selected = fileChooser.getSelectedFile();
                        SaveMap loaded = MapHandling.load(selected);
                        SelectSpawnPoint spawn = new SelectSpawnPoint(loaded);
                        spawn.getSpawnCollider().subscribe(collider -> {
                            printInteractions(new MapTransportCollider(points[0], points[1], selected.getName(), collider));
                            spawn.closeFrame();
                        });
                    }
                }
            }
            if (addEnemy.isSelected()) {
                Point[] points = generatePoint(point.get(0),point.get(1));
                if (points != null) {
                    printEnemy(new EnemySpawnCollider(points[0], points[1], "log"));
                }
            }
            if (removeEnemy.isSelected()) {
                removeEnemy(point.get(0));
            }

        }, err -> System.err.println(err.getMessage()));

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    /**
     * sets spawn location for the map
     * @param point position
     */
    private void addSpawn(Point point) {
        SpawnCollider collider = new SpawnCollider(
                new Point(point.x() - (Options.S_TILESIZE/2), point.y() - (Options.S_TILESIZE/2)),
                new Point(Options.S_TILESIZE, Options.S_TILESIZE));

        printInteractions(collider);

    }

    /**
     * set the loot buttons to visible or not
     * @param buttonPanel JPanel for the buttons
     * @param bool if loot button should be visible
     */
    private void showLootOptions(JPanel buttonPanel, Boolean bool) {
        Arrays.stream(buttonPanel.getComponents())
                .map(comp -> (JRadioButton) comp)
                .filter(comp -> comp.getName().equals("loot"))
                .forEach(comp -> {
                    comp.setVisible(bool);
                });
    }


    /**
     * removes a terrain collider
     * @param pressed pressed location
     */
    private void removeTerrain(Point pressed) {
        ArrayList<Collider> colls = new ArrayList<>();
        for (Collider c: this.terrain) {
            colls.add(checkColliderPosition(pressed.x(), pressed.y(), c, Options.terrainLayer));
        }
        this.terrain.removeAll(colls);
    }

    /**
     * removes an interaction collider
     * @param pressed pressed location
     */
    private void removeInteraction(Point pressed) {
        ArrayList<Collider> inter = new ArrayList<>();
        for (Collider c: this.interactions) {
            inter.add(checkColliderPosition(pressed.x(), pressed.y(), c, Options.interactionLayer));
        }
        this.interactions.removeAll(inter);
    }

    /**
     * removes an enemies
     * @param pressed pressed location
     */
    private void removeEnemy(Point pressed) {
        System.out.println("remove enemy");

        ArrayList<Collider> toRemove = new ArrayList<>();
        for (Collider enemy: this.enemies) {
            toRemove.add(checkColliderPosition(pressed.x(), pressed.y(), enemy, Options.enemyLayer));
        }
        this.enemies.removeAll(toRemove);
    }


    /**
     * changes an existing interaction collider to a loot collider
     * @param point clicked location
     * @param buttonPanel JPanel of buttons
     */
    private void addLoot(Point point, JPanel buttonPanel) {
        // gets selected loot button
        List<JRadioButton> buttons = Arrays.stream(buttonPanel.getComponents())
                .map(comp -> (JRadioButton) comp)
                .filter(comp -> comp.getName().equals("loot"))
                .filter(AbstractButton::isSelected)
                .toList();

        for (Component c: room.getComponentsInLayer(Options.interactionLayer)) {
            JLabel l = getLabelPositions(point, (JLabel) c);
            if (l != null) {
                l.setBorder(BorderFactory.createLineBorder(
                        (buttons.isEmpty()) ? new CommonLoot().getColor(): buttons.get(0).getForeground(), 3));
                l.repaint();

                removeInteraction(point);

                Color color = ((LineBorder)l.getBorder()).getLineColor();

                Loot loot;
                if (color.equals(new RareLoot().getColor())) {
                    loot = new RareLoot();
                } else if (color.equals(new LegendaryLoot().getColor())) {
                    loot = new LegendaryLoot();
                } else {
                    loot = new CommonLoot();
                }
                printInteractions(new LootCollider(
                        new Point(c.getX(), c.getY()),
                        new Point(c.getWidth(),
                                c.getHeight()), loot));
            }
        }
    }

    private JLabel getLabelPositions(Point points, JLabel label) {
        int cX = label.getX();
        int cY = label.getY();
        int sX = label.getWidth();
        int sY = label.getHeight();
        if (points.x() >= cX && points.y() >= cY && points.x() < cX+sX && points.y() < cY+sY) {
            return label;
        }
        return null;
    }


    /**
     * checks if pressed location is equal to a Collider
     * @param pX pressed x position
     * @param pY pressed y position
     * @param c component to check
     * @return Collider if it matches the location
     */

    private Collider checkColliderPosition(int pX, int pY,  Collider c, Integer layer) {
        int cX = c.size.x();
        int cY = c.size.y();
        int sX = c.size.w();
        int sY = c.size.h();
        if (pX >= cX && pY >= cY && pX < cX + sX && pY < cY + sY) {
            removeCollider(c, layer);
            return c;
        }
        return null;
    }

    /**
     * removes collider from room panel
     * @param col collider to remove
     * @param layer layer of room panel for component
     */
    private void removeCollider(Collider col, Integer layer) {
        for (Component roomComp: this.room.getComponentsInLayer(layer)) {
            if (col.size.x() == roomComp.getX() && col.size.y() == roomComp.getY()) {
                this.room.remove(roomComp);
            }
        }
        this.room.revalidate();
        this.room.repaint();
    }


    /**
     * save and prints terrain collider
     * @param col Collider
     */

    public void printTerrain(Collider col) {
        JLabel label = new JLabel();
        label.setBorder(BorderFactory.createLineBorder(col.getColor(), 3));
        label.setBounds(col.size.x(), col.size.y(), col.size.w(), col.size.h());


        this.terrain.add(col);
        this.room.add(label, Options.terrainLayer);
    }

    /**
     * save and prints terrain collider
     * @param col Collider
     */

    public void printBorder(Collider col) {
        JLabel label = new JLabel();
        label.setBorder(BorderFactory.createLineBorder(col.getColor(), 1));
        label.setBounds(col.size.x(), col.size.y(), col.size.w(), col.size.h());


        this.terrain.add(col);
        this.room.add(label, Options.borderLayer);
    }


    /**
     * Saves and prints interactable collider
     * @param col Collider
     */

    public void printInteractions(Collider col) {
        JLabel label = new JLabel();
        label.setBounds(col.size.x(), col.size.y(), col.size.w(), col.size.h());
        label.setBorder(BorderFactory.createLineBorder(col.getColor(), 3));

        this.interactions.add(col);
        this.room.add(label, Options.interactionLayer);
    }

    public JLabel printEnemy(Collider col) {
        JLabel label = new JLabel();
        label.setBorder(BorderFactory.createLineBorder(col.getColor(), 3));
        label.setBounds(col.size.x(), col.size.y(), col.size.w(), col.size.h());
        this.enemies.add(col);
        this.room.add(label, Options.enemyLayer);
        return label;
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
                i_stopX - i_startX,
                i_stopY - i_startY);

        if (size.x() != 0 || size.y() != 0) {
            Point[] points = new Point[2];
            points[0] = topLeft;
            points[1] = size;
            return points;
        }
        return null;
    }

}

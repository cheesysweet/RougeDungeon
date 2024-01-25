package rouge_dungeon_game;

import rouge_dungeon_game.CreateWorld.MapHandling;
import rouge_dungeon_game.CreateWorld.SaveMap;
import rouge_dungeon_game.Options.States;
import rouge_dungeon_game.collider.Collider;
import rouge_dungeon_game.collider.EnemySpawnCollider;
import rouge_dungeon_game.collider.SpawnCollider;
import rouge_dungeon_game.collider.TerrainCollider;
import rouge_dungeon_game.entity.*;
import rouge_dungeon_game.entity.Character;
import rouge_dungeon_game.socket.Server;
import rouge_dungeon_game.socket.SocketThread;
import rouge_dungeon_game.window.GameWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
    public Window window;
    public SaveMap currentMap;
    public Player player = new Player(Options.charSize, Options.charDamage);
    public Inventory inv = new Inventory(player);
    public List<ClientEntity> others = new ArrayList<>();
    public Point spawn;

    // Others
    public EntityHandler e_handler;
    private SocketThread socket;
    private boolean shrink;

    public Game(Window i_window) {
        window = i_window;

        this.start_game("garden_with_marketStall");
        // this.start_game("cave");
    }

    public Game(Window i_window, SocketThread socket, SaveMap map) {
        window = i_window;

        this.currentMap = map;
        this.setup_game(false);
    }

    public void start_game(String name) {
        SaveMap map = MapHandling.load(name);
        start_game(map);
    }

    public void setSocket(SocketThread socket) {
        this.socket = socket;
    }

    public void add_entity(Entity ent) {
        this.e_handler.addEntity(ent);
    }

    public void remove_entity(Character ent) {
        this.e_handler.removeEntity(ent);
    }

    public void changeMap(SaveMap map) {
        this.currentMap = map;
        if (socket != null) {
            if (socket instanceof Server) {
                ((Server) socket).changeMap(map);
                this.setup_game(true);
            } else {
                this.setup_game(false);
            }
        } else {
            this.setup_game(true);
        }
    }

    public void start_game(SaveMap map) {
        this.currentMap = map;
        this.setup_game(true);
        getStartpos();
    }

    public void getStartpos() {
        this.currentMap.interactions()
                .stream()
                .filter(a -> a instanceof SpawnCollider)
                .map(a -> (SpawnCollider) a)
                .findFirst()
                .ifPresent(a -> {
                    Point pos = new Point(a.size.x(), a.size.y());
                    this.spawn = pos;
                    this.player.setPosition(pos);
                });
    }

    public Player getPlayer() {
        return this.player;
    }

    private void setup_game(boolean shrink) {
        this.e_handler = new EntityHandler();
        this.e_handler.addEntity(this.player);
        for (var each : this.others) {
            this.e_handler.addEntity(each);
        }

        this.shrink = shrink;

        getGameWindow().setSize(this.currentMap.width() * Options.S_TILESIZE,
                this.currentMap.height() * Options.S_TILESIZE);

        // Load in the tiles from the samemap
        getGameWindow().addTiles(this.currentMap.background(), this.currentMap.middleGround(),
                this.currentMap.foreground(), true);

        // Add the colliders
        if (shrink) {
            this.e_handler.addColliders(this.currentMap.colliders().stream()
                    .peek(a -> a.size = new Rectangle(a.size.x() / Options.SCALE, a.size.y() /
                            Options.SCALE, a.size.w() / Options.SCALE, a.size.h() / Options.SCALE))
                    .collect(Collectors.toList()));
            this.e_handler.addColliders(this.currentMap.interactions().stream()
                    .peek(a -> a.size = new Rectangle(a.size.x() / Options.SCALE, a.size.y() /
                            Options.SCALE, a.size.w() / Options.SCALE, a.size.h() / Options.SCALE))
                    .collect(Collectors.toList()));
            this.e_handler.addColliders(this.currentMap.enemies().stream()
                    .peek(a -> a.size = new Rectangle(a.size.x() / Options.SCALE, a.size.y() /
                            Options.SCALE, a.size.w() / Options.SCALE, a.size.h() / Options.SCALE))
                    .collect(Collectors.toList()));
        } else {
            this.e_handler.addColliders(this.currentMap.colliders());
            this.e_handler.addColliders(this.currentMap.interactions());
            this.e_handler.addColliders(this.currentMap.enemies());
        }

        // Add enemies
        this.currentMap.enemies().forEach(a -> {
            this.e_handler.addEntity(new EnemyHandling((EnemySpawnCollider) a).getEnemy());
        });

    }

    /**
     * Used to add more entities
     */
    public void add_player(ClientEntity ent) {
        this.others.add((ClientEntity) ent);
        this.e_handler.addEntity(ent);
    }

    public SaveMap getCurrent() {
        return currentMap;
    }

    public void handle_input() {
        this.e_handler.handle();
    }

    public void update() {
        // Update entities
        this.e_handler.update();

        if(this.player.getHP().first() <= 0) {
            App.currState = States.PLAYER_DEAD;
        }
    }

    /**
     * It is important to do things in order here
     * At least for the different layers
     */
    public void render(long ticks) {
        synchronized (this.getGameWindow()) {
            // Clear the screen
            this.getGameWindow().clear();

            // Draw the background
            this.getGameWindow().drawBackground();

            // Draw the middleground
            this.getGameWindow().drawMiddleground();

            // Render entities
            this.e_handler.render(ticks);

            // Draw anything in the foreground
            this.getGameWindow().drawForeground();

            if (Options.DEBUG) {
                // Draw the colliders
                this.getGameWindow().drawOthers(this.e_handler.renderColliders());
            }

            inv.render(ticks);

            this.getGameWindow().repaint();
        }
    }

    public GameWindow getGameWindow() {
        return this.window.game_window();
    }

    public void setSpawn(SpawnCollider spawnCollider) {
        System.err.println(spawnCollider);
        Point pos;
        if (this.shrink) {
            pos = new Point(spawnCollider.size.x() * Options.SCALE, spawnCollider.size.y() * Options.SCALE);
        } else {
            pos = new Point(spawnCollider.size.x(), spawnCollider.size.y());
        }
        spawn = pos;
        this.player.setPosition(pos);
    }
}

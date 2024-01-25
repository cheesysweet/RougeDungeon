package rouge_dungeon_game.socket;

public abstract class SocketThread implements Runnable {

    public SocketThread() {
    }

    public abstract void run();

    public abstract void shutdown();

    public abstract String name();
}

package rouge_dungeon_game.socket;

import java.io.IOException;

public class OwnSocket extends java.net.Socket {
    private static int number = 0;
    public final int id;

    public OwnSocket() {
        this.id = number;
        number += 1;
    }

    public OwnSocket(String host, int socket) throws IOException {
        super(host, socket);
        this.id = number;
        number += 1;
    }
}

package rouge_dungeon_game.socket;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import rouge_dungeon_game.App;
import rouge_dungeon_game.Game;
import rouge_dungeon_game.Options;
import rouge_dungeon_game.Pair;
import rouge_dungeon_game.Point;
import rouge_dungeon_game.SendPair;
import rouge_dungeon_game.CreateWorld.SaveMap;
import rouge_dungeon_game.collider.SpawnCollider;
import rouge_dungeon_game.entity.ClientEntity;
import rouge_dungeon_game.entity.Player;

public class Client extends SocketThread {
    private final Socket socket;
    private Disposable networkDisposable;
    private HashMap<Integer, ClientEntity> clientMap = new HashMap<>();

    private Player player;
    private int clientnumber = 0;

    private final ObjectOutputStream output;

    private Disposable panelDisposable;

    public Client() throws IOException {
        super();
        var host = JOptionPane.showInputDialog("Which host?", "localhost");
        this.socket = new Socket(host, Options.SOCKET);
        this.output = new ObjectOutputStream(socket.getOutputStream());

        // Recieve the entire image when starting
        Observable.just(this.socket)
                .map(Socket::getInputStream)
                .map(ObjectInputStream::new)
                .subscribe(obj -> {
                    try {
                        var next = obj.readObject();
                        var pair = (Pair<Integer, SaveMap>) next;

                        // Get the map
                        App.game = new Game(App.game.window, this, pair.second());
                        clientnumber = pair.first();
                        this.player = App.game.player;
                        App.game.getStartpos();

                        this.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("Did not recieve the image");
                    }
                },
                        err -> {
                            System.err.println(err);
                            err.printStackTrace();
                        });
    }

    public Client(boolean other) throws IOException {
        super();
        this.socket = new Socket("localhost", Options.SOCKET);
        this.output = new ObjectOutputStream(socket.getOutputStream());

        this.run();
    }

    @Override
    public void run() {
        final CountDownLatch latch = new CountDownLatch(2);

        // Send data
        this.panelDisposable = this.player.getObservable()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> System.err.println("Drawing"))
                .doFinally(latch::countDown)
                .subscribe(action -> {
                    output.writeObject(new SendPair(clientnumber, action));
                },
                        err -> err.printStackTrace());

        // Receive data
        networkDisposable = Observable.create(emitter -> {
            Observable.just(this.socket)
                    .map(Socket::getInputStream)
                    .map(ObjectInputStream::new)
                    .subscribe(obj -> {
                        while (!emitter.isDisposed()) {
                            var next = obj.readObject();
                            emitter.onNext(next);
                        }
                    }, err -> System.err.println("Problem with client-receiving: " + err.getMessage()));
        })
                .subscribeOn(Schedulers.io())
                .doOnDispose(socket::close)
                .map(a -> (SendPair) a)
                .filter(pair -> pair.first() != clientnumber)
                .subscribe(
                        obj -> {
                            if (obj.second() instanceof ClientAction) {
                                var act = (ClientAction) obj.second();
                                if (clientMap.get(obj.first()) != null) {
                                    clientMap.get(obj.first()).do_action(act);
                                } else {
                                    var entity = new ClientEntity(Options.charSize,
                                            Options.charDamage,
                                            Options.clientImage);
                                    clientMap.put(obj.first(), entity);
                                    App.game.add_player(entity);
                                    entity.do_action(act);
                                }
                            }
                            if (obj.second() instanceof NewMap) {
                                var map = (NewMap) obj.second();
                                App.game.changeMap(map.map());
                            }
                        },
                        err -> System.err.println("From server: " + err.getMessage() + "\n"));
    }

    @Override
    public void shutdown() {
        networkDisposable.dispose();
        panelDisposable.dispose();
        Schedulers.shutdown();

        System.err.println("Exiting");
    }

    @Override
    public String name() {
        return "Client";
    }
}

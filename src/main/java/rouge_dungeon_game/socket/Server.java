package rouge_dungeon_game.socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import rouge_dungeon_game.App;
import rouge_dungeon_game.Options;
import rouge_dungeon_game.Pair;
import rouge_dungeon_game.Rectangle;
import rouge_dungeon_game.SendPair;
import rouge_dungeon_game.entity.ClientEntity;
import rouge_dungeon_game.CreateWorld.SaveMap;

public class Server extends SocketThread {

    public static class ConnectError extends Throwable {
        @Serial
        private static final long serialVersionUID = 1L;
        private final Socket socket;

        public ConnectError(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }
    }

    private final Disposable disposable;
    private Disposable panelDisposable;
    private Disposable con_disposable;

    private PublishSubject<Socket> connections;
    private ReplaySubject<SendPair> actionStream;

    private HashMap<Integer, Disposable> disposables;
    private HashMap<Integer, Disposable> actionDisposables;

    private HashMap<Integer, ClientEntity> clients;

    private boolean acceptsConnections = true;

    private int clientnumber = 10000;

    public Server() {
        var server = this;

        this.disposable = Observable.just(server)
                .subscribeOn(Schedulers.single())
                .doOnNext(e -> server.run())
                .doOnDispose(server::shutdown)
                .doOnSubscribe(d -> System.out.println("Server is running..."))
                .subscribe(e -> {
                }, err -> System.err.println(err.getMessage()));
    }

    public void changeMap(SaveMap map) {
        actionStream
                .onNext(new SendPair(0, new NewMap(map)));
    }

    @Override
    public void run() {
        this.connections = PublishSubject.create();
        this.disposables = new HashMap<>();
        this.actionDisposables = new HashMap<>();
        this.clients = new HashMap<>();
        this.actionStream = ReplaySubject.createWithSize(3);

        // The current hosts information
        this.panelDisposable = App.game.player.getObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(action -> {
                    var pair = new SendPair(clientnumber, action);
                    actionStream.onNext(pair);
                },
                        err -> System.err.printf("%s, Client: %s\n", err.getCause(), err.getMessage()));

        App.game.setSocket(this);

        Completable.create(emitter -> listenForConnections())
                .subscribeOn(Schedulers.single())
                .subscribe();

        this.con_disposable = connections
                .doOnNext(s -> System.out.println("TCP Connection Accepted"))
                .subscribe(this::listenToSocket, err -> System.err.println(err.getMessage()));
    }

    private void listenForConnections() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress("0.0.0.0", Options.SOCKET));
            while (acceptsConnections) {
                Socket socket = serverSocket.accept();

                // Send the entire drawing to the newly connected client
                Observable.just(socket)
                        .map(Socket::getOutputStream)
                        .map(ObjectOutputStream::new)
                        .subscribe(e -> {
                            e.writeObject(new Pair<Integer, SaveMap>(socket.hashCode(), App.game.getCurrent()));
                        })
                        .dispose();

                Observable.<Socket>create(emitter -> {
                    emitter.onNext(socket);
                    var pos = App.game.spawn;
                    var entity = new ClientEntity(
                            new Rectangle(pos.x(), pos.y(), Options.charSize.w(), Options.charSize.h()),
                            Options.charDamage, 
                            Options.clientImage);
                    clients.put(socket.hashCode(), entity);
                    App.game.add_player(entity);
                })
                        .observeOn(Schedulers.io())
                        .subscribe(connections);
            }
        }
    }

    private void listenToSocket(Socket socket) {

        // Recieve data
        Observable.<SendPair>create(emitter -> {
            socketToPrinter(socket)
                    .subscribe(br -> {
                        while (!emitter.isDisposed()) {
                            Object obj = br.readObject();
                            if (obj == null || socket.isClosed()) {
                                emitter.onError(new ConnectError(socket));
                                break;
                            } else {
                                var in = (SendPair) obj;
                                emitter.onNext(in);
                            }
                        }
                    }, err -> {
                        System.err.printf("%s: Server: On getting: %s\n", err.getCause(), err.getMessage());

                        if (err instanceof EOFException) {
                            var ent = clients.remove(socket.hashCode());
                            App.game.remove_entity(ent);
                        }
                    });
        })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> disposables.put(socket.hashCode(), d))
                .doOnError(this::handleError)
                .doOnNext(obj -> {
                    if (obj.second() instanceof ClientAction) {
                        var act = (ClientAction) obj.second();
                        if (clients.get(socket.hashCode()) != null) {
                            clients.get(socket.hashCode()).do_action(act);
                        }
                    }
                })
                .subscribe(actionStream::onNext,
                        err -> System.err.printf("%s: Server: On passing on: %s\n", err.getCause(), err.getMessage()));

        // Send data back
        actionStream
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> actionDisposables.put(d.hashCode(), d))
                .withLatestFrom(socketToWriter(socket), (m, pw) -> {
                    pw.writeObject(m);
                    pw.flush();
                    return true;
                })
                .subscribe(
                        (e) -> {
                        },
                        err -> System.err.printf("%s: Server: On sending: %s\n", err.getCause(), err.getMessage()));
    }

    private void handleError(Throwable error) {
        if (error instanceof ConnectError) {
            Socket socket = ((ConnectError) error).getSocket();
            disposables.get(socket.hashCode()).dispose();
            disposables.remove(socket.hashCode());
            actionDisposables.get(socket.hashCode()).dispose();
            actionDisposables.remove(socket.hashCode());
        }
    }

    private Observable<ObjectOutputStream> socketToWriter(Socket socket) {
        return Observable.just(socket)
                .map(Socket::getOutputStream)
                .map(ObjectOutputStream::new);
    }

    private Observable<ObjectInputStream> socketToPrinter(Socket socket) {
        return Observable.just(socket)
                .map(Socket::getInputStream)
                .map(ObjectInputStream::new);
    }

    public void shutdown() {
        acceptsConnections = false;
        panelDisposable.dispose();
        disposable.dispose();
        con_disposable.dispose();
        Schedulers.shutdown();
    }

    @Override
    public String name() {
        return "Server";
    }
}

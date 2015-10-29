package com.github.niqdev.openwebnet.rx;

import com.github.niqdev.openwebnet.domain.OpenConfig;
import com.github.niqdev.openwebnet.domain.OpenFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 */
public class OpenWebNetObservable {

    private static final Logger log = LoggerFactory.getLogger(OpenWebNetObservable.class);

    private static Observable<AsynchronousSocketChannel> connect(OpenConfig config) {
        /*
        return Async.start(() -> {
            try {
                AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
                client.connect(new InetSocketAddress(config.host(), config.port()));
                return client;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        */

        Observable.OnSubscribe<AsynchronousSocketChannel> onSubscribe = subscriber -> {
            try {
                AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
                client.connect(new InetSocketAddress(config.host(), config.port())).get();

                subscriber.onNext(client);
                subscriber.onCompleted();
            } catch (IOException | InterruptedException | ExecutionException e) {
                subscriber.onError(e);
            }
        };
        return Observable.create(onSubscribe);//.subscribeOn(Schedulers.io());
    }

    //List<OpenFrame>
    public static Observable<OpenFrame> send(OpenConfig config, OpenFrame frame) {
        return connect(config)
                //.observeOn(Schedulers.newThread())
                .flatMap(client -> {
                    return handshake(client);
                })
                .flatMap(client -> {
                    return send(client, frame.value());
                })
                .map(s -> {
                    return new OpenFrame() {
                        @Override
                        public String value() {
                            return s;
                        }
                    };
                });
    }

    private static Observable<AsynchronousSocketChannel> handshake(AsynchronousSocketChannel client) {
        try {
            Thread.sleep(3000);
            read(client);
            write(client, "-> " + new Date());
            read(client);
            log.debug("after handshake: " + Thread.currentThread().getName());
            return Observable.just(client);
        } catch (ExecutionException | InterruptedException e) {
            return Observable.error(e);
        }
    }

    private static Observable<String> send(AsynchronousSocketChannel client, String value) {
        return Observable.defer(() -> {
            try {
                Thread.sleep(3000);
                log.debug("after send " + Thread.currentThread().getName());
                return Observable.just("COMMAND");
            } catch (InterruptedException e) {
                return Observable.error(e);
            }
        });
    }

    // TODO
    private static void read(AsynchronousSocketChannel client) throws ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Future<Integer> read = client.read(buffer);

        Integer count = read.get();
        String message = new String(buffer.array()).trim();
        System.out.println("READ: " + count + "|" + message);
        //buffer.flip();
        //buffer.clear();
    }

    // TODO
    private static void write(AsynchronousSocketChannel client, String value) throws ExecutionException, InterruptedException {
        byte [] message = new String(value).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        Future<Integer> write = client.write(buffer);
        Integer count = write.get();
        System.out.println("WRITE: " + count + "|" + value);
    }

}

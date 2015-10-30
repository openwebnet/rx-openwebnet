package com.github.niqdev.openwebnet.rx;

import com.github.niqdev.openwebnet.domain.OpenConfig;
import com.github.niqdev.openwebnet.domain.OpenConstant;
import com.github.niqdev.openwebnet.domain.OpenContext;
import com.github.niqdev.openwebnet.domain.OpenFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.github.niqdev.openwebnet.domain.OpenConstant.CHANNEL_COMMAND;

/**
 *
 */
public class OpenWebNetObservable {

    private static final Logger log = LoggerFactory.getLogger(OpenWebNetObservable.class);

    private static Observable<OpenContext> connect(OpenConfig config) {
        Observable.OnSubscribe<OpenContext> onSubscribe = subscriber -> {
            try {
                AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
                client.connect(new InetSocketAddress(config.getHost(), config.getPort())).get();

                subscriber.onNext(new OpenContext(client));
                subscriber.onCompleted();
            } catch (IOException | InterruptedException | ExecutionException e) {
                subscriber.onError(e);
            }
        };
        return Observable.create(onSubscribe);
    }

    //List<OpenFrame>
    public static Observable<OpenFrame> send(OpenConfig config, OpenFrame frame, OpenConstant channel) {
        return connect(config)
            .flatMap(client -> {
                return handshake(client, channel);
            })
            .flatMap(client -> {
                return send(client, frame.val());
            })
            .map(value -> {
                return new OpenFrame(value);
            });
    }

    /**
     *
     */
    public static Observable<OpenFrame> rawCommand(OpenConfig config, String value) {
        return OpenWebNetObservable.send(config, new OpenFrame(value), CHANNEL_COMMAND).observeOn(Schedulers.io());
    }

    // TODO expected ACK
    private static Observable<OpenContext> handshake(OpenContext context, OpenConstant channel) {
        return Observable.defer(() -> {
            try {
                read(context);
                write(context, channel.val());
                read(context);

                return Observable.just(context);
            } catch (ExecutionException | InterruptedException e) {
                return Observable.error(e);
            }
        });
    }

    private static Observable<String> send(OpenContext context, String value) {
        return Observable.defer(() -> {
            try {
                write(context, value);
                String response = read(context);

                return Observable.just(response);
            } catch (ExecutionException | InterruptedException e) {
                return Observable.error(e);
            }
        });
    }

    private static String read(OpenContext context) throws ExecutionException, InterruptedException {
        ByteBuffer buffer = context.getEmptyBuffer();
        Future<Integer> read = context.getClient().read(buffer);
        // blocking
        Integer count = read.get();
        String message = new String(buffer.array()).trim();
        log.debug("READ {0}|{1}", count, message);
        return message;
    }

    private static void write(OpenContext context, String value) throws ExecutionException, InterruptedException {
        byte[] message = new String(value).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        Future<Integer> write = context.getClient().write(buffer);
        // blocking
        Integer count = write.get();
        log.debug("WRITE {}|{}", count, value);
    }

}

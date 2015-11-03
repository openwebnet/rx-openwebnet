package com.github.niqdev.openwebnet.rx;

import com.github.niqdev.openwebnet.domain.OpenConfig;
import com.github.niqdev.openwebnet.domain.OpenConstant;
import com.github.niqdev.openwebnet.domain.OpenContext;
import com.github.niqdev.openwebnet.domain.OpenFrame;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.github.niqdev.openwebnet.domain.OpenConstant.*;

/**
 *
 */
public class OpenWebNetObservable {

    private static final Logger log = LoggerFactory.getLogger(OpenWebNetObservable.class);

    /*
     * On Android {@link java.nio.channels.AsynchronousSocketChannel}
     * throws java.lang.ClassNotFoundException.
     * So use {@link java.nio.channels.SocketChannel}
     */
    private static Observable<OpenContext> connect(OpenConfig config) {
        Observable.OnSubscribe<OpenContext> onSubscribe = subscriber -> {
            try {
                SocketChannel client = SocketChannel.open();
                client.connect(new InetSocketAddress(config.getHost(), config.getPort()));

                subscriber.onNext(new OpenContext(client));
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        };
        return Observable.create(onSubscribe);
    }

    //List<OpenFrame>
    private static Observable<OpenFrame> send(OpenConfig config, OpenFrame frame, OpenConstant channel) {
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

    /*
     * TODO List<OpenFrame>
     */
    public static Observable<OpenFrame> rawCommand(OpenConfig config, String command) {
        return OpenWebNetObservable.send(config, new OpenFrame(command), CHANNEL_COMMAND);
    }

    private static Observable<OpenContext> handshake(OpenContext context, OpenConstant channel) {
        return Observable.defer(() -> {
            try {
                readAck(context);
                write(context, channel.val());
                readAck(context);

                return Observable.just(context);
            } catch (IOException e) {
                return Observable.error(e);
            }
        });
    }

    private static Observable<String> send(OpenContext context, String value) {
        return Observable.defer(() -> {
            try {
                write(context, Strings.nullToEmpty(value));
                String response = read(context);

                return Observable.just(response);
            } catch (IOException e) {
                return Observable.error(e);
            }
        });
    }

    private static String read(OpenContext context) throws IOException {
        ByteBuffer buffer = context.getEmptyBuffer();
        Integer count = context.getClient().read(buffer);
        String message = new String(buffer.array()).trim();
        log.debug("READ {}|{}", count, message);
        return message;
    }

    private static void readAck(OpenContext context) throws IOException {
        String expectedAck = read(context);
        Preconditions.checkArgument(expectedAck.equals(ACK.val()));
    }

    private static void write(OpenContext context, String value) throws IOException {
        byte[] message = new String(value).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        Integer count = context.getClient().write(buffer);
        log.debug("WRITE {}|{}", count, value);
    }

}

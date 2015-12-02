package com.github.niqdev.openwebnet.rx;

import com.github.niqdev.openwebnet.domain.*;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import rx.Observable;
import rx.Scheduler;
import rx.Statement;
import rx.functions.Func1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.niqdev.openwebnet.domain.OpenConstant.ACK;
import static com.github.niqdev.openwebnet.domain.OpenConstant.FRAME_END;

/**
 * @author niqdev
 */
public class OpenWebNetObservable {

    // no instance
    private OpenWebNetObservable(){}

    private static boolean DEBUG = true;

    /*
     * Notice that org.slf4j.Logger is blocking (sync)
     * Unable to use <code>Logger log = LoggerFactory.getLogger(OpenWebNetObservable.class)</code>
     */
    public static void logDebug(String value) {
        if (DEBUG) System.out.println(String.format("[%s] %s", Thread.currentThread().getName(), value));
    }

    /**
     * Start a new command session and execute an action.
     *
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>{@code rawAction} does not operate by default on a particular {@link Scheduler}.</dd>
     * </dl>
     *
     * @param host of the domotic system
     * @param port of the domotic system
     * @param action frame to execute
     *
     * @return {@link OpenSession}
     */
    public static Observable<OpenSession> rawAction(String host, int port, String action) {
        //return sendFrame(new OpenConfig(host, port), CHANNEL_COMMAND, new OpenFrame(action));
        throw new UnsupportedOperationException("refactoring");
    }

    private static Observable<List<OpenFrame>> sendFrame(OpenConfig config, OpenConstant channel, OpenFrame request) {
        return connect(config).flatMap(handshake(channel)).flatMap(send(request));
    }

    /*
     * On Android {@link java.nio.channels.AsynchronousSocketChannel}
     * throws java.lang.ClassNotFoundException.
     * So use {@link java.nio.channels.SocketChannel}
     */
    // TODO handle unsubscribe/close socket
    static Observable<OpenContext> connect(OpenConfig config) {
        return Observable.defer(() -> {
            try {
                logDebug("CONNECT-before");

                SocketChannel client = SocketChannel.open();
                client.connect(new InetSocketAddress(config.getHost(), config.getPort()));

                logDebug("CONNECT-after");
                return Observable.just(new OpenContext(client));
            } catch (IOException e) {
                return Observable.error(e);
            }
        })
        .timeout(5, TimeUnit.SECONDS)
        .doOnError(throwable -> {
            logDebug("ERROR-doOnError " + throwable);
        });
    }

    static Func1<OpenContext, Observable<OpenContext>> handshake(OpenConstant channel) {
        return context -> {
            return Observable.just(context)
                .flatMap(read())
                .flatMap(expectedAck(context))
                .flatMap(write(channel.val()))
                .flatMap(read())
                .flatMap(expectedAck(context));
        };
    }

    static Func1<OpenContext, Observable<List<OpenFrame>>> send(OpenFrame request) {
        return context -> {
            return Observable.just(context)
                .flatMap(write(request.getValue()))
                .flatMap(read())
                .flatMap(parseFrames())
                // TODO
                .finallyDo(() -> {
                    try {
                        // move in unsubscribe + clear buffer
                        logDebug("FINALLY-close");
                        context.getClient().close();
                    } catch (IOException e) {
                        throw new IllegalStateException("error while closing connection");
                    }
                });
        };
    }

    static Func1<OpenContext, Observable<String>> read() {
        return context -> {
            try {
                ByteBuffer buffer = context.getEmptyBuffer();
                Integer count = context.getClient().read(buffer);
                String message = new String(buffer.array()).trim();

                logDebug("READ: " + count + "|" + message);
                return Observable.just(message);
            } catch (IOException e) {
                return Observable.error(e);
            }
        };
    }

    static Func1<String, Observable<OpenContext>> expectedAck(OpenContext context) {
        return s -> {
            return Statement.ifThen(
                () -> { return s.equals(ACK.val()); },
                Observable.just(context),
                Observable.error(new Exception("expected ACK"))
            );
        };
    }

    static Func1<OpenContext, Observable<OpenContext>> write(String value) {
        return context -> {
            try {
                byte[] message = new String(value).getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(message);
                Integer count = context.getClient().write(buffer);

                logDebug("WRITE " + count + "|" + value);
                return Observable.just(context);
            } catch (IOException e) {
                return Observable.error(e);
            }
        };
    }

    static Func1<String, Observable<List<OpenFrame>>> parseFrames() {
        return frames -> {
            ImmutableList<OpenFrame> frameList =
                FluentIterable
                    .from(Splitter.on(FRAME_END.val())
                            .trimResults()
                            .omitEmptyStrings()
                            .split(frames))
                    .transform(value -> {
                        return value.concat(FRAME_END.val());
                    })
                    .transform(frame -> {
                        return new OpenFrame(frame);
                    }).toList();

            logDebug("PARSE " + frameList);
            return Observable.just(Lists.newArrayList(frameList));
        };
    }

}

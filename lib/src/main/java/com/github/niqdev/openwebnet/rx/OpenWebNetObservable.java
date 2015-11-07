package com.github.niqdev.openwebnet.rx;

import com.github.niqdev.openwebnet.domain.OpenConfig;
import com.github.niqdev.openwebnet.domain.OpenConstant;
import com.github.niqdev.openwebnet.domain.OpenContext;
import com.github.niqdev.openwebnet.domain.OpenFrame;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import rx.Observable;
import rx.Statement;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.niqdev.openwebnet.domain.OpenConstant.*;

/**
 * @author niqdev
 */
public class OpenWebNetObservable {

    // no instance
    private OpenWebNetObservable(){}

    /*
     * Using Schedulers.io() RxNewThreadScheduler/RxCachedThreadScheduler
     * often the job is interrupted and thread is killed, moreover debugging doesn't work.
     */
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static boolean DEBUG = true;

    /*
     * Notice that org.slf4j.Logger is blocking (sync)
     * Unable to use <code>Logger log = LoggerFactory.getLogger(OpenWebNetObservable.class)</code>
     */
    public static void logDebug(String value) {
        if (DEBUG) System.out.println(String.format("[%s] %s", Thread.currentThread().getName(), value));
    }

    /**
     * Sends an OpenWebNet command frame.
     *
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>{@code rawCommand} by default operate on a new thread</dd>
     * </dl>
     *
     * @param host of the domotic system
     * @param port of the domotic system
     * @param command frame to execute
     *
     * @return list of {@link OpenFrame}
     */
    public static Observable<List<OpenFrame>> rawCommand(String host, int port, String command) {
        return sendFrame(new OpenConfig(host, port), CHANNEL_COMMAND, new OpenFrame(command));
    }

    private static Observable<List<OpenFrame>> sendFrame(OpenConfig config, OpenConstant channel, OpenFrame frame) {
        return connectAsync(config).flatMap(handshake(channel)).flatMap(send(frame));
    }

    /*
     * On Android {@link java.nio.channels.AsynchronousSocketChannel}
     * throws java.lang.ClassNotFoundException.
     * So use {@link java.nio.channels.SocketChannel}
     */
    // TODO handle unsubscribe
    private static Observable<OpenContext> connectAsync(OpenConfig config) {
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
        .subscribeOn(Schedulers.from(executor))
        .timeout(5, TimeUnit.SECONDS)
        .doOnError(throwable -> {
            logDebug("ERROR-doOnError " + throwable);
        })
        .finallyDo(() -> {
            executor.shutdown();
            // TODO unsubscribe
        });
    }

    private static Func1<OpenContext, Observable<OpenContext>> handshake(OpenConstant channel) {
        return context -> {
            return Observable.just(context)
                .flatMap(read())
                .flatMap(expectedAck(context))
                .flatMap(write(channel.val()))
                .flatMap(read())
                .flatMap(expectedAck(context));
        };
    }

    private static Func1<OpenContext, Observable<List<OpenFrame>>> send(OpenFrame frame) {
        return context -> {
            return Observable.just(context)
                .flatMap(write(frame.getValue()))
                .flatMap(read())
                .flatMap(parseFrames());
        };
    }

    private static Func1<OpenContext, Observable<String>> read() {
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

    private static Func1<String, Observable<OpenContext>> expectedAck(OpenContext context) {
        return s -> {
            return Statement.ifThen(
                () -> { return s.equals(ACK.val()); },
                Observable.just(context),
                Observable.error(new Exception("expected ACK"))
            );
        };
    }

    private static Func1<OpenContext, Observable<OpenContext>> write(String value) {
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

package com.github.niqdev.openwebnet.rx;

import com.github.niqdev.openwebnet.domain.OpenConfig;
import com.github.niqdev.openwebnet.domain.OpenConstant;
import com.github.niqdev.openwebnet.domain.OpenContext;
import com.github.niqdev.openwebnet.domain.OpenFrame;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import rx.Observable;
import rx.Scheduler;
import rx.Statement;
import rx.functions.*;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static com.github.niqdev.openwebnet.domain.OpenConstant.CHANNEL_COMMAND;
import static com.github.niqdev.openwebnet.domain.OpenConstant.FRAME_END;

/**
 * @author niqdev
 */
public class OpenWebNetObservable {

    // no instance
    private OpenWebNetObservable(){}

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Return a {@link Scheduler}.
     *
     * Using Schedulers.io() RxNewThreadScheduler/RxCachedThreadScheduler
     * often the job is interrupted and thread is killed, moreover debugging doesn't work.
     *
     * @return Scheduler
     */
    public static Scheduler scheduler() {
        return Schedulers.from(executor);
    }

    /*
     * Notice that org.slf4j.Logger is blocking (sync)!
     * Unable to use <code>Logger log = LoggerFactory.getLogger(OpenWebNetObservable.class)</code>
     */
    private static void logThreadInfo(String info) {
        System.out.println(info + " " + Thread.currentThread().getName());
    }

    private static Observable<String> connect1(String a) {
        /* OK works async
        Observable.OnSubscribe<String> onSubscribe = subscriber -> {
            System.out.println("CREATE " + Thread.currentThread().getName());

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            subscriber.onNext(a);
            //subscriber.onError(new Exception("ERROR"));
            subscriber.onCompleted();
            //subscriber.unsubscribe();
        };
        return Observable.create(onSubscribe);
        */

        return Observable.defer(Async.toAsync(() -> {
            try {
                logThreadInfo("CONNECT-before");

                SocketChannel client = SocketChannel.open();
                client.connect(new InetSocketAddress("", 2));

                logThreadInfo("CONNECT-after");
                return client;
            } catch (IOException e) {
                return Observable.error(e);
            }
        })).map(client -> {
            return "#connected";
        });
    }

    public static Observable<String> exampleFlowAsyncClass() {
        System.out.println("sub-before: " + Thread.currentThread().getName());

        Observable<String> observable = connect1("#1")
            .subscribeOn(Schedulers.from(executor))
            //.subscribeOn(Schedulers.io())
            .flatMap(s -> {
                System.out.println("HANDSHAKE " + Thread.currentThread().getName() + "|" + s);
                return Observable.just("#2");
            })
            .flatMap(s -> {
                System.out.println("SEND " + Thread.currentThread().getName() + "|" + s);

                // TODO
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                executor.shutdown();
                return Observable.just("#3");
            });

        System.out.println("sub-after: " + Thread.currentThread().getName());
        return observable;
    }

    /*
     * TODO
     * Returns an Observable which sends an OpenWebNet frame, within a timeout of 5 seconds.
     *
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>{@code rawCommand} does not operate by default on a particular {@link Scheduler}.</dd>
     * </dl>
     *
     * @param config <i>HOST</i> and <i>PORT</i> of domotic system
     * @param command command to execute
     *
     * @return list of {@link OpenFrame}
     */
    public static Observable<List<OpenFrame>> rawCommand(String host, int port, String command) {
        return send(new OpenConfig(host, port), new OpenFrame(command), CHANNEL_COMMAND);
    }

    // TODO
    private static Observable<List<OpenFrame>> send(OpenConfig config, OpenFrame frame, OpenConstant channel) {
        return connect(config)
            //.timeout(5, TimeUnit.SECONDS)
            .flatMap(context -> {
                System.out.println("HANDSHAKE-0 " + Thread.currentThread().getName());
                //return handshake(context, channel);
                return Observable.just(context);
            })
            .flatMap(context -> {
                System.out.println("SEND-0 " + Thread.currentThread().getName());
                //return send(context, frame.getValue());
                return Observable.just("xxx##");
            })
            //.map(parseFrames());
            .map(s -> {
                System.out.println("PARSE-0 " + Thread.currentThread().getName());
                return Arrays.asList(new OpenFrame("aaa"), new OpenFrame("bbb"));
            })
            .finallyDo(() -> {
                executor.shutdown();
            });
    }

    /*
     * On Android {@link java.nio.channels.AsynchronousSocketChannel}
     * throws java.lang.ClassNotFoundException.
     * So use {@link java.nio.channels.SocketChannel}
     */
    // TODO handle unsubscribe
    // TODO handle error
    private static Observable<OpenContext> connect(OpenConfig config) {
        return Observable.defer(Async.toAsync(() -> {
            try {
                logThreadInfo("CONNECT-before");

                SocketChannel client = SocketChannel.open();
                client.connect(new InetSocketAddress(config.getHost(), config.getPort()));

                logThreadInfo("CONNECT-after");
                return new OpenContext(client);
            } catch (IOException e) {
                System.out.println("CONNECTION ERROR");
                // TODO
                throw new RuntimeException(e);
                //return Observable.error(e);
            }
        }));
    }

    private static Observable<OpenContext> handshake(OpenContext context, OpenConstant channel) {
        return Async.fromCallable(() -> {
            System.out.println("HANDSHAKE " + Thread.currentThread().getName());

            readAck(context);
            write(context, channel.val());
            readAck(context);
            return context;
        });
    }

    private static Observable<String> send(OpenContext context, String value) {
        return Async.fromCallable(() -> {
            System.out.println("SEND " + Thread.currentThread().getName());

            write(context, Strings.nullToEmpty(value));
            return read(context);
        });
    }

    private static String read(OpenContext context) throws IOException {
        ByteBuffer buffer = context.getEmptyBuffer();
        Integer count = context.getClient().read(buffer);
        String message = new String(buffer.array()).trim();
        System.out.println("READ " + count + "|" + message + "|" + Thread.currentThread().getName());
        return message;
    }

    // TODO
    private static void readAck(OpenContext context) throws IOException {
        String expectedAck = read(context);
        //Preconditions.checkArgument(expectedAck.equals(ACK.val()));
    }

    private static void write(OpenContext context, String value) throws IOException {
        byte[] message = new String(value).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        Integer count = context.getClient().write(buffer);
        System.out.println("WRITE " + count + "|" + value + "|" + Thread.currentThread().getName());
    }

    static Func1<String, List<OpenFrame>> parseFrames() {
        return frames -> {
            return FluentIterable
                .from(Splitter.on(FRAME_END.val())
                    .trimResults()
                    .omitEmptyStrings()
                    .split(frames))
                .transform(value -> { return value.concat(FRAME_END.val()); })
                .transform(frame -> {
                    return new OpenFrame(frame);
                }).toList();
        };
    }

}

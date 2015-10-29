package com.github.niqdev.openwebnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author niqdev
 */
public class SimpleClient {

    private static final Logger log = LoggerFactory.getLogger(SimpleClient.class);

    public static void main(String[] args) {

        //new SimpleClient().sendOld(args[0]);
        new SimpleClient().send(args[0]);

    }

    private void sendOld(String value) {
        log.debug("send: {0}", value);

        SocketClient client = new OpenWebNetClient(SocketClient.LOCALHOST, 20000);
        try {
            client.send(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // observer chain of future
    /*
    2 stream: read/write in chain

    read null
    read ACK
    send commandType
    read ACK
    send command
    read success/error
    */
    private void send(String value) {

        // TODO AsynchronousChannelGroup
        try {
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

            Func0<Future<Void>> connect = new Func0<Future<Void>>() {
                @Override
                public Future<Void> call() {
                    return client.connect(new InetSocketAddress("localhost", 20000));
                }
            };

            Async.startFuture(connect);

            ByteBuffer buffer = ByteBuffer.allocate(32);

//            Observable
//                    .from(client.connect(new InetSocketAddress("localhost", 20000)))
//                    .flatMap(Async.deferFuture(new Func1<String, Void>())
//                    .flatMap(Async.deferFuture(client.read(buffer)))
//
////            Observable.
//
//            // why get() null?

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void gos()
            throws IOException, InterruptedException, ExecutionException {

        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 3883);
        Future future = client.connect(hostAddress);
        future.get(); // returns null

        System.out.println("Client is started: " + client.isOpen());
        System.out.println("Sending messages to server: ");

        String [] messages = new String [] {"Time goes fast.", "What now?", "Bye."};

        for (int i = 0; i < messages.length; i++) {

            byte [] message = new String(messages [i]).getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            Future result = client.write(buffer);

            while (! result.isDone()) {
                System.out.println("... ");
            }

            System.out.println(messages [i]);
            buffer.clear();
            Thread.sleep(3000);
        } // for

        client.close();
    }

    private void example() {

        Scheduler scheduler = Schedulers.io();

        int numberOfThreads = 10;
        List<Observable<Double>> forks = new ArrayList<Observable<Double>>();

        for (int i = 0; i < numberOfThreads; i++) {
            final int finalI = i;
            Observable<Double> asyncTask = Async.start(new Func0<Double>() {
                @Override
                public Double call() {
                    Double aDouble = Double.valueOf(finalI);
//                    System.out.println("aDouble = " + aDouble);

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return aDouble;
                }
            }, scheduler);
            forks.add(asyncTask);
        }
    }
}

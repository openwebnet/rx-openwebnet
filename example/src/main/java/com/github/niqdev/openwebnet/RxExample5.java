package com.github.niqdev.openwebnet;

import rx.Observable;
import rx.Subscriber;
import rx.internal.operators.OnSubscribeDefer;

import java.beans.Statement;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 read null
 read ACK
 send commandType
 read ACK
 send command
 read success/error
 */
public class RxExample5 {

    public static void main(String[] args) {

        // TODO OnSubscribeDefer

        send("*1*1*21##").subscribe(s -> System.out.println(s));

    }

    private static Observable<String> send(String value) {

//        Observable
//                .from(client.connect(new InetSocketAddress("localhost", 20000)))
//                .flatMap(rx.Statement.ifThen())
//                .doOnError()



        Observable.OnSubscribe<String> onSubscribe = subscriber -> {
            try {
                AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
                client.connect(new InetSocketAddress("192.168.1.41", 20000));
                ByteBuffer buffer = ByteBuffer.allocate(1024);

//                Observable.from(client.read(buffer))
//                        .flatMap(readedInt -> {
//                            Observable.from(client.write())
//                        })
//                connection.get();

                read(client); // expect *#*1##
                write(client, "*99*0##"); // ?? 0 o 9
                read(client); // expect *#*1##

                write(client, value);
                read(client);

                subscriber.onNext("TODO");

                subscriber.onCompleted();
            } catch (IOException | InterruptedException | ExecutionException e) {
                subscriber.onError(e);
            }
        };

        return Observable.create(onSubscribe);
    }

    private static void read(AsynchronousSocketChannel client) throws ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Future<Integer> read = client.read(buffer);

        Integer count = read.get();
        String message = new String(buffer.array()).trim();
        System.out.println("READ: " + count + "|" + message);
        //buffer.flip();
        //buffer.clear();
    }

    private static void write(AsynchronousSocketChannel client, String value) throws ExecutionException, InterruptedException {
        byte [] message = new String(value).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        Future<Integer> write = client.write(buffer);
        Integer count = write.get();
        System.out.println("WRITE: " + count + "|" + value);
    }

}

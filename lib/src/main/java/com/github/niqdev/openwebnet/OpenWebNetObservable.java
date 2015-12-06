package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.domain.OpenContext;
import com.github.niqdev.openwebnet.message.OpenMessage;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import rx.Observable;
import rx.Statement;
import rx.functions.Func1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static com.github.niqdev.openwebnet.OpenSession.Channel;
import static com.github.niqdev.openwebnet.message.OpenMessage.ACK;
import static com.github.niqdev.openwebnet.message.OpenMessage.FRAME_END;

/**
 * @author niqdev
 */
public class OpenWebNetObservable {

    // no instance
    private OpenWebNetObservable(){}

    static Func1<OpenContext, Observable<OpenContext>> handshake(Channel channel) {
        return context -> {
            return Observable.just(context)
                .flatMap(read())
                .flatMap(expectedAck(context))
                .flatMap(write(channel.value()))
                .flatMap(read())
                .flatMap(expectedAck(context));
        };
    }

    static Func1<OpenContext, Observable<List<OpenMessage>>> send(OpenMessage request) {
        return context -> {
            return Observable.just(context)
                .flatMap(write(request.getValue()))
                .flatMap(read())
                .flatMap(parseMessages())
                // TODO
                .finallyDo(() -> {
                    try {
                        // move in unsubscribe + clear buffer
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
                return Observable.just(message);
            } catch (IOException e) {
                return Observable.error(e);
            }
        };
    }

    static Func1<String, Observable<OpenContext>> expectedAck(OpenContext context) {
        return s -> {
            return Statement.ifThen(
                () -> { return s.equals(ACK); },
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
                return Observable.just(context);
            } catch (IOException e) {
                return Observable.error(e);
            }
        };
    }

    static Func1<String, Observable<List<OpenMessage>>> parseMessages() {
        return messages -> {
            ImmutableList<OpenMessage> messageList =
                FluentIterable
                    .from(Splitter.on(FRAME_END)
                        .trimResults()
                        .omitEmptyStrings()
                        .split(messages))
                    .transform(value -> value.concat(FRAME_END))
                    .transform(message -> (OpenMessage)() -> message)
                    .toList();
            return Observable.just(Lists.newArrayList(messageList));
        };
    }

}

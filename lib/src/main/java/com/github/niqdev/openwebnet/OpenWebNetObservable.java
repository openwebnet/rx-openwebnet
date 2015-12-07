package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.message.OpenMessage;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import rx.Observable;
import rx.Statement;
import rx.functions.Action1;
import rx.functions.Func1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static com.github.niqdev.openwebnet.message.OpenMessage.ACK;
import static com.github.niqdev.openwebnet.message.OpenMessage.FRAME_END;

/**
 * @author niqdev
 */
public class OpenWebNetObservable {

    // no instance
    private OpenWebNetObservable(){}

    static Func1<OpenSession, Observable<OpenSession>> doHandshake() {
        return session -> Observable.just(session)
            .flatMap(read())
            .flatMap(expectedAck(session))
            .flatMap(write(session.getChannel().value()))
            .flatMap(read())
            .flatMap(expectedAck(session));
    }

    static Func1<OpenSession, Observable<OpenSession>> doRequest() {
        return session -> Observable.just(session)
            .flatMap(write(session.getRequest().getValue()))
            .flatMap(read())
            .flatMap(parseMessages(session));
    }

    static Func1<OpenSession, Observable<String>> read() {
        return session -> {
            try {
                ByteBuffer buffer = session.getEmptyBuffer();
                Integer count = session.getClient().read(buffer);
                String message = new String(buffer.array()).trim();
                return Observable.just(message);
            } catch (IOException e) {
                return Observable.error(e);
            }
        };
    }

    static Func1<String, Observable<OpenSession>> expectedAck(OpenSession session) {
        return s -> Statement.ifThen(
            () -> s.equals(ACK),
            Observable.just(session),
            Observable.error(new IllegalStateException("expected ACK")));
    }

    static Func1<OpenSession, Observable<OpenSession>> write(String value) {
        return session -> {
            try {
                byte[] message = new String(value).getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(message);
                Integer count = session.getClient().write(buffer);
                return Observable.just(session);
            } catch (IOException e) {
                return Observable.error(e);
            }
        };
    }

    static Func1<String, Observable<OpenSession>> parseMessages(OpenSession session) {
        return messages -> {
            FluentIterable
                .from(Splitter.on(FRAME_END)
                    .trimResults()
                    .omitEmptyStrings()
                    .split(messages))
                .transform(value -> value.concat(FRAME_END))
                .transform(message -> (OpenMessage) () -> message)
                .forEach(message -> session.addResponse(message));
            return Observable.just(session);
        };
    }

}

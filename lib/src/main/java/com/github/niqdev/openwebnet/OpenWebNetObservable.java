package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.message.OpenMessage;
import com.github.niqdev.openwebnet.message.ResponseOpenMessage;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import rx.Observable;
import rx.Statement;
import rx.functions.Func1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.niqdev.openwebnet.OpenSession.newSession;
import static com.github.niqdev.openwebnet.OpenWebNet.Channel;
import static com.github.niqdev.openwebnet.OpenWebNet.OpenGateway;
import static com.github.niqdev.openwebnet.message.OpenMessage.ACK;
import static com.github.niqdev.openwebnet.message.OpenMessage.FRAME_END;

/**
 * OpenWebNet observable helper class.
 *
 * @author niqdev
 */
class OpenWebNetObservable {

    private static void log(String message) {
        System.out.println(String.format("[%s] - %s", Thread.currentThread().getName(), message));
    }

    // no instance
    private OpenWebNetObservable() {}

    static Observable<OpenContext> connect(OpenGateway gateway) {
        return Observable.defer(() -> {
            try {
                OpenContext context = OpenContext.setup(gateway);
                context.connect();
                log("connected!");
                return Observable.just(context);
            } catch (IOException e) {
                return Observable.error(e);
            }
        })
        .doAfterTerminate(() -> {
            // TODO how retrieve context to handle unsubscribe/close socket?
            //context.disconnect();
        })
        .timeout(5, TimeUnit.SECONDS)
        .doOnError(throwable -> {
            System.out.println("ERROR connect: " + throwable);
        });
    }

    static Func1<OpenContext, Observable<OpenContext>> doHandshake(Channel channel) {
        return context -> Observable.just(context)
            .flatMap(read())
            .flatMap(expectedAck(context))
            .flatMap(write(channel.value()))
            .flatMap(read())
            .flatMap(verifyCredential(context))
            .flatMap(expectedAck(context));
    }

    static Func1<OpenContext, Observable<List<OpenSession>>> doRequests(List<OpenMessage> requests) {
        return context -> Observable.just(requests)
            .flatMapIterable(messages -> messages)
            .flatMap(request -> Observable.just(context).flatMap(doRequest(request)))
            .reduce(new ArrayList<>(), (openSessions, session) -> {
                openSessions.add(session);
                return openSessions;
            });
    }

    static Func1<OpenContext, Observable<OpenSession>> doRequest(OpenMessage request) {
        return context -> Observable.just(context)
            .flatMap(write(request.getValue()))
            .flatMap(read())
            .flatMap(parseMessages())
            .map(response -> newSession(request).addAllResponse(response));
    }

    static Func1<OpenContext, Observable<String>> read() {
        return context -> {
            try {
                ByteBuffer buffer = context.getEmptyBuffer();
                Integer count = context.getClient().read(buffer);
                String message = new String(buffer.array()).trim();
                log(String.format("read: %d|%s", count, message));
                return Observable.just(message);
            } catch (IOException e) {
                return Observable.error(e);
            }
        };
    }

    static Func1<String, Observable<OpenContext>> expectedAck(OpenContext context) {
        return s -> Statement.ifThen(
            () -> s.equals(ACK),
            Observable.just(context),
            Observable.error(new IllegalStateException("expected ACK")));
    }

    static Func1<String, Observable<String>> verifyCredential(OpenContext context) {
        return s -> {
            // 'Statement.ifThen' throws exception because subscribes both to 'then' and 'else'
            if (!context.hasCredential()) {
                log("without credential");
                return Observable.just(s);
            }
            log(String.format("with credential: %s", context.getCredential()));
            return Observable.just(context)
                .flatMap(write(buildPasswordMessage(context.getCredential(), s)))
                .flatMap(read());
        };
    }

    static Func1<OpenContext, Observable<OpenContext>> write(String value) {
        return context -> {
            try {
                byte[] message = new String(value).getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(message);
                Integer count = context.getClient().write(buffer);
                log(String.format("write: %d|%s", count, value));
                return Observable.just(context);
            } catch (IOException e) {
                return Observable.error(e);
            }
        };
    }

    static Func1<String, Observable<List<OpenMessage>>> parseMessages() {
        return messages -> {
            ImmutableList<OpenMessage> response =
                FluentIterable
                    .from(Splitter.on(FRAME_END)
                        .trimResults()
                        .omitEmptyStrings()
                        .split(messages))
                    .transform(value -> value.concat(FRAME_END))
                    .transform(message -> (OpenMessage) new ResponseOpenMessage(message))
                    .toList();
            return Observable.just(response);
        };
    }

    static String buildPasswordMessage(String password, String nonce) {
        return String.format("*#%s##", hashPassword(password, nonce));
    }

    /*
     * @see reference issue https://github.com/openwebnet/openwebnet-android/issues/51
     *
     * @param password
     * @param nonce
     * @return hashed password
     */
    static String hashPassword(String password, String nonce) {
        long msr = 0x7FFFFFFFL;
        long m_1 = 0xFFFFFFFFL;
        long m_8 = 0xFFFFFFF8L;
        long m_16 = 0xFFFFFFF0L;
        long m_128 = 0xFFFFFF80L;
        long m_16777216 = 0XFF000000L;
        boolean length = true;
        boolean flag = true;
        long num1 = 0L;
        long num2 = 0L;

        for (char c : nonce.toCharArray()) {
            num1 = num1 & m_1;
            num2 = num2 & m_1;

            switch (c) {
                case '1':
                    length = !flag;
                    if (!length) {
                        num2 = Long.parseLong(password);
                    }
                    num1 = num2 & m_128;
                    num1 = num1 >> 1;
                    num1 = num1 & msr;
                    num1 = num1 >> 6;
                    num2 = num2 << 25;
                    num1 = num1 + num2;
                    flag = false;
                    break;
                case '2':
                    length = !flag;
                    if (!length) {
                        num2 = Long.parseLong(password);
                    }
                    num1 = num2 & m_16;
                    num1 = num1 >> 1;
                    num1 = num1 & msr;
                    num1 = num1 >> 3;
                    num2 = num2 << 28;
                    num1 = num1 + num2;
                    flag = false;
                    break;
                case '3':
                    length = !flag;
                    if (!length) {
                        num2 = Long.parseLong(password);
                    }
                    num1 = num2 & m_8;
                    num1 = num1 >> 1;
                    num1 = num1 & msr;
                    num1 = num1 >> 2;
                    num2 = num2 << 29;
                    num1 = num1 + num2;
                    flag = false;
                    break;
                case '4':
                    length = !flag;
                    if (!length) {
                        num2 = Long.parseLong(password);
                    }
                    num1 = num2 << 1;
                    num2 = num2 >> 1;
                    num2 = num2 & msr;
                    num2 = num2 >> 30;
                    num1 = num1 + num2;
                    flag = false;
                    break;
                case '5':
                    length = !flag;
                    if (!length) {
                        num2 = Long.parseLong(password);
                    }
                    num1 = num2 << 5;
                    num2 = num2 >> 1;
                    num2 = num2 & msr;
                    num2 = num2 >> 26;
                    num1 = num1 + num2;
                    flag = false;
                    break;
                case '6':
                    length = !flag;
                    if (!length) {
                        num2 = Long.parseLong(password);
                    }
                    num1 = num2 << 12;
                    num2 = num2 >> 1;
                    num2 = num2 & msr;
                    num2 = num2 >> 19;
                    num1 = num1 + num2;
                    flag = false;
                    break;
                case '7':
                    length = !flag;
                    if (!length) {
                        num2 = Long.parseLong(password);
                    }
                    num1 = num2 & 0xFF00;
                    num1 = num1 + ((num2 & 0xFF) << 24);
                    num1 = num1 + ((num2 & 0xFF0000) >> 16);
                    num2 = num2 & m_16777216;
                    num2 = num2 >> 1;
                    num2 = num2 & msr;
                    num2 = num2 >> 7;
                    num1 = num1 + num2;
                    flag = false;
                    break;
                case '8':
                    length = !flag;
                    if (!length) {
                        num2 = Long.parseLong(password);
                    }
                    num1 = num2 & 0xFFFF;
                    num1 = num1 << 16;
                    long numx = num2 >> 1;
                    numx = numx & msr;
                    numx = numx >> 23;
                    num1 = num1 + numx;
                    num2 = num2 & 0xFF0000;
                    num2 = num2 >> 1;
                    num2 = num2 & msr;
                    num2 = num2 >> 7;
                    num1 = num1 + num2;
                    flag = false;
                    break;
                case '9':
                    length = !flag;
                    if (!length) {
                        num2 = Long.parseLong(password);
                    }
                    num1 = ~num2;
                    flag = false;
                    break;
                default:
                    num1 = num2;
                    break;
            }
            num2 = num1;
        }

        return String.valueOf(num1 & m_1);
    }

}

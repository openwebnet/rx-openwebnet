package com.github.niqdev.openwebnet;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.niqdev.openwebnet.OpenWebNet.gateway;

public class OpenWebNetApp {

    @Parameter(names={"--host", "-h"}, required = true, description = "IP address or hostname of the Gateway")
    String host;

    @Parameter(names={"--port", "-p"}, description = "Port of the Gateway")
    int port = 20000;

    @Parameter(names={"--password"}, description = "Optional password of the Gateway")
    String password;

    @Parameter(names={"--frame", "-f"}, required = true, description = "Request frame")
    String frame;

    public static void main(String[] args) {
        OpenWebNetApp main = new OpenWebNetApp();

        try {
            JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);

            main.run();
        } catch (ParameterException e) {
            e.usage();
        }
    }

    private void run() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        OpenWebNet
            .newClient(gateway(host, port, password))
            .send(() -> frame)
            .subscribeOn(Schedulers.from(executor))
            .doAfterTerminate(() -> {
                executor.shutdown();
                System.exit(0);
            })
            .subscribe(System.out::println, throwable -> System.exit(1));
    }

}

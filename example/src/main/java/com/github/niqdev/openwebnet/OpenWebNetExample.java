package com.github.niqdev.openwebnet;

import com.github.niqdev.openwebnet.message.*;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.niqdev.openwebnet.OpenWebNet.defaultGateway;
import static com.github.niqdev.openwebnet.OpenWebNet.gateway;
import static java.util.Arrays.asList;

/**
 * ./gradlew runOpenWebNetExample
 */
public class OpenWebNetExample {

    private static final String LOCALHOST = "localhost";
    private static final String LOCALHOST_ANDROID = "10.0.2.2";
    private static final String HOST = "192.168.1.41";
    private static final String HOST_DOMAIN = "vpn.home.it";
    private static final String HOST_PWD = "192.168.1.35";
    private static final String HOST_HTTP = "http://192.168.1.41";
    private static final String PASSWORD = "12345";
    private static final int PORT = 20000;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        //example1();
        //example2();
        //exampleLightStatus();
        //exampleLightTurnOn();
        //exampleHeating();
        //exampleSoundSystem();
        //exampleSoundSystemStatus();
        exampleScenario();
        //exampleScenarioStatus();
    }

    private static void example1() {
        OpenWebNet
            .newClient(defaultGateway(LOCALHOST))
            .send(() -> "*#1*21##")
            .subscribe(System.out::println);
    }

    private static void example2() {
        System.out.println("before " + Thread.currentThread().getName());
        OpenWebNet
            .newClient(gateway(LOCALHOST, PORT))
            .send(asList(() -> "*#1*21##", () -> "*#1*22##"))
            .subscribeOn(Schedulers.from(executor))
            .doOnError(throwable -> System.out.println("ERROR " + throwable))
            .doAfterTerminate(() -> executor.shutdown())
            .subscribe(System.out::println, throwable -> {});
        System.out.println("after " + Thread.currentThread().getName());
    }

    private static void exampleLightStatus() {
        OpenWebNet
            .newClient(gateway(HOST_PWD, PORT, PASSWORD))
            //.newClient(gateway(HOST, PORT))
            .send(Lighting.requestStatus("21"))
            .map(Lighting.handleStatus(() -> System.out.println("ON"), () -> System.out.println("OFF")))
            .subscribe(System.out::println);
    }

    private static void exampleLightTurnOn() {
        OpenWebNet
            .newClient(gateway(HOST_PWD, PORT, PASSWORD))
            //.newClient(gateway(HOST, PORT))
            .send(Lighting.requestTurnOn("21"))
            .map(Lighting.handleResponse(() -> System.out.println("success"), () -> System.out.println("fail")))
            .subscribe(System.out::println);
    }

    private static void exampleAutomation() {
        OpenWebNet
            .newClient(defaultGateway(LOCALHOST))
            .send(Automation.requestMoveUp("WHERE"))
            .map(Automation.handleResponse(() -> System.out.println("success"), () -> System.out.println("fail")))
            .subscribe(System.out::println);
    }

    private static void exampleHeating() {
        OpenWebNet
            .newClient(gateway(HOST_PWD, PORT, PASSWORD))
            //.newClient(gateway(HOST_DOMAIN, PORT))
            .send(Heating.requestTemperature("4"))
            .map(Heating.handleTemperature(value -> System.out.println(value), () -> System.out.println("error")))
            .subscribe(System.out::println);
    }

    private static void exampleSoundSystem() {
        OpenWebNet
            .newClient(gateway(HOST, PORT))
            //.send(SoundSystem.requestTurnOff("0"))
            .send(SoundSystem.requestTurnOff("#5"))
            //.send(SoundSystem.requestTurnOn("0"))
            .map(SoundSystem.handleResponse(() -> System.out.println("success"), () -> System.out.println("fail")))
            .subscribe(System.out::println);
    }

    private static void exampleSoundSystemStatus() {
        OpenWebNet
            .newClient(gateway(HOST, PORT))
            //.send(SoundSystem.requestTurnOn("#5"))
            .send(SoundSystem.requestStatus("51"))
            .map(SoundSystem.handleStatus(() -> System.out.println("ON"), () -> System.out.println("OFF")))
            .subscribe(System.out::println);
    }

    // TODO http + scenario
    private static void exampleScenario() {
        OpenWebNet
            .newClient(gateway(HOST_HTTP, PORT))
            .send(Scene.requestStart("31", Scene.Version.MH200N))
            //.send(Scene.requestStop("31"))
            .map(SoundSystem.handleResponse(() -> System.out.println("START"), () -> System.out.println("STOP")))
            .subscribe(System.out::println);
    }

    private static void exampleScenarioStatus() {
        OpenWebNet
            .newClient(gateway(HOST_HTTP, PORT))
            .send(Scene.requestStatus("31"))
            .map(Scene.handleStatus(() -> System.out.println("START"), () -> System.out.println("STOP")))
            .subscribe(System.out::println);
    }

}

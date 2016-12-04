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
        //exampleScenario();
        //exampleScenarioStatus();
        //exampleEnergy();
        //exampleSoundSystemVolume();
        //exampleSoundSystemStation();
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
            .doAfterTerminate(executor::shutdown)
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
            .send(Lighting.requestTurnOn("21", Lighting.Type.POINT_TO_POINT))
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
            .map(Heating.handleTemperature(System.out::println, () -> System.out.println("error")))
            .subscribe(System.out::println);
    }

    private static void exampleSoundSystem() {
        OpenWebNet
            .newClient(gateway(HOST, PORT))
            // OK no status
            //.send(SoundSystem.requestTurnOff("0", SoundSystem.Type.AMPLIFIER_GENERAL, SoundSystem.Source.STEREO_CHANNEL))
            // OK
            //.send(SoundSystem.requestTurnOff("5", SoundSystem.Type.AMPLIFIER_GROUP, SoundSystem.Source.STEREO_CHANNEL))
            // 51, 52, 53
            //.send(SoundSystem.requestTurnOff("53", SoundSystem.Type.AMPLIFIER_P2P, SoundSystem.Source.STEREO_CHANNEL))
            // 101, 103
            .send(SoundSystem.requestTurnOn("103", SoundSystem.Type.SOURCE_P2P, SoundSystem.Source.STEREO_CHANNEL))
            // KO
            //.send(SoundSystem.requestTurnOn("100", SoundSystem.Type.SOURCE_GENERAL, SoundSystem.Source.STEREO_CHANNEL))
            .map(SoundSystem.handleResponse(() -> System.out.println("success"), () -> System.out.println("fail")))
            .subscribe(System.out::println);
    }

    private static void exampleSoundSystemStatus() {
        OpenWebNet
            .newClient(gateway(HOST, PORT))
            // always off general
            //.send(SoundSystem.requestStatus("0", SoundSystem.Type.AMPLIFIER_GENERAL))
            // OK
            //.send(SoundSystem.requestStatus("5", SoundSystem.Type.AMPLIFIER_GROUP))
            // OK
            //.send(SoundSystem.requestStatus("53", SoundSystem.Type.AMPLIFIER_P2P))
            // OK
            .send(SoundSystem.requestStatus("103", SoundSystem.Type.SOURCE_P2P))
            .map(SoundSystem.handleStatus(() -> System.out.println("ON"), () -> System.out.println("OFF")))
            .subscribe(System.out::println);
    }

    private static void exampleScenario() {
        OpenWebNet
            .newClient(gateway(HOST, PORT))
            .send(Scenario.requestStart("32", Scenario.Version.MH200N))
            //.send(Scenario.requestStop("31"))
            .map(Scenario.handleResponse(() -> System.out.println("START"), () -> System.out.println("STOP")))
            .subscribe(System.out::println);
    }

    private static void exampleScenarioStatus() {
        OpenWebNet
            .newClient(gateway(HOST, PORT))
            .send(Scenario.requestStatus("32"))
            .map(Scenario.handleStatus(
                () -> System.out.println("STARTED"),
                () -> System.out.println("STOPPED"),
                () -> System.out.println("ENABLED"),
                () -> System.out.println("DISABLED")))
            .subscribe(System.out::println);
    }

    private static void exampleEnergy() {
        EnergyManagement.Version ev = EnergyManagement.Version.MODEL_F523;
        OpenWebNet
            .newClient(gateway(HOST_PWD, PORT, PASSWORD))
            //.send(asList(() -> "*#18*51*113##", () -> "*#18*51*54##", () -> "*#18*51*53##"))
            .send(asList(
                EnergyManagement.requestInstantaneousPower("1", ev),
                EnergyManagement.requestDailyPower("1", ev),
                EnergyManagement.requestMonthlyPower("1", ev)))
            .map(EnergyManagement.handlePowers(System.out::println, () -> System.out.println("error")))
            .subscribe(System.out::println);
    }

    private static void exampleSoundSystemVolume() {
        OpenWebNet
            .newClient(gateway(HOST, PORT))
            .send(SoundSystem.requestVolumeUp("51", SoundSystem.Type.AMPLIFIER_P2P))
            .map(SoundSystem.handleResponse(() -> System.out.println("success"), () -> System.out.println("fail")))
            .subscribe(System.out::println);
    }

    private static void exampleSoundSystemStation() {
        OpenWebNet
            .newClient(gateway(HOST, PORT))
            .send(SoundSystem.requestStationUp("103", SoundSystem.Type.SOURCE_P2P))
            .map(SoundSystem.handleResponse(() -> System.out.println("success"), () -> System.out.println("fail")))
            .subscribe(System.out::println);
    }

}

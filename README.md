# rx-openwebnet

[![Build Status](https://travis-ci.org/openwebnet/rx-openwebnet.svg?branch=master)](https://travis-ci.org/openwebnet/rx-openwebnet)
[![Download](https://api.bintray.com/packages/niqdev/maven/rx-openwebnet/images/download.svg)](https://bintray.com/niqdev/maven/rx-openwebnet/_latestVersion)

[OpenWebNet](http://www.myopen-legrandgroup.com/resources/own_protocol/default.aspx)
client written in Java 8 (retrolambda) and [RxJava](https://github.com/ReactiveX/RxJava). See also the [documentation](https://openwebnet.github.io/rx-openwebnet).

### Currently supports
* `WHO=1` Lighting: see also [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/Lighting.html)
* `WHO=2` Automation: see also [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/Automation.html)
* `WHO=4` Heating: see also [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/Heating.html)
* `WHO=16` SoundSystem: see also [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/SoundSystem.html)
* `WHO=17` Scenario: see also [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/Scenario.html)
* `WHO=18` EnergyManagement: see also [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/EnergyManagement.html)
* a single generic frame/message
* a list of generic frames/messages

### Gradle dependency
```
repositories {
    jcenter()
}
dependencies {
    compile 'com.github.openwebnet:rx-openwebnet:1.5.2'
}
```

### Examples
```java

// connects to the default gateway
OpenWebNet simpleClient = OpenWebNet.newClient(OpenWebNet.defaultGateway("192.168.1.41"));

// requests status light 21
simpleClient
    .send(Lighting.requestStatus("21"))
    .map(Lighting.handleStatus(() -> System.out.println("ON"), () -> System.out.println("OFF")))
    .subscribe(System.out::println);

// connects to the gateway with domain and password
OpenWebNet client = OpenWebNet.newClient(OpenWebNet.gateway("vpn.home.it", 20000, "12345"));

// turns light 21 on    
client
    .send(Lighting.requestTurnOn("21"))
    .map(Lighting.handleResponse(() -> System.out.println("success"), () -> System.out.println("fail")))
    .subscribe(System.out::println);
    
```
```java

// sends a list of generic frames/messages with a custom thread pool
ExecutorService executor = Executors.newSingleThreadExecutor();
OpenWebNet
    .newClient(OpenWebNet.gateway("192.168.1.41", 20000))
    .send(Arrays.asList(() -> "*#1*21##", () -> "*#1*22##"))
    .subscribeOn(Schedulers.from(executor))
    .doOnError(throwable -> System.out.println("ERROR " + throwable))
    .doAfterTerminate(() -> executor.shutdown())
    .subscribe(System.out::println, throwable -> {});

// turns light 21 on with a custom scheduler on Android
OpenWebNet
    .newClient(OpenWebNet.gateway("10.0.2.2", 20000))
    .send(Lighting.requestTurnOff("21"))
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .map(Lighting.handleResponse(() -> System.out.println("success"), () -> System.out.println("fail")))
    .subscribe(System.out::println, throwable -> {});
    
```

### Development tasks
```
export JAVA_HOME='/usr/lib/jvm/java-8-oracle'

./gradlew clean build
./gradlew :lib:test --debug
./gradlew copyJavaDoc
./gradlew runOpenWebNetExample
./gradlew bintrayUpload
```

<!--
TODO
* [publish bintray + travis-ci](http://docs.travis-ci.com/user/deployment/bintray/)
* missing tests
* test coverage
* unsubscribe and close socket
-->

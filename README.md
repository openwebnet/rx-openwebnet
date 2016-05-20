# rx-openwebnet

[![Build Status](https://travis-ci.org/openwebnet/rx-openwebnet.svg?branch=master)](https://travis-ci.org/openwebnet/rx-openwebnet)

[OpenWebNet](http://www.myopen-legrandgroup.com/resources/own_protocol/default.aspx)
client written in Java 8 (retrolambda) and [RxJava](https://github.com/ReactiveX/RxJava)

> work in progress

### Currently supports
* `WHO=1` Lighting
* `WHO=2` Automation
* a single generic frame/message
* a list of generic frames/messages

### Gradle dependency
```
repositories {
    jcenter()
}
dependencies {
    compile 'com.github.openwebnet:rx-openwebnet:0.7.2'
}
```

### Examples
```java

// connects to the gateway
OpenWebNet client = OpenWebNet.newClient(OpenWebNet.defaultGateway("192.168.1.41"));

// requests status light 21
client
    .send(Lighting.requestStatus("21"))
    .map(Lighting.handleStatus(() -> System.out.println("ON"), () -> System.out.println("OFF")))
    .subscribe(System.out::println);

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
    .finallyDo(() -> executor.shutdown())
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
./gradlew build
./gradlew :lib:test --debug
```

<!--
TODO
./gradlew runOpenWebNetExample

* [publish bintray + travis-ci](http://docs.travis-ci.com/user/deployment/bintray/)
* missing tests
* test coverage
* unsubscribe and close socket
-->

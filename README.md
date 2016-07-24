# rx-openwebnet

[![Build Status](https://travis-ci.org/openwebnet/rx-openwebnet.svg?branch=master)](https://travis-ci.org/openwebnet/rx-openwebnet)
[![Download](https://api.bintray.com/packages/niqdev/maven/rx-openwebnet/images/download.svg)](https://bintray.com/niqdev/maven/rx-openwebnet/_latestVersion)

[OpenWebNet](http://www.myopen-legrandgroup.com/resources/own_protocol/default.aspx)
client written in Java 8 (retrolambda) and [RxJava](https://github.com/ReactiveX/RxJava). See also the [documentation](http://openwebnet.github.io/rx-openwebnet).

> work in progress

### Currently supports
* `WHO=1` Lighting: see also [Javadoc](http://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/Lighting.html)
* `WHO=2` Automation: see also [Javadoc](http://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/Automation.html)
* `WHO=4` Heating: see also [Javadoc](http://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/Heating.html)
* a single generic frame/message
* a list of generic frames/messages

### Gradle dependency
```
repositories {
    jcenter()
}
dependencies {
    compile 'com.github.openwebnet:rx-openwebnet:1.0.0'
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
./gradlew build
./gradlew :lib:test --debug
./gradlew copyJavaDoc
./gradlew runOpenWebNetExample
```

### Licence

<a rel="license" href="http://creativecommons.org/licenses/by/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by/4.0/88x31.png" /></a><br /><span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">rx-openwebnet</span> by <a xmlns:cc="http://creativecommons.org/ns#" href="https://github.com/openwebnet" property="cc:attributionName" rel="cc:attributionURL">niqdev</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Attribution 4.0 International License</a>.<br />Based on a work at <a xmlns:dct="http://purl.org/dc/terms/" href="https://github.com/openwebnet/rx-openwebnet" rel="dct:source">https://github.com/openwebnet/rx-openwebnet</a>.

<!--
TODO
* [publish bintray + travis-ci](http://docs.travis-ci.com/user/deployment/bintray/)
* missing tests
* test coverage
* unsubscribe and close socket
-->

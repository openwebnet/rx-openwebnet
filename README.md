# rx-openwebnet

[![Build Status](https://travis-ci.org/openwebnet/rx-openwebnet.svg?branch=master)](https://travis-ci.org/openwebnet/rx-openwebnet)

[OpenWebNet](http://www.myopen-legrandgroup.com/resources/own_protocol/default.aspx)
client written in Java 8 and [RxJava](https://github.com/ReactiveX/RxJava)

> work in progress

### Build library
```
./gradlew build
```

### Example
```java
OpenWebNet
    .newClient(OpenWebNet.defaultGateway("192.168.1.41"))
    .send(() -> "*#1*21##")
    .subscribe(session -> session
        .getResponse().stream().forEach(System.out::println));

```
```java
ExecutorService executor = Executors.newSingleThreadExecutor();

OpenWebNet
    .newClient(OpenWebNet.gateway("192.168.1.41", 20000))
    .send(Arrays.asList(() -> "*#1*21##", () -> "*#1*22##"))
    .subscribeOn(Schedulers.from(executor))
    .doOnError(throwable -> System.out.println("ERROR " + throwable))
    .finallyDo(() -> executor.shutdown())
    .subscribe(sessions -> sessions.forEach(session ->
        session.getResponse().stream().forEach(System.out::println)));
```

### Gradle dependency (unstable)
```
repositories {
    maven {
        url  "http://dl.bintray.com/niqdev/maven"
    }
}
dependencies {
    compile 'com.github.openwebnet:rx-openwebnet:0.4'
}
```

TODO
* [bintray + travis-ci](http://docs.travis-ci.com/user/deployment/bintray/)
* link repo to jcenter
* tests
* test coverage
* unsubscribe and close socket

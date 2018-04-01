# rx-openwebnet

[![Build Status](https://travis-ci.org/openwebnet/rx-openwebnet.svg?branch=master)](https://travis-ci.org/openwebnet/rx-openwebnet)
[![Download](https://api.bintray.com/packages/niqdev/maven/rx-openwebnet/images/download.svg)](https://bintray.com/niqdev/maven/rx-openwebnet/_latestVersion)

[OpenWebNet](https://www.myopen-legrandgroup.com)
client written in Java 8 (retrolambda) and [RxJava](https://github.com/ReactiveX/RxJava), see also the [documentation](https://openwebnet.github.io/rx-openwebnet)

Supported frames

* `WHO=1` Lighting [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/Lighting.html)
* `WHO=2` Automation [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/Automation.html)
* `WHO=4` Heating [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/Heating.html)
* `WHO=16` SoundSystem [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/SoundSystem.html)
* `WHO=17` Scenario [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/Scenario.html)
* `WHO=18` EnergyManagement [Javadoc](https://openwebnet.github.io/rx-openwebnet/com/github/niqdev/openwebnet/message/EnergyManagement.html)
* a single generic frame/message
* a list of generic frames/messages

### Setup

Add the dependency to `build.gradle`
```
repositories {
    jcenter()
}
dependencies {
    compile 'com.github.openwebnet:rx-openwebnet:1.6.0'
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

### Development

```
export JAVA_HOME='/usr/lib/jvm/java-8-oracle'

./gradlew clean build

# verbose tests
./gradlew :lib:test --debug

# run example
./gradlew runOpenWebNetExample

# update javadocs
./gradlew copyJavaDoc

# publish javadoc
git subtree push --prefix javadoc origin gh-pages

# publish on bintray
./gradlew bintrayUpload

# list tasks
./gradlew tasks

# upgrade gradle version
./gradlew wrapper --gradle-version=4.6

# verify jar content
unzip lib/build/libs/lib-1.6.0.jar -d /tmp/openwebnet-jar
```

Command Line Interface
```
# build uber jar
./gradlew shadowJar

# show usage
java -jar lib/build/libs/openwebnet-1.6.0.jar

# example simple
java -jar lib/build/libs/openwebnet-1.6.0.jar \
  -h 192.168.1.41 \
  -f *#1*21##

# example complete
java -jar lib/build/libs/openwebnet-1.6.0.jar \
  --host "192.168.1.41" \
  --port 8080 \
  --password "12345" \
  --frame "*#1*21##"
```

<!--
TODO
* [publish bintray + travis-ci](http://docs.travis-ci.com/user/deployment/bintray/)
* missing tests
* test coverage
* unsubscribe and close socket
-->

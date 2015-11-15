# rx-openwebnet

[![Build Status](https://travis-ci.org/openwebnet/rx-openwebnet.svg?branch=master)](https://travis-ci.org/openwebnet/rx-openwebnet)

[OpenWebNet](http://www.myopen-legrandgroup.com/resources/own_protocol/default.aspx)
client written in Java 8 and [RxJava](https://github.com/ReactiveX/RxJava)

> work in progress

### Build library
```
./gradlew build
```

### TODO example
```java
OpenWebNetObservable
    .rawCommand("localhost", 20000, "*#1*21##")
    .subscribe(openFrames -> {
        openFrames.stream().forEach(frame -> {
            logDebug("FRAME: " + frame.getValue());
        });
    }, throwable -> {
        logDebug("ERROR " + throwable);
    });
```

### Gradle dependency (unstable)
```
repositories {
    maven {
        url  "http://dl.bintray.com/niqdev/maven"
    }
}
dependencies {
    compile 'com.github.openwebnet:rx-openwebnet:0.3'
}
```

TODO
* [bintray + travis-ci](http://docs.travis-ci.com/user/deployment/bintray/)
* link repo to jcenter
* missing tests
* unsubscribe and close socket
* utils

# rx-openwebnet

[OpenWebNet](http://www.myopen-legrandgroup.com/resources/own_protocol/default.aspx)
client written in Java 8 and [RxJava](https://github.com/ReactiveX/RxJava)

> work in progress

### Build library
```
./gradlew build
```

### TODO example usage
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

### Demo
```
# run server (from bash)
while true; do ((echo "*#*1##";) | nc -l 20000) done

# run client
./gradlew runOpenWebNetExample
```

### Gradle dependency (unstable)
```
repositories {
    maven {
        url  "http://dl.bintray.com/niqdev/maven"
    }
}
dependencies {
    compile 'com.github.openwebnet:rx-openwebnet:0.2.2'
}
```

TODO
* TEST !!!
* unsubscribe and close socket
* utils

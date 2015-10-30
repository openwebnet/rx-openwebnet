# rx-openwebnet

[OpenWebNet](http://www.myopen-legrandgroup.com/resources/own_protocol/default.aspx)
client written in Java 8 and [RxJava](https://github.com/ReactiveX/RxJava)

> work in progress

### Build library
```
./gradlew build
```

### Demo
```
# run server (bash)
while true; do ((echo "ACK";) | nc -l 20000) done

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
    compile 'com.github.niqdev:rx-openwebnet:0.2'
}
```

TODO
* gradle/repo with source and javadoc
* java 8
* rxjava
* TEST !!!
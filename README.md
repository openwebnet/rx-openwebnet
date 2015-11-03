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
    compile 'com.github.openwebnet:rx-openwebnet:0.1.4'
}
```

### TODO example usage
```java
OpenWebNetObservable.rawCommand(CONFIG, "*1*1*21##")
    .subscribe(openFrames -> {
        openFrames.stream().forEach(frame -> { System.out.println(frame); });
    });

final Button button = (Button) findViewById(R.id.button_light);
button.setOnClickListener(v -> {
    rawCommand(new OpenConfig("10.0.2.2", 20000), "*1*1*21##")
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(openFrame -> {
            ((TextView) findViewById(R.id.textView_result)).setText(openFrame.val());
        }, throwable -> {
            Log.d("ERROR", throwable.toString());
        });
});
```

TODO
* unsubscribe and close socket
* android helper
* TEST !!!
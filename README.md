# rx-openwebnet

> work in progress

```
gradle init --type java-library

mvn archetype:generate \
  -DgroupId=com.github.niqdev \
  -DartifactId=rx-openwebnet \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false
```

```
repositories {
    jcenter()
}
dependencies {
    compile 'com.github.niqdev:rx-openwebnet:0.1'
}
```

```
./gradlew
./gradlew bintrayUpload

https://dl.bintray.com/niqdev/maven/com/github/niqdev/rx-openwebnet/
```

TODO
* gradle
* java 8
* rxjava
* TEST !!!
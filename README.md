# camel-sftp-kexes

Demo for a possible bug in Camel SFTP where [keyExchangeProtocols settings](https://camel.apache.org/components/4.0.x/sftp-component.html#_endpoint_query_option_keyExchangeProtocols) leak between endpoints through Jsch. 

This project requires JDK 17, and uses TestContainers so running the test requires a container runtime (like Docker).

When running the [RoutesTest](https://github.com/mtuomiko/camel-sftp-kexes/blob/master/src/test/java/net/mtuomiko/kexes/RoutesTest.kt), you can see from the Jsch logging output that both endpoints end up sending the same `JSCH -> client proposal: KEX algorithms: diffie-hellman-group14-sha1,ext-info-c` proposal even though `diffie-hellman-group14-sha1` is only configured on one of them. The default list from Jsch has more options for kex algorithms.

Run test for example with `.\mvnw.cmd quarkus:test` on Windows or `./mvnw quarkus:test` otherwise.

Sample output:

```
[org.apa.cam.com.fil.rem.RemoteFileProducer] (Test runner thread) Not already connected/logged in. Connecting to: sftp://myuser1@localhost:65471/upload1?jschLoggingLevel=INFO&keyExchangeProtocols=diffie-hellman-group14-sha1&password=xxxxxx
...
[org.apa.cam.com.fil.rem.SftpOperations] (Test runner thread) JSCH -> client proposal: KEX algorithms: diffie-hellman-group14-sha1,ext-info-c
...
[org.apa.cam.com.fil.rem.RemoteFileProducer] (Test runner thread) Not already connected/logged in. Connecting to: sftp://myuser2@localhost:65477/upload2?jschLoggingLevel=INFO&password=xxxxxx
...
[org.apa.cam.com.fil.rem.SftpOperations] (Test runner thread) JSCH -> client proposal: KEX algorithms: diffie-hellman-group14-sha1,ext-info-c
```

### Diagnosis

The issue, as far as I see it, is at [SftpOperations.java#L231](https://github.com/apache/camel/blob/a7c819007682fb7ba96777f1c980b58ba839fd11/components/camel-ftp/src/main/java/org/apache/camel/component/file/remote/SftpOperations.java#L231) where the key exchange protocols are set to the Jsch class configuration which is a `static Hashtable<String, String> config = new Hashtable<>()` ([JSch.java#L40](https://github.com/mwiede/jsch/blob/b8368463ddeb708e7ee61749013d46bab7aaac18/src/main/java/com/jcraft/jsch/JSch.java#L40)).

In the Camel SFTP code you can also see that, for example, server host key algorithms are set to a Jsch Session, not the class configuration: [SftpOperations.java#L343](https://github.com/apache/camel/blob/a7c819007682fb7ba96777f1c980b58ba839fd11/components/camel-ftp/src/main/java/org/apache/camel/component/file/remote/SftpOperations.java#L343). I believe this is where the key exchange protocol configuration should also be set, unless there's something preventing that (for some reason?).



---

Everything below here is autogenerated by https://code.quarkus.io

---

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/camel-sftp-kexes-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Camel Core ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/core.html)): Camel core functionality and basic Camel languages: Constant, ExchangeProperty, Header, Ref, Simple and Tokenize
- Camel Kotlin ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/kotlin.html)): Write Camel integration routes in Kotlin
- Camel Direct ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/direct.html)): Call another endpoint from the same Camel Context synchronously
- Camel FTP ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/ftp.html)): Upload and download files to/from SFTP, FTP or SFTP servers

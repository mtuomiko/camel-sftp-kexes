package net.mtuomiko.kexes

import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.endpoint.EndpointRouteBuilder

class Routes : EndpointRouteBuilder() {

    override fun configure() {
        from(direct("start-route"))
            .routeId("start")
            .setBody(constant("ABC"))
            .setHeader(Exchange.FILE_NAME, constant("file.txt"))
            // This sftp producer has a custom key exchange protocol configuration
            .to(
                sftp("myuser1@localhost:{{sftp1.port}}/upload1")
                    .password("Hunter2")
                    .jschLoggingLevel(LoggingLevel.INFO)
                    .keyExchangeProtocols("diffie-hellman-group14-sha1")
            )
            // This sftp producer does not have any custom kex configuration, so it should use Jsch defaults. But the
            // kex configuration from above leaks into this endpoint
            .to(
                sftp("myuser2@localhost:{{sftp2.port}}/upload2")
                    .password("Hunter2")
                    .jschLoggingLevel(LoggingLevel.INFO)
            )
    }
}

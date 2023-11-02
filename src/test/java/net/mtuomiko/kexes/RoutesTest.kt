package net.mtuomiko.kexes

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport
import org.junit.jupiter.api.Test

@QuarkusTest
@QuarkusTestResource(SftpTestResource::class)
class RoutesTest : CamelQuarkusTestSupport() {

    @Test
    fun test() {
        sendBody("direct:start-route", null)
    }
}

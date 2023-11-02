package net.mtuomiko.kexes

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class SftpTestResource : QuarkusTestResourceLifecycleManager {

    private lateinit var sftpServer1: GenericContainer<*>
    private lateinit var sftpServer2: GenericContainer<*>

    override fun start(): MutableMap<String, String> {
        sftpServer1 = GenericContainer(DockerImageName.parse("atmoz/sftp:alpine-3.7"))
            .withExposedPorts(22)
            .withCommand("myuser1:Hunter2:::upload1")
        sftpServer1.start()

        sftpServer2 = GenericContainer(DockerImageName.parse("atmoz/sftp:alpine-3.7"))
            .withExposedPorts(22)
            .withCommand("myuser2:Hunter2:::upload2")
        sftpServer2.start()

        return mutableMapOf(
            "sftp1.port" to sftpServer1.getMappedPort(22).toString(),
            "sftp2.port" to sftpServer2.getMappedPort(22).toString()
        )
    }

    override fun stop() {
        sftpServer1.stop()
        sftpServer2.stop()
    }
}

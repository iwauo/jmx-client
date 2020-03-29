package pw.nabla.jmxclient

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class JmxClientTest {
    val objectName = "Catalina:type=ThreadPool,name=\"http-apr-8080\""
    val attributeName = "maxThreads"
    @Test
    fun `can retrieve an attribute from a JMX service`() {
        val v = JmxClient().getAttribute(
            objectName, attributeName
        )
        assertEquals(v, 200)
    }
}

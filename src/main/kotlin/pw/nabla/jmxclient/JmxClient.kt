package pw.nabla.jmxclient

import pw.nabla.jmxtool.JmxExporterConfig
import java.lang.IllegalArgumentException
import java.util.*
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import javax.management.openmbean.CompositeDataSupport
import javax.management.remote.JMXServiceURL
import javax.management.remote.JMXConnectorFactory

class JmxClient(
    val config: JmxExporterConfig.Endpoint
) {
    private val _connection: MBeanServerConnection by lazy { connect() }
    private val _mbeanNames: Set<ObjectName> by lazy {
        _connection.queryNames(null, null)
    }

    companion object {
        const val WEBLOGIC = "weblogic"
        const val GENERIC = "generic"
    }

    fun connect(): MBeanServerConnection {
        val url = when (config.type.toLowerCase()) {
            GENERIC -> "service:jmx:rmi:///jndi/rmi://${config.host}:${config.port}/jmxrmi"
            WEBLOGIC -> "service:jmx:iiop://${config.host}:${config.port}/jndi/weblogic.management.mbeanservers.runtime"
            else -> throw RuntimeException("Unknown connection type: ${config.type}")
        }
        val env = Hashtable<String, String>()

        if (config.type == WEBLOGIC) {
            env[JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES] = "weblogic.management.remote"
        }
        if (config.user.isNotEmpty()) {
            env[javax.naming.Context.SECURITY_PRINCIPAL] = config.user
            env[javax.naming.Context.SECURITY_CREDENTIALS] = config.pass
        }
        val connector = JMXConnectorFactory.connect(JMXServiceURL(url), env)
        return connector.mBeanServerConnection
    }

    fun getAttribute(mbeanName: String, attributeName: String, attributeKey: String? = null): Any {
        val name = _mbeanNames.find { n -> n.toString() == mbeanName } ?: throw IllegalArgumentException(
            "Unknown mBean name `${mbeanName}`. This should be one of: \n${_mbeanNames.joinToString("\n")}"
        )
        val attribute = _connection.getAttribute(name, attributeName)
        return (
            if (attribute !is CompositeDataSupport)
                attribute
            else {
                if (attributeKey == null)
                    mapOf(*attribute.compositeType.keySet().map { k -> k to attribute[k] }.toTypedArray())
                else
                    attribute[attributeKey]
            }
        )
    }
}
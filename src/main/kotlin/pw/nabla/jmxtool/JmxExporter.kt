package pw.nabla.jmxtool

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import picocli.CommandLine
import picocli.CommandLine.Parameters
import picocli.CommandLine.Command
import pw.nabla.jmxclient.JmxClient
import java.text.SimpleDateFormat
import java.io.*
import java.util.concurrent.Callable
import java.util.Date
import kotlin.system.exitProcess
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

@Command(
    name = "jmx-exporter",
    version = ["JMX Exporter 1.0.0"],
    mixinStandardHelpOptions = true,
    description = ["JMX metrics exporting tool"]
)
class JmxExporter : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["The configuration file."]
    )
    private lateinit var configFile: File

    lateinit var config: JmxExporterConfig

    private lateinit var jmxClient: JmxClient

    private lateinit var csvPrinter: CSVPrinter

    private lateinit var dateFormat: SimpleDateFormat


    override fun call(): Int {
        config = Yaml(Constructor(JmxExporterConfig::class.java)).load(configFile.readText())
        dateFormat = SimpleDateFormat(config.output.timestamp);
        val rowSeparator = if (config.output.usesCRLF) "\r\n" else "\n"
        csvPrinter = CSVPrinter(
            System.out,
            CSVFormat.DEFAULT
                .withHeader(*header().toTypedArray())
                .withRecordSeparator(rowSeparator)
//                .withAutoFlush(true)
        )
        jmxClient = JmxClient(config.endpoint )
        val rows = config.output.rows
        val interval = config.output.interval * 1000L

        (1..rows).forEach { _ ->
            Thread.sleep(interval)
            val record = fetchRecord()
            emit(record)
        }

        return 0
    }

    fun header(): List<String> = listOf("timestamp") + config.columns.map{ col -> col.name }

    fun fetchRecord(): List<Any> = listOf(dateFormat.format(Date())) + config.columns.map{ col ->
        jmxClient.getAttribute(col.type, col.attribute, col.key)
    }

    fun emit(record: List<Any>) {
        csvPrinter.printRecord(record)
    }
}

fun main(vararg args: String) {
    val cli = CommandLine(JmxExporter())
    val exitCode = cli.execute(*args)
    exitProcess(exitCode)
}

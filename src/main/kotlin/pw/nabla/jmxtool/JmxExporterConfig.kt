package pw.nabla.jmxtool

class JmxExporterConfig(
    var endpoint: Endpoint = Endpoint(),
    var output: Output = Output(),
    var columns: List<Column> = listOf()
) {
   class Endpoint(
       var type: String = "generic",
       var host: String = "localhost",
       var port: Int = 9000,
       var user: String = "",
       var pass: String = ""
   )

   class Output(
       var interval: Int = 5,
       var rows: Int = 10,
       var usesCRLF: Boolean = true,
       var timestamp: String = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
   )

   class Column(
       var name: String = "",
       var type: String = "",
       var attribute: String = "",
       var key: String? = null
   )
}

import java.io.*
import javax.net.ssl.SSLSocketFactory
import java.net.Socket

fun main() {
    val host = "mercader-server.onrender.com"
    val path = "/api/juegos/sistema/visitados"
    val jsonBody = """{"allGames":false,"order":"descendente","amount":10}"""

    // WARNING: This requires a valid token to not return 401. 
    // Wait, without a valid token it returns 401. 
    // Does 401 return `data`? No.
}

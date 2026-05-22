# Ejemplos de Uso: Integración del Token en Peticiones HTTP

## Patrón General: Usar Token en Headers

```kotlin
// Patrón en TODAS las peticiones que requieran autenticación:
val token by rememberAuthToken()

if (token != null) {
    val headers = mapOf(
        "Authorization" to "Bearer $token",
        "Content-Type" to "application/json"
    )
    
    // Hacer petición con headers
    api.fetchData(headers = headers)
}
```

---

## Ejemplo 1: En un ViewModel con Retrofit

```kotlin
class GameViewModel @Inject constructor(
    private val authUseCase: AuthenticationUseCase,
    private val gameApi: GameApi
) : ViewModel() {
    
    suspend fun loadGames() {
        try {
            // TODO: OBTENER TOKEN DEL CACHE
            val token = authUseCase.getAuthToken()
            
            if (token == null) {
                // Token no disponible, redirigir a login
                _uiState.value = UiState.Unauthorized
                return
            }
            
            // TODO: AGREGAR TOKEN EN PETICIÓN
            val games = gameApi.getGames(
                authorization = "Bearer $token"
            )
            
            _uiState.value = UiState.Success(games)
        } catch (e: Exception) {
            _uiState.value = UiState.Error(e.message ?: "Error desconocido")
        }
    }
}

// Interface de Retrofit
interface GameApi {
    @GET("/api/juegos")
    suspend fun getGames(
        @Header("Authorization") authorization: String
    ): List<Game>
}
```

---

## Ejemplo 2: En un Composable (UI Layer)

```kotlin
@Composable
fun GamesScreen() {
    // TODO: OBTENER TOKEN DEL CACHE con hook
    val token by rememberAuthToken()
    
    val viewModel = hiltViewModel<GameViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(token) {
        if (token != null) {
            // Token disponible, cargar juegos
            viewModel.loadGames(token!!)
        } else {
            // Sin token, redirigir a login
            // navigateToLogin()
        }
    }
    
    when (uiState) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> GamesList((uiState as UiState.Success).games)
        is UiState.Error -> Text("Error: ${(uiState as UiState.Error).message}")
        is UiState.Unauthorized -> Text("Sesión expirada, por favor inicia sesión nuevamente")
    }
}
```

---

## Ejemplo 3: En Compra (Carrito)

```kotlin
// En compra.controller.ts (Backend ya implementado):
// req.user viene del middleware autenticarToken
// Tiene id_usuario y nombre del JWT

export async function registerUserPurchase(req: Request, res: Response) {
  // El middleware ya extrae id_usuario del token
  const { id_usuario } = req.user as TokenTypes.TokenPayload;
  
  // Ya no necesita pedir el token, simplemente lo extrae del header
}

// En el Frontend (Kotlin):
suspend fun makePurchase(idMetodoPago: String) {
    try {
        // TODO: AGREGAR TOKEN DEL CACHE EN HEADER
        val token = authUseCase.getAuthToken()
        
        if (token == null) {
            throw Exception("No hay sesión iniciada")
        }
        
        val result = purchaseApi.registerPurchase(
            authorization = "Bearer $token",
            paymentMethod = idMetodoPago
        )
        
        // Actualizar timestamp de acceso
        authUseCase.refreshAccessTime()
        
        return result
    } catch (e: Exception) {
        if (e.message?.contains("401") == true) {
            // Token inválido o expirado
            authUseCase.logout()
            navigateToLogin()
        }
    }
}
```

---

## Interceptor de Retrofit (Alternativa Automática)

Si quieres automatizar el proceso y no agregar token manualmente en cada petición:

```kotlin
class AuthenticationInterceptor @Inject constructor(
    private val authUseCase: AuthenticationUseCase
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Omitir agregar token si la petición ya lo tiene (ej: login)
        if (shouldSkipToken(originalRequest.url.toString())) {
            return chain.proceed(originalRequest)
        }
        
        return try {
            // TODO: OBTENER TOKEN DEL CACHE
            val token = runBlocking {
                authUseCase.getAuthToken()
            }
            
            val newRequest = if (token != null) {
                // TODO: AGREGAR TOKEN AUTOMÁTICAMENTE
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                originalRequest
            }
            
            // Actualizar timestamp de acceso
            runBlocking {
                authUseCase.refreshAccessTime()
            }
            
            chain.proceed(newRequest)
        } catch (e: Exception) {
            chain.proceed(originalRequest)
        }
    }
    
    private fun shouldSkipToken(url: String): Boolean {
        // No agregar token en login/signin
        return url.contains("/login") || url.contains("/signin")
    }
}

// Registrar en Retrofit Builder:
val client = OkHttpClient.Builder()
    .addInterceptor(AuthenticationInterceptor(authUseCase))
    .build()

val retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl("http://localhost:3000")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

---

## Checklist para Cada Feature

- [ ] ¿Necesita autenticación? → Usar AuthenticationUseCase
- [ ] ¿Debo agregar token? → `val token = authUseCase.getAuthToken()`
- [ ] ¿Agregar en header? → `"Authorization" to "Bearer $token"`
- [ ] ¿Actualizar timestamp? → `authUseCase.refreshAccessTime()`
- [ ] ¿Validar 401? → Logout y redirigir a login
- [ ] ¿Token inválido por tiempo? → Cargar SplashAuthenticationScreen


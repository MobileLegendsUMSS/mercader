# 📋 Checklist de Implementación Paso a Paso

## Fase 1: Backend (mercader-server)

### ✅ Ya Implementado
- [x] Login genera token JWT (login.service.ts)
- [x] Middleware de autenticación (autenticacion.middleware.ts)
- [x] Controladores usan req.user del middleware

### ⚠️ Por Implementar
- [ ] **signin.service.ts**: Generar token al registrar usuario
  - [ ] Importar `jwt` en el archivo
  - [ ] Descomenta el código del TODO (línea ~28)
  - [ ] Probar que devuelva token en respuesta
  - [ ] Test: POST /api/signin → Verificar que devuelva `token` en response

---

## Fase 2: Frontend - Configuración Básica (mercader)

### Dependencias (probablemente ya instaladas)
```gradle
// build.gradle.kts del app
dependencies {
    // Retrofit para peticiones HTTP
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Hilt para inyección de dependencias
    implementation 'com.google.dagger:hilt-android:2.48'
    kapt 'com.google.dagger:hilt-compiler:2.48'
    
    // Corrutinas
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
```

### Inyección de Dependencias (Hilt Module)
- [ ] Crear archivo `app/src/main/java/com/example/mercader/di/AppModule.kt`
  ```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  object AppModule {
      
      @Provides
      @Singleton
      fun provideTokenRepository(context: Context): ITokenRepository =
          TokenRepository(context)
      
      @Provides
      @Singleton
      fun provideAuthenticationUseCase(
          tokenRepository: ITokenRepository
      ): AuthenticationUseCase = AuthenticationUseCase(tokenRepository)
  }
  ```

---

## Fase 3: Implementación de Token Repository

### Archivo: TokenRepository.kt ✅ CREADO
- [x] Interfaz `ITokenRepository`
- [x] Implementación `TokenRepository`
- [x] Métodos: saveToken, getToken, isTokenValid, clearToken, updateLastAccessTime

**Next Steps**:
- [ ] Revisar que esté en `data/local/TokenRepository.kt`
- [ ] Adaptar si uses DataStore en lugar de SharedPreferences

---

## Fase 4: Implementación de AuthenticationUseCase

### Archivo: AuthenticationUseCase.kt ✅ CREADO
- [x] Métodos: isUserAuthenticated, getAuthToken, saveAuthToken, logout, refreshAccessTime
- [x] Lógica de 15 días

**Next Steps**:
- [ ] Revisar que esté en `domain/usecases/AuthenticationUseCase.kt`
- [ ] Corregir inyección de SharedPreferences (pendiente en getDaysUntilExpiration)

---

## Fase 5: Componentes UI

### 5.1 AuthScreens.kt ✅ MODIFICADO CON TODOs
- [x] LoginScreen: Comentarios indicando dónde hacer API call
- [x] SignupScreen: Comentarios indicando dónde hacer API call

**Next Steps**:
- [ ] Crear un RetrofitService/ApiClient para peticiones
- [ ] Implementar login API call en LoginScreen
- [ ] Implementar signin API call en SignupScreen
- [ ] Guardar token post-login usando authUseCase.saveAuthToken(token)

### 5.2 AuthTokenHook.kt ✅ CREADO
- [x] Composable `rememberAuthToken()` para obtener token
- [x] Composable `rememberIsUserAuthenticated()` para verificar autenticación

**Next Steps**:
- [ ] Usar en pantallas que necesiten token
- [ ] Verificar que compila correctamente

### 5.3 SplashAuthenticationScreen.kt ✅ CREADO
- [x] Pantalla de splash que valida token al iniciar
- [x] Lógica de 15 días

**Next Steps**:
- [ ] Agregar en MainActivity como AppScreen.Splash
- [ ] Hacer que sea la pantalla inicial
- [ ] Test: Reiniciar app sin cerrar sesión → Debe ir directo al home
- [ ] Test: Reiniciar app después de 16 días sin usar → Debe ir al login

---

## Fase 6: Integración en MainActivity

### Modificaciones necesarias:
- [ ] Agregar `object Splash : AppScreen()` en sealed class
- [ ] Cambiar estado inicial: `AppScreen.Splash`
- [ ] Agregar caso en router:
  ```kotlin
  is AppScreen.Splash -> {
      SplashAuthenticationScreen(
          onNavigateToHome = { isAdmin ->
              currentScreen = if (isAdmin) AppScreen.AdminHome else AppScreen.UserHome
          },
          onNavigateToLogin = {
              currentScreen = AppScreen.Login
          }
      )
  }
  ```

---

## Fase 7: Integración en ViewModels

### Para CADA ViewModel que haga peticiones autenticadas:
- [ ] Inyectar `AuthenticationUseCase`
- [ ] En cada suspend function que acceda a API:
  ```kotlin
  suspend fun fetchData() {
      val token = authUseCase.getAuthToken() // ← OBTENER TOKEN
      if (token == null) {
          // Desloguear y navegar a login
          return
      }
      
      // Hacer petición con token en header
      val headers = mapOf("Authorization" to "Bearer $token")
      api.fetchData(headers)
      
      // Actualizar timestamp
      authUseCase.refreshAccessTime()
  }
  ```

### ViewModels a modificar:
- [ ] GameFormViewModel
- [ ] CollectionViewModel  
- [ ] CartViewModel
- [ ] AdminHome ViewModel
- [ ] Cualquier otro que haga peticiones

---

## Fase 8: Testing

### Test Local:
- [ ] Instalar app en emulador
- [ ] Test Login:
  - [ ] Ingresa credenciales válidas
  - [ ] Recibe token del backend
  - [ ] Se guarda en SharedPreferences
  - [ ] Va al home sin pedir login de nuevo
- [ ] Test Logout:
  - [ ] Cierra sesión
  - [ ] Token se elimina de cache
  - [ ] Próxima vez vuelve a login
- [ ] Test 15 días:
  - [ ] (Difícil de testear en tiempo real)
  - [ ] Puedes modificar FIFTEEN_DAYS_MS a 1 minuto para probar
  - [ ] O simular con fake data

### Test Backend:
- [ ] POST /api/login con credenciales → Devuelve token ✓
- [ ] POST /api/signin con nuevas credenciales → Devuelve token (VER TODO)
- [ ] GET /api/juegos con token válido → Devuelve datos ✓
- [ ] GET /api/juegos sin token → Devuelve 401 ✓
- [ ] GET /api/juegos con token inválido → Devuelve 401 ✓

---

## Fase 9: Optimización (Opcional)

### OkHttp Interceptor
- [ ] Crear `AuthenticationInterceptor`
- [ ] Agregar token automáticamente en TODAS las peticiones
- [ ] Ventaja: No repetir header en cada petición
- [ ] (Ver EJEMPLOS_USO_TOKEN.md)

### Refresh Token
- [ ] Backend: Generar refresh token con expiración de 30 días
- [ ] Guardar refresh token en cache
- [ ] Si token expira (401): Usar refresh para obtener nuevo token
- [ ] (Opcional, puede hacerse después)

---

## 📊 Orden Recomendado de Implementación

1. **Primero - Backend**: Modificar signin.service.ts (RÁPIDO)
2. **Segundo - Data Layer**: TokenRepository (COPIAR Y PEGAR)
3. **Tercero - Domain Layer**: AuthenticationUseCase (COPIAR Y PEGAR)
4. **Cuarto - UI Components**: SplashAuthenticationScreen (COPIAR Y PEGAR)
5. **Quinto - MainActivity**: Agregar Splash como pantalla inicial
6. **Sexto - API Calls**: Implementar login y signin con token
7. **Séptimo - Refresh Token**: Usar authUseCase en todos los ViewModels
8. **Octavo - Testing**: Probar flujos completos

---

## ⚠️ Errores Comunes a Evitar

- [ ] ❌ NO olvidar `authUseCase.refreshAccessTime()` después de cada petición
  - Si olvidas, el contador de 15 días no se resetea
- [ ] ❌ NO guardar token en plain text (ya está encriptado por SharedPreferences)
- [ ] ❌ NO hacer peticiones sin verificar que token no es null
- [ ] ❌ NO usar runBlocking en Interceptor (usar async correctamente)
- [ ] ❌ NO olvidar agregar header "Content-Type": "application/json"

---

## 🎯 Resultado Final

Cuando termines, el flujo será:

```
1. Usuario abre app
   ↓
2. Splash valida token (< 15 días)
   ↓
3. Si válido → Va al home (ya autenticado)
   Si inválido → Va al login
   ↓
4. En home, todas las peticiones usan token del cache
   ↓
5. Cada petición actualiza el timestamp (reset de 15 días)
   ↓
6. Cierra app y vuelve en 5 minutos → Sigue autenticado
   ↓
7. Cierra app y vuelve en 20 días → Va al login (expiró)
```

**LISTO! 🎉**


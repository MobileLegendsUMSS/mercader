# Implementación de Token Cache en Mercader (Frontend)

## Descripción General
Después de login/signin exitoso, se debe guardar el token JWT en el cache del dispositivo (SharedPreferences/DataStore) para evitar:
- Hacer peticiones repetidas a la BD
- Requerir login nuevamente al reiniciar la app
- Mejorar la experiencia del usuario

## Flujo Actual vs. Flujo Esperado

### FLUJO ACTUAL (Ineficiente):
```
Login/Signin → Validación local → onLoginSuccess → Usuario al home
[SIN TOKEN - Cada request necesitaría validar contra BD]
```

### FLUJO ESPERADO (Eficiente):
```
Login/Signin → API Call con credenciales → Recibe token JWT → 
Guardar en cache (SharedPreferences) → Navegar al home → 
Usar token en cache para futuras llamadas
```

---

## Componentes a Implementar

### 1. **TokenRepository** (Data Layer)
Ubicación: `app/src/main/java/com/example/mercader/data/local/`

**Responsabilidad**: Manejar almacenamiento y lectura del token en SharedPreferences

```kotlin
// Pseudo-código
interface TokenRepository {
    suspend fun saveToken(token: String, lastAccessTime: Long)
    suspend fun getToken(): String?
    suspend fun isTokenValid(): Boolean // Valida expiración de 15 días
    suspend fun clearToken()
}
```

### 2. **TokenManager / AuthViewModel** (Domain/UI Layer)
Ubicación: `app/src/main/java/com/example/mercader/domain/usecases/` o crear en `ui/`

**Responsabilidad**: Lógica de negocio para validar tokens y 15 días

**Features**:
- ✅ Verificar si token existe en cache
- ✅ Validar que no haya pasado más de 15 días sin uso
- ✅ Exponer token a cualquier componente que lo necesite

### 3. **Composable Hook** (Para acceder al token desde cualquier lugar)
Ubicación: `app/src/main/java/com/example/mercader/ui/`

```kotlin
@Composable
fun rememberAuthToken(): String? {
    // Obtener el token del ViewModel o desde el repositorio
    // Permitir su uso en cualquier composable
}
```

---

## Cambios Necesarios en Backend

### En `signin.service.ts`:
ACTUALMENTE devuelve solo el usuario, DEBERÍA devolver también el token:

```typescript
// AGREGAR: Generar token también en signin
const token = jwt.sign(
    {
      id: usuarioGuardado._id.toString(),
      nombre: usuarioGuardado.nombre
    },
    JWT_SECRET,
    { expiresIn: '24h' }
  );

  return {
    mensaje: 'Usuario registrado exitosamente',
    token,  // ← AGREGAR ESTO
    usuario: {
      id: usuarioGuardado._id.toString(),
      nombre: usuarioGuardado.nombre
    }
  };
```

---

## Implementación de Validador de 15 Días

```kotlin
// Lógica a agregar en TokenRepository
fun isTokenExpiredByInactivity(lastAccessTime: Long): Boolean {
    val currentTime = System.currentTimeMillis()
    val fifteenDaysInMs = 15 * 24 * 60 * 60 * 1000L // 15 días
    
    return (currentTime - lastAccessTime) > fifteenDaysInMs
}

// Cada vez que se acceda a la app:
fun onAppStart() {
    if (isTokenExpiredByInactivity(lastAccessTime)) {
        clearToken() // Desloguear
        navigateTo(LoginScreen)
    } else {
        updateLastAccessTime(System.currentTimeMillis())
    }
}
```

---

## Integración con Llamadas HTTP

### Patrón a seguir en cada controlador/ViewModel:

```kotlin
// Pseudo-código
suspend fun fetchUserData() {
    val token = getTokenFromCache() // ← AQUÍ SE OBTIENE DEL CACHE
    val response = api.getUserData(
        headers = mapOf("Authorization" to "Bearer $token")
    )
}
```

---

## Checklist de Implementación

- [ ] Crear `TokenRepository` interface y implementación
- [ ] Agregar lógica de almacenamiento en SharedPreferences
- [ ] Crear `AuthViewModel` con validación de 15 días
- [ ] Modificar `AuthScreens.kt` para hacer llamadas HTTP reales
- [ ] Agregar lógica de guardado de token post-login
- [ ] Crear composable hook para acceder al token
- [ ] Modificar `signin.service.ts` para devolver token
- [ ] Implementar refresh token si expira (opcional, 24h por ahora)
- [ ] Probar con SharedPreferences inspector en Android Studio


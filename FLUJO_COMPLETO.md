# Resumen Visual: Flujo Completo de Autenticación con Cache

## 🔄 Flujo de Login/Signin

```
┌─────────────────────────────────────────────────────────────────┐
│                     USUARIO ABRE MERCADER APP                   │
└─────────────┬───────────────────────────────────────────────────┘
              │
              ▼
    ┌─────────────────────┐
    │ ¿Hay token en cache?│
    └──┬──────────────┬───┘
       │              │
     SÍ│              │NO
       │              │
       ▼              ▼
  ┌─────────────────┐  ┌──────────────┐
  │ ¿Token válido?  │  │ Mostrar      │
  │ (< 15 días)     │  │ LoginScreen  │
  └┬─────────────┬──┘  └──────────────┘
   │             │           │
  SÍ│            │NO         │
   │             │           │
   │     ┌───────▼────────┐  │
   │     │ Limpiar cache  │  │
   │     │ Ir a Login     │  │
   │     └────────────────┘  │
   │                         │
   │  ┌──────────────────────┘
   │  │
   ▼  ▼
┌──────────────────────────────────┐
│ Usuario ingresa credenciales     │
└──────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────┐
│ POST /api/login (o /api/signin)  │
│ Body: { nombre, contrasenna }    │
└──────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────┐
│ Backend valida credenciales      │
│ Genera JWT token (24h)           │
└──────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────┐
│ Response:                        │
│ {                                │
│   mensaje: string,               │
│   token: "eyJhbGc...",           │
│   usuario: { id, nombre }        │
│ }                                │
└──────────────────────────────────┘
         │
         ▼
  ┌────────────────────────────────────────┐
  │ TODO: GUARDAR TOKEN EN CACHE           │
  │ TokenRepository.saveToken(token)       │
  │ - Guarda token en SharedPreferences    │
  │ - Registra timestamp de acceso actual  │
  └────────────────────────────────────────┘
         │
         ▼
  ┌────────────────────────────────────────┐
  │ Navegar a Home (Admin o User)          │
  │ onLoginSuccess(isAdmin = true/false)   │
  └────────────────────────────────────────┘
         │
         ▼
  ┌────────────────────────────────────────┐
  │ USUARIO YA AUTENTICADO                 │
  │ Puede usar token en peticiones          │
  └────────────────────────────────────────┘
```

---

## 📱 Flujo en Peticiones HTTP

```
┌─────────────────────────────────────┐
│ Necesito obtener datos del servidor │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│ val token = authUseCase.getToken()  │
│ (obtiene del cache si es válido)    │
└─────────────────────────────────────┘
         │
         ▼
    ┌────────────────┐
    │ ¿Token nulo?   │
    └┬───────────┬───┘
     │           │
    NO│          │SÍ
     │           │
     │      ┌────▼──────────────────┐
     │      │ navigateToLogin()     │
     │      │ (Sesión expirada)     │
     │      └───────────────────────┘
     │
     ▼
┌─────────────────────────────────────┐
│ Hacer petición con token en header: │
│                                     │
│ headers = {                         │
│   "Authorization": "Bearer $token"  │
│ }                                   │
│                                     │
│ GET /api/juegos                     │
│ + Authorization: Bearer eyJhbGc...  │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│ Backend recibe petición             │
│ Middleware autenticarToken:         │
│ - Extrae token de header            │
│ - Valida JWT (firmado, no expirado) │
│ - Extrae usuario del token          │
└─────────────────────────────────────┘
         │
         ▼
    ┌────────────────┐
    │ ¿Token válido? │
    └┬───────────┬───┘
     │           │
    SÍ│          │NO
     │           │
     │    ┌──────▼──────────┐
     │    │ Response 401    │
     │    │ "Token expirado"│
     │    └─────────────────┘
     │           │
     │           ▼
     │    ┌──────────────────────┐
     │    │ Frontend:            │
     │    │ - Limpiar cache      │
     │    │ - navigateToLogin()  │
     │    └──────────────────────┘
     │
     ▼
┌─────────────────────────────────────┐
│ req.user tiene:                     │
│ { id_usuario, nombre }              │
│ (extrae automáticamente del token)  │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│ Ejecutar lógica de negocio          │
│ con usuario autenticado             │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│ Response 200 con datos solicitados  │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│ Frontend:                           │
│ - Recibe datos                      │
│ - Actualizar timestamp de acceso    │
│   authUseCase.refreshAccessTime()   │
│ - Mostrar datos en UI               │
└─────────────────────────────────────┘
```

---

## ⏰ Validación de 15 Días (Expiración por Inactividad)

```
DÍA 1: Usuario inicia sesión
       lastAccessTime = AHORA
       token guardado en cache
       
DÍA 7: Usuario abre la app
       diff = AHORA - lastAccessTime = 7 días
       7 < 15 → Token válido ✓
       lastAccessTime = AHORA (reset)
       
DÍA 14: Usuario abre la app
        diff = AHORA - lastAccessTime = 1 día
        1 < 15 → Token válido ✓
        lastAccessTime = AHORA (reset)
        
DÍA 16: Usuario abre la app
        diff = AHORA - lastAccessTime = 16 días
        16 > 15 → Token EXPIRADO ✗
        Limpiar cache
        navigateToLogin()
        Usuario debe iniciar sesión de nuevo
```

---

## 🔧 Cambios Requeridos

### Backend (mercader-server)
- [x] Login ya genera y devuelve token ✓
- [ ] Signin debe generar y devolver token (VER signin.service.ts con TODO)
- [x] Middleware autenticarToken ya está implementado ✓
- [x] Los controladores ya usan req.user ✓

### Frontend (mercader)
- [ ] AuthScreens.kt: Implementar llamadas HTTP a login/signin (VER TODOs)
- [ ] Crear TokenRepository para manejar SharedPreferences (ARCHIVO CREADO)
- [ ] Crear AuthenticationUseCase para lógica de negocio (ARCHIVO CREADO)
- [ ] Crear AuthTokenHook composable para acceder al token (ARCHIVO CREADO)
- [ ] Crear SplashAuthenticationScreen para validar al iniciar (ARCHIVO CREADO)
- [ ] Modificar MainActivity para usar SplashAuthenticationScreen
- [ ] En todos los ViewModel: Inyectar AuthenticationUseCase
- [ ] En todas las peticiones HTTP: Agregar token en header

---

## 📦 Flujo de Carpetas

```
mercader/
├── IMPLEMENTACION_TOKEN_CACHE.md      ← Documentación general
├── EJEMPLOS_USO_TOKEN.md              ← Ejemplos de código
├── app/src/main/java/com/example/mercader/
│   ├── data/
│   │   └── local/
│   │       └── TokenRepository.kt      ← CREADO: Maneja cache
│   ├── domain/
│   │   └── usecases/
│   │       └── AuthenticationUseCase.kt ← CREADO: Lógica de auth
│   ├── common/
│   │   ├── utils/
│   │   │   └── AuthTokenHook.kt       ← CREADO: Hook para token
│   │   └── components/
│   │       └── SplashAuthenticationScreen.kt ← CREADO: Splash con validación
│   └── ui/
│       └── screens/
│           └── auth/
│               └── AuthScreens.kt     ← MODIFICADO: Comentarios TODO

mercader-server/
└── src/services/
    └── signin.service.ts              ← MODIFICADO: Comentarios TODO
```


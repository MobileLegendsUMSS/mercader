# ❓ Preguntas Frecuentes - Splash & Token Cache

## ¿Qué es "Splash"? ¿Por qué se llama así?

**Splash** es un término común en desarrollo, especialmente en aplicaciones móviles.

### Definición:
Una **Splash Screen** (pantalla de bienvenida) es la primera pantalla que ve el usuario cuando abre una app. Normalmente:
- Muestra el logo de la aplicación
- Realiza inicializaciones en el background
- Valida el estado de la app
- Después de unos segundos, lleva al usuario a la pantalla correspondiente

### En Mercader:
```
┌─────────────────────────────────────────────┐
│                                             │
│            SPLASH SCREEN                    │
│            ⏳ Loading...                     │
│                                             │
│         (Se valida el token aquí)           │
│                                             │
└─────────────────────────────────────────────┘
                      ↓
      ┌──────────────────────────────┐
      │ ¿Hay token válido?           │
      └──┬──────────────────────┬────┘
         │                      │
         │ SÍ                   │ NO
         │                      │
         ▼                      ▼
    ┌─────────┐          ┌──────────┐
    │  HOME   │          │  LOGIN   │
    └─────────┘          └──────────┘
```

### Nombre "Splash":
- **Splash** = "Salpicadura" en inglés
- Se llama así porque es una pantalla que "salpica" en el inicio
- Típicamente muestra el logo de la app como si fuera una salpicadura

### Ejemplo de otras apps conocidas:
- **Instagram**: Muestra el logo de Instagram al iniciar
- **TikTok**: Muestra un splash con animación
- **Netflix**: Muestra el logo y carga contenido en background
- **WhatsApp**: Muestra el splash mientras conecta con el servidor

---

## ¿El token se guardaría automáticamente si implemento esto?

**SÍ, TOTALMENTE AUTOMÁTICO! 🎉**

### Flujo Automático sin NextSteps:

```
1. Usuario presiona "Ingresar" en LoginScreen
                    ↓
2. Se llama viewModel.login(nombre, contrasenna)
                    ↓
3. AuthViewModel hace petición HTTP a /api/login
                    ↓
4. Backend devuelve: {
     mensaje: "...",
     token: "eyJhbGc...",
     usuario: { id, nombre }
   }
                    ↓
5. AuthViewModel AUTOMÁTICAMENTE:
   - authUseCase.saveAuthToken(token)  ← GUARDA EN CACHE
   - authUseCase.refreshAccessTime()   ← GUARDA TIMESTAMP
                    ↓
6. Se ejecuta onLoginSuccess(isAdmin)
                    ↓
7. UI navega al home
                    ↓
8. Usuario LISTO, completamente autenticado
```

**NO necesitas hacer nada más, está todo automático en el ViewModel!**

---

## El Token se Guarda... ¿Dónde?

```
┌─────────────────────────────────────────────────────────┐
│            DISPOSITIVO DEL USUARIO                      │
│                                                         │
│  ┌──────────────────────────────────────────────────┐  │
│  │         MEMORIA DEL DISPOSITIVO (RAM)            │  │
│  │  Variables temporales de la app                  │  │
│  │  (Se borra cuando se cierra la app)              │  │
│  └──────────────────────────────────────────────────┘  │
│                                                         │
│  ┌──────────────────────────────────────────────────┐  │
│  │    ALMACENAMIENTO PERSISTENTE (SharedPrefs)      │  │
│  │  ← ← ← AQUÍ SE GUARDA EL TOKEN ← ← ←           │  │
│  │                                                  │  │
│  │  Ruta: /data/data/com.example.mercader/         │  │
│  │        shared_prefs/mercader_auth.xml            │  │
│  │                                                  │
│  │  Contenido:                                      │  │
│  │  <string name="jwt_token">eyJhbGc...</string>    │  │
│  │  <long name="last_access_time">1234567890</long> │  │
│  │                                                  │  │
│  │  (Se mantiene aunque se cierre la app)           │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Diferencia Importante:

| Tipo | Duración | Ubicación | Uso |
|------|----------|-----------|-----|
| **RAM (Memoria temporal)** | Mientras está abierta | Memoria | Variables locales |
| **SharedPreferences** | Permanente | Almacenamiento | Token, configuraciones |
| **BD (Database)** | Permanente | Almacenamiento | Datos complejos |

**En Mercader**: El token se guarda en **SharedPreferences** (archivo XML encriptado)

---

## Flujo Completo de Una Sesión

### DÍA 1 - Usuario instala la app

```
└─► App abre
    └─► Splash carga
        └─► No hay token
            └─► Va al Login
                └─► Usuario entra credenciales
                    └─► API devuelve token ✅
                        └─► TOKEN GUARDADO EN CACHE
                            └─► Navega al Home
                                └─► Usuario listo!
```

**SharedPreferences contiene:**
```xml
jwt_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
last_access_time = 1234567890
```

### DÍA 1 - Cierra la app y la abre de nuevo (5 minutos después)

```
└─► App abre
    └─► Splash carga
        └─► Lee SharedPreferences
            └─► Token existe ✅
                └─► Valida: ¿< 15 días sin usar? SÍ ✅
                    └─► Actualiza last_access_time
                        └─► Navega directo al Home
                            └─► Usuario sigue autenticado!
                                (SIN pedir login de nuevo)
```

### DÍA 20 - Cierra la app y la abre de nuevo

```
└─► App abre
    └─► Splash carga
        └─► Lee SharedPreferences
            └─► Token existe ✅
                └─► Valida: ¿< 15 días sin usar? NO ❌
                    (Pasaron 20 días)
                    └─► Limpia cache (logout automático)
                        └─► Navega al Login
                            └─► Usuario debe iniciar sesión de nuevo
```

---

## Resumen de lo que Pasa Automáticamente

### ✅ Automático (YA ESTÁ HECHO)

1. ✅ Login/Signin hace petición HTTP
2. ✅ Backend devuelve token
3. ✅ Token se guarda en SharedPreferences
4. ✅ Timestamp se guarda
5. ✅ Splash valida token al abrir
6. ✅ Si < 15 días → Navega al home
7. ✅ Si > 15 días → Navega al login
8. ✅ Cada petición actualiza timestamp (reset de 15 días)

### ❌ NO Necesitas Hacer

- ❌ No necesitas crear archivos manuales
- ❌ No necesitas hacer "NextSteps" especiales
- ❌ No necesitas pedir el token a la BD cada vez
- ❌ No necesitas hacer login otra vez si cierras y abres la app en 15 días

---

## Flujo Visual: Login a Autenticado

```
┌──────────────────────┐
│  LoginScreen Abierto │
│                      │
│  [Nombre:  ____]     │
│  [Contraseña: ____]  │
│  [ INGRESAR ]        │
└──────────────────────┘
         │
    Usuario presiona
       INGRESAR
         │
         ▼
┌──────────────────────────────────┐
│  ViewModel.login() se ejecuta    │
│                                  │
│  authApi.login(nombre, pass)     │
│  (petición HTTP)                 │
└──────────────────────────────────┘
         │
    Esperando...
         │
         ▼
┌──────────────────────────────────┐
│  Backend valida en BD            │
│  Genera JWT token                │
│  Devuelve respuesta              │
└──────────────────────────────────┘
         │
    Response recibida
         │
         ▼
┌──────────────────────────────────┐
│  ✅ Response tiene token         │
│                                  │
│  authUseCase.saveAuthToken(...)  │
│  ← GUARDA EN SharedPreferences   │
│                                  │
│  authUseCase.refreshAccessTime() │
│  ← GUARDA TIMESTAMP              │
└──────────────────────────────────┘
         │
   Token y timestamp
     guardados ✅
         │
         ▼
┌──────────────────────────────────┐
│  UI navega al Home               │
│                                  │
│  onLoginSuccess(isAdmin = true)  │
└──────────────────────────────────┘
         │
   ¡USUARIO AUTENTICADO!
   El token está guardado en cache
   Todas las futuras peticiones
   lo usan automáticamente
```

---

## Preguntas Frecuentes Adicionales

### P: ¿Qué pasa si reinicio el dispositivo?
**R**: El token está en SharedPreferences, que sobrevive a reinicios. Al abrir la app, Splash lo valida y navega al home (si < 15 días).

### P: ¿Qué pasa si borro la app de la tienda de Google Play?
**R**: Se borran todos los datos, incluyendo el token. Al reinstalar, va al login.

### P: ¿Puedo ver el token en el dispositivo?
**R**: Sí, con Android Studio → Device File Explorer → `/data/data/com.example.mercader/shared_prefs/mercader_auth.xml`

### P: ¿El token está encriptado?
**R**: SharedPreferences NO encripta por defecto, pero está en una carpeta protegida del sistema. Para máxima seguridad, usa EncryptedSharedPreferences.

### P: ¿Qué pasa si cambio de usuario en el dispositivo?
**R**: Cada usuario (profile) en Android tiene su propia carpeta `/data/data/`, así que cada uno tiene su propio token.

### P: ¿Se envía el token en cada petición?
**R**: Sí, en el header `Authorization: Bearer eyJhbGc...`


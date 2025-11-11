
        <!-- 🔔 Canal de notificaciones por defecto -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_channel_id"
            android:value="ventas_channel"/>
        
        <!-- 🔔 Ícono por defecto para notificaciones -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_icon"
            android:resource="@drawable/ic_notification"/>
        
        <!-- 🔔 Color por defecto para notificaciones -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_color"
            android:resource="@color/notification_color"/>

        <meta-data
            android:name="flutterEmbedding"
            android:value="2"/>
    </application>
</manifest>
```

#### 2.4 Crear archivo de colores `android/app/src/main/res/values/colors.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="notification_color">#2196F3</color>
</resources>
```

#### 2.5 Añadir ícono de notificación

Descarga un ícono PNG de 24x24 (blanco sobre transparente) y colócalo en:
```
android/app/src/main/res/drawable/ic_notification.png
```

O usa este comando para generar uno simple:
```bash
# Usa cualquier herramienta de diseño o simplemente copia el ícono de launcher
```

---

## 🔧 PARTE 3: Código de Flutter

### Paso 1: Crear servicio de notificaciones

**Archivo:** `lib/services/fcm_service.dart`

```dart
import 'dart:convert';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

/// Manejador de notificaciones en segundo plano
@pragma('vm:entry-point')
Future<void> firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  await Firebase.initializeApp();
  print('📬 Notificación recibida en segundo plano: ${message.messageId}');
  print('   Título: ${message.notification?.title}');
  print('   Cuerpo: ${message.notification?.body}');
}

class FcmService {
  static final FcmService _instance = FcmService._internal();
  factory FcmService() => _instance;
  FcmService._internal();

  final FirebaseMessaging _fcm = FirebaseMessaging.instance;
  final FlutterLocalNotificationsPlugin _localNotifications = FlutterLocalNotificationsPlugin();
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  String? _currentToken;

  /// Inicializa Firebase Cloud Messaging
  Future<void> initialize() async {
    try {
      // Solicitar permisos
      NotificationSettings settings = await _fcm.requestPermission(
        alert: true,
        badge: true,
        sound: true,
        provisional: false,
        criticalAlert: false,
      );

      if (settings.authorizationStatus == AuthorizationStatus.authorized) {
        print('✅ Permisos de notificaciones concedidos');
      } else if (settings.authorizationStatus == AuthorizationStatus.provisional) {
        print('⚠️ Permisos de notificaciones provisionales');
      } else {
        print('❌ Permisos de notificaciones denegados');
        return;
      }

      // Configurar notificaciones locales
      await _initializeLocalNotifications();

      // Obtener token FCM
      _currentToken = await _fcm.getToken();
      if (_currentToken != null) {
        print('🔑 Token FCM obtenido: ${_currentToken!.substring(0, 20)}...');
        await _enviarTokenAlBackend(_currentToken!);
      }

      // Escuchar cambios en el token
      _fcm.onTokenRefresh.listen((newToken) {
        print('🔄 Token FCM actualizado');
        _currentToken = newToken;
        _enviarTokenAlBackend(newToken);
      });

      // Manejar notificaciones en primer plano
      FirebaseMessaging.onMessage.listen(_handleForegroundMessage);

      // Manejar cuando se toca una notificación
      FirebaseMessaging.onMessageOpenedApp.listen(_handleNotificationTap);

      // Verificar si la app se abrió desde una notificación
      RemoteMessage? initialMessage = await _fcm.getInitialMessage();
      if (initialMessage != null) {
        _handleNotificationTap(initialMessage);
      }

      print('✅ FCM Service inicializado correctamente');
    } catch (e) {
      print('❌ Error al inicializar FCM: $e');
    }
  }

  /// Configura las notificaciones locales
  Future<void> _initializeLocalNotifications() async {
    const AndroidInitializationSettings androidSettings = AndroidInitializationSettings('@drawable/ic_notification');
    
    const InitializationSettings initSettings = InitializationSettings(
      android: androidSettings,
    );

    await _localNotifications.initialize(
      initSettings,
      onDidReceiveNotificationResponse: _onNotificationTapped,
    );

    // Crear canal de notificaciones para Android
    const AndroidNotificationChannel channel = AndroidNotificationChannel(
      'ventas_channel',
      'Ventas y Reservas',
      description: 'Notificaciones sobre tus reservas de viajes',
      importance: Importance.high,
      playSound: true,
      enableVibration: true,
    );

    await _localNotifications
        .resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()
        ?.createNotificationChannel(channel);
  }

  /// Maneja notificaciones cuando la app está en primer plano
  void _handleForegroundMessage(RemoteMessage message) {
    print('📬 Notificación recibida en primer plano');
    print('   Título: ${message.notification?.title}');
    print('   Cuerpo: ${message.notification?.body}');

    // Mostrar notificación local
    _showLocalNotification(message);
  }

  /// Muestra una notificación local
  Future<void> _showLocalNotification(RemoteMessage message) async {
    const AndroidNotificationDetails androidDetails = AndroidNotificationDetails(
      'ventas_channel',
      'Ventas y Reservas',
      channelDescription: 'Notificaciones sobre tus reservas de viajes',
      importance: Importance.high,
      priority: Priority.high,
      playSound: true,
      enableVibration: true,
      icon: '@drawable/ic_notification',
    );

    const NotificationDetails platformDetails = NotificationDetails(
      android: androidDetails,
    );

    await _localNotifications.show(
      message.hashCode,
      message.notification?.title ?? 'Nueva notificación',
      message.notification?.body ?? '',
      platformDetails,
      payload: jsonEncode(message.data),
    );
  }

  /// Maneja cuando se toca una notificación
  void _handleNotificationTap(RemoteMessage message) {
    print('👆 Usuario tocó la notificación');
    print('   Datos: ${message.data}');

    // Aquí puedes navegar a una pantalla específica
    String? type = message.data['type'];
    String? ventaId = message.data['ventaId'];

    if (type != null && ventaId != null) {
      // TODO: Navegar a la pantalla de detalle de venta
      // Navigator.push(...);
      print('   → Tipo: $type, VentaID: $ventaId');
    }
  }

  /// Callback cuando se toca una notificación local
  void _onNotificationTapped(NotificationResponse response) {
    if (response.payload != null) {
      Map<String, dynamic> data = jsonDecode(response.payload!);
      print('👆 Usuario tocó notificación local');
      print('   Datos: $data');
      
      // TODO: Navegar según los datos
    }
  }

  /// Envía el token FCM al backend de Spring Boot
  Future<void> _enviarTokenAlBackend(String token) async {
    try {
      String? authToken = await _storage.read(key: 'token');
      if (authToken == null) {
        print('⚠️ No hay token de autenticación, no se puede enviar FCM token');
        return;
      }

      const String url = 'http://10.0.2.2:8080/api/fcm/token';  // Para emulador
      // const String url = 'http://TU_IP:8080/api/fcm/token';  // Para dispositivo físico

      final response = await http.post(
        Uri.parse(url),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $authToken',
        },
        body: jsonEncode({
          'fcmToken': token,
        }),
      );

      if (response.statusCode == 200) {
        print('✅ Token FCM enviado al backend correctamente');
      } else {
        print('❌ Error al enviar token FCM: ${response.statusCode}');
        print('   Respuesta: ${response.body}');
      }
    } catch (e) {
      print('❌ Error al enviar token FCM al backend: $e');
    }
  }

  /// Elimina el token FCM del backend (al cerrar sesión)
  Future<void> eliminarToken() async {
    try {
      String? authToken = await _storage.read(key: 'token');
      if (authToken == null) return;

      const String url = 'http://10.0.2.2:8080/api/fcm/token';

      final response = await http.delete(
        Uri.parse(url),
        headers: {
          'Authorization': 'Bearer $authToken',
        },
      );

      if (response.statusCode == 200) {
        print('✅ Token FCM eliminado del backend');
      }
    } catch (e) {
      print('❌ Error al eliminar token FCM: $e');
    }
  }

  /// Obtiene el token FCM actual
  String? get currentToken => _currentToken;
}
```

---

### Paso 2: Actualizar `main.dart`

```dart
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'config/theme_config.dart';
import 'providers/auth_provider.dart';
import 'providers/paquete_provider.dart';
import 'providers/venta_provider.dart';
import 'screens/auth/login_screen.dart';
import 'screens/main_screen.dart';
import 'services/fcm_service.dart';

// 🔔 Manejador de notificaciones en segundo plano
@pragma('vm:entry-point')
Future<void> firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  await Firebase.initializeApp();
  print('📬 Notificación en segundo plano: ${message.notification?.title}');
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  // 🔔 Inicializar Firebase
  await Firebase.initializeApp();
  
  // 🔔 Configurar manejador de segundo plano
  FirebaseMessaging.onBackgroundMessage(firebaseMessagingBackgroundHandler);
  
  runApp(const AgenciaMovilApp());
}

class AgenciaMovilApp extends StatelessWidget {
  const AgenciaMovilApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthProvider()),
        ChangeNotifierProvider(create: (_) => PaqueteProvider()),
        ChangeNotifierProvider(create: (_) => VentaProvider()),
      ],
      child: MaterialApp(
        title: 'Agencia de Viajes',
        debugShowCheckedModeBanner: false,
        theme: ThemeConfig.lightTheme,
        localizationsDelegates: const [
          GlobalMaterialLocalizations.delegate,
          GlobalWidgetsLocalizations.delegate,
          GlobalCupertinoLocalizations.delegate,
        ],
        supportedLocales: const [
          Locale('es', 'ES'),
        ],
        locale: const Locale('es', 'ES'),
        home: const AuthCheckScreen(),
      ),
    );
  }
}

class AuthCheckScreen extends StatefulWidget {
  const AuthCheckScreen({super.key});

  @override
  State<AuthCheckScreen> createState() => _AuthCheckScreenState();
}

class _AuthCheckScreenState extends State<AuthCheckScreen> {
  @override
  void initState() {
    super.initState();
    
    // Verificar autenticación
    Future.microtask(() async {
      await context.read<AuthProvider>().checkAuthStatus();
      
      // 🔔 Inicializar FCM después de verificar autenticación
      if (context.read<AuthProvider>().isAuthenticated) {
        await FcmService().initialize();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<AuthProvider>(
      builder: (context, authProvider, _) {
        if (authProvider.isLoading) {
          return const Scaffold(
            body: Center(
              child: CircularProgressIndicator(),
            ),
          );
        }

        if (authProvider.isAuthenticated) {
          return const MainScreen();
        }

        return const LoginScreen();
      },
    );
  }
}
```

---

### Paso 3: Actualizar `AuthProvider` para inicializar FCM al hacer login

**Archivo:** `lib/providers/auth_provider.dart`

Añade en el método `login()` después de guardar el token:

```dart
import '../services/fcm_service.dart';

// ... en el método login(), después de guardar el token ...

if (response.success) {
  await _storage.write(key: 'token', value: response.data!.token);
  await _storage.write(key: 'user_id', value: response.data!.usuario.id);
  
  // 🔔 Inicializar FCM después del login
  await FcmService().initialize();
  
  _token = response.data!.token;
  _isAuthenticated = true;
  notifyListeners();
}
```

Y en el método `logout()`:

```dart
Future<void> logout() async {
  // 🔔 Eliminar token FCM del backend
  await FcmService().eliminarToken();
  
  await _storage.delete(key: 'token');
  await _storage.delete(key: 'user_id');
  
  _token = null;
  _isAuthenticated = false;
  notifyListeners();
}
```

---

## 🧪 PARTE 4: Pruebas

### Prueba 1: Verificar que FCM funciona

1. **Ejecuta la app** en tu dispositivo/emulador
2. **Inicia sesión** con un cliente
3. **Revisa los logs** en la consola:
   ```
   ✅ Permisos de notificaciones concedidos
   🔑 Token FCM obtenido: cXxxx...
   ✅ Token FCM enviado al backend correctamente
   ✅ FCM Service inicializado correctamente
   ```

### Prueba 2: Enviar notificación de prueba

Desde Postman:

```http
POST http://localhost:8080/api/fcm/test
Headers:
  Authorization: Bearer TU_TOKEN_JWT
```

Deberías recibir una notificación de prueba en tu dispositivo.

### Prueba 3: Crear venta desde Angular

1. **Abre Angular** en tu navegador
2. **Crea una nueva venta** para el cliente que tiene la app abierta
3. **La app móvil debe recibir** una notificación:
   ```
   🎉 Nueva Reserva Registrada
   Tu reserva para Paquete Turístico ha sido registrada exitosamente
   ```

### Prueba 4: Confirmar venta desde Angular

1. **Confirma una venta** del cliente
2. **Debe llegar notificación**:
   ```
   ✅ Reserva Confirmada
   Tu reserva para Paquete Turístico ha sido confirmada
   ```

---

## 🔍 Troubleshooting

### ❌ No recibo notificaciones

**Verificar:**
1. Firebase está correctamente configurado
2. `google-services.json` está en `android/app/`
3. Token FCM se envió al backend (revisar logs)
4. Spring Boot tiene `firebase-service-account.json` en `resources/`
5. Permisos de notificaciones concedidos en el dispositivo

**Comandos útiles:**
```bash
# Limpiar build
flutter clean
flutter pub get
cd android && ./gradlew clean && cd ..

# Reconstruir
flutter build apk --debug
```

### ❌ Error: "Default FirebaseApp is not initialized"

**Solución:** Asegúrate de que `Firebase.initializeApp()` se llama en `main()` antes de cualquier otra cosa.

### ❌ Error: "google-services.json not found"

**Solución:** Verifica que el archivo esté en `android/app/google-services.json` (no en `android/`).

### ❌ Error: "Invalid package name"

**Solución:** El `applicationId` en `build.gradle` debe coincidir exactamente con el paquete registrado en Firebase.

---

## 📊 Tipos de Notificaciones

### 1. Venta Creada (`VENTA_CREADA`)
```json
{
  "type": "VENTA_CREADA",
  "ventaId": "xxx",
  "nombrePaquete": "Caribe Paradisíaco",
  "monto": "1850.0"
}
```

### 2. Venta Confirmada (`VENTA_CONFIRMADA`)
```json
{
  "type": "VENTA_CONFIRMADA",
  "ventaId": "xxx",
  "nombrePaquete": "Caribe Paradisíaco",
  "monto": "1850.0"
}
```

### 3. Venta Editada (`VENTA_EDITADA`)
```json
{
  "type": "VENTA_EDITADA",
  "ventaId": "xxx",
  "nombrePaquete": "Caribe Paradisíaco",
  "cambio": "Detalles actualizados"
}
```

### 4. Venta Cancelada (`VENTA_CANCELADA`)
```json
{
  "type": "VENTA_CANCELADA",
  "ventaId": "xxx",
  "nombrePaquete": "Caribe Paradisíaco"
}
```

---

## 🎨 Personalización

### Cambiar colores de notificaciones

**Archivo:** `android/app/src/main/res/values/colors.xml`

```xml
<resources>
    <color name="notification_color">#FF5722</color>  <!-- Cambia este color -->
</resources>
```

### Cambiar ícono de notificaciones

Reemplaza el archivo:
```
android/app/src/main/res/drawable/ic_notification.png
```

### Cambiar sonido de notificaciones

Agrega un archivo `.mp3` o `.wav` en:
```
android/app/src/main/res/raw/notification_sound.mp3
```

Y modifica en `PushNotificationService.java`:
```java
.setNotification(AndroidNotification.builder()
    .setSound("notification_sound")  // Sin extensión
    .build())
```

---

## 🔒 Seguridad

### Tokens FCM

- ✅ Los tokens se almacenan en el backend asociados al usuario
- ✅ Se eliminan al cerrar sesión
- ✅ Se actualizan automáticamente si caducan
- ✅ Solo el backend puede enviar notificaciones

### Datos Sensibles

- ❌ **NO envíes información sensible** en las notificaciones
- ✅ Solo envía IDs y textos generales
- ✅ Los detalles se cargan desde el backend cuando el usuario abre la notificación

---

## 📚 Recursos Adicionales

- [Firebase Cloud Messaging - Flutter](https://firebase.google.com/docs/cloud-messaging/flutter/client)
- [Flutter Local Notifications](https://pub.dev/packages/flutter_local_notifications)
- [Firebase Console](https://console.firebase.google.com/)

---

## ✅ Checklist Final

### Backend (Spring Boot)
- [x] Dependencia `firebase-admin` añadida en `pom.xml`
- [x] Campo `fcmToken` añadido a modelo `Usuario`
- [x] `FirebaseConfig.java` creado
- [x] `PushNotificationService.java` creado
- [x] `FcmTokenController.java` creado
- [x] `VentaService.java` envía notificaciones
- [x] `firebase-service-account.json` en `resources/`

### Flutter
- [ ] Dependencias añadidas en `pubspec.yaml`
- [ ] `google-services.json` en `android/app/`
- [ ] `android/build.gradle` configurado
- [ ] `android/app/build.gradle` configurado
- [ ] `AndroidManifest.xml` configurado
- [ ] `fcm_service.dart` creado
- [ ] `main.dart` actualizado con Firebase
- [ ] `auth_provider.dart` inicializa FCM
- [ ] Pruebas realizadas

---

**¡Listo!** 🎉 Tu app móvil ahora recibirá notificaciones push en tiempo real.

**Nota:** Recuerda cambiar `http://10.0.2.2:8080` por la IP real de tu servidor cuando pruebes en dispositivos físicos.
# 🔔 Guía de Configuración: Notificaciones Push con Firebase Cloud Messaging

**Proyecto:** Agencia de Viajes - App Móvil Flutter  
**Fecha:** 11 de Noviembre, 2025  
**Versión:** 1.0.0  
**Compatible con:** Android 10+ (API 29+)

---

## 📋 ¿Qué Lograremos?

Tu app móvil recibirá **notificaciones push en tiempo real** cuando:
- ✅ Se cree una reserva desde el sistema web (Angular)
- ✅ Se confirme una reserva
- ✅ Se edite una reserva
- ✅ Se cancele una reserva

Las notificaciones funcionarán:
- ✅ En **primer plano** (app abierta)
- ✅ En **segundo plano** (app minimizada)
- ✅ Con **dispositivo bloqueado**

---

## 🔧 PARTE 1: Configuración en Firebase Console

### Paso 1: Crear Proyecto en Firebase

1. Ve a: https://console.firebase.google.com/
2. Click en **"Agregar proyecto"**
3. Nombre del proyecto: `agencia-viajes-movil`
4. Deshabilita Google Analytics (opcional)
5. Click en **"Crear proyecto"**

### Paso 2: Registrar App Android

1. En Firebase Console, click en el ícono de **Android**
2. **Nombre del paquete Android**: `com.agencia.agencia_movil`
   - ⚠️ **IMPORTANTE**: Este nombre debe coincidir con el de tu `build.gradle`
3. **Nombre de la app**: Agencia Móvil
4. Click en **"Registrar app"**

### Paso 3: Descargar google-services.json

1. Firebase te mostrará un archivo `google-services.json`
2. **Descarga** el archivo
3. **Colócalo** en: `android/app/google-services.json`
   ```
   agencia_movil/
   └── android/
       └── app/
           └── google-services.json  ← AQUÍ
   ```

### Paso 4: Obtener Server Key para Spring Boot

1. En Firebase Console, ve a **⚙️ Configuración del proyecto**
2. Pestaña **"Cuentas de servicio"**
3. Click en **"Generar nueva clave privada"**
4. Se descarga `agencia-viajes-movil-xxxxx.json`
5. **Renombra** el archivo a: `firebase-service-account.json`
6. **Colócalo** en Spring Boot: `src/main/resources/firebase-service-account.json`

---

## 📦 PARTE 2: Configuración de Flutter

### Paso 1: Actualizar `pubspec.yaml`

Agrega estas dependencias:

```yaml
dependencies:
  flutter:
    sdk: flutter
  flutter_localizations:
    sdk: flutter
  
  # ... tus dependencias existentes ...
  
  # 🔔 Notificaciones Push
  firebase_core: ^2.24.2
  firebase_messaging: ^14.7.9
  flutter_local_notifications: ^16.3.0
```

Luego ejecuta:
```bash
flutter pub get
```

---

### Paso 2: Configurar Android

#### 2.1 Editar `android/build.gradle` (proyecto)

```gradle
buildscript {
    ext.kotlin_version = '1.9.0'
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.1.0'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        // 🔔 Añadir plugin de Firebase
        classpath 'com.google.gms:google-services:4.4.0'
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

#### 2.2 Editar `android/app/build.gradle`

```gradle
plugins {
    id "com.android.application"
    id "kotlin-android"
    id "dev.flutter.flutter-gradle-plugin"
    // 🔔 Aplicar plugin de Firebase
    id "com.google.gms.google-services"
}

android {
    namespace "com.agencia.agencia_movil"  // Verificar que coincida con Firebase
    compileSdk 34  // Android 14
    
    defaultConfig {
        applicationId "com.agencia.agencia_movil"  // Verificar que coincida
        minSdk 24      // Android 7.0 (soporta Android 10+ sin problemas)
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
        
        // 🔔 Configuración para notificaciones
        multiDexEnabled true
    }
    
    // ... resto de la configuración ...
}

dependencies {
    // 🔔 Dependencias de Firebase
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-messaging'
}
```

#### 2.3 Editar `android/app/src/main/AndroidManifest.xml`

Añade estos permisos y configuraciones:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- 🔔 Permisos para notificaciones -->
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.WAKE_LOCK"/>
    <uses-permission android:name="android.permission.VIBRATE"/>
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>

    <application
        android:label="Agencia Móvil"
        android:name="${applicationName}"
        android:icon="@mipmap/ic_launcher">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:launchMode="singleTop"
            android:theme="@style/LaunchTheme"
            android:configChanges="orientation|keyboardHidden|keyboard|screenSize|smallestScreenSize|locale|layoutDirection|fontScale|screenLayout|density|uiMode"
            android:hardwareAccelerated="true"
            android:windowSoftInputMode="adjustResize">
            
            <!-- Deep linking para abrir la app desde notificaciones -->
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
            
            <!-- 🔔 Intent filter para notificaciones -->
            <intent-filter>
                <action android:name="FLUTTER_NOTIFICATION_CLICK"/>
                <category android:name="android.intent.category.DEFAULT"/>
            </intent-filter>
        </activity>

        <!-- 🔔 Servicio de Firebase Messaging -->
        <service
            android:name="com.google.firebase.messaging.FirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT"/>
            </intent-filter>
        </service>


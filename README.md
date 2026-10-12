# Threads Tomcat

Aplicación educativa para observar cómo Spring MVC atiende peticiones HTTP concurrentes mediante el pool de hilos de Tomcat. Compara el comportamiento de un dato compartido con `static` y de un dato guardado en `ThreadLocal`, con y sin limpieza al terminar la petición.

## Tecnologías

- Java 25
- Spring Boot 4.1.1 y Spring MVC
- Tomcat embebido
- Maven Wrapper
- VisualVM y Apache JMeter para observar la JVM y generar peticiones concurrentes (herramientas opcionales, no son dependencias de la aplicación)

## Requisitos

- JDK 25 instalado y disponible en `PATH` (`JAVA_HOME` configurado si es necesario).

## Ejecución

Desde la raíz del proyecto, inicia el servidor:

```bash
./mvnw spring-boot:run
```

En Windows, utiliza:

```bat
mvnw.cmd spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

## Endpoints

Las tres rutas aceptan opcionalmente el parámetro `user`. Cada petición espera un segundo y responde con el nombre del hilo que la atendió, el usuario actual y un indicador `loggedIn`.

| Método | Ruta | Comportamiento |
| --- | --- | --- |
| `GET` | `/api/threads` | Guarda el usuario en un campo `static` compartido por todos los hilos. |
| `GET` | `/api/threads/thread-local` | Guarda el usuario en un `ThreadLocal` y no lo limpia al finalizar. |
| `GET` | `/api/threads/thread-local-clean` | Usa un `ThreadLocal` y lo limpia con `remove()` al terminar la petición. |

Ejemplos:

```text
http://localhost:8080/api/threads?user=Ana
http://localhost:8080/api/threads
http://localhost:8080/api/threads/thread-local?user=Ana
http://localhost:8080/api/threads/thread-local
http://localhost:8080/api/threads/thread-local-clean?user=Ana
http://localhost:8080/api/threads/thread-local-clean
```

La respuesta es JSON, con esta forma:

```json
{
  "threadName": "http-nio-8080-exec-1",
  "user": "Ana",
  "loggedIn": true
}
```

Si no hay usuario guardado, `user` será `null` y `loggedIn` será `false`.

## Qué se puede observar

- **`static`:** el valor se comparte entre todas las peticiones de la aplicación, no solo entre las atendidas por un mismo hilo. Una petición puede sobrescribir el valor que otra consulta; es estado compartido y no representa un mecanismo de autenticación.
- **`ThreadLocal` sin limpieza:** el valor queda asociado al hilo. Como Tomcat reutiliza hilos, una petición posterior atendida por el mismo hilo puede heredar el dato de la anterior. La respuesta incluye `threadName` para ayudar a observarlo, aunque no se garantiza qué hilo atenderá cada petición.
- **`ThreadLocal` con limpieza:** elimina el valor en un bloque `finally`, incluso si la petición termina con una excepción. Esta es la variante que muestra la importancia de limpiar el estado ligado al hilo.

Estas rutas son demostraciones didácticas; el proyecto no implementa autenticación ni incluye Spring Security. En una aplicación servlet, Spring Security normalmente asocia su contexto al hilo y lo limpia al finalizar la petición.

### Prueba con VisualVM y JMeter

1. Inicia la aplicación y conecta VisualVM al proceso de Java.
2. Configura en JMeter una petición HTTP a uno de los endpoints y varios hilos de usuario para generar concurrencia.
3. Observa los hilos del proceso en VisualVM y compara sus nombres con los campos `threadName` de las respuestas.

## Pruebas

Ejecuta la suite de pruebas con:

```bash
./mvnw test
```

## Estructura principal

```text
src/
├── main/java/es/cesguiro/threads_tomcat/
│   ├── ThreadDemoController.java  # Endpoints de demostración
│   ├── SharedUser.java            # Estado compartido con static
│   └── ThreadLocalUser.java       # Estado asociado a cada hilo
└── test/java/es/cesguiro/threads_tomcat/
    └── ThreadsTomcatApplicationTests.java
```

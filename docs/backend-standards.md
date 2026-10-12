---
description: Estándares de desarrollo de backend, buenas prácticas y convenciones para la aplicación Spring Boot.
globs: ["src/main/java/**/*.java", "src/test/java/**/*.java", "pom.xml", "build.gradle"]
alwaysApply: false
---

# Proyecto educativo: hilos en Tomcat

## Objetivo

Aplicación mínima para que el alumnado observe cómo Spring MVC procesa peticiones concurrentes con el pool de hilos de Tomcat. Las rutas de demostración mantienen ocupada la petición durante 1 segundo y devuelven en JSON el hilo y el usuario actual. Incluyen variantes con `static`, `ThreadLocal` sin limpieza y `ThreadLocal` con limpieza.

El proyecto también sirve de base para explicar estado compartido, condiciones de carrera y, en una ampliación posterior, el contexto de seguridad asociado a una petición.

## Tecnologías y herramientas

- Java 25.
- Spring Boot 4.1.1 con Spring MVC.
- Tomcat embebido como contenedor servlet.
- Maven y Maven Wrapper para construir y ejecutar.
- VisualVM para observar la JVM y sus hilos.
- Apache JMeter para generar carga concurrente.

VisualVM y JMeter se usan fuera de la aplicación y no se añaden como dependencias Maven.

## Alcance y conceptos de concurrencia

- Mantener los ejemplos pequeños y visibles; no añadir capas o dependencias que no sean necesarias para la demostración.
- El estado `static` pertenece a la clase y puede ser accedido por todos los hilos. No equivale a estado local de un hilo; el acceso concurrente mutable puede provocar condiciones de carrera.
- Tomcat reutiliza los hilos de su pool para atender distintas peticiones.
- `ThreadLocal` separa el valor por hilo, pero no por petición ni por usuario. Hay que llamar a `remove()` al terminar la petición para que la reutilización del hilo no filtre el valor a otra petición.
- En el modelo servlet habitual, Spring Security mantiene el `SecurityContext` asociado al hilo mediante `ThreadLocal` y limpia el contexto al finalizar la petición. Este proyecto no incluye Spring Security en su configuración actual.

## Pruebas

Escribir primero pruebas para el comportamiento HTTP. Ejecutar la suite con `./mvnw test` antes de dar por terminado un cambio.

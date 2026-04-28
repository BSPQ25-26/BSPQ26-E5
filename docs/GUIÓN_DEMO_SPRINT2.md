# Guión de Defensa - Sprint 2 Review Meeting
**Duración Total:** 30 minutos
**Requisito indispensable:** TODOS los miembros del equipo deben hablar y tener asignada una parte.

---

## ⚠️ IMPORTANTE ANTES DE LA DEFENSA
Todavía os queda **una cosa pendiente** que debes hacer antes de ir a presentar:
- **VisualVM Snapshot:** Tienes que arrancar el backend, abrir el programa VisualVM en tu ordenador, hacer peticiones al programa y guardar un "Snapshot" (fichero `.nps`). Si no lo haces, no tendrás qué enseñar en el paso 4.

---

## ESTRUCTURA DE LA PRESENTACIÓN

### 1. Introducción y Objetivo del Sprint 2 (1-2 mins)
- Explicar brevemente cuál era la meta principal de este Sprint (ej. "Nuestra meta era tener la integración del menú, o la pasarela de pagos, etc.").
- **Mostrar el Agile Board** (Tablero) general con las columnas.
- **Mostrar el Product Backlog** resumido.

### 2. YouTrack - Organización Scrum (3-5 mins)
*Enseñad la herramienta YouTrack en la pantalla.*
- Enseñar las **Issues** creadas.
- Explicar **cómo se han distribuido las tareas** entre los miembros del equipo.
- Mostrar el **Burndown Chart** de YouTrack y analizarlo (si hay picos o si es lineal, explicar por qué ha sido así).
- Hablar un poco sobre el proceso de *planning* y los tiempos estimados vs reales.

### 3. YouTrack - Integración con Git (2-3 mins)
*Demostrad que sabéis enlazar el código con las tareas.*
- Enseñar cómo los **commits** en GitHub están enlazados con las tareas en YouTrack.
- Mostrar los **comentarios** en los commits y en las propias tareas de YouTrack para demostrar que el equipo se comunica.

### 4. Presentación de Testing y Rendimiento (5-7 mins)
*Esta es la parte técnica fuerte que acabamos de preparar.*
- **Cobertura (JaCoCo):** Abrid el archivo `docs/reports/coverage/index.html`. Mostrad que la cobertura general de código es **superior al 50%**. Explicad que habéis hecho tests tanto positivos como negativos, cubriendo excepciones.
- **Integración (Frontend-Backend):** Mencionad que tenéis tests en el frontend que atacan directamente a los endpoints del servidor (usando `fetchThroughNode`), probando que la red y el servidor funcionan.
- **Log4J:** Enseñar rápido en el código o consola que los logs usan Log4J y no hay `System.out.println`.
- **Rendimiento (ContiPerf):** Abrid el reporte `docs/reports/performance/index.html`. 
  - Mostrad que hay peticiones con **invocaciones, hilos, average y max**.
  - Mostrad el test de **throughput** (número de operaciones por segundo).
  - Mostrad el test de **duration** que dura 10 segundos.
  - Enseñad el test que está **rojo** y explicad: *"Lo hemos forzado a fallar exigiéndole una latencia de 1 milisegundo para cumplir el requisito de tener al menos un test fallido"*.
- **Perfilado (VisualVM):** Mostrad en vivo o enseñad la captura del Snapshot de VisualVM para ver los tiempos de ejecución de los métodos de Java.

### 5. DEMO de la 2ª Release (7-10 mins)
*Hora de enseñar el producto.*
- **Flujo principal:** Enseñad la aplicación funcionando. Haced un recorrido completo (login, pedir comida, rol de restaurante/rider, etc.) según lo que hayáis implementado en este sprint.
- Si da tiempo, enseñad cómo la app maneja algún error (ej: meter mal la contraseña).

### 6. Conclusiones y Retrospectiva (2-3 mins)
- **Eventos:** ¿Qué ha pasado durante el sprint? ¿Alguien estuvo enfermo, hubo puentes, exámenes?
- **Dificultades:** Hablar de los problemas que os han surgido (ej: configurar ContiPerf, unir el frontend con el backend, conflictos de Git).

### 7. Next Steps (1 min)
- Mencionad que tras la reunión crearéis el **Tag de la Release 2** en Git.
- Confirmar que vais a hacer la Sprint 2 Retrospective y la Sprint 3 Planning Meeting (limpieza del Product Backlog).

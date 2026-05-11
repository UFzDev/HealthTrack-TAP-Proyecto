# HealthTrack Community: Especificaciones del Sistema

Plataforma colaborativa para el monitoreo y prevención de enfermedades crónicas como **hipertensión**, **diabetes** y **obesidad**.

---

## 1. Aplicación de Escritorio (JavaFX)

### Módulos Principales

#### 1.1 Gestión de Usuarios
- Registro y login con roles diferenciados:
  - Paciente
  - Médico
  - Administrador

#### 1.2 Registro de Métricas
Debe incluir **fecha y hora automática** para los siguientes indicadores:
- Presión arterial (sistólica/diastólica)
- Glucosa
- Peso e Índice de Masa Corporal (IMC)
- Frecuencia cardíaca

#### 1.3 Visualización Avanzada
- Gráficos de evolución (línea y barras)
- Cálculo de promedios y tendencias
- Alertas visuales automáticas ante valores críticos

#### 1.4 Reportes
- Generación de historial clínico en PDF con gráficos embebidos
- Exportación de datos a Excel

#### 1.5 Recomendaciones Inteligentes
Basadas en reglas y consumo de servicios web (APIs):
- **API de Salud Pública**: Para recomendaciones oficiales
- **APIs de Clima**: Para sugerir actividades físicas (ej. si llueve, sugerir ejercicio en casa)
- **APIs Nutricionales**: Para sugerencias alimenticias (ej. reducir sodio ante presión elevada)

---

## 2. Aplicación Móvil Complementaria (Kotlin)

### Tecnología
Desarrollada nativamente en **Kotlin** para Android.

### Funcionalidades
- Registro rápido de datos de salud
- Visualización básica de métricas registradas
- Notificaciones y recordatorios para el usuario
- Sincronización en tiempo real con la aplicación de escritorio a través de **Firebase**

---

## 3. Enfoque Colaborativo y Sincronización

El sistema debe permitir la interacción entre diferentes actores:

- **Pacientes**: Comparten sus datos en tiempo real con sus médicos asignados
- **Médicos**: Capacidad de monitorear a múltiples pacientes de forma simultánea desde la app de escritorio
- **Familiares**: Pueden recibir alertas si el sistema detecta tendencias peligrosas o valores críticos  

---

## 4. Estado de Cumplimiento (Iteración Actual)

| Requerimiento | Estado | Evidencia actual |
|---|---|---|
| Registro y login por roles (Paciente, Médico, Administrador) | Cumplido | Flujos existentes de login/registro y carga por rol en `MainContainerController` |
| Registro de métricas con fecha/hora automática | Parcial | Interfaz de captura y timestamp automático implementados en dashboard de paciente; falta persistencia en backend |
| Métricas: PA, glucosa, peso/IMC, frecuencia cardíaca | Parcial | Campos y visualización presentes en UI; falta modelo/DAO dedicado para almacenamiento histórico |
| Gráficos de línea y barras | Cumplido (UI) | Dashboard de paciente incluye tendencia (línea) y promedios (barras) |
| Cálculo de promedios y tendencias | Parcial | Cálculo básico y visual en controlador; falta cálculo sobre dataset real |
| Alertas visuales automáticas de valores críticos | Cumplido (UI) | Reglas de alerta en paciente + tabla de alertas críticas en médico |
| Reporte PDF con gráficos embebidos | Parcial | Interfaz y acciones de exportación listas; falta integración completa con servicio de reportes clínicos |
| Exportación a Excel | Parcial | Acción de UI lista; falta conexión final con exportador de métricas clínicas |
| Recomendaciones inteligentes (APIs salud, clima, nutrición) | Parcial | Panel de recomendaciones visible; falta integración real con APIs externas |
| Monitoreo simultáneo de múltiples pacientes por médico | Cumplido (UI) | Tabla de pacientes y alertas en dashboard médico |
| Alertas para familiares | Pendiente | Campo/modelo de contacto existe, falta flujo de notificación |

---

## 5. Diseño de Interfaces (Implementado en esta iteración)

### 5.1 Panel Paciente
- Tarjetas de métricas clave: presión arterial, glucosa, IMC y frecuencia cardíaca.
- Formulario de registro rápido con campos para PA, glucosa, peso, altura y FC.
- Fecha y hora automática del último registro.
- Estado visual de riesgo: normal, advertencia y crítico.
- Visualización avanzada:
  - Gráfico de línea para evolución.
  - Gráfico de barras para promedios por métrica.
  - Selector de periodo (semana/mes/año).

### 5.2 Panel Médico
- Resumen operativo (pacientes activos, alertas altas, revisiones pendientes).
- Tabla de alertas críticas con paciente, lectura, nivel de riesgo y actualización.
- Tabla de pacientes asignados con condición, última medición y tendencia.
- Gráfico de tendencia poblacional (glucosa promedio).
- Panel de recomendaciones del día y acción para exportar resumen PDF.

### 5.3 Panel Administrador
- KPIs de operación (usuarios, médicos activos, reportes diarios).
- Estado de sincronización (Firebase) y estado de APIs externas.
- Registro de logs de actividad.
- Acciones rápidas de exportación (PDF/Excel) y verificación de sincronización.

---

## 6. Brechas Prioritarias para Cierre

1. Persistir métricas clínicas (histórico por paciente) con modelo/DAO dedicado.
2. Conectar dashboards con datos reales de Firestore en lugar de datos de muestra.
3. Integrar flujo completo de reportes clínicos PDF/Excel usando métricas reales.
4. Implementar motor de recomendaciones con APIs de salud, clima y nutrición.
5. Activar notificaciones para familiares ante tendencias o umbrales críticos.


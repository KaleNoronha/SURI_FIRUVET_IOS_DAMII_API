# Suri Firuvet API

Backend REST de la plataforma **Suri Firuvet**, una aplicación móvil iOS para la gestión de citas veterinarias. Permite a los dueños de mascotas registrarse, gestionar sus mascotas y agendar citas en las clínicas veterinarias Firuvet.

---

## Visión del Producto

Suri Firuvet nace de la necesidad de centralizar la atención veterinaria para dueños de mascotas que buscan una experiencia digital, rápida y confiable. A través de la app móvil, un usuario puede registrarse con su cuenta de Firebase, agregar sus mascotas y agendar citas en cualquiera de las clínicas Firuvet disponibles en Lima.

El sistema no solo gestiona las operaciones del día a día, sino que también mantiene un historial completo de auditoría de cada acción realizada, lo que permite a los administradores tener trazabilidad total del sistema.

---

## Historias de Usuario

### HU-01 — Registro de cliente
> Como dueño de mascota, quiero registrarme en la plataforma usando mi cuenta de Firebase, para tener un perfil asociado a mis mascotas y citas.

**Criterios de aceptación:**
- El cliente se registra enviando sus datos personales junto con el `uid` generado por Firebase
- No se permite registrar dos clientes con el mismo `uid` — el sistema responde con `409 Conflict`
- Los campos `nombCli`, `apeCli` y `uid` son obligatorios — el sistema valida y responde `400` si faltan
- Al registrarse exitosamente, el sistema devuelve el objeto cliente con su `id` de Postgres
- Se genera automáticamente un evento de auditoría `CLIENTE_CREADO` publicado en RabbitMQ

---

### HU-02 — Consulta de perfil
> Como cliente registrado, quiero consultar mi perfil en la plataforma, para verificar mis datos personales.

**Criterios de aceptación:**
- El cliente puede consultar su perfil enviando su `id` de Postgres
- Si el cliente no existe, el sistema responde con `404 Not Found`
- La respuesta incluye nombre completo calculado dinámicamente

---

### HU-03 — Gestión de mascotas
> Como cliente registrado, quiero agregar, editar y eliminar mis mascotas, para tener un registro actualizado de mis animales.

**Criterios de aceptación:**
- Solo el dueño de la mascota puede modificarla o eliminarla — verificado por `idCliente`
- Cada mascota tiene un tipo asociado (perro, gato, conejo, hámster, ave)
- Una mascota puede tener apodos y alergias registradas
- Si el cliente o tipo de mascota no existen al crear, el sistema responde `400`
- Si se intenta modificar/eliminar una mascota de otro cliente, responde `403 Forbidden`
- Se registra un evento de auditoría por cada operación: `MASCOTA_CREADA`, `MASCOTA_MODIFICADA`, `MASCOTA_ELIMINADA`

---

### HU-04 — Agendamiento de citas
> Como cliente, quiero agendar una cita veterinaria para mi mascota en la clínica de mi preferencia, para garantizar una atención oportuna.

**Criterios de aceptación:**
- La cita debe incluir tipo de cita, fecha, mascota, cliente y clínica
- El cliente debe existir en el sistema para poder crear una cita
- Solo el dueño puede modificar o cancelar su cita — verificado por `idCliente`
- Si se intenta modificar/eliminar una cita de otro cliente, responde `403 Forbidden`
- Se registra un evento de auditoría por cada operación: `CITA_CREADA`, `CITA_MODIFICADA`, `CITA_ELIMINADA`

---

### HU-05 — Auditoría de movimientos
> Como administrador, quiero consultar el historial de todas las operaciones realizadas en el sistema, para tener trazabilidad completa de los cambios.

**Criterios de aceptación:**
- Cada creación, modificación o eliminación publica un `AuditoriaEvent` en RabbitMQ
- El evento es consumido asincrónicamente y persistido en la tabla `evento_log`
- Se puede consultar el historial completo, filtrado por cliente, módulo o entidad específica
- Cada log incluye el payload JSON completo de la entidad en el momento del evento

---

## Arquitectura

```
App Móvil (iOS)
      |
      | HTTP/REST
      ▼
┌─────────────────────────────┐
│      Suri Firuvet API        │
│       (Quarkus JVM)          │
│                             │
│  Resources → Services → EM  │
│                             │
│  Publisher ──► RabbitMQ     │
│  Consumer  ◄── RabbitMQ     │
└─────────────────────────────┘
      |                  |
      ▼                  ▼
 PostgreSQL          evento_log
 (Supabase)         (auditoría)
```

| Componente | Tecnología | Proveedor |
|---|---|---|
| API | Quarkus 3.36.3 JVM | Render |
| Base de datos | PostgreSQL | Supabase |
| Mensajería | RabbitMQ | CloudAMQP |
| Autenticación | Firebase Auth | Google |

---

## Lógica de Negocio

### Flujo de registro de cliente
1. Firebase autentica al usuario en la app y devuelve un `uid`
2. La app llama `POST /api/clientes` enviando los datos personales y el `uid`
3. La API valida que no exista otro cliente con ese `uid`
4. Se persiste el cliente en Postgres y se genera un `id` numérico
5. La app almacena ese `id` para todas las operaciones posteriores
6. Se publica el evento `CLIENTE_CREADO` en RabbitMQ

### Flujo de gestión de mascotas
1. Con el `id` del cliente, la app lista sus mascotas via `GET /api/mascotas?idCliente=`
2. Para agregar una mascota, la app envía `POST /api/mascotas` con `idCliente` e `idTipoMascota`
3. La API valida que el cliente y el tipo de mascota existan
4. Se persiste la mascota y se publica el evento correspondiente

### Flujo de agendamiento de cita
1. La app lista catálogos: tipos de cita, clínicas y mascotas del cliente
2. El cliente selecciona mascota, tipo de cita, clínica y fecha
3. La app llama `POST /api/citas` con el `idCliente`
4. La API valida que el cliente exista, persiste la cita y publica `CITA_CREADA`

### Flujo de auditoría
1. Cada operación CRUD en services publica un `AuditoriaEvent` a RabbitMQ via `AuditoriaEventPublisher`
2. `AuditoriaEventConsumer` recibe el mensaje del exchange `suri.auditoria`
3. Llama a `EventoLogService.registrarAuditoria()` que persiste el evento en `evento_log`
4. El payload JSON completo de la entidad queda guardado en `datos_json`

### Manejo de errores
Todos los errores de persistencia y restricciones de BD son capturados por `GlobalExceptionHandler`, que devuelve respuestas JSON estructuradas con mensajes legibles en lugar de stack traces.

---

## Endpoints

### Clientes `/api/clientes`
| Método | Ruta | Body | Respuesta | Descripción |
|--------|------|------|-----------|-------------|
| GET | `/` | — | `ClienteDTO[]` | Listar todos los clientes |
| GET | `/{id}` | — | `ClienteDTO` | Obtener cliente por id |
| POST | `/` | `ClienteRequest` | `ClienteDTO` | Registrar nuevo cliente |
| PUT | `/{id}` | `ClienteRequest` | `ClienteDTO` | Modificar cliente |
| DELETE | `/{id}` | — | `204` | Eliminar cliente |

**ClienteRequest:**
```json
{
  "nombCli": "Kaled",
  "apeCli": "Noronha",
  "fecNac": "15/05/2000",
  "uid": "firebase-uid-aqui"
}
```

**ClienteDTO:**
```json
{
  "id": 1,
  "nombCli": "Kaled",
  "apeCli": "Noronha",
  "fecNac": "2000-05-15",
  "uid": "firebase-uid-aqui",
  "nombreCompleto": "Kaled Noronha"
}
```

---

### Mascotas `/api/mascotas`
| Método | Ruta | Body | Respuesta | Descripción |
|--------|------|------|-----------|-------------|
| GET | `/?idCliente=` | — | `MascotaDTO[]` | Listar mascotas por cliente |
| GET | `/{id}` | — | `MascotaDTO` | Obtener mascota por id |
| POST | `/` | `MascotaRequest` | `MascotaDTO` | Registrar nueva mascota |
| PUT | `/{id}` | `MascotaRequest` | `MascotaDTO` | Modificar mascota |
| DELETE | `/{id}?idCliente=` | — | `204` | Eliminar mascota |

**MascotaRequest:**
```json
{
  "nombMas": "Firulais",
  "idTipoMascota": 1,
  "idCliente": 1
}
```

**MascotaDTO:**
```json
{
  "id": 1,
  "nombMas": "Firulais",
  "idTipoMascota": 1,
  "nombreTipo": "Perro",
  "idCliente": 1
}
```

---

### Citas `/api/citas`
| Método | Ruta | Body | Respuesta | Descripción |
|--------|------|------|-----------|-------------|
| GET | `/?idCliente=` | — | `CitaDTO[]` | Listar citas por cliente |
| GET | `/{id}` | — | `CitaDTO` | Obtener cita por id |
| POST | `/` | `CitaRequest` | `CitaDTO` | Crear cita |
| PUT | `/{id}` | `CitaRequest` | `200` | Modificar cita |
| DELETE | `/{id}?idCliente=` | — | `204` | Eliminar cita |

**CitaRequest:**
```json
{
  "idTipoCita": 1,
  "fecha": "2025-07-10T09:00:00",
  "comentario": "Revisión general",
  "idMascota": 1,
  "idCliente": 1,
  "idClinica": 1
}
```

**CitaDTO:**
```json
{
  "idCita": 1,
  "nombreTipoCita": "Consulta General",
  "fecha": "2025-07-10T09:00:00",
  "comentario": "Revisión general",
  "idMascota": 1,
  "nombreMascota": "Firulais",
  "idCliente": 1,
  "nombreCliente": "Kaled Noronha",
  "idClinica": 1,
  "nombreClinica": "Firuvet San Miguel"
}
```

---

### Catálogos
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/tipos-cita` | Listar tipos de cita |
| GET | `/api/tipos-mascota` | Listar tipos de mascota |
| GET | `/api/clinicas` | Listar clínicas |

---

### Auditoría `/api/logs`
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/` | Todos los logs |
| GET | `/cliente/{idCliente}` | Logs por cliente |
| GET | `/modulo/{modulo}` | Logs por módulo (`CLIENTES`, `MASCOTAS`, `CITAS`) |
| GET | `/entidad/{entidad}/registro/{id}` | Logs por entidad y id de registro |

---

### Health
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/q/health` | Estado general |
| GET | `/q/health/live` | Liveness |
| GET | `/q/health/ready` | Readiness (BD + RabbitMQ) |

---

## Detalle Técnico

### Stack
| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Quarkus | 3.36.3 | Framework |
| Hibernate ORM | — | Persistencia (EntityManager, sin Panache) |
| RESTEasy JAX-RS | — | Endpoints REST |
| Jackson | — | Serialización JSON |
| SmallRye Reactive Messaging | — | Integración RabbitMQ |
| SmallRye Health | — | Health checks |
| SmallRye OpenAPI | — | Swagger UI |
| Lombok | 1.18.36 | Reducción de boilerplate |

### Estructura de paquetes
```
com.surifiruvet
├── entity/           # Entidades JPA (@Getter @Setter)
│   ├── Cliente
│   ├── Mascota       # Incluye apodos y alergias
│   ├── Cita
│   ├── Clinica
│   ├── TipoCita
│   ├── TipoMascota
│   └── EventoLog     # Tabla de auditoría
├── dto/              # DTOs (@Data) y Requests (@Data)
│   ├── ClienteDTO    # Incluye getNombreCompleto() calculado
│   ├── ClienteRequest
│   ├── MascotaDTO
│   ├── MascotaRequest
│   ├── CitaDTO
│   └── CitaRequest
├── resource/         # Endpoints JAX-RS (@ApplicationScoped)
│   ├── ClienteResource
│   ├── MascotaResource
│   ├── CitaResource
│   ├── CatalogoResource
│   ├── ClinicaResource
│   └── EventoLogResource
├── service/          # Lógica de negocio (@ApplicationScoped)
│   ├── ClienteService
│   ├── MascotaService
│   ├── CitaService
│   ├── CatalogoService
│   ├── ClinicaService
│   └── EventoLogService
├── messaging/        # Integración RabbitMQ
│   ├── AuditoriaEvent       # record Java con factory method crear()
│   ├── AuditoriaEventPublisher  # Emitter al canal auditoria-out
│   └── AuditoriaEventConsumer   # @Incoming auditoria-in
├── exception/
│   └── GlobalExceptionHandler   # Manejo global de errores JPA
└── health/
    └── ApiHealthCheck            # @Liveness check
```

### Convenciones Lombok
| Tipo de clase | Anotación |
|---|---|
| Entidades JPA | `@Getter` + `@Setter` (evita problemas con Hibernate lazy loading) |
| DTOs | `@Data` |
| Requests | `@Data` |
| Records | Sin Lombok (Java records nativos) |

### Schema de BD
```sql
-- Tablas principales (todas con id BIGINT)
cliente       (id, nombcli, apecli, fecnac DATE, uid VARCHAR UNIQUE)
tipo_mascota  (id, nombre)
tipo_cita     (id, nombre)
clinica       (id, nombre, direccion)
mascota       (id, nombmas, tipomas→tipo_mascota, idcliente→cliente, apodos, alergias)
cita          (idcita, tipocita→tipo_cita, fecha, comentario, idmascota→mascota, idcliente→cliente, idclinica→clinica)

-- Auditoría
evento_log (
  id BIGINT PK,
  tipo_evento VARCHAR(100) NOT NULL,
  modulo      VARCHAR(100),
  accion      VARCHAR(100),
  entidad     VARCHAR(100),
  id_registro BIGINT,
  uid         VARCHAR(255),
  descripcion VARCHAR(255),
  datos_json  TEXT,
  estado      VARCHAR(50) NOT NULL,
  creado_en   TIMESTAMP   -- auto via @PrePersist
)

-- Índices
CREATE INDEX idx_evento_log_uid      ON evento_log (uid);
CREATE INDEX idx_evento_log_entidad  ON evento_log (entidad, id_registro);
CREATE INDEX idx_evento_log_modulo   ON evento_log (modulo);
```

### Mensajería RabbitMQ
```
Exchange:     suri.auditoria (direct)
Routing key:  auditoria.movimiento
Cola:         suri.auditoria.movimientos
Failure:      reject
```

El `AuditoriaEvent` es un Java record con los campos:
`tipoEvento`, `modulo`, `accion`, `entidad`, `idRegistro`, `uid`, `descripcion`, `datosJson`, `generadoEn`

Tipos de evento publicados:
- `CLIENTE_CREADO`, `CLIENTE_MODIFICADO`, `CLIENTE_ELIMINADO`
- `MASCOTA_CREADA`, `MASCOTA_MODIFICADA`, `MASCOTA_ELIMINADA`
- `CITA_CREADA`, `CITA_MODIFICADA`, `CITA_ELIMINADA`

### Variables de entorno
```env
DB_USER=
DB_PASSWORD=
DB_URL=jdbc:postgresql://<host>:<port>/<db>
RABBITMQ_HOST=
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=
RABBITMQ_PASSWORD=
RABBITMQ_VIRTUAL_HOST=
```

### Correr en local
```powershell
# 1. Crear .env con las variables de entorno
# 2. Cargar variables y levantar en modo dev:
foreach($line in Get-Content .env){ $parts = $line -split '=',2; if($parts[0] -ne ''){ [System.Environment]::SetEnvironmentVariable($parts[0], $parts[1]) } }; ./mvnw quarkus:dev
```

### Swagger UI
```
http://localhost:8080/q/swagger-ui
```

### Health Check
```
http://localhost:8080/q/health
http://localhost:8080/q/health/live
http://localhost:8080/q/health/ready
```

### Despliegue en Render
- Modo: **JVM** (quarkus-run.jar)
- Las variables de entorno se configuran en el dashboard de Render
- El servicio se redespliega automáticamente con cada push a la rama configurada

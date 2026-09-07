# prueba_profa_back

API REST de **asistencia de empleados** para la prueba técnica de Profamilia.

Permite registrar empleados, consultarlos y marcar si están presentes o ausentes. El frontend previsto es Angular en `http://localhost:4200`.

---

## Tabla de contenidos

1. [Stack](#stack)
2. [Arquitectura e implementación](#arquitectura-e-implementación)
3. [Modelo de datos](#modelo-de-datos)
4. [Requisitos](#requisitos)
5. [Configuración](#configuración)
6. [Cómo ejecutar](#cómo-ejecutar)
7. [Contratos (DTOs)](#contratos-dtos)
8. [Endpoints](#endpoints)
9. [Casos de uso](#casos-de-uso)
10. [Manejo de errores](#manejo-de-errores)
11. [CORS](#cors)
12. [Pruebas](#pruebas)
13. [Decisiones y limitaciones](#decisiones-y-limitaciones)

---

## Stack

| Pieza | Valor |
| --- | --- |
| Lenguaje | Java 17 |
| Framework | Spring Boot 4.1.1 |
| Build | Maven Wrapper (`mvnw` / `mvnw.cmd`) |
| API | Spring Web MVC |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL 8 (`profamilia_asistencia_db`) |
| Utilidades | Lombok |
| Puerto | `8080` (por defecto de Spring Boot) |

Artefacto Maven: `profamilia:prueba:0.0.1-SNAPSHOT`.

---

## Arquitectura e implementación

El código sigue capas clásicas de Spring. El Controller no contiene reglas de negocio; la Entity no sale por la API.

```
Cliente HTTP (Angular / curl / Postman)
        │
        ▼
┌───────────────────┐
│  EmployController │  HTTP, status codes
└─────────┬─────────┘
          ▼
┌───────────────────┐
│  EmployService    │  casos de uso
│  EmployServiceImpl│
└─────────┬─────────┘
          ├──────────────────┐
          ▼                  ▼
┌─────────────────┐  ┌──────────────────┐
│  EmployMapper   │  │ EmployRepository │
│  DTO ↔ Entity   │  │ JPA / JPQL       │
└─────────────────┘  └────────┬─────────┘
                              ▼
                         MySQL
                      tabla empleados
```

### Paquetes (`profamilia.prueba`)

| Paquete | Rol |
| --- | --- |
| `controller` | Endpoints REST |
| `service` / `service.impl` | Interfaz + lógica de negocio |
| `mapper` | Conversión Entity ↔ DTO |
| `repository` | Acceso a datos |
| `model` | Entidades JPA (no se exponen) |
| `dto` | Contratos de entrada y salida |
| `config` | CORS |

Punto de entrada: `profamilia.prueba.PruebaApplication`.

### Flujo interno por operación

**Listar**

1. `GET /api/employees`
2. `EmployServiceImpl.getAllEmployees()` (`@Transactional(readOnly = true)`)
3. `employRepository.findAll()`
4. Cada `EmployEntity` se convierte con `EmployMapper.toDto`

**Crear**

1. `POST /api/employees` con `EmployRequestDTO`
2. `EmployMapper.toEntity`: copia `fullName` y `position`, **fuerza `present = false`**
3. `employRepository.save`
4. Respuesta `201` con `EmployResponseDTO` (incluye el `id` generado)

**Cambiar asistencia**

1. `PUT /api/employees/{id}/status` con body `true` o `false`
2. Busca el empleado; si no existe → `RuntimeException` → **404**
3. Si `present` actual es igual al solicitado → `IllegalStateException` → **409**
4. `employRepository.updateStatusById` (JPQL `@Modifying`)
5. Actualiza el objeto en memoria y responde `200` con el DTO

---

## Modelo de datos

Hibernate crea o actualiza el esquema (`ddl-auto: update`). No hay scripts SQL versionados.

**Tabla `empleados`**

| Columna MySQL | Campo Java | Tipo | Restricciones |
| --- | --- | --- | --- |
| `id` | `id` | `BIGINT` / `Long` | PK, `IDENTITY` |
| `nombre_completo` | `fullName` | `VARCHAR(150)` | `NOT NULL` |
| `puesto` | `position` | `VARCHAR(100)` | `NOT NULL` |
| `presente` | `present` | `BOOLEAN` | `NOT NULL`, default `false` |

Entidad: `EmployEntity`.

Actualización de estado (JPQL):

```sql
UPDATE EmployEntity e SET e.present = :present WHERE e.id = :id
```

---

## Requisitos

- JDK 17+
- MySQL escuchando en `localhost:3306`
- Base de datos creada:

```sql
CREATE DATABASE profamilia_asistencia_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

No hace falta crear la tabla a mano: Hibernate la genera al arrancar.

---

## Configuración

Archivo: `src/main/resources/application.yaml`

```yaml
spring:
  application:
    name: prueba
  datasource:
    url: jdbc:mysql://localhost:3306/profamilia_asistencia_db
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

Credenciales actuales de desarrollo: usuario `root`, contraseña `123456`. Cámbialas según tu MySQL local.

---

## Cómo ejecutar

Desde la raíz del repositorio.

**Windows (PowerShell)**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS**

```bash
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080`.

Tests:

```powershell
.\mvnw.cmd test
```

---

## Contratos (DTOs)

Las entidades JPA no viajan en JSON. La API usa:

### `EmployRequestDTO` (entrada de creación)

```json
{
  "fullName": "Ana Pérez",
  "position": "Enfermera"
}
```

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `fullName` | `string` | Nombre completo |
| `position` | `string` | Puesto |

No incluye `present`: el sistema lo pone en `false`.

Hoy no hay Bean Validation (`@NotBlank`, etc.). Un body vacío o nulos se persisten si MySQL/Hibernate lo permiten; `fullName` y `position` son `nullable = false` a nivel de columna.

### `EmployResponseDTO` (salida)

```json
{
  "id": 1,
  "fullName": "Ana Pérez",
  "position": "Enfermera",
  "present": false
}
```

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `id` | `number` | Identificador generado |
| `fullName` | `string` | Nombre completo |
| `position` | `string` | Puesto |
| `present` | `boolean` | `true` = presente, `false` = ausente |

---

## Endpoints

Base: `http://localhost:8080`

`Content-Type: application/json`

| Método | Ruta | Descripción | Éxito |
| --- | --- | --- | --- |
| `GET` | `/api/employees` | Listar todos los empleados | `200` |
| `POST` | `/api/employees` | Crear empleado | `201` |
| `PUT` | `/api/employees/{id}/status` | Cambiar asistencia | `200` |

No hay autenticación. No hay `DELETE`, ni `GET` por id, ni actualización de nombre/puesto.

---

### `GET /api/employees`

Devuelve todos los registros, sin paginación ni filtros.

**Request**

```http
GET /api/employees HTTP/1.1
Host: localhost:8080
```

**Response `200`**

```json
[
  {
    "id": 1,
    "fullName": "Ana Pérez",
    "position": "Enfermera",
    "present": false
  },
  {
    "id": 2,
    "fullName": "Carlos Gómez",
    "position": "Médico",
    "present": true
  }
]
```

Lista vacía: `[]`.

```powershell
curl http://localhost:8080/api/employees
```

---

### `POST /api/employees`

Crea un empleado. `present` siempre nace en `false`.

**Request**

```http
POST /api/employees HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "fullName": "Ana Pérez",
  "position": "Enfermera"
}
```

**Response `201 Created`**

```json
{
  "id": 1,
  "fullName": "Ana Pérez",
  "position": "Enfermera",
  "present": false
}
```

```powershell
curl -X POST http://localhost:8080/api/employees `
  -H "Content-Type: application/json" `
  -d "{\"fullName\":\"Ana Pérez\",\"position\":\"Enfermera\"}"
```

---

### `PUT /api/employees/{id}/status`

Cambia solo el flag de asistencia. El body es un **booleano JSON**, no un objeto.

**Request**

```http
PUT /api/employees/1/status HTTP/1.1
Host: localhost:8080
Content-Type: application/json

true
```

**Response `200`**

```json
{
  "id": 1,
  "fullName": "Ana Pérez",
  "position": "Enfermera",
  "present": true
}
```

**Errores**

| Situación | Status | Body (texto plano) |
| --- | --- | --- |
| El `id` no existe | `404` | `Employee not found` |
| Ya está en ese estado | `409` | `Employee is already active (present)` o `Employee is already inactive (absent)` |

```powershell
curl -X PUT http://localhost:8080/api/employees/1/status `
  -H "Content-Type: application/json" `
  -d "true"
```

---

## Casos de uso

### UC-01 — Registrar un empleado nuevo

**Actor:** personal administrativo (o el frontend Angular).

**Precondición:** la API y MySQL están en ejecución.

**Flujo principal**

1. El actor envía `POST /api/employees` con nombre y puesto.
2. El mapper construye `EmployEntity` con `present = false`.
3. Se persiste en `empleados`.
4. Se responde `201` con el empleado creado (incluye `id`).

**Resultado:** el empleado queda registrado como **ausente**.

**Regla:** el cliente no puede crear a alguien ya marcado como presente.

---

### UC-02 — Consultar la lista de asistencia

**Actor:** cualquier cliente HTTP.

**Flujo principal**

1. `GET /api/employees`.
2. El servicio lee todos los registros y los mapea a DTO.

**Resultado:** arreglo JSON con `id`, nombre, puesto y `present`.

**Variante:** si no hay empleados, se responde `200` con `[]`.

---

### UC-03 — Marcar llegada (ausente → presente)

**Actor:** quien registra asistencia.

**Precondición:** existe un empleado con `id = 1` y `present = false`.

**Flujo principal**

1. `PUT /api/employees/1/status` con body `true`.
2. El servicio comprueba que el estado actual es distinto.
3. Ejecuta el `UPDATE` JPQL y responde el DTO con `present: true`.

**Resultado:** el empleado queda **presente**.

---

### UC-04 — Marcar salida (presente → ausente)

Igual que UC-03, con body `false`.

**Precondición:** `present = true`.

**Resultado:** `present: false`. Mensaje de conflicto si ya estaba ausente: `Employee is already inactive (absent)`.

---

### UC-05 — Intento de marcar el mismo estado (conflicto)

**Precondición:** el empleado `1` ya está presente.

**Flujo**

1. `PUT /api/employees/1/status` con `true`.
2. El servicio detecta `employee.getPresent().equals(present)`.
3. Lanza `IllegalStateException`.
4. El controller responde **409 Conflict**.

**Body**

```
Employee is already active (present)
```

Evita writes redundantes y deja claro al cliente que no hubo cambio.

---

### UC-06 — Cambiar estado de un empleado inexistente

**Flujo**

1. `PUT /api/employees/999/status` con `true` o `false`.
2. `findById` no encuentra fila → `RuntimeException("Employee not found")`.
3. El controller responde **404 Not Found**.

**Body**

```
Employee not found
```

---

### Escenario de integración típico (frontend)

Orden sugerido para el cliente Angular:

1. Al cargar el tablero → `GET /api/employees`.
2. Formulario “nuevo empleado” → `POST /api/employees` → añadir el objeto `201` a la lista (nace ausente).
3. Toggle de asistencia → `PUT /api/employees/{id}/status` con el **booleano opuesto** al actual.
4. Si llega `409`, no cambiar la UI: el servidor ya tenía ese estado.
5. Si llega `404`, quitar o recargar el registro (el id ya no existe).

---

## Manejo de errores

No hay `@ControllerAdvice` global. Solo `PUT /{id}/status` captura excepciones en el controller.

| Excepción | Origen | HTTP | Body |
| --- | --- | --- | --- |
| `IllegalStateException` | Estado de asistencia igual al solicitado | `409` | mensaje en texto |
| `RuntimeException` | Empleado no encontrado | `404` | `Employee not found` |
| Otras (JSON inválido, etc.) | Spring MVC | `400` / `500` | error por defecto de Spring |

`GET` y `POST` no tienen `try/catch` propio.

`IllegalStateException` extiende `RuntimeException`. El `catch` de `IllegalStateException` va **antes** del de `RuntimeException`, de modo que el 409 no se convierte en 404.

---

## CORS

`SecurityConfig` declara origen `http://localhost:4200`, métodos `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`, headers `*` y `allowCredentials: true`.

No hay dependencia de Spring Security en el `pom.xml`. El bean usa `CorsConfigurationSource` del paquete **reactivo** (`web.cors.reactive`) mientras la API corre sobre **Web MVC (servlet)**. Si el navegador bloquea CORS, hay que alinear esa configuración con el stack servlet (`org.springframework.web.cors`).

---

## Pruebas

`src/test/java/profamilia/prueba/PruebaApplicationTests` solo comprueba que el contexto de Spring arranca.

No hay tests de controller, servicio ni repositorio.

---

## Decisiones y limitaciones

| Tema | Estado actual |
| --- | --- |
| Nombres | Clases `Employ*`; rutas `/api/employees` |
| Idioma | Campos JSON en inglés; columnas SQL en español |
| Validación de entrada | Sin `@Valid` / Bean Validation |
| Body de status | `Boolean` crudo, no DTO |
| Autenticación | No implementada |
| Paginación / filtros | No |
| GET por id / DELETE / update de datos | No |
| Excepciones de dominio | `RuntimeException` / `IllegalStateException`, no jerarquía propia |
| Esquema | `ddl-auto: update` (adecuado para demo, no para producción) |

Principios del proyecto (`.sdd/constitution.md`): controllers sin negocio, entities fuera de la API, DTOs en la frontera, lógica en Service, persistencia solo en Repository, mapeo en Mapper.

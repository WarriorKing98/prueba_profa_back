# Arquitectura

## Contexto

API REST para gestionar empleados y su estado de asistencia (presente / ausente). El frontend previsto es Angular en `http://localhost:4200`.

## Stack técnico actual

| Pieza | Valor |
| --- | --- |
| Lenguaje | Java 17 |
| Framework | Spring Boot 4.1.1 |
| Build | Maven (`mvnw`) |
| API | Spring Web MVC (`spring-boot-starter-webmvc`) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL (`profamilia_asistencia_db`) |
| Driver | `mysql-connector-j` |
| Utilidades | Lombok |
| Tests | `spring-boot-starter-webmvc-test`, `spring-boot-starter-data-jpa-test` |

Configuración en `src/main/resources/application.yaml`:

- Datasource: `jdbc:mysql://localhost:3306/profamilia_asistencia_db`
- `hibernate.ddl-auto: update`
- `show-sql: true`

## Paquete raíz

`profamilia.prueba`

Punto de entrada: `PruebaApplication`.

## Capas

Flujo de una petición:

```
Cliente HTTP
    → Controller
        → Service (interfaz + impl)
            → Mapper (DTO ↔ Entity)
            → Repository
                → MySQL
```

| Capa | Paquete | Responsabilidad |
| --- | --- | --- |
| Controller | `controller` | Endpoints HTTP, status codes, sin lógica de negocio |
| Service | `service` / `service.impl` | Casos de uso y reglas de negocio |
| Mapper | `mapper` | Conversión Entity ↔ DTO |
| Repository | `repository` | Acceso a datos con Spring Data JPA |
| Model | `model` | Entidades JPA; no se exponen en la API |
| DTO | `dto` | Contratos de entrada y salida |
| Config | `config` | Configuración de aplicación (CORS) |

## Módulo de empleados

Único dominio implementado. Recurso HTTP: `/api/employees`.

| Método | Ruta | Acción |
| --- | --- | --- |
| `GET` | `/api/employees` | Listar todos los empleados |
| `POST` | `/api/employees` | Crear empleado (`present` inicia en `false`) |
| `PUT` | `/api/employees/{id}/status` | Actualizar asistencia; 409 si el estado no cambia; 404 si no existe |

Contratos:

- Entrada de creación: `EmployRequestDTO` (`fullName`, `position`)
- Salida: `EmployResponseDTO` (`id`, `fullName`, `position`, `present`)

Persistencia:

- Entidad: `EmployEntity`
- Tabla: `empleados`
- Columnas: `id`, `nombre_completo`, `puesto`, `presente`
- Repositorio: `EmployRepository` (`JpaRepository` + `updateStatusById`)

## CORS

`SecurityConfig` registra orígenes permitidos para `http://localhost:4200`, métodos `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`, headers `*` y credenciales.

No hay Spring Security en `pom.xml`. El bean usa `CorsConfigurationSource` reactivo (`web.cors.reactive`); el stack de la API es servlet (Web MVC).

## Manejo de errores (estado actual)

No existe `@ControllerAdvice`. `EmployController` captura excepciones solo en `PUT /{id}/status`:

- `RuntimeException` → 404
- `IllegalStateException` → 409 (empleado ya está en ese estado)

`EmployServiceImpl` lanza esas excepciones desde el caso de uso.

## Pruebas

`PruebaApplicationTests` solo verifica que el contexto de Spring arranca. No hay tests de controlador, servicio ni repositorio.

## Fuera de alcance actual

- Autenticación / autorización
- Validación Bean Validation en DTOs
- Soft delete o auditoría
- Paginación
- Módulos distintos de empleados

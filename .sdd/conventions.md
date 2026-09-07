# Convenciones

## Paquetes

Todo el código vive bajo `profamilia.prueba`, organizado por capa, no por feature:

```
profamilia.prueba
├── config
├── controller
├── dto
├── mapper
├── model
├── repository
├── service
└── service.impl
```

## Nombres

| Tipo | Convención | Ejemplo |
| --- | --- | --- |
| Entidad | `{Dominio}Entity` | `EmployEntity` |
| Repositorio | `{Dominio}Repository` | `EmployRepository` |
| Servicio (interfaz) | `{Dominio}Service` | `EmployService` |
| Servicio (impl) | `{Dominio}ServiceImpl` | `EmployServiceImpl` |
| Mapper | `{Dominio}Mapper` | `EmployMapper` |
| DTO entrada | `{Dominio}RequestDTO` | `EmployRequestDTO` |
| DTO salida | `{Dominio}ResponseDTO` | `EmployResponseDTO` |
| Controller | `{Dominio}Controller` | `EmployController` |

El dominio de empleados se nombra `Employ` en Java. Las rutas HTTP usan el plural en inglés: `/api/employees`.

Campos Java en inglés (`fullName`, `position`, `present`). Columnas MySQL en español (`nombre_completo`, `puesto`, `presente`). Tabla: `empleados`.

## Estilo de código

- Inyección por constructor con `@RequiredArgsConstructor` (Lombok).
- Controllers: `@RestController` + `@RequestMapping` a nivel de clase.
- Servicios: interfaz en `service`, implementación en `service.impl` con `@Service`.
- Transacciones: `@Transactional` en métodos de escritura; `@Transactional(readOnly = true)` en lecturas.
- Repositorios: `JpaRepository`; actualizaciones parciales con `@Modifying` y `@Query` JPQL.
- Mappers: `@Component` con métodos explícitos `toEntity` / `toDto` (sin MapStruct).
- Entidades: `@Getter` / `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`. No exponerlas en la API.
- DTOs: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`.
- Respuestas HTTP: `ResponseEntity`. Creación con `201 CREATED`; listados con `200 OK`.
- Un empleado nuevo siempre nace con `present = false` (lo fija el mapper, no el cliente).

## API

- Prefijo de recursos: `/api/{recurso}`.
- JSON en cuerpo de peticiones y respuestas.
- Identificadores de ruta: `{id}` de tipo `Long`.
- El body de `PUT /{id}/status` es un `Boolean` (`present`), no un DTO.

## Persistencia

- Estrategia de ID: `GenerationType.IDENTITY`.
- Esquema: Hibernate `ddl-auto: update`.
- Columnas obligatorias marcadas con `nullable = false` y `length` donde aplica.

## Configuración

- YAML: `application.yaml`.
- Beans de infraestructura en `config` con `@Configuration`.

## Tests

- Clases de test en el mismo paquete que la aplicación: `profamilia.prueba`.
- Convención Spring Boot: `{Clase}Tests`.

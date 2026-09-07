# Constitución del proyecto

Backend de prueba técnica Profamilia: API REST de asistencia de empleados.

## Stack

- Java 17
- Spring Boot 4.1.1
- Maven
- MySQL
- JPA / Hibernate
- Lombok
- REST API

## Principios

- SOLID
- Clean Code
- Separación de responsabilidades por capas

## Reglas no negociables

- Los Controllers no contienen lógica de negocio.
- Las Entities no se exponen directamente mediante la API.
- Toda entrada externa debe utilizar DTO.
- La lógica de negocio pertenece a Service.
- Los errores deben manejarse mediante excepciones controladas.
- La persistencia se accede solo desde Repository.
- El mapeo Entity ↔ DTO se realiza en Mapper, no en Controller ni en Entity.

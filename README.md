# Turnerito - Backend (Spring Boot)

Aplicación backend para gestión de turnos (API REST) protegida con Auth0. Incluye usuarios (admin, practitioner, client), turnos y agenda de profesionales.

- Integrante: Alan Sanjurjo

## Requisitos
- Java 17+ (JDK)
- Maven Wrapper (incluido: mvnw/mvnw.cmd)
- PostgreSQL (Neon u otro) con URL de conexión
- Cuenta de Auth0 (Tenant, API y aplicación creadas)

## Variables de entorno
Crear un archivo .env en back/ con las siguientes claves:

```
# Database
DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=

# Auth0 Resource Server (API)
AUTH0_ISSUER_URI=
AUTH0_AUDIENCE=
AUTH0_ROLES_CLAIM=

# CORS origin for the frontend
FRONTEND_ORIGIN=http://localhost:3000
```

Notas:
- AUTH0_ISSUER_URI debe apuntar al dominio del tenant (con barra final).
- AUTH0_AUDIENCE debe coincidir con el “Identifier” de la API en Auth0.
- AUTH0_ROLES_CLAIM es el claim personalizado donde vienen los roles en el access token.

## Instalación y ejecución (desarrollo)
1) Posicionarse en la carpeta back/.
2) Crear/ajustar el archivo .env con las variables anteriores.
3) Iniciar la API:
   - Windows (CMD): mvnw.cmd spring-boot:run
   - Linux/Mac: ./mvnw spring-boot:run
4) La API quedará disponible en http://localhost:8080
   - Health check: GET /actuator/health

## Ejecución como JAR
- Compilar: mvnw.cmd clean package (o ./mvnw clean package)
- Ejecutar: java -jar target/back-0.0.1-SNAPSHOT.jar

## Tecnologías utilizadas
- Spring Boot 3 (Web, Validation)
- Spring Security (Resource Server JWT con Auth0)
- JPA/Hibernate
- Flyway (migraciones en resources/db/migration)
- PostgreSQL
- Lombok
- Maven

## Desarrollo conjunto con el Frontend
- Asegurar que FRONTEND_ORIGIN en el .env del backend coincida con la URL del front (por defecto http://localhost:3000).
- Arrancar primero el backend y luego el frontend para evitar errores de conexión.
# Medical Appointment System - Sistema de Gestión de Citas Médicas

Sistema completo de microservicios para la gestión de citas médicas, desarrollado con Spring Boot y arquitectura de microservicios. El sistema permite administrar doctores, pacientes, citas, calendarios y seguros médicos de manera integral.

## Tabla de Contenidos

- [Inicio Rápido](#inicio-rápido)
- [Descripción General](#descripción-general)
- [Arquitectura del Sistema](#arquitectura-del-sistema)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Servicios y Componentes](#servicios-y-componentes)
- [Modelo de Datos](#modelo-de-datos)
- [API Endpoints](#api-endpoints)
- [Seguridad y Autenticación](#seguridad-y-autenticación)
- [Configuración e Instalación](#configuración-e-instalación)
- [Uso del Sistema](#uso-del-sistema)

---

## Inicio Rápido

### 🚀 Usuario Administrativo por Defecto

**IMPORTANTE**: Al iniciar el sistema por primera vez, se crea automáticamente un usuario **Secretaria** con permisos administrativos completos. Este usuario es necesario para comenzar a usar el sistema y crear otros usuarios.

#### Credenciales por Defecto

```
📧 Email:    admin@sanatory.com
🔑 Password: 00000000
👤 Rol:      SECRETARY (Administrador)
```

⚠️ **Nota de Seguridad**: Cambia estas credenciales inmediatamente después del primer acceso en un entorno de producción.

### 🔐 Cómo Realizar el Login

#### Endpoint de Autenticación

```
POST http://localhost:8080/auth/login
```

#### Paso 1: Realizar la petición de login

**Con cURL**:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@sanatory.com",
    "password": "00000000"
  }'
```

**Con Postman/Insomnia**:
- URL: `http://localhost:8080/auth/login`
- Método: `POST`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
```json
{
  "username": "admin@sanatory.com",
  "password": "00000000"
}
```

#### Paso 2: Obtener el Token JWT

Si las credenciales son correctas, recibirás una respuesta como esta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJtZWRpY2FsLWFwcG9pbnRtZW50Iiwic3ViIjoiYWRtaW5Ac2FuYXRvcnkuY29tIiwidXNlcklkIjoxLCJyb2xlcyI6WyJST0xFX1NFQ1JFVEFSWSJdLCJpYXQiOjE3MzM3ODI4MDAsImV4cCI6MTczMzc4NjQwMH0.abc123...",
  "userId": 1,
  "email": "admin@sanatory.com",
  "roles": ["ROLE_SECRETARY"]
}
```

#### Paso 3: Usar el Token en Requests Subsecuentes

Una vez que tienes el token, inclúyelo en el header `Authorization` de todas tus peticiones:

```bash
# Ejemplo: Crear un nuevo doctor
curl -X POST http://localhost:8080/doctor/create \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@clinic.com",
    "dni": "12345678",
    "phoneNumber": "+54911234567",
    "password": "doctor123"
  }'
```

**En Postman/Insomnia**:
- Pestaña "Authorization"
- Type: `Bearer Token`
- Token: `{tu-token-jwt-aquí}`

#### Paso 4: Validación del Token

- ✅ El token es válido por **60 minutos** (configurable)
- ✅ Después de 60 minutos, debes hacer login nuevamente
- ✅ El token contiene tu `userId` y `roles`

#### Errores Comunes

**Error 401 - Unauthorized**:
```json
{
  "error": "Invalid credentials"
}
```
- Verifica que el email y password sean correctos
- Asegúrate de que el usuario esté activo (`enabled: true`)

**Error 403 - Forbidden**:
```json
{
  "error": "Access denied"
}
```
- Tu token es válido pero no tienes permisos para ese endpoint
- Verifica que tu rol tenga acceso a la operación solicitada

**Token Expirado**:
```json
{
  "error": "Token has expired"
}
```
- Realiza login nuevamente para obtener un nuevo token

### 📋 Primeros Pasos Después del Login

Una vez autenticado como **SECRETARY**, puedes:

1. **Crear Doctores**: `POST /doctor/create`
2. **Crear Pacientes**: `POST /patient/create`
3. **Crear Calendarios**: `POST /doctorCalendar`
4. **Crear Seguros**: `POST /api/v1/health-insurances`
5. **Crear Tipos de Citas**: `POST /appointmentTypes`
6. **Agendar Citas**: `POST /appointment/create`

Ver la sección [Uso del Sistema](#uso-del-sistema) para ejemplos completos.

---

## Descripción General

Medical Appointment System es una solución empresarial basada en microservicios que facilita la gestión completa de un consultorio o clínica médica. El sistema permite:

- **Gestión de Usuarios**: Administración de doctores, pacientes y personal administrativo (secretarias)
- **Sistema de Citas**: Creación, seguimiento y cancelación de citas médicas
- **Calendarios Médicos**: Gestión de disponibilidad de doctores con patrones semanales y excepciones
- **Seguros Médicos**: Integración con seguros de salud, planes de cobertura y cálculo automático de costos
- **Autenticación Centralizada**: Sistema JWT para autenticación y autorización segura
- **Configuración Centralizada**: Gestión de configuraciones desde un repositorio Git

### Características Principales

✅ Arquitectura de microservicios escalable y modular
✅ Autenticación y autorización basada en JWT
✅ Descubrimiento automático de servicios con Eureka
✅ API Gateway como punto de entrada unificado
✅ Configuración centralizada con Spring Cloud Config
✅ Control de acceso basado en roles (RBAC)
✅ Documentación automática de APIs con OpenAPI/Swagger
✅ Tolerancia a fallos con Resilience4j
✅ Comunicación inter-servicios con OpenFeign

### 🔑 Credenciales de Acceso Inicial

> **⚠️ IMPORTANTE**: El sistema crea automáticamente un usuario administrativo al iniciar por primera vez. Usa estas credenciales para tu primer acceso:

```
📧 Email:    admin@sanatory.com
🔑 Password: 00000000
🌐 Endpoint: POST http://localhost:8080/auth/login
```

Ver la sección [Inicio Rápido](#inicio-rápido) para instrucciones detalladas de login.

---

## Arquitectura del Sistema

### Diagrama de Arquitectura

```
                                    ┌─────────────────────┐
                                    │  Eureka Server      │
                                    │  (8761)             │
                                    │  Service Discovery  │
                                    └──────────┬──────────┘
                                               │
                          ┌────────────────────┼────────────────────┐
                          │                    │                    │
                    ┌─────▼──────┐      ┌─────▼──────┐      ┌─────▼──────┐
                    │ Config     │      │ API Gateway│      │ Services   │
                    │ Server     │◄─────┤ (8080)     │◄─────┤ (8084-8087)│
                    │ (8888)     │      │ JWT Auth   │      │            │
                    └────────────┘      └─────┬──────┘      └─────┬──────┘
                                               │                    │
                   ┌───────────────────────────┼────────────────────┘
                   │                           │
       ┌───────────▼───────────┐   ┌──────────▼──────────┐
       │   UserService         │   │ AppointmentService  │
       │   (8084)              │   │ (8085)              │
       │   - Doctors           │   │ - Appointments      │
       │   - Patients          │   │ - Appointment Types │
       │   - Secretaries       │   └─────────────────────┘
       │   - Authentication    │
       └───────────────────────┘   ┌─────────────────────┐
                                    │ CalendarService     │
       ┌───────────────────────┐   │ (8086)              │
       │ HealthInsuranceService│   │ - Doctor Calendars  │
       │ (8087)                │   │ - Availability      │
       │ - Health Insurances   │   │ - Exceptions        │
       │ - Coverage Plans      │   └─────────────────────┘
       │ - Patient Insurances  │
       └───────────────────────┘
```

### Comunicación entre Servicios

```
Cliente → API Gateway → [UserService / AppointmentService / CalendarService / HealthInsuranceService]
                ↓
        Eureka Discovery
                ↓
        OpenFeign Clients
                ↓
        JWT Validation
```

### Flujo de Autenticación

```
1. Usuario → POST /auth/login (API Gateway)
   ├─ Credenciales: { username, password }

2. API Gateway → UserService.validateCredentials() [Feign]
   ├─ UserService verifica en BD (Patient/Doctor/Secretary)
   ├─ Valida password con BCrypt
   └─ Retorna: { valid, userId, email, roles }

3. API Gateway → JwtUtils.createToken()
   ├─ Genera JWT con claims: userId, roles, email
   ├─ Firma con HMAC256
   └─ Expira en 60 minutos (configurable)

4. Cliente recibe: { token: "eyJhbGc..." }
   └─ Incluye en requests: Authorization: Bearer <token>

5. Servicios → JwtAuthenticationFilter
   ├─ Valida token
   ├─ Extrae userId, email, roles
   ├─ Crea CustomUserDetails
   └─ Establece en SecurityContext
```

---

## Tecnologías Utilizadas

### Framework Principal
- **Java 17+**
- **Spring Boot** 3.3.5 / 3.5.0
- **Spring Cloud** 2023.0.3 / 2025.0.0

### Microservicios
- **Spring Cloud Netflix Eureka** - Service Discovery
- **Spring Cloud Config** - Configuración centralizada
- **Spring Cloud Gateway** - API Gateway
- **Spring Cloud OpenFeign** - Cliente HTTP declarativo
- **Spring Cloud LoadBalancer** - Balanceo de carga

### Seguridad
- **Spring Security** 6.x
- **JWT (Auth0 java-jwt)** 4.4.0 - Tokens de autenticación
- **BCrypt** - Hash de contraseñas

### Persistencia
- **Spring Data JPA** - ORM
- **MySQL** - Base de datos relacional
- **Hibernate** - Implementación JPA

### Resiliencia
- **Resilience4j** 2.1.0 - Circuit breaker, retry, bulkhead

### Documentación
- **SpringDoc OpenAPI** 2.3.0 - 2.5.0 - Documentación Swagger

### Herramientas
- **Lombok** 1.18.30 - Reducción de código boilerplate
- **Spring Boot Actuator** - Métricas y monitoreo
- **Spring Boot DevTools** - Desarrollo

---

## Servicios y Componentes

### 1. Eureka Server (Puerto 8761)

**Descripción**: Servidor de descubrimiento de servicios que permite el registro y localización dinámica de microservicios.

**Responsabilidades**:
- Registro automático de servicios
- Health checking de instancias
- Dashboard de monitoreo de servicios
- Service discovery para comunicación inter-servicio

**URL**: `http://localhost:8761`

---

### 2. Config Server (Puerto 8888)

**Descripción**: Servidor de configuración centralizada que obtiene configuraciones desde un repositorio Git.

**Responsabilidades**:
- Gestión centralizada de configuraciones
- Configuración específica por entorno (dev, prod, etc.)
- Actualización de configuraciones sin reiniciar servicios
- Versionado de configuraciones con Git

**Repositorio de Configuración**: `https://github.com/eugeniaarias16/sanatory-config.git`

**URL**: `http://localhost:8888`

---

### 3. API Gateway (Puerto 8080)

**Descripción**: Punto de entrada unificado para todos los servicios. Maneja autenticación, enrutamiento y seguridad.

**Responsabilidades**:
- Enrutamiento de requests a servicios backend
- Autenticación de usuarios (generación de JWT)
- Validación de tokens JWT
- Load balancing hacia servicios registrados
- CORS handling

**Endpoints Principales**:
- `POST /auth/login` - Autenticación de usuarios

**Seguridad**: Sin autenticación requerida para `/auth/**` y `/actuator/**`

---

### 4. User Service (Puerto 8084)

**Descripción**: Gestión completa de usuarios del sistema (doctores, pacientes y secretarias).

**Responsabilidades**:
- CRUD de doctores
- CRUD de pacientes
- CRUD de secretarias
- Validación de credenciales
- Gestión de estados de cuenta (enabled, locked, expired)
- Inicialización de usuario administrativo por defecto

**Base de Datos**: `user_service_db`

**Entidades**:
- `Doctor` - Médicos del sistema
- `Patient` - Pacientes
- `Secretary` - Personal administrativo

**Roles**: `ROLE_DOCTOR`, `ROLE_PATIENT`, `ROLE_SECRETARY`

**Credenciales por Defecto**:
- Email: `admin@sanatory.com`
- Password: `00000000`
- Rol: `SECRETARY`

---

### 5. Appointment Service (Puerto 8085)

**Descripción**: Gestión completa del sistema de citas médicas.

**Responsabilidades**:
- Creación y gestión de citas
- Validación de disponibilidad de doctores
- Cálculo automático de costos con seguro
- Estados de citas (SCHEDULED, CONFIRMED, CANCELLED, COMPLETED)
- Gestión de tipos de citas (consultas, exámenes, etc.)
- Búsquedas avanzadas (por doctor, paciente, fecha, rango)

**Base de Datos**: `appointment_service_db`

**Entidades**:
- `Appointment` - Citas médicas
- `AppointmentType` - Tipos de citas con duración y precio base

**Estados de Cita**:
- `SCHEDULED` - Agendada
- `CONFIRMED` - Confirmada
- `CANCELLED` - Cancelada
- `COMPLETED` - Completada

**Relaciones Externas**:
- `doctorId` → UserService
- `patientId` → UserService
- `doctorCalendarId` → CalendarService
- `patientInsuranceId` → HealthInsuranceService

---

### 6. Calendar Service (Puerto 8086)

**Descripción**: Gestión de calendarios médicos, disponibilidad y excepciones.

**Responsabilidades**:
- Gestión de calendarios por doctor
- Patrones de disponibilidad semanal
- Excepciones de calendario (vacaciones, días festivos, etc.)
- Validación de disponibilidad para citas
- Gestión de múltiples calendarios por doctor
- Control de zona horaria

**Base de Datos**: `calendar_service_db`

**Entidades**:
- `DoctorCalendar` - Calendario de un doctor
- `AvailabilityPattern` - Patrones de disponibilidad por día de semana
- `CalendarException` - Excepciones (días no disponibles)

**Tipos de Excepciones**:
- `UNAVAILABLE` - No disponible
- `VACATION` - Vacaciones
- `SICK_LEAVE` - Licencia por enfermedad
- `HOLIDAY` - Día festivo
- `CONFERENCE` - Conferencias/Capacitación
- `EMERGENCY` - Emergencia
- `PERSONAL` - Razón personal
- `MAINTENANCE` - Mantenimiento de oficina
- `CUSTOM` - Personalizado

---

### 7. Health Insurance Service (Puerto 8087)

**Descripción**: Gestión de seguros médicos, planes de cobertura y seguros de pacientes.

**Responsabilidades**:
- CRUD de compañías de seguros
- Gestión de planes de cobertura
- Asignación de seguros a pacientes
- Cálculo de porcentajes de cobertura
- Validación de credenciales de seguro
- Estadísticas de pacientes por seguro

**Base de Datos**: `health_insurance_service_db`

**Entidades**:
- `HealthInsurance` - Compañías de seguros
- `CoveragePlan` - Planes de cobertura con porcentajes
- `PatientInsurance` - Seguros asignados a pacientes

**Relaciones**:
```
HealthInsurance (1) ──┬──► (N) CoveragePlan
                      │
                      └──► (N) PatientInsurance
                                    │
                    CoveragePlan ◄──┘
```

---

### 8. Shared Security Module

**Descripción**: Módulo compartido de seguridad que proporciona componentes JWT reutilizables.

**Componentes**:
- `JwtUtils` - Validación y decodificación de tokens JWT
- `JwtAuthenticationFilter` - Filtro de autenticación para requests
- `CustomUserDetails` - Implementación personalizada de UserDetails
- `SecurityService` - Métodos de autorización (isSecretaryOrDoctor, isSecretaryOrPatient, etc.)

**Uso**: Importado como dependencia en todos los servicios de negocio.

---

## Modelo de Datos

### User Service

#### Doctor
```java
{
  "id": Long,
  "firstName": String,
  "lastName": String,
  "email": String (unique),
  "dni": String (unique),
  "phoneNumber": String (unique),
  "password": String (BCrypt),
  "enabled": boolean,
  "credentialsExpired": boolean,
  "accountLocked": boolean
}
```

#### Patient
```java
{
  "id": Long,
  "dni": String (unique),
  "firstName": String,
  "lastName": String,
  "email": String (unique),
  "phoneNumber": String (unique),
  "password": String (BCrypt),
  "enabled": boolean,
  "credentialsExpired": boolean,
  "accountLocked": boolean
}
```

#### Secretary
```java
{
  "id": Long,
  "dni": String (unique),
  "firstName": String,
  "lastName": String,
  "email": String (unique),
  "password": String (BCrypt),
  "enabled": boolean,
  "credentialsExpired": boolean,
  "accountLocked": boolean
}
```

---

### Appointment Service

#### Appointment
```java
{
  "id": Long,
  "doctorId": Long,                    // Referencia a UserService
  "doctorCalendarId": Long,             // Referencia a CalendarService
  "patientId": Long,                    // Referencia a UserService
  "appointmentType": AppointmentType,   // Relación @ManyToOne
  "patientInsuranceId": Long,           // Referencia a HealthInsuranceService
  "date": LocalDateTime,
  "status": AppointmentStatus,          // SCHEDULED, CONFIRMED, CANCELLED, COMPLETED
  "consultationCost": BigDecimal,
  "coveragePercentage": BigDecimal,
  "amountToPay": BigDecimal,
  "notes": String,
  "createdAt": LocalDate
}
```

#### AppointmentType
```java
{
  "id": Long,
  "name": String (unique),
  "description": String,
  "durationMin": int,
  "bufferTimeMin": int,
  "basePrice": BigDecimal,
  "isActive": boolean
}
```

---

### Calendar Service

#### DoctorCalendar
```java
{
  "id": Long,
  "doctorId": Long,                         // Referencia a UserService
  "name": String,
  "isActive": boolean,
  "timeZone": ZoneId,
  "availabilityPatterns": List<AvailabilityPattern>,  // @OneToMany
  "calendarExceptions": List<CalendarException>       // @OneToMany
}
```

#### AvailabilityPattern
```java
{
  "id": Long,
  "doctorCalendar": DoctorCalendar,         // @ManyToOne
  "dayOfWeek": DayOfWeek,                   // MONDAY-SUNDAY
  "startTime": LocalTime,
  "endTime": LocalTime,
  "isActive": boolean
}
```

#### CalendarException
```java
{
  "id": Long,
  "doctorCalendar": DoctorCalendar,         // @ManyToOne (null for GLOBAL/SEMI_GLOBAL)
  "doctorId": Long,                          // null for GLOBAL, required for SEMI_GLOBAL/SPECIFIC
  "scope": ExceptionScope,                   // GLOBAL, SEMI_GLOBAL, SPECIFIC
  "startDate": LocalDate (@Future),
  "endDate": LocalDate,                      // null = single-day exception
  "startTime": LocalTime,                    // null = full-day exception
  "endTime": LocalTime,                      // null = full-day exception
  "exceptionType": ExceptionType,            // UNAVAILABLE, VACATION, HOLIDAY, etc.
  "reason": String,
  "isFullDay": boolean,                      // auto-calculated
  "isSingleDay": boolean,                    // auto-calculated
  "isActive": boolean
}
```

---

### Health Insurance Service

#### HealthInsurance
```java
{
  "id": Long,
  "companyName": String,
  "companyCode": Long (unique),
  "phoneNumber": String (unique),
  "email": String (unique),
  "isActive": boolean,
  "coveragePlans": List<CoveragePlan>,         // @OneToMany
  "patientInsurances": List<PatientInsurance>  // @OneToMany
}
```

#### CoveragePlan
```java
{
  "id": Long,
  "healthInsurance": HealthInsurance,          // @ManyToOne
  "name": String (unique),
  "description": String,
  "coverageValuePercentage": BigDecimal,       // precision=5, scale=2
  "isActive": boolean,
  "patientInsurances": List<PatientInsurance>  // @OneToMany
}
```

#### PatientInsurance
```java
{
  "id": Long,
  "patientDni": String,                        // Referencia a UserService
  "credentialNumber": String (unique),
  "healthInsurance": HealthInsurance,          // @ManyToOne
  "coveragePlan": CoveragePlan,                // @ManyToOne
  "createdAt": LocalDate,
  "isActive": Boolean
}
```

---

## API Endpoints

### Convenciones

- **Base URL**: `http://localhost:8080` (API Gateway)
- **Autenticación**: Bearer Token JWT en header `Authorization`
- **Formato**: JSON

### Roles y Permisos

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| `SECRETARY` | Personal administrativo | Acceso completo a todos los endpoints |
| `DOCTOR` | Médico | Acceso a sus propios datos y calendarios |
| `PATIENT` | Paciente | Acceso a sus propios datos y citas |

---

### Authentication (API Gateway)

**Base Path**: `/auth`

Todos los endpoints de autenticación se manejan a través del **API Gateway** en el puerto **8080**. No requieren autenticación previa (son públicos).

#### Login

Autentica un usuario y retorna un token JWT válido por 60 minutos.

**Endpoint Completo**:
```
POST http://localhost:8080/auth/login
```

**Request**:
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin@sanatory.com",
  "password": "00000000"
}
```

**Campos del Request**:
| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `username` | String | ✅ Sí | Email del usuario (doctor, paciente o secretaria) |
| `password` | String | ✅ Sí | Contraseña del usuario |

**Response Exitoso (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJtZWRpY2FsLWFwcG9pbnRtZW50Iiwic3ViIjoiYWRtaW5Ac2FuYXRvcnkuY29tIiwidXNlcklkIjoxLCJyb2xlcyI6WyJST0xFX1NFQ1JFVEFSWSJdLCJpYXQiOjE3MzM3ODI4MDAsImV4cCI6MTczMzc4NjQwMH0.abc123...",
  "userId": 1,
  "email": "admin@sanatory.com",
  "roles": ["ROLE_SECRETARY"]
}
```

**Campos del Response**:
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `token` | String | Token JWT firmado (incluir en header Authorization) |
| `userId` | Long | ID del usuario autenticado |
| `email` | String | Email del usuario |
| `roles` | Array | Lista de roles del usuario (ROLE_SECRETARY, ROLE_DOCTOR, ROLE_PATIENT) |

**Errores Posibles**:

**401 Unauthorized** - Credenciales inválidas:
```json
{
  "error": "Invalid credentials",
  "message": "Username or password is incorrect"
}
```

**401 Unauthorized** - Cuenta deshabilitada:
```json
{
  "error": "Account disabled",
  "message": "This account has been disabled"
}
```

**401 Unauthorized** - Cuenta bloqueada:
```json
{
  "error": "Account locked",
  "message": "This account has been locked"
}
```

**Ejemplo con cURL**:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@sanatory.com",
    "password": "00000000"
  }'
```

**Ejemplo con JavaScript (fetch)**:
```javascript
const response = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'admin@sanatory.com',
    password: '00000000'
  })
});

const data = await response.json();
console.log('Token:', data.token);
console.log('User ID:', data.userId);
console.log('Roles:', data.roles);

// Guardar token para requests futuros
localStorage.setItem('authToken', data.token);
```

**Usar el Token en Requests Subsecuentes**:

Todos los endpoints protegidos requieren el token JWT en el header `Authorization`:

```bash
curl -X GET http://localhost:8080/doctor \
  -H "Authorization: Bearer {tu-token-aquí}"
```

**Duración del Token**:
- ⏱️ **60 minutos** (configurable con variable `JWT_EXPIRATION_MINUTES`)
- Después de expirar, debes realizar login nuevamente

---

### User Service Endpoints

#### Doctors

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|----------|
| GET | `/doctor` | Lista todos los doctores | SECRETARY |
| GET | `/doctor/{doctorId}` | Obtiene doctor por ID | SECRETARY o el mismo DOCTOR |
| GET | `/doctor/firstName/{firstName}` | Busca doctores por nombre | SECRETARY |
| GET | `/doctor/lastName/{lastName}` | Busca doctores por apellido | SECRETARY |
| GET | `/doctor/dni/{dni}` | Obtiene doctor por DNI | SECRETARY |
| POST | `/doctor/create` | Crea un nuevo doctor | SECRETARY |
| PUT | `/doctor/update/{doctorId}` | Actualiza doctor | SECRETARY o el mismo DOCTOR |
| PATCH | `/doctor/disable/{dni}` | Desactiva doctor | SECRETARY |
| PATCH | `/doctor/enable/{dni}` | Activa doctor | SECRETARY |
| DELETE | `/doctor/delete/{id}` | Elimina doctor | SECRETARY |

**Ejemplo - Crear Doctor**:
```http
POST /doctor/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan.perez@example.com",
  "dni": "12345678",
  "phoneNumber": "+54911234567",
  "password": "password123"
}

Response 201:
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan.perez@example.com",
  "dni": "12345678",
  "phoneNumber": "+54911234567",
  "enabled": true
}
```

---

#### Patients

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|----------|
| GET | `/patient` | Lista todos los pacientes | SECRETARY |
| GET | `/patient/id/{patientId}` | Obtiene paciente por ID | SECRETARY o el mismo PATIENT |
| GET | `/patient/dni/{dni}` | Obtiene paciente por DNI | SECRETARY |
| GET | `/patient/email/{email}` | Obtiene paciente por email | SECRETARY |
| POST | `/patient/create` | Crea un nuevo paciente | SECRETARY |
| PUT | `/patient/update/id/{patientId}` | Actualiza paciente por ID | SECRETARY o el mismo PATIENT |
| PUT | `/patient/update/dni/{dni}` | Actualiza paciente por DNI | SECRETARY |
| PATCH | `/patient/disable/{dni}` | Desactiva paciente | SECRETARY |
| PATCH | `/patient/enable/{dni}` | Activa paciente | SECRETARY |
| DELETE | `/patient/delete/id/{id}` | Elimina paciente por ID | SECRETARY |
| DELETE | `/patient/delete/dni/{dni}` | Elimina paciente por DNI | SECRETARY |

**Ejemplo - Crear Paciente**:
```http
POST /patient/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "firstName": "María",
  "lastName": "González",
  "email": "maria.gonzalez@example.com",
  "dni": "87654321",
  "phoneNumber": "+54917654321",
  "password": "password123",
  "birthDate": "1990-05-15",
  "address": "Av. Principal 123"
}
```

---

#### Secretaries

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|----------|
| GET | `/secretary` | Lista todas las secretarias | SECRETARY |
| GET | `/secretary/id/{id}` | Obtiene secretaria por ID | SECRETARY |
| GET | `/secretary/dni/{dni}` | Obtiene secretaria por DNI | SECRETARY |
| GET | `/secretary/email/{email}` | Obtiene secretaria por email | SECRETARY |
| POST | `/secretary/create` | Crea una nueva secretaria | SECRETARY |
| PUT | `/secretary/update/id/{id}` | Actualiza secretaria por ID | SECRETARY |
| PUT | `/secretary/update/dni/{dni}` | Actualiza secretaria por DNI | SECRETARY |
| PATCH | `/secretary/disable/{dni}` | Desactiva secretaria | SECRETARY |
| PATCH | `/secretary/enable/{dni}` | Activa secretaria | SECRETARY |
| DELETE | `/secretary/delete/id/{id}` | Elimina secretaria por ID | SECRETARY |
| DELETE | `/secretary/delete/dni/{dni}` | Elimina secretaria por DNI | SECRETARY |

---

### Appointment Service Endpoints

#### Appointments

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|----------|
| GET | `/appointment/{id}` | Obtiene cita por ID | SECRETARY |
| GET | `/appointment/patient/{patientId}` | Obtiene citas de un paciente | SECRETARY o el mismo PATIENT |
| GET | `/appointment/patient/dni/{patientDni}` | Obtiene citas por DNI del paciente | SECRETARY |
| GET | `/appointment/patient/dni/{patientDni}/search-date-range?startDate={start}&endDate={end}` | Citas del paciente en rango de fechas | SECRETARY |
| GET | `/appointment/patient/id/{patientId}/upcoming?date={date}` | Citas futuras del paciente (date opcional, default: hoy) | SECRETARY o el mismo PATIENT |
| GET | `/appointment/patient/dni/{patientDni}/upcoming?date={date}` | Citas futuras por DNI (date opcional, default: hoy) | SECRETARY |
| GET | `/appointment/doctor/{doctorId}` | Obtiene citas de un doctor | SECRETARY o el mismo DOCTOR |
| GET | `/appointment/doctor/{doctorId}/search-date-range?startDate={start}&endDate={end}` | Citas del doctor en rango | SECRETARY o el mismo DOCTOR |
| GET | `/appointment/doctor/doctorCalendar/{doctorCalendarId}` | Citas por ID de calendario | SECRETARY |
| GET | `/appointment/doctor/{doctorId}/today` | Citas de hoy del doctor | SECRETARY o el mismo DOCTOR |
| GET | `/appointment/search-specific?patientId={id}&doctorId={id}&date={date}` | Cita específica por paciente, doctor y fecha | SECRETARY |
| POST | `/appointment/create` | Crea una nueva cita | SECRETARY |
| PATCH | `/appointment/cancel/{id}` | Cancela cita por ID | SECRETARY |

**Ejemplo - Crear Cita**:
```http
POST /appointment/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "doctorId": 1,
  "patientDni": "12345678",
  "appointmentTypeId": 1,
  "credentialNumber": "OS-001-2025",
  "doctorCalendarId": 1,
  "date": "2025-12-15",
  "time": "10:00:00",
  "notes": "Control mensual"
}

Response 200:
{
  "id": 1,
  "doctorId": 1,
  "doctorFirstName": "Juan",
  "doctorLastName": "Pérez",
  "patientId": 2,
  "patientFirstName": "María",
  "patientLastName": "González",
  "patientDni": "12345678",
  "appointmentTypeName": "Consulta General",
  "patientInsuranceId": 3,
  "date": "2025-12-15T10:00:00",
  "status": "SCHEDULED",
  "consultationCost": 5000.00,
  "coveragePercentage": 70.00,
  "amountToPay": 1500.00,
  "notes": "Control mensual",
  "createdAt": "2025-12-15"
}
```

---

#### Appointment Types

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|----------|
| GET | `/appointmentTypes/{id}` | Obtiene tipo de cita por ID | Autenticado |
| GET | `/appointmentTypes/name/{name}` | Obtiene tipo por nombre | Autenticado |
| GET | `/appointmentTypes/search/name?name={name}` | Busca tipos por nombre (LIKE) | Autenticado |
| GET | `/appointmentTypes/search/price-range?minPrice={min}&maxPrice={max}` | Busca por rango de precio | Autenticado |
| POST | `/appointmentTypes` | Crea nuevo tipo de cita | Autenticado |
| PATCH | `/appointmentTypes/{id}` | Actualiza tipo de cita | Autenticado |
| DELETE | `/appointmentTypes/{id}` | Elimina tipo de cita | Autenticado |

**Ejemplo - Crear Tipo de Cita**:
```http
POST /appointmentTypes
Content-Type: application/json

{
  "name": "Consulta General",
  "description": "Consulta médica general",
  "durationMin": 30,
  "bufferTimeMin": 10,
  "basePrice": 5000.00
}
```

---

### Calendar Service Endpoints

#### Doctor Calendars

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|----------|
| GET | `/doctorCalendar/{id}` | Obtiene calendario por ID | SECRETARY |
| GET | `/doctorCalendar/active/doctor/{doctorId}` | Calendarios activos del doctor | SECRETARY o el mismo DOCTOR |
| GET | `/doctorCalendar/doctor/{doctorId}` | Todos los calendarios del doctor | SECRETARY o el mismo DOCTOR |
| GET | `/doctorCalendar/active/search?doctorId={id}&name={name}` | Busca calendario por doctor y nombre | SECRETARY o el mismo DOCTOR |
| POST | `/doctorCalendar` | Crea nuevo calendario | SECRETARY |
| PATCH | `/doctorCalendar/{id}` | Actualiza calendario | SECRETARY |
| DELETE | `/doctorCalendar/{id}` | Elimina calendario | SECRETARY |

**Ejemplo - Crear Calendario**:
```http
POST /doctorCalendar
Authorization: Bearer {token}
Content-Type: application/json

{
  "doctorId": 1,
  "name": "Consultorio Principal",
  "timeZone": "America/Argentina/Buenos_Aires",
  "availabilityPatterns": [
    {
      "dayOfWeek": "MONDAY",
      "startTime": "08:00:00",
      "endTime": "12:00:00"
    },
    {
      "dayOfWeek": "MONDAY",
      "startTime": "14:00:00",
      "endTime": "18:00:00"
    }
  ]
}
```

---

#### Availability Patterns

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|----------|
| GET | `/availabilityPattern/id/{id}` | Obtiene patrón por ID | SECRETARY |
| GET | `/availabilityPattern/doctorCalendar/{id}` | Patrones por calendario | SECRETARY |
| GET | `/availabilityPattern/search/calendarAndDay?calendarId={id}&dayOfWeek={day}` | Patrones activos por calendario y día | SECRETARY |
| GET | `/availabilityPattern/doctor/{doctorId}` | Patrones por doctor | SECRETARY |
| GET | `/availabilityPattern/doctor/{doctorId}/day/{dayOfWeek}` | Patrones por doctor y día | SECRETARY |
| POST | `/availabilityPattern/create` | Crea nuevo patrón | SECRETARY |
| PATCH | `/availabilityPattern/update/{id}` | Actualiza patrón | SECRETARY |
| PATCH | `/availabilityPattern/softDelete/{id}` | Desactiva patrón | SECRETARY |
| DELETE | `/availabilityPattern/delete/{id}` | Elimina patrón | SECRETARY |

---

#### Calendar Exceptions

Las excepciones de calendario tienen 3 niveles de alcance (**scope**):
- **GLOBAL**: Aplica a todos los doctores y calendarios (ej: feriado nacional)
- **SEMI_GLOBAL**: Aplica a todos los calendarios de un doctor específico (ej: vacaciones del doctor)
- **SPECIFIC**: Aplica solo a un calendario específico (ej: mantenimiento de consultorio)

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|----------|
| GET | `/calendarException/{id}` | Obtiene excepción activa por ID | SECRETARY |
| GET | `/calendarException/doctor/{doctorId}` | Todas las excepciones de un doctor (GLOBAL + SEMI_GLOBAL + SPECIFIC) | SECRETARY |
| GET | `/calendarException/global` | Todas las excepciones GLOBAL activas | SECRETARY |
| GET | `/calendarException/semi-global/doctor/{doctorId}` | Excepciones SEMI_GLOBAL activas por doctor | SECRETARY |
| GET | `/calendarException/specific/doctorCalendar/{doctorCalendarId}` | Excepciones SPECIFIC activas por calendario | SECRETARY |
| GET | `/calendarException/doctor/{doctorId}/date/{date}` | Excepciones aplicables a un doctor en una fecha | SECRETARY |
| POST | `/calendarException` | Crea nueva excepción | SECRETARY |
| PATCH | `/calendarException/{id}` | Actualiza excepción (PATCH parcial con JsonNullable) | SECRETARY |
| DELETE | `/calendarException/{id}` | Elimina excepción | SECRETARY |

**Ejemplo - Crear Excepción GLOBAL (Feriado)**:
```http
POST /calendarException
Authorization: Bearer {token}
Content-Type: application/json

{
  "scope": "GLOBAL",
  "startDate": "2026-12-25",
  "exceptionType": "HOLIDAY",
  "reason": "Navidad"
}
```

**Ejemplo - Crear Excepción SEMI_GLOBAL (Vacaciones del doctor)**:
```http
POST /calendarException
Authorization: Bearer {token}
Content-Type: application/json

{
  "scope": "SEMI_GLOBAL",
  "doctorId": 1,
  "startDate": "2026-01-15",
  "endDate": "2026-01-30",
  "exceptionType": "VACATION"
}
```

**Ejemplo - Crear Excepción SPECIFIC (Ausencia parcial en un consultorio)**:
```http
POST /calendarException
Authorization: Bearer {token}
Content-Type: application/json

{
  "scope": "SPECIFIC",
  "doctorCalendarId": 1,
  "startDate": "2026-03-10",
  "startTime": "08:00:00",
  "endTime": "12:00:00",
  "exceptionType": "CONFERENCE",
  "reason": "Congreso de cardiología"
}
```

**Ejemplo - Actualizar Excepción (PATCH parcial)**:
```http
PATCH /calendarException/1
Authorization: Bearer {token}
Content-Type: application/json

{
  "reason": "Congreso internacional de cardiología",
  "endDate": "2026-03-11"
}
```
Solo se modifican los campos enviados. Enviar un campo como `null` lo setea a null (ej: `"endDate": null` convierte a excepción de un solo día).

---

### Health Insurance Service Endpoints

#### Health Insurances

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|----------|
| GET | `/api/v1/health-insurances/{id}` | Obtiene seguro por ID | SECRETARY |
| GET | `/api/v1/health-insurances/company-name/{companyName}` | Obtiene seguro por nombre | SECRETARY |
| GET | `/api/v1/health-insurances/company-code/{companyCode}` | Obtiene seguro por código | SECRETARY |
| GET | `/api/v1/health-insurances/search?name={name}` | Busca seguros por nombre | SECRETARY |
| GET | `/api/v1/health-insurances/{insuranceId}/coverage-plans` | Planes de cobertura del seguro | SECRETARY |
| GET | `/api/v1/health-insurances/{insuranceId}/patients` | Pacientes del seguro | SECRETARY |
| GET | `/api/v1/health-insurances/{insuranceId}/active-patients-count` | Cuenta pacientes activos | SECRETARY |
| POST | `/api/v1/health-insurances` | Crea nuevo seguro | SECRETARY |
| PATCH | `/api/v1/health-insurances/{insuranceId}` | Actualiza seguro | SECRETARY |
| PATCH | `/api/v1/health-insurances/{insuranceId}/deactivate` | Desactiva seguro | SECRETARY |
| DELETE | `/api/v1/health-insurances/{insuranceId}` | Elimina seguro | SECRETARY |

**Ejemplo - Crear Seguro**:
```http
POST /api/v1/health-insurances
Authorization: Bearer {token}
Content-Type: application/json

{
  "companyName": "OSDE",
  "companyCode": 123456,
  "phoneNumber": "+541143211234",
  "email": "info@osde.com.ar"
}
```

---

#### Coverage Plans

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|----------|
| GET | `/coveragePlan/{id}` | Obtiene plan por ID | SECRETARY |
| GET | `/coveragePlan/healthInsurance/{healthInsuranceId}` | Planes por seguro | SECRETARY |
| GET | `/coveragePlan/active/healthInsurance/{healthInsuranceId}` | Planes activos por seguro | SECRETARY |
| GET | `/coveragePlan/name/{name}` | Obtiene plan por nombre | SECRETARY |
| POST | `/coveragePlan/create` | Crea nuevo plan | SECRETARY |
| PATCH | `/coveragePlan/update/{id}` | Actualiza plan | SECRETARY |
| PATCH | `/coveragePlan/softDelete/{id}` | Desactiva plan | SECRETARY |
| DELETE | `/coveragePlan/delete/{id}` | Elimina plan | SECRETARY |

**Ejemplo - Crear Plan de Cobertura**:
```http
POST /coveragePlan/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "healthInsuranceId": 1,
  "name": "Plan 310",
  "description": "Plan completo con cobertura del 70%",
  "coverageValuePercentage": 70.00
}
```

---

#### Patient Insurances

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|----------|
| GET | `/patientInsurance/id/{patientInsuranceId}` | Obtiene seguro de paciente por ID | SECRETARY |
| GET | `/patientInsurance/patientDni/{dni}` | Seguros por DNI del paciente | SECRETARY |
| GET | `/patientInsurance/credentialNumber/{credentialNumber}` | Obtiene por número de credencial | SECRETARY |
| GET | `/patientInsurance/healthInsurance/{healthInsuranceId}` | Seguros por compañía | SECRETARY |
| GET | `/patientInsurance/coveragePlan/{coveragePlanId}` | Seguros por plan de cobertura | SECRETARY |
| POST | `/patientInsurance/create` | Crea nuevo seguro de paciente | SECRETARY |
| PATCH | `/patientInsurance/update/{patientInsuranceId}` | Actualiza plan de cobertura | SECRETARY |
| PATCH | `/patientInsurance/softDelete/{patientInsuranceId}` | Desactiva seguro de paciente | SECRETARY |
| DELETE | `/patientInsurance/delete/{patientInsuranceId}` | Elimina seguro de paciente | SECRETARY |

**Ejemplo - Asignar Seguro a Paciente**:
```http
POST /patientInsurance/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "patientDni": "87654321",
  "credentialNumber": "OSDE-87654321-001",
  "healthInsuranceId": 1,
  "coveragePlanId": 1
}
```

---

## Seguridad y Autenticación

### JWT (JSON Web Token)

El sistema utiliza JWT para autenticación stateless. Los tokens contienen:

**Payload**:
```json
{
  "iss": "medical-appointment-system",
  "sub": "admin@sanatory.com",
  "userId": 1,
  "roles": ["ROLE_SECRETARY"],
  "iat": 1733782800,
  "exp": 1733786400,
  "jti": "uuid-randomico"
}
```

**Configuración**:
- Algoritmo: HMAC256
- Expiración: 60 minutos (configurable con `JWT_EXPIRATION_MINUTES`)
- Secret: Configurable con variable de entorno `JWT_SECRET`

### Roles y Control de Acceso

#### ROLE_SECRETARY (Secretaria)
- **Permisos**: Acceso completo a todos los endpoints
- **Operaciones**: CRUD completo de doctores, pacientes, citas, calendarios, seguros

#### ROLE_DOCTOR (Doctor)
- **Permisos**: Acceso a sus propios datos y calendario
- **Operaciones**:
  - Ver y actualizar su propio perfil
  - Ver y gestionar su calendario y disponibilidad
  - Ver sus propias citas

#### ROLE_PATIENT (Paciente)
- **Permisos**: Acceso a sus propios datos
- **Operaciones**:
  - Ver y actualizar su propio perfil
  - Ver sus propias citas

### Anotaciones de Seguridad

**A Nivel de Controlador**:
```java
@PreAuthorize("hasRole('SECRETARY')")
public class DoctorController { ... }
```

**A Nivel de Método**:
```java
@PreAuthorize("@securityService.isSecretaryOrDoctor(#doctorId)")
public ResponseEntity<DoctorResponseDto> findDoctorById(@PathVariable Long doctorId) { ... }
```

**Métodos de SecurityService**:
- `isSecretaryOrSelf(Long requestedId)`: Verifica si es SECRETARY o el usuario solicitante
- `isSecretaryOrDoctor(Long doctorId)`: Verifica si es SECRETARY o el DOCTOR específico
- `isSecretaryOrPatient(Long patientId)`: Verifica si es SECRETARY o el PATIENT específico

### Hash de Contraseñas

- **Algoritmo**: BCrypt
- **Configuración**: `BCryptPasswordEncoder` con configuración por defecto
- **Uso**: Todas las contraseñas se hashean antes de almacenarse en BD

---

## Configuración e Instalación

### Prerrequisitos

- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+
- Git

### Variables de Entorno

Configurar las siguientes variables de entorno:

```bash
# JWT Configuration
export JWT_SECRET="your-secret-key-min-256-bits"
export JWT_ISSUER="medical-appointment-system"
export JWT_EXPIRATION_MINUTES=60

# MySQL Configuration (por servicio)
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME_USER=user_service_db
export DB_NAME_APPOINTMENT=appointment_service_db
export DB_NAME_CALENDAR=calendar_service_db
export DB_NAME_INSURANCE=health_insurance_service_db
export DB_USERNAME=root
export DB_PASSWORD=your-password

# Eureka Configuration
export EUREKA_HOST=localhost
export EUREKA_PORT=8761

# Config Server
export CONFIG_SERVER_URL=http://localhost:8888
```

### Instalación

1. **Clonar el repositorio**:
```bash
git clone https://github.com/your-repo/medical-appointment-system.git
cd medical-appointment-system
```

2. **Crear bases de datos MySQL**:
```sql
CREATE DATABASE user_service_db;
CREATE DATABASE appointment_service_db;
CREATE DATABASE calendar_service_db;
CREATE DATABASE health_insurance_service_db;
```

3. **Compilar todos los servicios**:
```bash
mvn clean install
```

4. **Iniciar servicios en orden**:

```bash
# 1. Eureka Server
cd eureka-server
mvn spring-boot:run

# 2. Config Server
cd ../config-server
mvn spring-boot:run

# 3. User Service
cd ../UserService
mvn spring-boot:run

# 4. Otros servicios (en paralelo)
cd ../AppointmentService && mvn spring-boot:run &
cd ../CalendarService && mvn spring-boot:run &
cd ../HealthInsuranceService && mvn spring-boot:run &

# 5. API Gateway
cd ../api-gate-way
mvn spring-boot:run
```

### Verificación

1. **Eureka Dashboard**: `http://localhost:8761`
   - Verificar que todos los servicios estén registrados

2. **Health Checks**:
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8085/actuator/health
curl http://localhost:8086/actuator/health
curl http://localhost:8087/actuator/health
```

3. **Login**:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@sanatory.com",
    "password": "00000000"
  }'
```

---

## Uso del Sistema

### Flujo Típico de Trabajo

#### 1. Autenticación
```bash
# Login como secretaria
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@sanatory.com",
    "password": "00000000"
  }'

# Guardar el token recibido
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 2. Crear un Doctor
```bash
curl -X POST http://localhost:8080/doctor/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@clinic.com",
    "dni": "12345678",
    "phoneNumber": "+54911234567",
    "password": "doctor123"
  }'
```

#### 3. Crear Calendario para el Doctor
```bash
curl -X POST http://localhost:8080/doctorCalendar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "doctorId": 1,
    "name": "Consultorio Principal",
    "timeZone": "America/Argentina/Buenos_Aires",
    "availabilityPatterns": [
      {
        "dayOfWeek": "MONDAY",
        "startTime": "09:00:00",
        "endTime": "13:00:00"
      },
      {
        "dayOfWeek": "MONDAY",
        "startTime": "15:00:00",
        "endTime": "19:00:00"
      }
    ]
  }'
```

#### 4. Crear un Paciente
```bash
curl -X POST http://localhost:8080/patient/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "María",
    "lastName": "González",
    "email": "maria.gonzalez@email.com",
    "dni": "87654321",
    "phoneNumber": "+54917654321",
    "password": "patient123",
    "birthDate": "1990-05-15",
    "address": "Av. Libertador 1234"
  }'
```

#### 5. Crear un Seguro y Asignarlo al Paciente
```bash
# Crear seguro
curl -X POST http://localhost:8080/api/v1/health-insurances \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "OSDE",
    "companyCode": 123456,
    "phoneNumber": "+541143211234",
    "email": "info@osde.com.ar"
  }'

# Crear plan de cobertura
curl -X POST http://localhost:8080/coveragePlan/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "healthInsuranceId": 1,
    "name": "Plan 310",
    "description": "Cobertura 70%",
    "coverageValuePercentage": 70.00
  }'

# Asignar seguro al paciente
curl -X POST http://localhost:8080/patientInsurance/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "patientDni": "87654321",
    "credentialNumber": "OSDE-87654321-001",
    "healthInsuranceId": 1,
    "coveragePlanId": 1
  }'
```

#### 6. Crear un Tipo de Cita
```bash
curl -X POST http://localhost:8080/appointmentTypes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Consulta General",
    "description": "Consulta médica general",
    "durationMin": 30,
    "bufferTimeMin": 10,
    "basePrice": 5000.00
  }'
```

#### 7. Crear una Cita
```bash
curl -X POST http://localhost:8080/appointment/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "doctorId": 1,
    "doctorCalendarId": 1,
    "patientId": 1,
    "appointmentTypeId": 1,
    "patientInsuranceId": 1,
    "date": "2025-12-15T10:00:00",
    "notes": "Primera consulta"
  }'
```

#### 8. Consultar Citas del Doctor
```bash
# Citas de hoy
curl -X GET "http://localhost:8080/appointment/doctor/1/today" \
  -H "Authorization: Bearer $TOKEN"

# Citas en rango de fechas
curl -X GET "http://localhost:8080/appointment/doctor/1/search-date-range?startDate=2025-12-01&endDate=2025-12-31" \
  -H "Authorization: Bearer $TOKEN"
```

#### 9. Agregar Excepción al Calendario (Feriado Global)
```bash
curl -X POST http://localhost:8080/calendarException \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "scope": "GLOBAL",
    "startDate": "2026-12-25",
    "exceptionType": "HOLIDAY",
    "reason": "Navidad"
  }'
```

#### 10. Agregar Vacaciones del Doctor (SEMI_GLOBAL)
```bash
curl -X POST http://localhost:8080/calendarException \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "scope": "SEMI_GLOBAL",
    "doctorId": 1,
    "startDate": "2026-01-15",
    "endDate": "2026-01-30",
    "exceptionType": "VACATION"
  }'
```

---

### Documentación Interactiva (Swagger)

Cada servicio expone documentación Swagger/OpenAPI:

- **User Service**: `http://localhost:8084/swagger-ui.html`
- **Appointment Service**: `http://localhost:8085/swagger-ui.html`
- **Calendar Service**: `http://localhost:8086/swagger-ui.html`
- **Health Insurance Service**: `http://localhost:8087/swagger-ui.html`

---

## Características Adicionales

### Circuit Breaker (Resilience4j)

Los servicios implementan circuit breakers para tolerancia a fallos en llamadas inter-servicios.

**Configuración típica**:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      userService:
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
        slidingWindowSize: 10
```

### Load Balancing

Spring Cloud LoadBalancer distribuye las solicitudes entre instancias de servicios registrados en Eureka.

### Actuator Endpoints

Métricas y monitoreo disponibles en `/actuator`:
- `/actuator/health` - Estado de salud del servicio
- `/actuator/metrics` - Métricas del servicio
- `/actuator/info` - Información del servicio

---

## Consideraciones de Despliegue

### Producción

Para despliegue en producción, considerar:

1. **Seguridad**:
   - Rotar `JWT_SECRET` regularmente
   - Usar HTTPS para todas las comunicaciones
   - Configurar CORS apropiadamente
   - Implementar rate limiting

2. **Base de Datos**:
   - Configurar conexiones pool
   - Implementar backups automáticos
   - Usar réplicas read-only para consultas

3. **Escalabilidad**:
   - Ejecutar múltiples instancias de cada servicio
   - Usar load balancer externo (Nginx, HAProxy)
   - Considerar Kubernetes para orquestación

4. **Monitoreo**:
   - Implementar ELK Stack (Elasticsearch, Logstash, Kibana)
   - Usar Prometheus + Grafana para métricas
   - Configurar alertas con Alertmanager



---

## Contacto y Soporte

Para reportar bugs, solicitar features o contribuir al proyecto:

- **Repositorio**: [GitHub Repository](https://github.com/eugeniaarias16/Sanatory-System-Appointments-Manager)
- **Issues**: [GitHub Issues](https://github.com/eugeniaarias16/Sanatory-System-Appointments-Manager/issues)

---

## Licencia

Este proyecto está bajo la licencia [MIT License](LICENSE).

---

**Versión**: 1.0.0
**Última Actualización**: Diciembre 2025
**Desarrollado con**: Spring Boot 3.x y Spring Cloud

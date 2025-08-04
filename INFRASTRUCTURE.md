# Módulo de Infraestructura - Plataforma Iglesia

## Arquitectura Hexagonal

Este módulo implementa la capa de infraestructura siguiendo los principios de la arquitectura hexagonal (puertos y adaptadores).

### Estructura

```
infraestructure/
├── adapters/
│   └── out/
│       ├── entities/           # Entidades de MongoDB
│       ├── repositories/       # Repositorios de Spring Data
│       ├── UserUseCasesImpl.java
│       └── ServicesUseCasesImpl.java
└── config/
    └── MongoConfig.java       # Configuración de MongoDB
```

## Entidades de MongoDB

### UserEntity
- **Colección**: `users`
- **Campos**: id, name, email, phoneNumber, role, createdAt
- **Métodos**: `fromDomain()`, `toDomain()`

### ServiceEntity
- **Colección**: `services`
- **Campos**: id, serviceDate, practiceDate, directors[], musiciansList[], songsList[], location, createdAt
- **Métodos**: `fromDomain()`, `toDomain()`

### MusiciansListEntity
- **Campos**: musician (UserEntity), instrument
- **Métodos**: `fromDomain()`, `toDomain()`

### SongsEntity
- **Campos**: youtubeLink, tone, name, artist
- **Métodos**: `fromDomain()`, `toDomain()`

## Repositorios

### UserRepository
- `findByEmail(String email)`
- `findByPhoneNumber(String phoneNumber)`
- `findByRole(UserModel.Role role)`
- `findAllMusicians()`
- `findAllDirectors()`

### ServiceRepository
- `findByServiceDate(LocalDate serviceDate)`
- `findByServiceDateBetween(LocalDate startDate, LocalDate endDate)`
- `findByDirectorId(String directorId)`
- `findByMusicianId(String musicianId)`
- `findExpiredServices(LocalDate currentDate)`

## Adaptadores de Salida

### UserUseCasesImpl
Implementa `UserUseCases` del dominio:
- `createUser(UserModel user)`
- `getUserByEmail(String email)`
- `getUserById(String id)`
- `updateUser(UserModel user)`
- `deleteUser(String id)`
- `getAllUsers()`
- `getUserByPhoneNumber(String phoneNumber)`

### ServicesUseCasesImpl
Implementa `ServicesUseCases` del dominio:
- `createService(ServiceModel service)`
- `getServiceById(String id)`
- `updateService(ServiceModel service)`
- `deleteService(String id)`
- `getAllServices()`
- `getServicesByDirector(String directorId)`
- `getServicesByMusician(String musicianId)`

## Configuración

### MongoDB
- **Host**: localhost
- **Puerto**: 27017
- **Base de datos**: plataforma_iglesia
- **Auto-index-creation**: true

### Configuración de Test
- **Base de datos**: plataforma_iglesia_test
- **Profile**: test

## Tests

### Tests Unitarios
- `UserUseCasesImplTest`: Tests para el adaptador de usuarios
- `ServicesUseCasesImplTest`: Tests para el adaptador de servicios

### Tests de Integración
- `IntegrationTest`: Test de integración con MongoDB

## Características

### Conversión Automática
- Conversión automática entre entidades de dominio y entidades de MongoDB
- Métodos `fromDomain()` y `toDomain()` en cada entidad

### Validaciones
- Validación de existencia antes de actualizar/eliminar
- Manejo de excepciones con mensajes descriptivos

### Optimización
- Uso de Spring Data MongoDB para consultas optimizadas
- Queries personalizadas con `@Query`
- Indexación automática

### Base de Datos No Relacional
- Documentos anidados para relaciones complejas
- Consultas eficientes con MongoDB
- Escalabilidad horizontal

## Uso

```java
@Autowired
private UserUseCases userUseCases;

// Crear usuario
UserModel user = new UserModel();
user.setName("Juan Pérez");
user.setEmail("juan@iglesia.com");
user.setRole(UserModel.Role.MUSICIAN);
UserModel createdUser = userUseCases.createUser(user);

// Buscar usuario
UserModel foundUser = userUseCases.getUserById(createdUser.getId());
```

## Próximos Pasos

1. **Controladores REST**: Implementar adaptadores de entrada (controladores)
2. **Validaciones**: Agregar validaciones más robustas
3. **Auditoría**: Implementar auditoría de cambios
4. **Caché**: Implementar caché para consultas frecuentes
5. **Migrations**: Implementar sistema de migraciones de MongoDB 
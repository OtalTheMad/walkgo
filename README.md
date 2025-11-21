# 🏃‍♂️ WalkGo

WalkGo es una aplicación de caminatas y seguimiento de actividad con:

- **API REST** en Spring Boot (JWT, MySQL).
- **App Android** en Java usando Retrofit + OkHttp.
- Módulos de **login**, **perfil**, **seguidores (social)** y **recorridos** con métricas globales y semanales.

---

## 📂 Estructura del proyecto

### Backend (Spring Boot)

Paquetes principales:

- `org.walkgo.api`
  - `config` / `security` – Configuración de seguridad y JWT.
  - `controller` – Controladores REST.
  - `model` – Entidades JPA (`Usuario`, `Perfil`, `Estadistica`, `Amigo`, `Recorrido`, etc.).
  - `repository` – Interfaces `JpaRepository`.
  - `service` – Lógica de negocio (login, recorridos, amigos, etc.).

Tablas clave en la base de datos (MySQL):

- `usuarios`
- `perfiles`
- `estadisticas`
- `recorridos`
- tabla de seguidores (antes “amigos”, con columnas tipo `id_seg`, `id_usuario`, `id_seguido`)

---

### Android (App WalkGo)

Paquetes principales:

- `com.example.walkgo`
  - Activities: `HomeActivity`, `SeguidoresActivity`, etc.
  - Adapters: `AmigosAdapter`, etc.
- `com.api.walkgo`
  - `RetrofitClient`, `JwtInterceptor`
  - Activities de módulos conectados a la API: `LoginActivity`, `PerfilActivity`
  - `models`: clases de modelo (`Usuario`, `Perfil`, `Estadistica`, `Amigo`, `ApiAmigo`, `ApiCreateAmigo`, `ApiUpdateAmigo`, etc.).
  - Interfaces Retrofit: `UsuarioService`, `PerfilService`, `EstadisticaService`, `AmigosAPI`, `RecorridoService` (según se vaya ampliando).

---

## ⚙️ Backend – Configuración y ejecución

### Requisitos

- Java 17+ (o la versión que uses en el proyecto).
- Gradle o Maven (según esté configurado).
- MySQL en ejecución.
- Variable de entorno `JWT_SECRET` definida (clave HMAC suficientemente larga, por ejemplo 32+ caracteres).

### Configuración

En `application.properties` o `application.yml`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/walkgo?useSSL=false&serverTimezone=UTC
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

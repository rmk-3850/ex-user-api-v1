# 🧾 User Service

[![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-3.4+-59666C?logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![MariaDB](https://img.shields.io/badge/MariaDB-10.x%20%7C%2011-003545?logo=mariadb&logoColor=white)](https://mariadb.org/)
[![Gradle](https://img.shields.io/badge/Gradle-9.3.0-02303A?logo=gradle&logoColor=white)](https://gradle.org/)

JWT 기반 인증을 제공하는 사용자 관리 마이크로서비스입니다. 회원 가입/로그인/회원 정보 조회·수정·탈퇴 기능을 제공하며, 공통 응답 포맷과 글로벌 예외 처리, Swagger 기반 API 문서를 포함합니다.

---

## 📋 목차

- [🛠 Tech Stack](#-tech-stack)
- [🌟 Key Features](#-key-features)
- [🧱 Architecture](#-architecture)
- [🚀 실행 방법](#-실행-방법)
- [📚 API 엔드포인트](#-api-엔드포인트)
- [⚙️ 성능 최적화 & 트러블슈팅](#️-성능-최적화--트러블슈팅)
- [☕ Java 21 기능 활용](#-java-21-기능-활용)
- [🔒 Security 개요](#-security-개요)

---

## 🛠 Tech Stack

### Language
| 항목 | 버전 | 비고 |
|------|------|------|
| **Java** | 21 | Gradle Toolchain, `JavaLanguageVersion.of(21)` |

```11:15:build.gradle
java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}
```

### Frameworks & Libraries
| 항목 | 버전 |
|------|------|
| Spring Boot | 4.0.2 |
| Spring Web MVC | (spring-boot-starter-webmvc) |
| Spring Data JPA | 3.4+ (Hibernate 7.x 매니지드) |
| Spring Security | (spring-boot-starter-security) |
| Bean Validation | (spring-boot-starter-validation) |
| JWT (jjwt-api) | 0.11.5 |
| springdoc-openapi (Swagger) | 2.7.0 |
| Lombok | (annotationProcessor) |

### Database
| 항목 | 버전 |
|------|------|
| MariaDB | mariadb-java-client |
| 테스트 DB | H2 (인메모리) |

### Build & Test
| 항목 | 버전 |
|------|------|
| Gradle Wrapper | 9.3.0 |
| JUnit | useJUnitPlatform() |

---

## 🌟 Key Features

### 비즈니스 로직 흐름

- **회원 가입 (Sign Up)**
  - `UserController.signUp()` → `SignService.signUp()` → `UserRepository.save()` 순으로 호출
  - 요청 DTO(`SignUpRequestDto`)에 대해 Bean Validation(아이디/비밀번호/전화번호/이메일/권한 리스트) 수행
  - 비밀번호는 `PasswordEncoder`로 BCrypt 해시 후 저장

- **로그인 (Sign In) & JWT 발급**
  - `UserController.signIn()` → `SignService.signIn()`
  - UID로 사용자 조회 후 비밀번호 일치 검증
  - 성공 시 `JwtTokenProvider.createToken(uid, roles)`로 Access Token 발급
  - 응답은 `UserResponse<SignInResponseDto>`로 래핑

- **회원 정보 조회/수정/탈퇴**
  - 조회: `GET /id/{id}` → `SignService.select(id)`
  - 수정: `PUT /id/{id}` → `SignService.update(id, dto)`
  - 탈퇴: `DELETE /id/{id}` → `SignService.delete(id)`
  - 모든 비즈니스 로직에서 `UserResponse<T>` 공통 응답 포맷 사용

- **중복 체크**
  - UID 중복: `GET /auth/uid/{uid}`
  - Email 중복: `GET /auth/email/{email}`

- **예외 처리 & 공통 응답**
  - `BusinessException`을 `GlobalExceptionHandler`에서 `UserResponse.fail()`로 변환

```13:17:src/main/java/com/rm/exception/GlobalExceptionHandler.java
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<UserResponse<Void>> handleBusiness(BusinessException e) {
	    return ResponseEntity.status(e.getErrorCode().getStatus()).body(UserResponse.fail(e.getErrorCode()));
	}
```

---

## 🧱 Architecture

### 📦 디렉토리 구조

```text
user-service/
├── build.gradle
├── settings.gradle
├── docker-compose.yml
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/com/rm/
│   │   │   ├── UserServiceApplication.java
│   │   │   ├── exception/
│   │   │   │   ├── BusinessException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── time/
│   │   │   │   └── TimeConfig.java
│   │   │   ├── user/
│   │   │   │   ├── config/
│   │   │   │   │   ├── PasswordEncoderConfiguration.java
│   │   │   │   │   └── SecurityConfiguration.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── UserController.java
│   │   │   │   ├── dto/                      # record 기반 DTO
│   │   │   │   │   ├── ErrorCode.java
│   │   │   │   │   ├── SignInResponseDto.java
│   │   │   │   │   ├── SignRequestEssence.java
│   │   │   │   │   ├── SignResponseEssence.java
│   │   │   │   │   ├── SignUpRequestDto.java
│   │   │   │   │   ├── SignUpResponseDto.java
│   │   │   │   │   ├── UpdateRequestDto.java
│   │   │   │   │   └── UserResponse.java
│   │   │   │   ├── entity/
│   │   │   │   │   └── User.java
│   │   │   │   ├── exception/
│   │   │   │   │   ├── CustomAccessDeniedHandler.java
│   │   │   │   │   ├── CustomAuthenticationEntryPoint.java
│   │   │   │   │   ├── PasswordNotMatchException.java
│   │   │   │   │   └── UserNotFoundException.java
│   │   │   │   ├── filter/
│   │   │   │   │   └── InternalHeaderFilter.java
│   │   │   │   ├── infra/
│   │   │   │   │   └── JwtTokenProvider.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── UserRepository.java
│   │   │   │   └── service/
│   │   │   │       └── SignService.java
│   │   │   └── valid/
│   │   │       ├── Password.java / PasswordValidator.java
│   │   │       └── Telephone.java / TelephoneValidator.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-local.yml
│   └── test/
│       └── java/com/rm/
│           └── UserServiceApplicationTests.java
└── docker/mariadb/user/init/
    └── init.sql
```

### 🗄 데이터 모델 (ERD 텍스트)

```
┌─────────────────────────────────────────────────────────────────┐
│                         user 테이블                               │
├─────────────────────────────────────────────────────────────────┤
│ id              BIGINT (PK, AUTO_INCREMENT)                      │
│ uid             VARCHAR (UNIQUE, NOT NULL)   ← 로그인 아이디      │
│ name            VARCHAR (NOT NULL)                               │
│ password        VARCHAR (NOT NULL)          ← BCrypt 해시         │
│ phone_number    VARCHAR(20) (NOT NULL)                            │
│ email           VARCHAR(255) (UNIQUE, NOT NULL)                   │
└─────────────────────────────────────────────────────────────────┘
         │
         │ 1:N (ElementCollection)
         ▼
┌─────────────────────────────────────────────────────────────────┐
│              user_roles 테이블 (내부 매핑)                         │
├─────────────────────────────────────────────────────────────────┤
│ user_id         BIGINT (FK)                                      │
│ roles           VARCHAR                                          │
└─────────────────────────────────────────────────────────────────┘
```

**User 엔티티 코드:**

```31:65:src/main/java/com/rm/user/entity/User.java
@Entity
@Table(
	name = "user",
	uniqueConstraints = {@UniqueConstraint(columnNames = "email")}
)
// ...
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, unique = true)
	private String uid;
	// ...
	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
	private List<String> roles=new ArrayList<String>();
```

---

## 🚀 실행 방법

### 1) 필수 요구 사항

- **Java 21** 설치
- **Gradle Wrapper** 사용 (별도 Gradle 설치 불필요)
- **MariaDB** 10.x 이상 (또는 `docker-compose`로 실행)

### 2) `application.yml` / `application-local.yml` 예시

**`src/main/resources/application.yml`**

```yaml
spring:
  application:
    name: user-service
  profiles:
    active: local
```

**`src/main/resources/application-local.yml` (로컬 프로필)**

```yaml
server:
  port: '8081'

app:
  env: local

jwt:
  secret: ${secret}              # 환경 변수 또는 .env
  token-valid-ms: ${token-valid-ms}

spring:
  datasource:
    url: jdbc:mariadb://127.0.0.1:3307/${MYSQL_DATABASE}
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}
    driver-class-name: org.mariadb.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
```

**환경 변수 예시 (.env)**

```bash
MYSQL_DATABASE=user_schema
MYSQL_USER=user_app
MYSQL_PASSWORD=1234
secret=your-jwt-secret-key-change-in-production
token-valid-ms=3600000
```

> ⚠️ `docker/mariadb/user/init/init.sql`에서 `user_schema`, `user_app` 사용자·DB 생성. 포트 3307로 노출.

### 3) 애플리케이션 실행

```bash
# Windows (PowerShell/CMD)
gradlew.bat clean bootRun

# Unix-like (WSL, macOS, Linux)
./gradlew clean bootRun
```

기동 후 `http://localhost:8081`에서 API 접근 (포트는 `application-local.yml` 기준).

---

## 📚 API 엔드포인트

### 🔎 Swagger UI / OpenAPI

- **OpenAPI UI:** `http://localhost:8081/swagger-ui.html` 또는 `http://localhost:8081/swagger-ui/index.html`

### 📡 주요 REST API (테이블)

| Method | URL | 설명 | Request Body | Response |
|--------|-----|------|--------------|----------|
| GET | `/auth/uid/{uid}` | UID 중복 여부 조회 | - | `Boolean` |
| GET | `/auth/email/{email}` | Email 중복 여부 조회 | - | `Boolean` |
| GET | `/auth` | 로그인 | `SignRequestEssence` | `UserResponse<SignInResponseDto>` |
| POST | `/auth` | 회원 가입 | `SignUpRequestDto` | `UserResponse<SignUpResponseDto>` |
| GET | `/id/{id}` | 회원 정보 조회 | - | `UserResponse<SignUpResponseDto>` |
| PUT | `/id/{id}` | 회원 정보 수정 | `UpdateRequestDto` | `UserResponse<SignUpResponseDto>` |
| DELETE | `/id/{id}` | 회원 탈퇴 | - | `UserResponse<Void>` |

> 📌 조회/수정/탈퇴(`/id/{id}`)는 인증 필요. 요청 시 `X-User-Uid`, `X-User-Roles` 헤더 또는 JWT 기반 인증 필요.

---

## ⚙️ 성능 최적화 & 트러블슈팅

### 1) JPA N+1 문제 인지 및 설계 선택

현재 `User` 엔티티는 다른 엔티티와 `@OneToMany`, `@ManyToOne` 등 복잡한 연관 관계가 없고, 권한(`roles`)만 `@ElementCollection(fetch = FetchType.EAGER)`로 관리합니다.

```61:64:src/main/java/com/rm/user/entity/User.java
	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
	private List<String> roles=new ArrayList<String>();
```

- **EAGER 선택 이유**
  - 로그인 시 권한 정보를 한 번에 로딩해 `InternalHeaderFilter` 및 인증 체인에서 지연 최소화
- **잠재 이슈**
  - 향후 `User`에 `@OneToMany` 관계가 늘어나면 EAGER가 조인 카디널리티를 늘려 성능 저하 가능
  - 이 경우 **LAZY + Fetch Join / Batch Size** 전략 전환 권장

### 2) Fetch Join / Batch Size 적용 전략 (확장 시)

현재는 복잡한 연관 로딩이 없어 N+1이 발생하지 않지만, ERD 확장 시 아래와 같이 최적화할 수 있습니다.

**엔티티 측면 (Batch Size)**

```java
@OneToMany(mappedBy = "user")
@BatchSize(size = 100)
private List<Order> orders = new ArrayList<>();
```

**Repository 측면 (Fetch Join)**

```java
@Query("select u from User u join fetch u.orders where u.id = :id")
Optional<User> findWithOrdersById(@Param("id") Long id);
```

**글로벌 배치 사이즈 (`application.yml`)**

```yaml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100
```

> 💡 실제 프로젝트에서는 JPA 쿼리 로그 및 APM으로 N+1 패턴을 식별한 뒤, **Fetch Join → Batch Size → 캐싱** 순으로 적용하는 것을 권장합니다.

### 3) 서비스 계층 트랜잭션 전략

회원 관련 비즈니스 로직은 `SignService`에서 `@Transactional`로 처리합니다.

```47:56:src/main/java/com/rm/user/service/SignService.java
	@Transactional
	public UserResponse<SignUpResponseDto> select(Long id){
		User user=getUserOrThrow(id);
		return UserResponse.success(new SignUpResponseDto(
				new SignResponseEssence(user.getId(), user.getUid(), user.getName()),
				user.getPhoneNumber(),
				user.getEmail()
		));
	}
```

- 읽기/쓰기를 트랜잭션으로 감싸 **일관성** 확보
- 필요 시 `@Transactional(readOnly = true)`로 읽기 전용 최적화 적용 가능

---

## ☕ Java 21 기능 활용

### 1) `record` 기반 DTO 설계

프로젝트 전반에서 Java 21 **`record`**를 활용해 DTO를 정의합니다.

**UserResponse (공통 응답 래퍼)**

```5:11:src/main/java/com/rm/user/dto/UserResponse.java
public record UserResponse<T>(
		boolean success,
		HttpStatus status,
		String code,
		String msg,
		T data
	) {
```

**SignUpRequestDto**

```14:21:src/main/java/com/rm/user/dto/SignUpRequestDto.java
public record SignUpRequestDto(
		@Valid SignRequestEssence e,
		@Schema(description = "공백불가")
		@NotBlank String name,
		@Telephone String phoneNumber,
		@Email@NotNull String email,
		@NotNull List<String> roles
	) {
```

- **장점:** 불변 구조, 자동 생성된 `equals`/`hashCode`/`toString`, Validation 애노테이션 함께 선언 가능
- **record DTO 목록:** `UserResponse`, `SignUpRequestDto`, `SignUpResponseDto`, `SignInResponseDto`, `SignRequestEssence`, `SignResponseEssence`, `UpdateRequestDto`

### 2) 그 외 Java 21 기능

- **Virtual Threads**, **Switch Expressions** 등은 현재 코드베이스에서 직접 사용하지 않음
- 향후 IO 바운드 작업 증가 시 Virtual Threads로 스레드 자원 최적화, 복잡한 분기 시 Switch Expressions로 가독성 향상 등을 고려할 수 있는 구조

---

## 🔒 Security 개요

### InternalHeaderFilter 기반 인증

보호된 경로(`/id/{id}` 등)는 `InternalHeaderFilter`를 통해 인증합니다. 내부 서비스 간 통신 시 `X-User-Uid`, `X-User-Roles` 헤더로 인증합니다.

```31:47:src/main/java/com/rm/user/config/SecurityConfiguration.java
		http.httpBasic(httpBasic->httpBasic.disable())
			.csrf(csrf->csrf.disable())
			.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth->auth
				.requestMatchers(
		            "/v2/api-docs",
		            "/swagger-resources/**",
		            "/swagger-ui.html",
		            "/swagger/**").permitAll()
				.anyRequest().hasRole("USER")
			)
			// ...
			.addFilterBefore(new InternalHeaderFilter(resolver),
				UsernamePasswordAuthenticationFilter.class
			);
```

- 세션 미사용(`STATELESS`), Swagger 경로는 `permitAll()`, 나머지는 `hasRole("USER")` 필요

---

## 🧪 테스트

- **테스트 의존성:** `spring-boot-starter-*-test`, `com.h2database:h2`
- JPA 테스트 시 H2 인메모리 DB 사용
- 통합 테스트로 컨트롤러/서비스/레포지토리 검증 가능

```bash
./gradlew test
```

---

## ✅ 정리

- **Java 21 & Spring Boot 4** 기반 사용자 관리 서비스
- **record 기반 DTO**, **글로벌 예외 처리**, **JWT 발급**, **InternalHeader 기반 인증**, **Swagger 문서화** 핵심
- JPA N+1은 현재 구조상 발생하지 않으나, **Fetch Join / Batch Size** 등 확장 시 대응 전략 명확화
- 설정 예시·API 테이블·아키텍처를 참고해 로컬 기동 및 확장 가능

## 🧾 User Service

JWT 기반 인증을 제공하는 사용자 관리 마이크로서비스입니다. 회원 가입/로그인/회원 정보 조회·수정/탈퇴 기능을 제공하며, 공통 응답 포맷과 글로벌 예외 처리, Swagger 기반 API 문서를 포함합니다.

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-6DB33F?logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-59666C?logo=spring&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-10.x-003545?logo=mariadb&logoColor=white)

---

## 🛠 Tech Stack

- **Language**
  - Java 21 (Gradle Toolchain, `JavaLanguageVersion.of(21)`)  
    ```11:14:build.gradle
    java {
    	toolchain {
    		languageVersion = JavaLanguageVersion.of(21)
    	}
    }
    ```

- **Frameworks & Libraries**
  - Spring Boot 4.0.2 (`org.springframework.boot` Gradle 플러그인)
  - Spring Web MVC (`spring-boot-starter-webmvc`)
  - Spring Data JPA (`spring-boot-starter-data-jpa`)
  - Spring Security (`spring-boot-starter-security`)
  - Bean Validation (`spring-boot-starter-validation`)
  - JWT: `io.jsonwebtoken:jjwt-api:0.11.5`
  - OpenAPI/Swagger: `springdoc-openapi-starter-webmvc-ui:2.7.0`
  - Lombok

- **Database**
  - MariaDB (`org.mariadb.jdbc:mariadb-java-client`)
  - 테스트 DB: H2 (`com.h2database:h2`)

- **Build & Test**
  - Gradle Wrapper (`gradlew`, `gradlew.bat`)
  - JUnit + Spring Boot Test (`useJUnitPlatform()`)

---

## 🌟 Key Features (비즈니스 로직 흐름)

- **회원 가입 (Sign Up)**
  - `UserController.signUp()` → `SignService.signUp()` → `UserRepository.save()` 순으로 호출
  - 요청 DTO(`SignUpRequestDto`)에 대해 Bean Validation(아이디/비밀번호/전화번호/이메일/권한 리스트)을 수행
  - 비밀번호는 `PasswordEncoder`로 해시 후 저장, `UserResponse` 공통 응답 형태로 성공 결과 반환

- **로그인 (Sign In) & JWT 발급**
  - `UserController.signIn()` → `SignService.signIn()`
  - 아이디(uid)로 사용자 조회 후, 비밀번호 일치 여부 검증
  - 성공 시 `JwtTokenProvider.createToken(uid, roles)`를 통해 Access Token 발급
  - 응답은 `UserResponse<SignInResponseDto>`로 래핑

- **회원 정보 조회/수정/탈퇴**
  - 조회: `GET /api/user/id/{id}` → `SignService.select(id)`  
  - 수정: `PUT /api/user/id/{id}` → `SignService.update(id, dto)`  
  - 탈퇴: `DELETE /api/user/id/{id}` → `SignService.delete(id)`  
  - 모든 비즈니스 로직에서 `UserResponse<T>` 공통 응답 포맷 사용, 에러는 `ErrorCode` 기반으로 응답

- **중복 체크**
  - UID 중복 여부: `GET /api/user/uid/{uid}`
  - Email 중복 여부: `GET /api/user/email/{email}`

- **예외 처리 & 공통 응답**
  - 비즈니스 예외(`BusinessException`)를 `GlobalExceptionHandler`에서 받아 `UserResponse.fail()`로 변환
    ```13:20:src/main/java/com/rm/exception/GlobalExceptionHandler.java
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<UserResponse<Void>> handleBusiness(BusinessException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(UserResponse.fail(e.getErrorCode()));
    }
    ```

---

## 🧱 Architecture

### 📦 패키지 구조

```text
src/main/java/com/rm
 ├─ UserServiceApplication.java        # Spring Boot 엔트리 포인트
 ├─ exception
 │   ├─ BusinessException.java
 │   └─ GlobalExceptionHandler.java   # 공통 예외 처리
 ├─ time
 │   └─ TimeConfig.java
 ├─ user
 │   ├─ config
 │   │   ├─ PasswordEncoderConfiguration.java
 │   │   └─ SecurityConfiguration.java  # Spring Security & JWT 필터 설정
 │   ├─ controller
 │   │   └─ UserController.java         # REST API 엔드포인트
 │   ├─ dto                             # record 기반 DTO 모음
 │   ├─ entity
 │   │   └─ User.java                   # JPA 엔티티 (UserDetails 구현)
 │   ├─ exception
 │   │   ├─ CustomAccessDeniedHandler.java
 │   │   ├─ CustomAuthenticationEntryPoint.java
 │   │   ├─ PasswordNotMatchException.java
 │   │   └─ UserNotFoundException.java
 │   ├─ filter
 │   │   └─ JwtAuthenticationFilter.java
 │   ├─ infra
 │   │   └─ JwtTokenProvider.java
 │   ├─ repository
 │   │   └─ UserRepository.java
 │   └─ service
 │       ├─ SignService.java            # 회원 가입/로그인/CRUD 비즈니스 로직
 │       └─ UserdetailsServiceImpl.java
 └─ valid
     ├─ Password.java / PasswordValidator.java
     ├─ Telephone.java / TelephoneValidator.java
```

### 🗄 데이터 모델 (ERD 텍스트 표현)

- **`user` 테이블 (`User` 엔티티)**
  - `id` (PK, Long, auto-generated)
  - `uid` (로그인 아이디, unique, not null)
  - `name` (사용자 이름, not null)
  - `password` (BCrypt 등으로 해시된 비밀번호, not null)
  - `phone_number` (전화번호, 길이 20)
  - `email` (이메일, unique, not null)
  - `roles` (권한 리스트, `@ElementCollection`, 별도 테이블에 문자열 컬럼으로 저장)

```30:63:src/main/java/com/rm/user/entity/User.java
@Entity
@Table(
	name = "user",
	uniqueConstraints = {@UniqueConstraint(columnNames = "email")}
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails{

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, unique = true)
	private String uid;
	// ...
	@Column(nullable = false,length = 255)
	private String email;

	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
	private List<String> roles=new ArrayList<String>();
}
```

---

## 🚀 실행 방법

### 1) 필수 요구 사항

- **Java 21** 설치
- **Gradle Wrapper** 사용 (별도 Gradle 설치 불필요)
- **MariaDB** 10.x 이상 (또는 docker-compose로 실행)

### 2) `application.yml` 예시 (로컬 환경)

`src/main/resources/application.yml`에 다음과 같은 로컬 프로필 설정을 둘 수 있습니다. (아래 값은 예시입니다.)

```yaml
spring:
  application:
    name: user-service

  profiles:
    active: local

  datasource:
    url: jdbc:mariadb://localhost:3306/userdb
    username: user
    password: password
    driver-class-name: org.mariadb.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
        default_batch_fetch_size: 100  # 컬렉션 로딩 최적화를 위한 기본 배치 사이즈 예시

jwt:
  secret: example-secret-key-change-me
  expiration-seconds: 3600

server:
  port: 8080
```

> 실제 운영/개발 환경에서는 비밀번호·JWT 시크릿을 환경 변수나 별도 설정 파일로 분리하세요.

### 3) 애플리케이션 실행

```bash
# Windows (PowerShell/CMD)
gradlew.bat clean bootRun

# Unix-like (WSL, macOS, Linux)
./gradlew clean bootRun
```

애플리케이션이 성공적으로 기동되면 기본적으로 `http://localhost:8080`에서 API에 접근할 수 있습니다.

---

## 📚 API 엔드포인트

### 🔎 Swagger UI / OpenAPI

- OpenAPI UI (springdoc 기본 경로):  
  - `http://localhost:8080/swagger-ui/index.html`

### 📡 주요 REST API (요약)

| Method | URL                     | 설명                       | Request Body                 | Response                           |
|--------|------------------------|----------------------------|------------------------------|------------------------------------|
| GET    | `/api/user/uid/{uid}`  | 아이디 중복 여부 조회     | -                            | `Boolean`                          |
| GET    | `/api/user/email/{em}` | 이메일 중복 여부 조회     | -                            | `Boolean`                          |
| GET    | `/api/user/id/{id}`    | 회원 정보 조회            | -                            | `UserResponse<SignUpResponseDto>` |
| GET    | `/api/user`            | 로그인 (요청 바디 사용)   | `SignRequestEssence`        | `UserResponse<SignInResponseDto>` |
| POST   | `/api/user`            | 회원 가입                 | `SignUpRequestDto`          | `UserResponse<SignUpResponseDto>` |
| PUT    | `/api/user/id/{id}`    | 회원 정보 수정            | `UpdateRequestDto`          | `UserResponse<SignUpResponseDto>` |
| DELETE | `/api/user/id/{id}`    | 회원 탈퇴                 | -                            | `UserResponse<Void>`              |

컨트롤러 레벨 구현은 다음과 같이 `UserResponse<T>` 공통 응답을 사용합니다.

```50:58:src/main/java/com/rm/user/controller/UserController.java
@Operation(description = "개인 정보 조회")
@GetMapping("/id/{id}")
public ResponseEntity<UserResponse<SignUpResponseDto>> select(
		@Parameter(description = "id",required = true)
		@PathVariable("id") Long id
	){
	UserResponse<SignUpResponseDto> data=signService.select(id);
	return ResponseEntity.status(data.status()).body(data);
}
```

---

## ⚙️ 성능 최적화 (JPA) & 트러블슈팅 ⭐

### 1) JPA N+1 문제 인지 및 설계 상의 선택

현재 `User` 엔티티는 다른 엔티티와의 복잡한 연관 관계(`@OneToMany`, `@ManyToOne` 등)를 가지지 않고, 권한(`roles`)만 `@ElementCollection(fetch = FetchType.EAGER)`로 관리합니다.

```61:63:src/main/java/com/rm/user/entity/User.java
@ElementCollection(fetch = FetchType.EAGER)
@Builder.Default
private List<String> roles=new ArrayList<String>();
```

- **장점**
  - 로그인 시 권한 정보 로딩을 위해 별도의 추가 쿼리 없이 한 번에 가져올 수 있어, 인증 필터 체인에서의 지연을 줄입니다.
- **잠재 이슈**
  - 향후 `User`가 `@OneToMany` 관계를 여러 개 가지게 되면, EAGER 전략은 조인 결과의 카디널리티를 급격히 늘려 성능 저하를 유발할 수 있습니다.
  - 이 때는 **LAZY + Fetch Join / Batch Size** 전략으로 전환하는 것이 권장됩니다.

### 2) Fetch Join / Batch Size 적용 전략 (예시)

현재 코드에는 복잡한 연관 로딩이 없기 때문에 직접적인 N+1은 발생하지 않지만, ERD 확장 시 다음과 같은 방식으로 최적화를 적용할 수 있습니다.

- **엔티티 측면 (Batch Size)**

```java
@OneToMany(mappedBy = "user")
@BatchSize(size = 100)
private List<Order> orders = new ArrayList<>();
```

- **레포지토리 측면 (Fetch Join)**

```java
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u join fetch u.orders where u.id = :id")
    Optional<User> findWithOrdersById(@Param("id") Long id);
}
```

- **글로벌 배치 사이즈 설정 (예시)**  
  위에서 예시로 보여준 것처럼, `application.yml`에 다음 속성을 추가해 N+1을 완화할 수 있습니다.

```yaml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100
```

> 실제 프로젝트에서는 JPA 쿼리 로그 및 APM(Profile)을 통해 N+1 쿼리 패턴을 먼저 식별한 뒤, **Fetch Join → Batch Size → 캐싱** 순으로 단계적 최적화를 적용하는 것을 추천합니다.

### 3) 서비스 계층 트랜잭션 전략

회원 관련 비즈니스 로직은 `SignService`에서 트랜잭션 단위로 처리합니다.

```49:56:src/main/java/com/rm/user/service/SignService.java
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

- 읽기/쓰기 로직을 명확히 트랜잭션으로 감싸 **일관성**을 확보하고, 추후 필요 시 읽기 전용 최적화(`@Transactional(readOnly = true)`)를 적용할 수 있는 구조입니다.

---

## 🧪 Java 21 기능 활용

### 1) `record` 기반 DTO 설계

이 프로젝트는 Java 21의 **`record`**를 적극적으로 활용해 DTO를 정의합니다.

```5:10:src/main/java/com/rm/user/dto/UserResponse.java
public record UserResponse<T>(
		boolean success,
		HttpStatus status,
		String code,
		String msg,
		T data
	) {
```

```13:19:src/main/java/com/rm/user/dto/SignUpRequestDto.java
public record SignUpRequestDto(
		@Valid SignRequestEssence e,
		@Schema(description = "공백불가")
		@NotBlank String name,
		@Telephone String phoneNumber,
		@Email@NotNull String email,
		@NotNull List<String> roles
	) {
```

- **장점**
  - 불변(immutable) 데이터 구조를 간결하게 표현할 수 있어, 요청/응답 DTO에 적합합니다.
  - `equals`, `hashCode`, `toString`, 생성자 등이 자동 생성되어 보일러플레이트 코드가 크게 감소합니다.
  - Validation 애노테이션을 컴팩트하게 함께 선언할 수 있습니다.

### 2) 그 외 Java 21 기능

- 현재 코드 베이스에서는 **Virtual Threads**나 **Switch Expressions** 같은 기능은 직접 사용하지 않지만,
  - 동시성 이슈가 큰 IO 바운드 작업이 추가될 경우 Virtual Threads를 통한 스레드 자원 활용 최적화,
  - 복잡한 분기 로직에 Switch Expressions를 도입해 가독성 향상,
  을 고려할 수 있는 구조입니다.

---

## 🧩 Security & JWT 개요

- **JWT 필터 체인 구성**

```22:41:src/main/java/com/rm/user/config/SecurityConfiguration.java
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {
	private final JwtTokenProvider tokenProvider;

	public SecurityFilterChain securityFilterChain(HttpSecurity http,
			CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
			CustomAccessDeniedHandler customAccessDeniedHandler
			) {
		http.httpBasic(httpBasic->httpBasic.disable())
			.csrf(csrf->csrf.disable())
			.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth->auth
				.anyRequest().hasRole("USER")
			)
			.exceptionHandling(exception->exception
				.authenticationEntryPoint(customAuthenticationEntryPoint)
				.accessDeniedHandler(customAccessDeniedHandler)
			)
			.addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
				UsernamePasswordAuthenticationFilter.class
			);
		return http.build();
	}
```

- 세션을 사용하지 않고(`STATELESS`) JWT 기반으로 인증을 수행하며, 커스텀 진입점/접근 거부 핸들러를 통해 일관된 에러 응답을 제공합니다.
- Swagger 관련 경로는 `WebSecurityCustomizer`를 통해 무시(permit) 처리합니다.

---

## 🧪 테스트

- 테스트 의존성:
  - `spring-boot-starter-data-jpa-test`
  - `spring-boot-starter-security-test`
  - `spring-boot-starter-validation-test`
  - `spring-boot-starter-webmvc-test`
  - `com.h2database:h2`

기본적으로 JPA는 H2 인메모리 DB를 사용해 테스트할 수 있도록 구성되어 있으며, 추후 통합 테스트에서 컨트롤러/서비스/레포지토리를 함께 검증하는 구조로 확장할 수 있습니다.

---

## ✅ 정리

- Java 21 & Spring Boot 4 기반의 사용자 관리 서비스로, **record 기반 DTO**, **글로벌 예외 처리**, **JWT 인증**, **Swagger 문서화**가 핵심입니다.
- JPA N+1 문제는 현재 구조상 바로 발생하지 않지만, **Fetch Join / Batch Size / 기본 배치 사이즈 설정**을 통해 확장 시 성능 문제에 대응할 수 있도록 설계 방향을 명확히 했습니다.
- README의 설정 예시와 API 테이블, 아키텍처 다이어그램을 참고해 로컬 환경에서 손쉽게 기동하고 확장할 수 있습니다.


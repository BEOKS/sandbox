# Cloud Infrastructure Sandbox

Spring Boot + Kotlin 기반 클라우드 인프라 학습 및 실험 프로젝트 모음입니다.

## 프로젝트 구조

```
sandbox/
├── auth/                  # ABAC 기반 인증/인가 시스템
├── cloud-platform/        # 멀티테넌트 클라우드 플랫폼
└── db-versioning/         # DB 마이그레이션 도구 비교
```

---

## auth - ABAC 정책 엔진 기반 인증 시스템

Spring Authorization Server 기반의 OAuth2/OIDC 인증 서버와 ABAC(Attribute-Based Access Control) 정책 엔진을 통한 세밀한 권한 제어 시스템입니다.

### 주요 기능
- OAuth2/OIDC 표준 인증
- ABAC 정책 엔진 (SpEL 기반 조건식 평가)
- 클라우드 리소스 관리 (VM, S3, VPC)
- 감사 로그 추적

### 기술 스택
- Kotlin 2.0.21, Spring Boot 3.3.5
- PostgreSQL, Spring Security 6.x, JWT
- Gradle (Kotlin DSL)

### 실행 포트
- 인증 서버: `9000`
- 리소스 API: `8080`

---

## cloud-platform - 멀티테넌트 클라우드 서비스

공공기관용 엄격한 격리가 필요한 멀티테넌트 클라우드 서비스 플랫폼입니다.

### 주요 기능
- **Database per Tenant**: 테넌트별 완전 분리된 데이터베이스
- **네트워크 격리**: 민간/공공 별도 인프라
- **차별화된 인증**: 민간(JWT), 공공(X.509 인증서)
- 클라우드 리소스 관리 (VM, S3, VPC)

### 기술 스택
- Kotlin, Spring Boot 3.2.2
- PostgreSQL 15, Redis
- Docker, Docker Compose

### 실행 포트
- 민간 테넌트 API: `8080`
- 공공 테넌트 API: `9090`
- 관리자 API: `8888`

---

## db-versioning - DB 마이그레이션 도구 비교

Flyway와 Liquibase 두 가지 DB 버전 관리 도구의 사용법을 비교하는 샘플 프로젝트입니다.

### 프로젝트 구성

| 프로젝트 | 도구 | 마이그레이션 형식 |
|---------|------|-----------------|
| `flyway-sample` | Flyway | SQL 파일 (V1__, V2__...) |
| `liquibase-sample` | Liquibase | YAML Changelog |

### 포함된 마이그레이션 예시
1. 사용자 테이블 생성
2. 컬럼 추가 (phone)
3. 주문 테이블 생성
4. 데이터 마이그레이션 (users → members) - Liquibase only

### 기술 스택
- Java, Spring Boot
- H2/PostgreSQL
- Gradle (Kotlin DSL)

---

## 빠른 시작

### 사전 요구사항
- JDK 17+ (auth는 JDK 21+)
- Docker & Docker Compose
- PostgreSQL (또는 Docker로 실행)

### 각 프로젝트 빌드

```bash
# auth
cd auth && ./gradlew build

# cloud-platform
cd cloud-platform && ./gradlew build

# db-versioning
cd db-versioning/flyway-sample && ./gradlew build
cd db-versioning/liquibase-sample && ./gradlew build
```

## 라이선스

MIT License

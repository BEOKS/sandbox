# Cloud Infrastructure Platform

Spring Boot 3.3 + Kotlin 기반의 클라우드 인프라 관리 플랫폼으로, ABAC(Attribute-Based Access Control) 정책 엔진을 통한 세밀한 권한 제어를 제공합니다.

## 주요 기능

- **OAuth2/OIDC 인증**: Spring Authorization Server 기반의 표준 OAuth2 인증
- **ABAC 정책 엔진**: 속성 기반 접근 제어를 통한 세밀한 권한 관리
- **리소스 관리**: VM, S3 버킷, VPC 등 클라우드 리소스 CRUD
- **감사 로그**: 모든 리소스 접근 및 변경 이력 추적
- **정책 조건 평가**: SpEL 기반 복잡한 조건식 평가

## 프로젝트 구조

```
cloud-infrastructure-platform/
├── common/              # 공통 유틸리티, 예외, DTO
├── domain/              # JPA 엔티티, Repository
├── auth-server/         # OAuth2/OIDC 인증 서버 (포트: 9000)
├── policy-engine/       # ABAC 정책 평가 엔진
└── resource-api/        # 리소스 관리 REST API (포트: 8080)
```

## 기술 스택

- **언어**: Kotlin 2.0.21
- **프레임워크**: Spring Boot 3.3.5
- **빌드 도구**: Gradle 8.10.2 (Kotlin DSL)
- **데이터베이스**: PostgreSQL
- **보안**: Spring Security 6.x, OAuth2, JWT
- **문서화**: SpringDoc OpenAPI 3

## 시작하기

### 1. 사전 요구사항

- JDK 21+
- PostgreSQL 15+
- Gradle 8.10.2+ (또는 프로젝트에 포함된 gradlew 사용)

### 2. 데이터베이스 설정

```sql
CREATE DATABASE cloud_auth;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE cloud_auth TO postgres;
```

### 3. 빌드

```bash
./gradlew build
```

### 4. 인증 서버 실행

```bash
./gradlew :auth-server:bootRun
```

인증 서버: http://localhost:9000/auth

### 5. 리소스 API 서버 실행

```bash
./gradlew :resource-api:bootRun
```

리소스 API: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html

## ABAC 정책 예시

### 정책 조건 JSON 형식

```json
{
  "StringEquals": {
    "#subject.attributes['department']": "engineering"
  },
  "StringLike": {
    "#resource.tags['environment']": "prod*"
  }
}
```

### 지원하는 조건 연산자

- `StringEquals`: 문자열 동등 비교
- `StringLike`: 와일드카드(*) 패턴 매칭
- `NumericEquals`: 숫자 동등 비교
- `NumericGreaterThan`: 숫자 크기 비교 (>)
- `NumericLessThan`: 숫자 크기 비교 (<)
- `Bool`: 불린 값 비교
- `IpAddress`: IP 주소 범위 체크

### 정책 평가 컨텍스트

- **Subject (주체)**: 사용자 ID, 역할, 속성 (부서, 직급 등)
- **Resource (리소스)**: 리소스 ID, 타입, 소유자, 태그
- **Action (액션)**: create, read, update, delete, list
- **Environment (환경)**: IP 주소, 시간, 지역 등

## API 사용 예시

### 1. OAuth2 토큰 발급

```bash
curl -X POST http://localhost:9000/auth/oauth2/token \
  -u cloud-client:secret \
  -d "grant_type=client_credentials" \
  -d "scope=resource:read resource:write"
```

### 2. VM 생성

```bash
curl -X POST http://localhost:8080/api/v1/vms \
  -H "Authorization: Bearer {access_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "web-server-01",
    "instanceType": "t3.medium",
    "cpuCores": 2,
    "memoryGb": 4,
    "diskGb": 20,
    "tags": {
      "environment": "production",
      "team": "backend"
    }
  }'
```

### 3. VM 목록 조회

```bash
curl -X GET http://localhost:8080/api/v1/vms/my \
  -H "Authorization: Bearer {access_token}"
```

## 도메인 모델

### 사용자 (User)
- 사용자 기본 정보
- 역할 (Roles)
- 사용자 속성 (UserAttribute): 부서, 직급, 위치 등

### 리소스 (Resource)
- **VirtualMachine**: 가상 머신 (CPU, 메모리, 디스크)
- **S3Bucket**: S3 버킷 (버전 관리, 암호화)
- **VPC**: 가상 프라이빗 클라우드 (CIDR, 서브넷)
- **ResourceTag**: 리소스 태그

### 정책 (Policy)
- 정책 규칙 (PolicyRule)
- 효과 (ALLOW/DENY)
- 액션 패턴
- 리소스 패턴
- 조건식

### 감사 로그 (AuditLog)
- 사용자 액션
- 리소스 접근 이력
- 성공/실패 기록

## 개발 가이드

### 새로운 리소스 타입 추가

1. `domain/resource` 패키지에 엔티티 클래스 생성
2. Repository 인터페이스 생성
3. `resource-api`에 DTO, Service, Controller 추가
4. `AbacAuthorizationManager`에서 리소스 타입 매핑 추가

### 새로운 정책 조건 연산자 추가

`SpelConditionEvaluator`에 새로운 평가 메서드 추가

```kotlin
private fun evaluateCustomCondition(
    value: Any,
    spelContext: StandardEvaluationContext
): Boolean {
    // 구현
}
```

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

# Cloud Platform - Multi-Tenant Cloud Service

엄격한 격리가 필요한 공공기관용 클라우드 서비스 플랫폼입니다. VM, S3, VPC 등의 클라우드 리소스를 민간과 공공기관에 완전히 분리된 환경으로 제공합니다.

## 프로젝트 개요

### 주요 특징

- **완전한 테넌트 격리**: Database per Tenant 패턴으로 데이터베이스 수준에서 완전 분리
- **네트워크 격리**: 민간/공공 별도 애플리케이션 및 네트워크 인프라
- **차별화된 인증**: 민간(JWT), 공공(X.509 인증서)
- **클라우드 리소스 관리**: VM, S3 Storage, VPC 등 핵심 클라우드 서비스 제공
- **멀티 모듈 아키텍처**: Spring Boot + Kotlin 기반 확장 가능한 구조

### 기술 스택

- **언어**: Kotlin
- **프레임워크**: Spring Boot 3.2.2
- **데이터베이스**: PostgreSQL 15
- **캐시**: Redis
- **빌드**: Gradle (Kotlin DSL)
- **인증**: JWT (민간), X.509 Certificate (공공)
- **컨테이너**: Docker, Docker Compose

## 프로젝트 구조

```
cloud-platform/
├── common/                    # 공통 모듈 (멀티테넌시 핵심 로직)
├── infrastructure/            # 인프라 모듈
├── private-tenant-api/        # 민간 테넌트 API (포트: 8080)
├── public-tenant-api/         # 공공 테넌트 API (포트: 9090)
├── admin-api/                 # 관리자 API (포트: 8888)
└── docker/                    # Docker 설정
```

## 시작하기

### 사전 요구사항

- JDK 17 이상
- Docker & Docker Compose
- Gradle 8.5+ (또는 프로젝트 포함된 Gradle Wrapper 사용)

### 로컬 개발 환경 실행

#### 1. 데이터베이스 및 인프라 시작

```bash
cd cloud-platform/docker
docker-compose up -d postgres-private postgres-public redis-private
```

이 명령은 다음을 시작합니다:
- PostgreSQL (민간 테넌트): localhost:5432
- PostgreSQL (공공 테넌트): localhost:5433
- Redis: localhost:6379

#### 2. 애플리케이션 빌드

```bash
cd cloud-platform
./gradlew build
```

#### 3. 애플리케이션 실행

**민간 테넌트 API**
```bash
./gradlew :private-tenant-api:bootRun
```
- URL: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

**공공 테넌트 API**
```bash
./gradlew :public-tenant-api:bootRun
```
- URL: http://localhost:9090
- Swagger UI: http://localhost:9090/swagger-ui.html

**관리자 API**
```bash
./gradlew :admin-api:bootRun
```
- URL: http://localhost:8888
- Swagger UI: http://localhost:8888/swagger-ui.html

### Docker Compose로 전체 스택 실행

```bash
cd cloud-platform/docker
docker-compose up -d
```

모든 서비스가 컨테이너로 실행됩니다.

## API 사용 예시

### 민간 테넌트 (JWT 인증)

#### 1. 로그인 (JWT 토큰 발급)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "password",
    "tenantId": "private_tenant_001"
  }'
```

응답:
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "tenantId": "private_tenant_001",
  "userId": 1
}
```

#### 2. VM 생성

```bash
curl -X POST http://localhost:8080/api/vms \
  -H "Authorization: Bearer {YOUR_JWT_TOKEN}" \
  -H "X-Tenant-ID: private_tenant_001" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "web-server-01",
    "instanceType": "t2.medium",
    "imageId": "ubuntu-22.04",
    "vpcId": 1
  }'
```

#### 3. S3 버킷 생성

```bash
curl -X POST http://localhost:8080/api/s3/buckets \
  -H "Authorization: Bearer {YOUR_JWT_TOKEN}" \
  -H "X-Tenant-ID: private_tenant_001" \
  -H "Content-Type: application/json" \
  -d '{
    "bucketName": "my-app-data",
    "region": "ap-northeast-2",
    "storageClass": "STANDARD"
  }'
```

#### 4. VPC 생성

```bash
curl -X POST http://localhost:8080/api/vpcs \
  -H "Authorization: Bearer {YOUR_JWT_TOKEN}" \
  -H "X-Tenant-ID: private_tenant_001" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "production-vpc",
    "cidrBlock": "10.0.0.0/16",
    "region": "ap-northeast-2"
  }'
```

### 공공 테넌트 (X.509 인증서 인증)

공공 테넌트 API는 클라이언트 인증서를 사용한 mTLS 인증이 필요합니다.

```bash
curl -X GET https://localhost:9090/api/vms \
  --cert client-cert.pem \
  --key client-key.pem \
  --cacert ca-cert.pem \
  -H "X-Tenant-ID: public_tenant_gov_001"
```

### 관리자 API

#### 테넌트 생성

```bash
curl -X POST http://localhost:8888/api/admin/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "tenantId": "private_tenant_003",
    "name": "Startup Company",
    "type": "PRIVATE",
    "databaseUrl": "jdbc:postgresql://localhost:5432/private_tenant_003",
    "databaseUsername": "postgres",
    "databasePassword": "postgres",
    "description": "New startup customer",
    "contactEmail": "admin@startup.com"
  }'
```

#### 테넌트 목록 조회

```bash
curl -X GET http://localhost:8888/api/admin/tenants
```

## 아키텍처

### 멀티테넌시 전략

#### Database per Tenant 패턴

각 테넌트는 완전히 독립된 데이터베이스를 사용합니다:

```
PostgreSQL Instance 1 (Private) - Port 5432
├── tenant_registry (테넌트 메타데이터)
├── private_tenant_001
└── private_tenant_002

PostgreSQL Instance 2 (Public) - Port 5433
├── tenant_registry
├── public_tenant_gov_001
└── public_tenant_gov_002
```

#### 동적 DataSource 라우팅

- `TenantContext`: ThreadLocal로 현재 테넌트 정보 관리
- `TenantDataSourceRouter`: AbstractRoutingDataSource를 확장하여 동적 DB 라우팅
- `TenantInterceptor`: 요청마다 테넌트 식별 및 컨텍스트 설정

### 네트워크 격리

#### 개발 환경
- 별도 Docker 네트워크로 민간/공공 격리
- 포트 분리: 8080 (민간), 9090 (공공)

#### 프로덕션 환경 (권장)
- 별도 VPC 또는 Kubernetes Namespace
- Network Policy를 통한 트래픽 격리
- 별도 로드밸런서 및 도메인

### 인증 시스템

#### 민간 테넌트 (JWT)
1. 사용자 로그인 → JWT 토큰 발급
2. 토큰에 tenantId 포함
3. 모든 요청에 `Authorization: Bearer {token}` 헤더 필요
4. JwtAuthenticationFilter에서 토큰 검증 및 테넌트 컨텍스트 설정

#### 공공 테넌트 (X.509 Certificate)
1. 클라이언트가 mTLS 연결 시 인증서 제시
2. 인증서 검증 (유효기간, 발급기관 등)
3. 인증서에서 Organization 정보 추출 → tenantId 매핑
4. CertificateAuthenticationFilter에서 인증 처리

## 보안 고려사항

### 데이터 격리
- ✅ 테넌트별 완전 분리된 데이터베이스
- ✅ 애플리케이션 레벨 테넌트 검증
- ✅ Cross-tenant 쿼리 방지

### 네트워크 격리
- ✅ 민간/공공 별도 네트워크 구성
- ✅ Docker 네트워크 분리
- ⚠️ 프로덕션: VPC/Firewall 설정 필요

### 인증/인가
- ✅ 민간: JWT with refresh token rotation
- ✅ 공공: X.509 클라이언트 인증서
- ⚠️ 프로덕션: OCSP/CRL 인증서 폐기 검증 구현 필요

### 감사 로그
- ✅ BaseEntity로 생성/수정 시간 및 사용자 추적
- ⚠️ TODO: 상세 감사 로그 시스템 구현

## 환경 변수

### Private Tenant API

| 변수 | 설명 | 기본값 |
|------|------|--------|
| `SERVER_PORT` | 서버 포트 | 8080 |
| `SPRING_DATASOURCE_URL` | Registry DB URL | jdbc:postgresql://localhost:5432/tenant_registry |
| `SPRING_DATASOURCE_USERNAME` | DB 사용자명 | postgres |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호 | postgres |
| `JWT_SECRET` | JWT 서명 키 (256bit+) | (변경 필요) |
| `JWT_EXPIRATION` | JWT 만료 시간 (ms) | 86400000 (24h) |

### Public Tenant API

| 변수 | 설명 | 기본값 |
|------|------|--------|
| `SERVER_PORT` | 서버 포트 | 9090 |
| `SERVER_SSL_ENABLED` | SSL 활성화 | false (개발), true (프로덕션) |
| `SERVER_SSL_CLIENT_AUTH` | 클라이언트 인증 | need |

## 확장 가능성

### 새로운 클라우드 리소스 추가

1. 엔티티 정의: `common` 또는 각 API 모듈의 `domain` 패키지
2. Repository, Service, Controller 구현
3. 테넌트 검증 로직 포함 (`TenantContext` 사용)

### 새로운 테넌트 타입 추가

1. `TenantType` enum에 타입 추가
2. 새로운 API 모듈 생성
3. 인증 전략 구현
4. DataSource 설정

## 트러블슈팅

### 데이터베이스 연결 오류

```
org.postgresql.util.PSQLException: Connection refused
```

**해결책**: PostgreSQL 컨테이너가 실행 중인지 확인
```bash
docker ps | grep postgres
docker-compose -f docker/docker-compose.yml up -d postgres-private postgres-public
```

### 테넌트를 찾을 수 없음

```
TenantNotFoundException: Unable to resolve tenant from request
```

**해결책**:
1. 요청 헤더에 `X-Tenant-ID` 포함 여부 확인
2. JWT 토큰에 tenantId가 포함되어 있는지 확인
3. Admin API로 테넌트가 등록되어 있는지 확인

### JWT 토큰 검증 실패

```
Invalid JWT token
```

**해결책**:
1. 토큰 만료 여부 확인
2. `JWT_SECRET` 환경 변수가 올바른지 확인
3. 토큰 형식: `Bearer {token}` 확인

## 라이선스

This project is proprietary software.

## 연락처

- 프로젝트 관리자: Cloud Platform Team
- 이슈 및 문의: GitHub Issues

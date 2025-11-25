# 정책 예제

## 192.x.x.x 대역 관리자 사용자의 192 VPC 쓰기 권한 정책

### 정책 설명

이 정책은 다음 조건을 모두 만족하는 경우에만 VPC에 대한 쓰기 권한을 허용합니다:

1. **사용자 역할**: 사용자의 역할(roles)에 'admin'이 포함되어 있어야 합니다
2. **IP 주소**: 사용자의 IP 주소가 192.x.x.x 대역이어야 합니다
3. **VPC CIDR**: VPC의 CIDR 블록이 192.x.x.x로 시작해야 합니다

### 정책 조건 JSON

```json
{
  "StringLike": {
    "#subject.roles": "*admin*"
  },
  "IpAddress": {
    "#environment.ipAddress": "192.*.*.*"
  },
  "StringLike": {
    "#resource.attributes['cidrBlock']": "192.*.*.*"
  }
}
```

### 정책 규칙

#### 1. VPC 생성 권한 (create)

- **액션 패턴**: `create`
- **리소스 패턴**: `vpc:*`
- **효과**: `ALLOW`
- **조건**: 위의 조건 JSON 사용

#### 2. VPC 수정 권한 (update)

- **액션 패턴**: `update`
- **리소스 패턴**: `vpc:*`
- **효과**: `ALLOW`
- **조건**: 위의 조건 JSON 사용

#### 3. VPC 삭제 권한 (delete)

- **액션 패턴**: `delete`
- **리소스 패턴**: `vpc:*`
- **효과**: `ALLOW`
- **조건**: 위의 조건 JSON 사용

#### 4. 모든 쓰기 권한 (포괄적)

- **액션 패턴**: `*:write`
- **리소스 패턴**: `vpc:*`
- **효과**: `ALLOW`
- **조건**: 위의 조건 JSON 사용

### 사용 예시

이 정책이 적용되면:

✅ **허용되는 경우**:
- IP 주소가 `192.168.1.100`이고 역할이 `admin`인 사용자가 CIDR이 `192.168.0.0/16`인 VPC를 생성/수정/삭제
- IP 주소가 `192.0.0.1`이고 역할이 `superadmin`인 사용자가 CIDR이 `192.10.0.0/16`인 VPC를 생성/수정/삭제

❌ **거부되는 경우**:
- IP 주소가 `10.0.0.1`인 관리자가 192 VPC에 접근 (IP 주소가 192 대역이 아님)
- IP 주소가 `192.168.1.100`인 일반 사용자(admin 역할 없음)가 192 VPC에 접근
- IP 주소가 `192.168.1.100`이고 역할이 `admin`인 사용자가 CIDR이 `10.0.0.0/16`인 VPC에 접근 (VPC CIDR이 192로 시작하지 않음)

### 주의사항

이 정책이 제대로 작동하려면 `EvaluationContext`의 `resource.attributes`에 VPC의 `cidrBlock` 정보가 포함되어 있어야 합니다. 

리소스 컨텍스트를 생성할 때 VPC 리소스의 경우 다음과 같이 `cidrBlock`을 attributes에 추가해야 합니다:

```kotlin
EvaluationContext.Resource(
    resourceId = vpc.resourceId,
    resourceType = "vpc",
    ownerId = vpc.ownerId,
    attributes = mapOf("cidrBlock" to vpc.cidrBlock)
)
```


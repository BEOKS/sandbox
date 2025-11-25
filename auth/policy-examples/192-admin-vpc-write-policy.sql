-- 192.x.x.x 대역 관리자 사용자의 192 VPC 쓰기 권한 정책
-- 이 SQL 스크립트는 정책과 정책 규칙을 데이터베이스에 삽입합니다.

-- 정책 생성
INSERT INTO policies (name, description, enabled, priority, created_at, updated_at)
VALUES (
    '192-admin-vpc-write-policy',
    '192.x.x.x 대역의 관리자 사용자만 192 VPC에 대해 쓰기 권한을 가집니다',
    true,
    100,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 정책 규칙 1: VPC 생성 권한
INSERT INTO policy_rules (name, effect, description, action_pattern, resource_pattern, condition, policy_id, created_at, updated_at)
SELECT 
    '192-admin-vpc-create-allow',
    'ALLOW',
    '192.x.x.x 대역의 관리자가 192 VPC 생성 권한 허용',
    'create',
    'vpc:*',
    '{
      "StringLike": {
        "#subject.roles": "*admin*"
      },
      "IpAddress": {
        "#environment.ipAddress": "192.*.*.*"
      },
      "StringLike": {
        "#resource.attributes[''cidrBlock'']": "192.*.*.*"
      }
    }',
    id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM policies
WHERE name = '192-admin-vpc-write-policy';

-- 정책 규칙 2: VPC 수정 권한
INSERT INTO policy_rules (name, effect, description, action_pattern, resource_pattern, condition, policy_id, created_at, updated_at)
SELECT 
    '192-admin-vpc-update-allow',
    'ALLOW',
    '192.x.x.x 대역의 관리자가 192 VPC 수정 권한 허용',
    'update',
    'vpc:*',
    '{
      "StringLike": {
        "#subject.roles": "*admin*"
      },
      "IpAddress": {
        "#environment.ipAddress": "192.*.*.*"
      },
      "StringLike": {
        "#resource.attributes[''cidrBlock'']": "192.*.*.*"
      }
    }',
    id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM policies
WHERE name = '192-admin-vpc-write-policy';

-- 정책 규칙 3: VPC 삭제 권한
INSERT INTO policy_rules (name, effect, description, action_pattern, resource_pattern, condition, policy_id, created_at, updated_at)
SELECT 
    '192-admin-vpc-delete-allow',
    'ALLOW',
    '192.x.x.x 대역의 관리자가 192 VPC 삭제 권한 허용',
    'delete',
    'vpc:*',
    '{
      "StringLike": {
        "#subject.roles": "*admin*"
      },
      "IpAddress": {
        "#environment.ipAddress": "192.*.*.*"
      },
      "StringLike": {
        "#resource.attributes[''cidrBlock'']": "192.*.*.*"
      }
    }',
    id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM policies
WHERE name = '192-admin-vpc-write-policy';

-- 정책 규칙 4: 모든 쓰기 권한 (포괄적)
INSERT INTO policy_rules (name, effect, description, action_pattern, resource_pattern, condition, policy_id, created_at, updated_at)
SELECT 
    '192-admin-vpc-write-allow',
    'ALLOW',
    '192.x.x.x 대역의 관리자가 192 VPC에 대해 쓰기 권한 허용',
    '*:write',
    'vpc:*',
    '{
      "StringLike": {
        "#subject.roles": "*admin*"
      },
      "IpAddress": {
        "#environment.ipAddress": "192.*.*.*"
      },
      "StringLike": {
        "#resource.attributes[''cidrBlock'']": "192.*.*.*"
      }
    }',
    id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM policies
WHERE name = '192-admin-vpc-write-policy';


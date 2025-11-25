#!/bin/bash
# 특정 버전 범위의 SQL만 추출하여 DBA에게 전달

set -e

FROM_TAG=${1:-""}
TO_TAG=${2:-""}
DB_TYPE=${3:-"h2"}  # h2, mysql, postgresql, oracle

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
OUTPUT_DIR="$PROJECT_DIR/build/liquibase/releases"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

mkdir -p "$OUTPUT_DIR"

cd "$PROJECT_DIR"

echo "=== DBA용 SQL 패키지 생성 ==="
echo "DB 타입: $DB_TYPE"
echo "From Tag: ${FROM_TAG:-처음부터}"
echo "To Tag: ${TO_TAG:-최신}"
echo ""

RELEASE_DIR="$OUTPUT_DIR/release_$TIMESTAMP"
mkdir -p "$RELEASE_DIR"

# 마이그레이션 SQL
echo "[1/4] 마이그레이션 SQL 생성..."
./gradlew updateSQL \
  -PliquibaseCommandValue="$TO_TAG" \
  -q 2>/dev/null || ./gradlew updateSQL -q

cp "$PROJECT_DIR/build/liquibase/migration.sql" "$RELEASE_DIR/01_migration.sql" 2>/dev/null || true

# 롤백 SQL
echo "[2/4] 롤백 SQL 생성..."
cat > "$RELEASE_DIR/02_rollback.sql" << 'ROLLBACK_HEADER'
-- =============================================
-- ROLLBACK SCRIPT
-- 문제 발생 시 이 스크립트를 순서대로 실행하세요
-- =============================================

ROLLBACK_HEADER

# 체크섬 검증 쿼리
echo "[3/4] 검증 쿼리 생성..."
cat > "$RELEASE_DIR/03_verify.sql" << 'VERIFY_SQL'
-- =============================================
-- 마이그레이션 검증 쿼리
-- 실행 후 결과를 확인하세요
-- =============================================

-- 적용된 changeSet 확인
SELECT id, author, filename, dateexecuted, orderexecuted, exectype
FROM DATABASECHANGELOG
ORDER BY orderexecuted DESC;

-- 테이블 목록 확인 (DB별로 수정 필요)
-- H2/MySQL: SHOW TABLES;
-- PostgreSQL: \dt
-- Oracle: SELECT table_name FROM user_tables;

VERIFY_SQL

# README
echo "[4/4] README 생성..."
cat > "$RELEASE_DIR/README.md" << README_CONTENT
# DB 마이그레이션 패키지

## 생성 일시
$TIMESTAMP

## 파일 설명
| 파일 | 설명 | 실행 순서 |
|------|------|-----------|
| 01_migration.sql | 마이그레이션 DDL/DML | 1 |
| 02_rollback.sql | 롤백 스크립트 | 문제 시 |
| 03_verify.sql | 검증 쿼리 | 2 |

## 실행 절차

### 사전 작업
1. 현재 DB 백업
2. 개발/스테이징 환경에서 테스트 완료 확인

### 실행
\`\`\`sql
-- 1. 마이그레이션 실행
@01_migration.sql

-- 2. 검증
@03_verify.sql

-- 3. 문제 발생 시
@02_rollback.sql
\`\`\`

### 주의사항
- 반드시 백업 후 실행
- 트랜잭션 내에서 실행 권장
- 피크 시간 외 실행

## 담당자
- 개발팀: [담당자명]
- 연락처: [연락처]
README_CONTENT

echo ""
echo "=== 완료 ==="
echo ""
echo "패키지 위치: $RELEASE_DIR"
echo ""
ls -la "$RELEASE_DIR"
echo ""
echo "이 디렉토리를 압축하여 DBA에게 전달하세요:"
echo "  zip -r release_$TIMESTAMP.zip $RELEASE_DIR"

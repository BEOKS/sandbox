#!/bin/bash
# DBA에게 전달할 SQL 스크립트 생성

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
OUTPUT_DIR="$PROJECT_DIR/build/liquibase"

mkdir -p "$OUTPUT_DIR"

cd "$PROJECT_DIR"

echo "=== Liquibase SQL 추출 ==="
echo ""

# 1. 전체 마이그레이션 SQL 생성
echo "[1/3] 전체 마이그레이션 SQL 생성..."
./gradlew updateSQL -q
echo "  -> $OUTPUT_DIR/migration.sql"

# 2. 롤백 SQL 생성 (최근 N개 changeSet)
echo "[2/3] 롤백 SQL 생성..."
./gradlew rollbackCountSQL -PliquibaseCommandValue=5 -q 2>/dev/null || true
echo "  -> $OUTPUT_DIR/rollback.sql"

# 3. 변경 이력 체크
echo "[3/3] 현재 DB 상태 확인..."
./gradlew status -q 2>/dev/null || echo "  (DB 연결 필요)"

echo ""
echo "=== 완료 ==="
echo ""
echo "생성된 파일들:"
ls -la "$OUTPUT_DIR"/*.sql 2>/dev/null || echo "  (SQL 파일 없음)"
echo ""
echo "이 SQL 파일들을 DBA에게 전달하세요."

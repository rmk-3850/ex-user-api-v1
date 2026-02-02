#!/bin/bash
# 1. .env 파일이 있는지 확인 후 로드
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
else
    echo ".env 파일이 없습니다. 확인해 주세요."
    exit 1
fi
docker-compose up -d

# MySQL 준비 대기
until docker exec user-mariadb mariadb -u root -p"$MYSQL_ROOT_PASSWORD" -e "SELECT 1" --silent; do
  echo "User MySQL 준비 중..."
  sleep 2
done
echo "✅ User MySQL 준비 완료"

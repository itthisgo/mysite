#!/bin/bash

APP_JAR=/home/ubuntu/myapp/mysite.jar
LOG_DIR=/home/ubuntu/myapp
LOG_FILE=$LOG_DIR/app.log

echo "[Start] Starting Spring Boot App..."

# 로그 디렉터리 및 파일 준비
touch "$LOG_FILE"
chown ubuntu:ubuntu "$LOG_FILE"
chmod 644 "$LOG_FILE"

# 앱 실행 (백그라운드)
nohup java -jar "$APP_JAR" > "$LOG_FILE" 2>&1 &

echo "[Start] Application started, logs at $LOG_FILE"

#!/bin/bash

echo "[Install] Preparing application directory and permissions..."

APP_DIR=/home/ubuntu/myapp

# 디렉터리 소유권 변경
sudo chown -R ubuntu:ubuntu "$APP_DIR"
chmod -R 755 "$APP_DIR"

# .jar 권한 설정
chmod +x "$APP_DIR"/mysite.jar

echo "[Install] mysite.jar 권한 설정 및 디렉터리 준비 완료"

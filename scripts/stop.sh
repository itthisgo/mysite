#!/bin/bash

echo "[Stop] Stopping Spring Boot App..."

PID=$(pgrep -f 'java -jar /home/ubuntu/myapp/mysite.jar')

if [ -n "$PID" ]; then
  kill "$PID"
  echo "Spring Boot app (PID: $PID) stopped."
else
  echo "No Spring Boot process found."
fi

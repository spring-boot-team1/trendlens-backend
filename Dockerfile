# 1) Build Stage
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# gradle 관련 파일만 먼저 복사 (캐싱 최적화)
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

# Dependencies 먼저 내려서 캐싱 활성화
RUN ./gradlew dependencies --no-daemon || true

# 나머지 소스 복사
COPY src ./src

# Build Jar
RUN ./gradlew bootJar --no-daemon

# 2) Run Stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Build Stage에서 만든 jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

############################
# 1) Build stage (JDK)
############################
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace

# Gradle wrapper & 설정 먼저 복사(캐시 잘 쓰기 위함)
COPY gradlew gradle/ /workspace/
COPY build.gradle* settings.gradle* /workspace/
RUN chmod +x gradlew

# 의존성 워밍업 (빌드캐시)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon help || true

# 나머지 소스 복사 후 빌드
COPY . /workspace
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon clean bootJar -x test

# 산출물 꺼내두기
RUN JAR="$(ls build/libs/*.jar | head -n1)" && \
    install -D "$JAR" /out/app.jar

# ✅ 가드: user 레포의 메인 클래스가 JAR에 반드시 존재해야 함
ARG MAIN_CLASS_PATH=com/tekcit/festival/FestivalServiceApplication.class
RUN jar tf /out/app.jar | grep -q "$MAIN_CLASS_PATH" \
 || (echo "ERROR: missing $MAIN_CLASS_PATH in built JAR; check project structure" >&2; exit 1)


############################
# 2) Runtime stage (JRE)
############################
FROM eclipse-temurin:17-jre-jammy AS runtime

# 비루트 사용자 생성
RUN groupadd -r spring \
 && useradd -r -g spring -d /home/spring -s /usr/sbin/nologin spring \
 && mkdir -p /app /home/spring /etc/keys /etc/firebase \
 && chown -R spring:spring /app /home/spring /etc/keys /etc/firebase

WORKDIR /app

# 빌드 산출물 복사
COPY --from=build --chown=spring:spring /out/app.jar /app/app.jar

USER spring
EXPOSE 8080

# 컨테이너 메모리 친화 옵션
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

# 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
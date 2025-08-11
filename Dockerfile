FROM eclipse-temurin:23-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew bootJar --no-daemon



FROM eclipse-temurin:23-jre

WORKDIR /app

RUN apt-get update && \
    apt-get install -y curl && \
    curl -LO "https://github.com/oras-project/oras/releases/download/v1.2.2/oras_1.2.2_linux_amd64.tar.gz" && \
    mkdir -p oras-install/ && \
    tar -zxf oras_1.2.2_linux_amd64.tar.gz -C oras-install/ && \
    mv oras-install/oras /usr/local/bin/ && \
    rm -rf oras_1.2.2_linux_amd64.tar.gz oras-install/ && \
    apt-get purge -y --auto-remove curl && \
    rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
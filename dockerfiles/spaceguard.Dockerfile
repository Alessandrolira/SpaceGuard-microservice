# =====================================================================
# Dockerfile do microsservico spaceguard (API REST + JWT)
# Build context esperado: RAIZ do repositorio
#   docker build -f dockerfiles/spaceguard.Dockerfile -t spaceguard .
# =====================================================================

# ---- Stage 1: build (compila e empacota o JAR) ----
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY spaceguard/pom.xml .
RUN mvn -B dependency:go-offline
COPY spaceguard/src ./src
RUN mvn -B clean package -DskipTests

# ---- Stage 2: runtime (somente JRE + JAR) ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

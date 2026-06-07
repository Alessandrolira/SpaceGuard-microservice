# =====================================================================
# Dockerfile do microsservico inpe-ingestor (ingestao de focos do INPE)
# Build context esperado: RAIZ do repositorio
#   docker build -f dockerfiles/inpe-ingestor.Dockerfile -t inpe-ingestor .
# =====================================================================

# ---- Stage 1: build (compila e empacota o JAR) ----
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY inpe-ingestor/pom.xml .
RUN mvn -B dependency:go-offline
COPY inpe-ingestor/src ./src
RUN mvn -B clean package -DskipTests

# ---- Stage 2: runtime (somente JRE + JAR) ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]

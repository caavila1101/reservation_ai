# Usa una imagen base con Java 17 o superior
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR generado por Gradle/Maven
COPY build/libs/reservation-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto en el que corre Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

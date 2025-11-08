# Usa una imagen oficial de Java 17
FROM eclipse-temurin:17-jdk-alpine

# Crea el directorio de trabajo
WORKDIR /app

# Copia el jar generado al contenedor
COPY target/checkout-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java","-jar","app.jar"]

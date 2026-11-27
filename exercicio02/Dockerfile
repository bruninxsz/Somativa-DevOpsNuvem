# Etapa 1: compila a aplicacao com Maven + JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
# os testes ja rodam no GitHub Actions; aqui so empacotamos
RUN mvn -B package -DskipTests

# Etapa 2: imagem final, apenas com o JRE e o .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# o Railway define a variavel PORT; a aplicacao le via server.port=${PORT:8080}
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

FROM eclipse-temurin:25-jdk
RUN apt-get update && apt-get install -y maven
COPY ExpressionHandler /ExpressionHandler
RUN cd /ExpressionHandler && mvn install
COPY BodmasExpression/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
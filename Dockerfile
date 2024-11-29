FROM amazoncorretto:17
ARG INSTALL_MAVEN="true"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]



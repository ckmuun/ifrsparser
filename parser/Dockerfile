FROM registry.access.redhat.com/ubi8/openjdk-11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN ls
ENTRYPOINT ["java","-jar","app.jar"]

FROM openjdk:11-jdk-slim
EXPOSE 8761
COPY build/libs/registry-1.0-SNAPSHOT.jar .
ENTRYPOINT ["java","-jar","registry-1.0-SNAPSHOT.jar"]
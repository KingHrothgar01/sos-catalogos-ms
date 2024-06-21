FROM eclipse-temurin:11
RUN mkdir /opt/app
COPY target/*.jar /opt/app/app.jar
CMD ["java", "-Dspring.profiles.active=mysql", "-jar", "/opt/app/app.jar"]

FROM eclipse-temurin:11
RUN mkdir /opt/app
COPY target/sos-catalogos-ms-0.0.1-SNAPSHOT.jar /opt/app
CMD ["java", "-Dspring.profiles.active=mysql", "-jar", "/opt/app/sos-catalogos-ms-0.0.1-SNAPSHOT.jar"]

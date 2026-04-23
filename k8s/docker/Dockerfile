FROM openjdk:17.0.2
COPY ./Netpickz_back-0.0.1-SNAPSHOT.jar /usr/src/myapp/Netpickz_back.jar
#ENTRYPOINT ["java", "-Dspring.profiles.active=${spring_profiles_active}", "-Dapplication.role=${application_role}", "-Dpostgresql.filepath=${postgresql_filepath}", "-jar", "/usr/src/myapp/app.jar"]
ENTRYPOINT ["java", "-jar", "/usr/src/myapp/Netpickz_back.jar"]
EXPOSE 8080
WORKDIR /usr/src/myapp
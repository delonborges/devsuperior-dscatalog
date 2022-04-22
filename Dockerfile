FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD ./backend/target/dscatalog-0.0.1-SNAPSHOT.jar devsuperior-dscatalog.jar
ENTRYPOINT ["java","-jar","/devsuperior-dscatalog.jar"]
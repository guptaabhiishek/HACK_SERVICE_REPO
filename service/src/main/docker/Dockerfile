FROM java:8
VOLUME /tmp
ADD service.war app.jar
RUN ["touch", "/app.jar"]
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
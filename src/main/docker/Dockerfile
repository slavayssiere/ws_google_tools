FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD ws_google_tools-1.0.4.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 5000
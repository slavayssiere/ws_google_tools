FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ARG VERSION
ADD ws_google_tools-${VERSION}.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 5000
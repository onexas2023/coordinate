FROM openjdk:8u342-jre
COPY ./config/boot.yaml ./config/coordinate-userconfig.xml /app/config/
COPY ./target/coordinate-app-0.0.0.XASHUB-DEV-SNAPSHOT.jar /app/
WORKDIR /app
ENV JAVA_OPTS="-XshowSettings:vm"
CMD java $JAVA_OPTS -jar coordinate-app-0.0.0.XASHUB-DEV-SNAPSHOT.jar
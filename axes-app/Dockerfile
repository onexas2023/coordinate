FROM openjdk:8u342-jre
COPY ./config/boot.yaml /app/config/
COPY ./target/axes-app-0.0.0.XASHUB-DEV-SNAPSHOT.jar /app/
WORKDIR /app
ENV JAVA_OPTS="-XshowSettings:vm"
CMD java $JAVA_OPTS -jar axes-app-0.0.0.XASHUB-DEV-SNAPSHOT.jar
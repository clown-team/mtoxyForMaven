FROM mcr.microsoft.com/java/jdk:11-zulu-ubuntu

MAINTAINER smilex<msmliexx1@gmail.com>
WORKDIR /root

ARG VERSION="1.0-SNAPSHOT"

ADD target/mtoxyForMaven-$VERSION.jar app.jar
ADD config.conf config.conf
ADD PrometheusAgent-1.0.jar PrometheusAgent-1.0.jar

RUN bash -c "touch /root/app.jar"
ENTRYPOINT ["java", "-javaagent:PrometheusAgent-1.0.jar", "-XX:+UseShenandoahGC", "-Xms128M", "-Xmx256M", "-Dmonitor.port=6061", "-jar", "app.jar"]
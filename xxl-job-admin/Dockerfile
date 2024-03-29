FROM openjdk:8-jre-slim
MAINTAINER wxy

WORKDIR /xxl-job-admin

#ENV TZ=PRC

ENV JAVA_OPTS="-Xms512m -Xmx512m"

#RUN mkdir -p /docker/docker-compose/xxl-job-admin/logs
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
EXPOSE 9900

ADD target/xxl-job-admin.jar ./app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

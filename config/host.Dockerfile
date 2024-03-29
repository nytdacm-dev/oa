FROM amazoncorretto:17

ENV ENVIRONMENT=production
ENV TZ=Asia/Shanghai
ENV LANG=zh_CN.UTF-8

RUN mkdir /app
RUN mkdir /config

WORKDIR /app

COPY ./oa*.jar /app/oa.jar
COPY ./logback.xml /config/logback.xml

CMD ["java", "-Dlogging.config=/config/logback.xml", "-jar", "oa.jar"]

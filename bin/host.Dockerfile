FROM amazoncorretto:17-alpine3.17

ENV ENVIRONMENT=production

RUN mkdir /app
RUN mkdir /config

WORKDIR /app

COPY ./oa*.jar /app/oa.jar
COPY ./logback.xml /config/logback.xml

CMD ["java", "-Dlogback.configurationFile=/config/logback.xml", "-Dthin.root=/mvn", "-jar", "oa.jar"]

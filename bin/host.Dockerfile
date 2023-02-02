FROM amazoncorretto:17-alpine3.17

ENV ENVIRONMENT=production

RUN mkdir /app

WORKDIR /app

COPY ./oa*.jar /app/oa.jar

CMD ["java", "-Dthin.root=/mvn", "-jar", "oa.jar"]

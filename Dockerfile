FROM amazoncorretto:17-alpine3.17
RUN mkdir /app
WORKDIR /app
COPY build/libs/oa-*.jar /app/oa.jar
EXPOSE 8080
CMD [ "java", "-jar", "oa.jar" ]

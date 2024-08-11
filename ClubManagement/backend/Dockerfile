# build stage
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /backend

COPY . .

RUN mvn clean package -DwarName=ClubManagement

# run stage
FROM tomcat:10.0.27-jre17

COPY --from=build /backend/target/ClubManagement-api.war $CATALINA_HOME/webapps/
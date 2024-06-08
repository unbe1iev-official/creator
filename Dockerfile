FROM openjdk:22-bullseye as builder

RUN apt-get update && apt-get install -y maven

ENV MAVEN_CLI_OPTS '-B -DskipTests -Dmaven.repo.local=/opt/.m2/repository'

COPY --from=common:1.0.0 /opt/.m2/repository /opt/.m2/repository

WORKDIR /tmp/maven

ADD pom.xml /tmp/maven
RUN mvn $MAVEN_CLI_OPTS verify --fail-never

ADD ./src /tmp/maven/src
RUN mvn $MAVEN_CLI_OPTS package

FROM openjdk:22-bullseye

COPY --from=builder /tmp/maven/target/creator-0.0.1-SNAPSHOT.jar /srv/creator.jar

WORKDIR /srv
EXPOSE 8001

ENTRYPOINT ["java", "-jar", "/srv/creator.jar"]

FROM  maven:3.3.3-jdk-8 as builder

WORKDIR /tmp
RUN   mkdir -p $HOME/.m2
RUN echo $HOME

RUN    echo '<settings><mirrors><mirror><mirrorOf>*</mirrorOf><name>remote-repos</name><url>https://{URL}</url><id>remote-repos</id></mirror></mirrors></settings>' > $HOME/.m2/settings.xml

RUN    echo '<settings><mirrors><mirror><mirrorOf>*</mirrorOf><name>remote-repos</name><url>https://{URL}</url><id>remote-repos</id></mirror></mirrors></settings>' > /usr/share/maven/conf/settings.xml
COPY    . .

#RUN     mvn install -Dmaven.test.skip=true
RUN    mvn -q install

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

FROM    openjdk:8-jre-alpine

COPY    --from=builder tmp/target/builder*.jar  app.jar

RUN     mkdir -p /data
ARG     specified_port=8081
ENV     EXPOSED_PORT=$specified_port
EXPOSE  $EXPOSED_PORT

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "-Djava.io.tmpdir=/data/cache/tomcat", "app.jar"]
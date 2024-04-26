#
# BUILD ENVIRONMENT
# -----------------
FROM gradle:8.7.0-jdk17 as GRADLE
ARG APPLICATION_VERSION
ARG APPLICATION_NAME
ARG APPLICATION_PORT
ARG NACOS_IP
ARG NACOS_NAMESPACE
ARG NACOS_GROUP
ARG NACOS_UNAME
ARG NACOS_PASSWD
ARG CHANNEL

LABEL authors="kaiyu.shan@outlook.com"
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY ../build.gradle $APP_HOME
COPY ../settings.gradle $APP_HOME
COPY gradle $APP_HOME/gradle
COPY --chown=gradle:gradle . /home/gradle/src
USER root
RUN chown -R gradle /home/gradle/src
RUN gradle build || return 0
COPY . .
RUN gradle $APPLICATION_NAME:clean
RUN gradle $APPLICATION_NAME:build

#
# IMAGE TARGETS
# -------------
FROM openjdk:21 as JDK
ENV APPLICATION_JAR_FILE_NAME=${APPLICATION_NAME}-$APPLICATION_VERSION.jar
ENV APP_HOME=/usr/app/
COPY --from=GRADLE $APP_HOME/build/libs/$APPLICATION_JAR_FILE_NAME .
EXPOSE $APPLICATION_PORT
ENTRYPOINT ["java","-jar","$APPLICATION_JAR_FILE_NAME","-Dport=$APPLICATION_PORT","-Dnacos_ip=$NACOS_IP","-Dnacos_namespace=$NACOS_NAMESPACE","-Dnacos_username=$NACOS_UNAME","-Dnacos_group=$NACOS_GROUP","-Dnacos_passwd=$NACOS_PASSWD","-Dspring.profiles.active=$CHANNEL"]

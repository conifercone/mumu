#
# BUILD ENVIRONMENT
# -----------------

FROM gradle:8.7.0-jdk17 as gradle
LABEL authors="kaiyu.shan@outlook.com"


FROM openjdk:21 as jdk
#
# IMAGE TARGETS
# -------------
ENTRYPOINT ["java","-jar","/app.jar"]

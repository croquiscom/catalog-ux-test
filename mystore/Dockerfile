FROM adoptopenjdk/openjdk11:jdk-11.0.10_9-alpine-slim AS builder
MAINTAINER lee.sangyop@zigzag.kr

ENV TZ "Asia/Seoul"
ENV PORT $PORT
RUN apk add bash
ADD entrypoint.sh entrypoint.sh
ADD build/libs/datadog.jar datadog.jar
ADD build/libs/catalog-api.jar catalog-api.jar

USER nobody

ARG STAGE
ARG SERVICE_NAME
ARG TAG

RUN echo "$STAGE / $SERVICE_NAME"

ENV SERVICE_NAME $SERVICE_NAME
ENV SPRING_PROFILES_ACTIVE $STAGE
ENV TAG $TAG

EXPOSE $PORT
EXPOSE 8126
ENTRYPOINT ["sh", "entrypoint.sh"]
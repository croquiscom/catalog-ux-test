#!/bin/sh
DATADOG_ENV=zigzag-$SPRING_PROFILES_ACTIVE
SERVICE_NAME=catalog-api

echo $SERVICE_NAME
echo $SPRING_PROFILES_ACTIVE
echo $TAG


JAVA_OPTS="-javaagent:./datadog.jar -Ddd.version=$TAG -Ddd.integrations.enabled=true -Ddd.trace.enabled=true -Ddd.integration.spring-web.enabled=false -Ddd.http.server.route-based-naming=true -Ddd.http.server.tag.query-string=true -Ddd.profiling.enabled=true -Ddd.agent.port=8126 -Ddd.trace.global.tags=env:$DATADOG_ENV,service:$SERVICE_NAME,plugins:false -Ddd.jmxfetch.enabled=true -Ddd.service.name=$SERVICE_NAME -Ddd.service.mapping=mysql:$SERVICE_NAME-mysql,redis:$SERVICE_NAME-redis,java-aws-sdk:$SERVICE_NAME-aws-sdk -Ddd.trace.analytics.enabled=false -server -Xms6g -Xmx6g -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -XX:FlightRecorderOptions=stackdepth=512 -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -XX:ParallelGCThreads=3 -XX:+DisableExplicitGC -XX:+UseStringDeduplication -Dfile.encoding=UTF8 -Dsun.net.inetaddr.ttl=0 -Dtag=$TAG"
java $JAVA_OPTS -jar ./mystore-catalog-api.jar
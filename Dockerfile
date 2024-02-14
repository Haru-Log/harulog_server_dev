FROM gradle:8.5-jdk17

LABEL authors="keon"

WORKDIR /home/gradle/project

COPY . .

RUN echo "systemProp.http.proxyHost=krmp-proxy.9rum.cc\nsystemProp.http.proxyPort=3128\nsystemProp.https.proxyHost=krmp-proxy.9rum.cc\nsystemProp.https.proxyPort=3128" > /root/.gradle/gradle.properties

RUN ./gradlew clean build -x test

CMD ["java", "-jar", "-Dspring.profiles.active=dev", "-Dhttp.proxyHost=krmp-proxy.9rum.cc", "-Dhttp.proxyPort=3128", "-Dhttps.proxyHost=krmp-proxy.9rum.cc", "-Dhttps.proxyPort=3128", "/home/gradle/project/build/libs/harulog-1.0.jar"]
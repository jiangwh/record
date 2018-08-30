# The DockerFile
FROM centos
MAINTAINER jiang_wh@126.com
ADD ./tomcat-1.0.0.jar /app/
ADD ./jdk1.8.0_151.tar /jdk/

# RUN tar -xvf /app/jdk1.8.0_151.tar --directory /app

ENV JAVA_HOME /jdk/jdk1.8.0_151
ENV PATH $JAVA_HOME/bin:$PATH

ENV TZ Asia/Shanghai

EXPOSE 8080

ENTRYPOINT ["java","-jar","-Xmx4g","-Xms4g","/app/tomcat-1.0.0.jar"]

# build the docker image
# docker build -t [myweb] .
# run docker export port 18080 
# docker run --name myweb -p 18080:8080 -d myweb   

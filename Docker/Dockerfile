FROM jenkins/jenkins:latest

USER root

# install jenkins plugins
COPY ./jenkins-plugins /usr/share/jenkins/plugins
RUN while read i ; \ 
	do /usr/local/bin/install-plugins.sh $i ; \
	done < /usr/share/jenkins/plugins

RUN apt-get update \
      && DEBIAN_FRONTEND=noninteractive apt-get install -y \
      sudo \
      build-essential \
      xorg \
      libxrender1 \
      xauth \
      xserver-xorg \
      xserver-xorg-core \
      xvfb

RUN curl -sSL https://get.docker.com/ | sh

ENV JENKINS_USER admin
ENV JENKINS_PASS admin

# allows to skip Jenkins setup wizard
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

# volume for Jenkins settings
VOLUME /var/jenkins_home

COPY config.xml /var/jenkins_home/jobs/pipeline/config.xml


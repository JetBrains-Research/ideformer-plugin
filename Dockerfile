FROM ubuntu:20.04

# Install Java
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    && java -version

# Install Git
RUN apt-get install -y \
    git \
    && git --version

# Install Python3 and pip3
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    python3-setuptools \
    python3-wheel \
    && python3 --version

# Disable gradle daemon
ENV GRADLE_OPTS "-Dorg.gradle.daemon=false"

# Copy the plugin
COPY ide-former-plugin /ide-former-plugin

# prebuild the plugin with gradle
RUN cd /ide-former-plugin && ./gradlew buildPlugin

ENTRYPOINT ["bash", "/ide-former-plugin/runPluginStarter.sh"]

CMD []
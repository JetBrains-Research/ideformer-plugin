FROM ubuntu:20.04

# Install Java
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    && rm -rf /var/lib/apt/lists/* \
    && java -version

# Install Git
RUN apt-get update && apt-get install -y \
    git \
    && rm -rf /var/lib/apt/lists/* \
    && git --version

# Install Python3 and pip3
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    python3-setuptools \
    python3-wheel \
    && rm -rf /var/lib/apt/lists/* \
    && python3 --version

# Install rsync and tmux
RUN apt-get update && apt-get install -y \
    rsync \
    tmux \
    && rm -rf /var/lib/apt/lists/* \
    && rsync --version \
    && tmux -V

# Disable gradle daemon
ENV GRADLE_OPTS "-Dorg.gradle.daemon=false"

# Copy the plugin
COPY ide-former-plugin /ide-former-plugin

EXPOSE 8080

# prebuild the plugin with gradle
RUN cd /ide-former-plugin && ./gradlew buildPlugin

ENTRYPOINT ["bash", "/ide-former-plugin/runPluginStarter.sh"]

CMD []
#!/usr/bin/env bash

# https://stackoverflow.com/a/246128
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
if uname -s | grep -iq cygwin ; then
    DIR=$(cygpath -w "$DIR")
    PWD=$(cygpath -w "$PWD")
fi

# check number of arguments
if [ $# -gt 3 ]; then
    echo "Illegal number of parameters"
    echo "Usage: runPluginStarter.sh <pathToProject> <serverHost> <serverPort>"
    echo "\t<pathToProject> - path to the project to be opened in IDE, default is the plugin itself"
    echo "\t<serverHost> - host of the server to connect to, default is localhost"
    echo "\t<serverPort> - port of the server to connect to, default is 8080"
    echo ""
    echo "Examples:"
    echo "\t./runPluginStarter.sh /project"
    echo "\t./runPluginStarter.sh /project localhost 9080"
    exit 1
fi

# set default values
if [ -z "$1" ]; then
    echo "No path to project specified. Using the plugin itself at $DIR"
    PATH_TO_PROJECT="$DIR"
else
    PATH_TO_PROJECT=$1
fi

if [ -z "$2" ]; then
    echo "No server host specified. Using 127.0.0.1"
    SERVER_HOST="127.0.0.1"
else
    SERVER_HOST=$2
fi

if [ -z "$3" ]; then
    echo "No server port specified. Using 8080"
    SERVER_PORT="8080"
else
    SERVER_PORT=$3
fi

"$DIR/gradlew" -p "$DIR" runIdeAssistantPlugin -Prunner=ide-server -PpathToProject="$PATH_TO_PROJECT" -PserverHost="$SERVER_HOST" -PserverPort="$SERVER_PORT"
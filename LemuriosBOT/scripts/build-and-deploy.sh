#!/bin/bash
show_lemur_ascii_art() {
echo "    ##       ######    ##   ##     ##     ##  #######"
echo "   ##       #         ## # # ##     ##     ##  ##    ##"
echo "  ##       ###       ##   #   ##     ##     ##  ######"
echo " ##       #         ##         ##     ##     ##  ##   ##"
echo "######## ######    ##           ##     #########  ##    ##"
    echo "  Welcome to Lemur's Discord Bot!"
}

show_lemur_ascii_art
CONTAINER_NAME="lemurios-bot"
if [ "$(docker ps -a -q -f name=$CONTAINER_NAME)" ]; then
    echo "Container $CONTAINER_NAME is running. Stopping and removing it..."
    docker stop $CONTAINER_NAME
    docker rm $CONTAINER_NAME
else
    echo "No running container named $CONTAINER_NAME found, creating it from scratch."
fi
cd ..
docker build -f 'docker/Dockerfile' -t $CONTAINER_NAME .
docker run -d --restart always  -p 18081:18081 --name $CONTAINER_NAME $CONTAINER_NAME


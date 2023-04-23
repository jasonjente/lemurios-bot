#!/bin/bash
docker build -t my-discord-bot .

docker run -d my-discord-bot
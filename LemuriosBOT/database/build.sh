#!/usr/bin/bash
docker build -t my-postgres-db .
docker run -p 5432:5432 --name my-postgres-container -d my-postgres-db



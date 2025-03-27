#!/bin/bash

# Function to check if the 'my-postgres-db' container is running
is_postgres_container_running() {
    docker ps -f "name=my-postgres-container" --format "{{.Names}}" | grep -q "my-postgres-container"
}

# Function to display script usage
show_usage() {
    echo "Usage: $0 [--debug] [--help]"
    echo "Options:"
    echo "  --debug  : Enable debugging mode"
    echo "  --help   : Show usage information"
    exit 1
}

# Initialize variables
DEBUG=false

# Parse command line options
while [[ $# -gt 0 ]]; do
    case "$1" in
        --debug)
            DEBUG=true
            shift
            ;;
        --help)
            show_usage
            ;;
        *)
            show_usage
            ;;
    esac
done

# Check if the 'my-postgres-db' container is running
echo "Checking database container..."

if ! is_postgres_container_running; then
    echo "Starting database container..."
    docker start my-postgres-container
fi

# Start Spring Boot application with optional debugging
if [ "$DEBUG" = true ]; then
    nohup mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" > /dev/null &
else
    nohup mvn spring-boot:run > /dev/null &
fi

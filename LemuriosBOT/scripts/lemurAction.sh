#!/bin/bash

################################################################

#######                       ######
#######                       ######
##                              ##  ##
##### ##	## ##  #####  ####  ##      #####  ##  #####  ####
##### ##	## ## ##  ## ##  ## ##  ## ##   ## ## ##  ## ##
##    ##	## ####   ## ##     ##  ## ##   ## ####   ##  ###
## 	  ##	## ##     ## ##  ## ##  ## ##   ## ##     ##    ##
##	  ######## ##     ##  ####  ##  ##  #####  ##     ##  ####

################################################################

# Function to display script usage
show_usage() {
    echo "Usage: $0 [--debug] [--help] [--kill]"
    echo "Options:"
    echo "  --debug  : Enable debugging mode"
    echo "  --help   : Show usage information"
    echo "  --kill   : Stop the Discord bot"
    exit 1
}

# Function to display "Lemur" ASCII art
show_lemur_ascii_art() {
echo "    ##       ######    ##   ##     ##     ##  #######"
echo "   ##       #         ## # # ##     ##     ##  ##    ##"    
echo "  ##       ###       ##   #   ##     ##     ##  ######"
echo " ##       #         ##         ##     ##     ##  ##   ##"
echo "######## ######    ##           ##     #########  ##    ##"
    echo "  Welcome to Lemur's Discord Bot!"
}

# Function to check if the 'my-postgres-db' container is running
is_postgres_container_running() {
	echo "checking if the database container is running"
    docker ps -f "name=docker_db_1" --format "{{.Names}}" | grep -q "docker_db_1"
}


# Call the function to display "Lemur" ASCII art
show_lemur_ascii_art
# Define the name of the Java process
PROCESS_NAME="java"

# Define the path to the PID file
PID_FILE="bot_pid.txt"

# Initialize a variable to hold the option
OPT=""




# Parse command line options
while [[ $# -gt 0 ]]; do
    case "$1" in
        --debug|-d)
            DEBUG=true
            shift
            ;;
        --help|-h)
            show_usage
            ;;
        --kill|-k)  # Allow both --kill and -k as options
            OPT="--kill"
            shift
            ;;
        *)
            show_usage
            ;;
    esac
done

# Check if the script is invoked with the --kill or -k option
if [ "$OPT" = "--kill" ]; then
    # Rest of the kill script remains the same...
    if [ -f "$PID_FILE" ]; then
         # Read the PID from the file
        BOT_PID=$(cat "$PID_FILE")

        # Check if the process is running
        if ps -p $BOT_PID > /dev/null; then
            echo "Stopping Discord bot with PID $BOT_PID..."
            kill $BOT_PID
            rm "$PID_FILE"  # Remove the PID file
            echo "Discord bot stopped."
        else
            echo "Discord bot is not currently running."
        fi
    else
        echo "PID file not found. Discord bot may not be running."
    fi
    exit 0
fi

# Check if the 'my-postgres-db' container is running
echo "Checking database container..."

if ! is_postgres_container_running; then
    echo "Starting database container..."
    docker start docker_db_1
fi

# Start Spring Boot application with optional debugging
if [ "$DEBUG" = true ]; then
    nohup mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" > /dev/null &
else
    nohup mvn spring-boot:run > /dev/null &
fi

# Save the PID of the background process to the PID file
echo $! > "$PID_FILE"

echo "Discord bot started with PID $!."

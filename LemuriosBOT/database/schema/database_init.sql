CREATE DATABASE LemuriosBotDatabase;

-- Create admin user with full permissions
CREATE ROLE LEMURIOS_BOT_ADMIN LOGIN PASSWORD 'LEMURIOS_BOT_ADMIN';
GRANT ALL PRIVILEGES ON DATABASE LemuriosBotDatabase TO LEMURIOS_BOT_ADMIN;

-- Create user with limited permissions
CREATE ROLE LEMURIOS_BOT_USER LOGIN PASSWORD 'LEMURIOS_BOT_USER';


-- Set search_path for admin user
ALTER ROLE LEMURIOS_BOT_ADMIN SET search_path = public;

-- Run schema as admin user
\i /lemurios_schema/lemurios_bot_schema.sql

-- Grant privileges to admin user
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO LEMURIOS_BOT_ADMIN;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO LEMURIOS_BOT_ADMIN;

-- Grant SELECT, INSERT, UPDATE privileges to user on specific tables
GRANT SELECT, INSERT, UPDATE ON history_entry TO LEMURIOS_BOT_USER;
GRANT SELECT, INSERT, UPDATE ON bot_command TO LEMURIOS_BOT_USER;
GRANT SELECT, INSERT, UPDATE ON command_execution TO LEMURIOS_BOT_USER;
GRANT SELECT, INSERT, UPDATE ON discord_server TO LEMURIOS_BOT_USER;
GRANT SELECT, INSERT, UPDATE ON server_user TO LEMURIOS_BOT_USER;

-- Grant USAGE privilege to user on all sequences
GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO LEMURIOS_BOT_USER;
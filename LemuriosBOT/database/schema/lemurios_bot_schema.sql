-- History Entry Entity
CREATE SEQUENCE IF NOT EXISTS entry_gen START WITH 1 INCREMENT BY 50;

CREATE TABLE history_entry
(
    entry_id       BIGINT                      NOT NULL,
    full_tag_name  VARCHAR(255)                NOT NULL,
    command_issued VARCHAR(2048)                 NOT NULL,
    created_on     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_historyentry PRIMARY KEY (entry_id)
);

-- Bot Command Entity
CREATE SEQUENCE IF NOT EXISTS bot_command_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE bot_command
(
    id   BIGINT       NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_botcommand PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS command_execution_seq START WITH 1 INCREMENT BY 50;


-- Command Execution Entity has a *-1 association with bot_command
CREATE TABLE command_execution
(
    id          BIGINT                      NOT NULL,
    executed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    command_id  BIGINT,
    CONSTRAINT pk_commandexecution PRIMARY KEY (id)
);

ALTER TABLE command_execution
    ADD CONSTRAINT FK_COMMANDEXECUTION_ON_COMMA

-- Discord Server Entity
CREATE SEQUENCE IF NOT EXISTS discord_server_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE discord_server
(
    id       BIGINT       NOT NULL,
    guild_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_discordserver PRIMARY KEY (id)
);

-- ServerUser Entity

CREATE SEQUENCE IF NOT EXISTS server_user_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE server_user
(
    server_user_id BIGINT  NOT NULL,
    user_tag       VARCHAR(255),
    server_id      BIGINT,
    points         INTEGER NOT NULL,
    CONSTRAINT pk_server_user PRIMARY KEY (server_user_id)
);

CREATE TABLE server_user_command_executions
(
    server_user_server_user_id BIGINT NOT NULL,
    command_executions_id      BIGINT NOT NULL
);

-- Discord music radio:
CREATE SEQUENCE IF NOT EXISTS discord_server_gen START WITH 1 INCREMENT BY 50;

CREATE TABLE custom_radio_link
(
    id               BIGINT       NOT NULL,
    custom_url       VARCHAR(10000),
    discord_guild_id VARCHAR(255) NOT NULL,
    genre            VARCHAR(255) NOT NULL,
    CONSTRAINT pk_custom_radio_link PRIMARY KEY (id)
);


-- constraints:

ALTER TABLE custom_radio_link
    ADD CONSTRAINT uc_custom_radio_link_discord_guild UNIQUE (discord_guild_id);

ALTER TABLE custom_radio_link
    ADD CONSTRAINT uc_custom_radio_link_genre UNIQUE (genre);

ALTER TABLE server_user_command_executions
    ADD CONSTRAINT uc_server_user_command_executions_commandexecutions UNIQUE (command_executions_id);

ALTER TABLE server_user
    ADD CONSTRAINT FK_SERVER_USER_ON_SERVER FOREIGN KEY (server_id) REFERENCES discord_server (id);

ALTER TABLE server_user_command_executions
    ADD CONSTRAINT fk_serusecomexe_on_command_execution FOREIGN KEY (command_executions_id) REFERENCES command_execution (id);

ALTER TABLE server_user_command_executions
    ADD CONSTRAINT fk_serusecomexe_on_server_user FOREIGN KEY (server_user_server_user_id) REFERENCES server_user (server_user_id);
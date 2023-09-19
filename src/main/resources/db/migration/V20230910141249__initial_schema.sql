CREATE TABLE IF NOT EXISTS organization
(
    organization_id   BIGINT PRIMARY KEY,
    organization_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "user"
(
    user_id  BIGINT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS role
(
    role_id   BIGINT PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TYPE VERB AS ENUM ('get', 'list', 'create', 'update', 'delete');
CREATE TYPE RESOURCE AS ENUM ('*', 'organization_user', 'survey', 'question_group', 'question', 'question_choice', 'survey_question_group', 'question_group_question', 'answer');

CREATE TABLE IF NOT EXISTS permission
(
    permission_id BIGINT PRIMARY KEY,
    resource      RESOURCE NOT NULL,
    verb          VERB     NOT NULL,
    UNIQUE (resource, verb)
);

CREATE TABLE IF NOT EXISTS role_permission
(
    role_id       BIGINT,
    permission_id BIGINT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role (role_id)
        ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission (permission_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS organization_user
(
    organization_id BIGINT,
    user_id         BIGINT,
    role_id         BIGINT,
    PRIMARY KEY (organization_id, user_id),
    FOREIGN KEY (organization_id) REFERENCES organization (organization_id)
        ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES "user" (user_id)
        ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role (role_id)
);

CREATE TABLE IF NOT EXISTS survey
(
    survey_id       BIGINT PRIMARY KEY,
    organization_id BIGINT                   NOT NULL,
    survey_title    VARCHAR(255)             NOT NULL,
    start_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date_time   TIMESTAMP WITH TIME ZONE NOT NULL,
    is_public       BOOLEAN                  NOT NULL,
    FOREIGN KEY (organization_id) REFERENCES organization (organization_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_group
(
    question_group_id    BIGINT PRIMARY KEY,
    organization_id      BIGINT       NOT NULL,
    question_group_title VARCHAR(255) NOT NULL,
    question_group_type  VARCHAR(255) NOT NULL,
    FOREIGN KEY (organization_id) REFERENCES organization (organization_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question
(
    question_id     BIGINT PRIMARY KEY,
    organization_id BIGINT        NOT NULL,
    question_text   VARCHAR(1024) NOT NULL,
    FOREIGN KEY (organization_id) REFERENCES organization (organization_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS descriptive_question
(
    question_id BIGINT PRIMARY KEY,
    FOREIGN KEY (question_id) REFERENCES question (question_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS selective_question
(
    question_id BIGINT PRIMARY KEY,
    max_choices INTEGER NOT NULL,
    FOREIGN KEY (question_id) REFERENCES question (question_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_choice
(
    question_choice_id   BIGINT PRIMARY KEY,
    question_id          BIGINT        NOT NULL,
    question_choice_text VARCHAR(1024) NOT NULL,
    score                SMALLINT      NOT NULL DEFAULT 0,
    allow_free_text      BOOLEAN       NOT NULL DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES selective_question (question_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS survey_question_group
(
    survey_id         BIGINT NOT NULL,
    question_group_id BIGINT NOT NULL,
    PRIMARY KEY (survey_id, question_group_id),
    FOREIGN KEY (survey_id) REFERENCES survey (survey_id)
        ON DELETE CASCADE,
    FOREIGN KEY (question_group_id) REFERENCES question_group (question_group_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_group_question
(
    question_group_id BIGINT  NOT NULL,
    question_id       BIGINT  NOT NULL,
    required          BOOLEAN NOT NULL,
    PRIMARY KEY (question_group_id, question_id),
    FOREIGN KEY (question_group_id) REFERENCES question_group (question_group_id)
        ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question (question_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS answer
(
    answer_id         BIGINT PRIMARY KEY,
    survey_id         BIGINT       NOT NULL,
    question_group_id BIGINT       NOT NULL,
    question_id       BIGINT       NOT NULL,
    respondent_id     VARCHAR(128) NOT NULL,
    FOREIGN KEY (survey_id) REFERENCES survey (survey_id)
        ON DELETE CASCADE,
    FOREIGN KEY (question_group_id) REFERENCES question_group (question_group_id)
        ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question (question_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS descriptive_answer
(
    answer_id   BIGINT PRIMARY KEY,
    answer_text VARCHAR(1024) NOT NULL,
    FOREIGN KEY (answer_id) REFERENCES answer (answer_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chosen_answer
(
    answer_id          BIGINT NOT NULL,
    question_choice_id BIGINT NOT NULL,
    answer_text        VARCHAR(1024),
    PRIMARY KEY (answer_id, question_choice_id),
    FOREIGN KEY (answer_id) REFERENCES answer (answer_id)
        ON DELETE CASCADE,
    FOREIGN KEY (question_choice_id) REFERENCES question_choice (question_choice_id)
        ON DELETE CASCADE
);
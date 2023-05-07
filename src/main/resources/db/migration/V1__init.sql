CREATE TABLE IF NOT EXISTS survey
(
    survey_id       CHAR(13) PRIMARY KEY,
    survey_title    VARCHAR(255)             NOT NULL,
    start_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date_time   TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS question_group
(
    question_group_id    CHAR(13) PRIMARY KEY,
    question_group_title VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS question
(
    question_id   CHAR(13) PRIMARY KEY,
    question_text VARCHAR(1024) NOT NULL
);

CREATE TABLE IF NOT EXISTS descriptive_question
(
    question_id CHAR(13) PRIMARY KEY,
    FOREIGN KEY (question_id) REFERENCES question (question_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS selective_question
(
    question_id CHAR(13) PRIMARY KEY,
    max_choices INTEGER NOT NULL,
    FOREIGN KEY (question_id) REFERENCES question (question_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_choice
(
    question_choice_id   CHAR(13) PRIMARY KEY,
    question_id          CHAR(13)      NOT NULL,
    question_choice_text VARCHAR(1024) NOT NULL,
    allow_free_text      BOOL          NOT NULL DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES selective_question (question_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS survey_question_group
(
    survey_id         CHAR(13) NOT NULL,
    question_group_id CHAR(13) NOT NULL,
    PRIMARY KEY (survey_id, question_group_id),
    FOREIGN KEY (survey_id) REFERENCES survey (survey_id)
        ON DELETE CASCADE,
    FOREIGN KEY (question_group_id) REFERENCES question_group (question_group_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_group_question
(
    question_group_id CHAR(13) NOT NULL,
    question_id       CHAR(13) NOT NULL,
    required          BOOL     NOT NULL,
    PRIMARY KEY (question_group_id, question_id),
    FOREIGN KEY (question_group_id) REFERENCES question_group (question_group_id)
        ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question (question_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS answer
(
    answer_id         CHAR(13) PRIMARY KEY,
    survey_id         CHAR(13)     NOT NULL,
    question_group_id CHAR(13)     NOT NULL,
    question_id       CHAR(13)     NOT NULL,
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
    answer_id   CHAR(13) PRIMARY KEY,
    answer_text VARCHAR(1024) NOT NULL,
    FOREIGN KEY (answer_id) REFERENCES answer (answer_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chosen_answer
(
    answer_id          CHAR(13) NOT NULL,
    question_choice_id CHAR(13) NOT NULL,
    answer_text        VARCHAR(1024),
    PRIMARY KEY (answer_id, question_choice_id),
    FOREIGN KEY (answer_id) REFERENCES answer (answer_id)
        ON DELETE CASCADE,
    FOREIGN KEY (question_choice_id) REFERENCES question_choice (question_choice_id)
        ON DELETE CASCADE
);
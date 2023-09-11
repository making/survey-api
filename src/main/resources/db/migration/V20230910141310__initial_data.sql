INSERT INTO role(role_id, role_name)
VALUES (E'\\x06c7cc4b954e9dd0', 'admin');
INSERT INTO role(role_id, role_name)
VALUES (E'\\x06c7cc4b954e9dd1', 'voter');

INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cc91801e0e20', '*', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cc91801e0e21', '*', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cc91801e0e22', '*', 'create');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cc91801e0e23', '*', 'update');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cc91801e0e24', '*', 'delete');

INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd0', E'\\x06c7cc91801e0e20');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd0', E'\\x06c7cc91801e0e21');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd0', E'\\x06c7cc91801e0e22');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd0', E'\\x06c7cc91801e0e23');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd0', E'\\x06c7cc91801e0e24');

INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbd0', 'survey', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbd1', 'question_group', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbd2', 'question_group', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbd3', 'question', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbd4', 'question', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbd5', 'question_choice', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbd6', 'question_choice', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbd7', 'survey_question_group', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbd8', 'survey_question_group', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbd9', 'question_group_question', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbda', 'question_group_question', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (E'\\x06c7cdd7b5d9fbdb', 'answer', 'create');


INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbd0');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbd1');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbd2');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbd3');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbd4');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbd5');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbd6');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbd7');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbd8');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbd9');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbda');
INSERT INTO role_permission(role_id, permission_id)
VALUES (E'\\x06c7cc4b954e9dd1', E'\\x06c7cdd7b5d9fbdb');
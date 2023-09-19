INSERT INTO role(role_id, role_name)
VALUES (488583709592559056, 'admin');
INSERT INTO role(role_id, role_name)
VALUES (488583709592559057, 'voter');

INSERT INTO permission(permission_id, resource, verb)
VALUES (488584009884765728, '*', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488584009884765729, '*', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488584009884765730, '*', 'create');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488584009884765731, '*', 'update');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488584009884765732, '*', 'delete');

INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559056, 488584009884765728);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559056, 488584009884765729);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559056, 488584009884765730);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559056, 488584009884765731);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559056, 488584009884765732);

INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612752, 'survey', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612753, 'question_group', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612754, 'question_group', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612755, 'question', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612756, 'question', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612757, 'question_choice', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612758, 'question_choice', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612759, 'survey_question_group', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612760, 'survey_question_group', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612761, 'question_group_question', 'get');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612762, 'question_group_question', 'list');
INSERT INTO permission(permission_id, resource, verb)
VALUES (488585410945612763, 'answer', 'create');


INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612752);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612753);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612754);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612755);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612756);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612757);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612758);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612759);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612760);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612761);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612762);
INSERT INTO role_permission(role_id, permission_id)
VALUES (488583709592559057, 488585410945612763);
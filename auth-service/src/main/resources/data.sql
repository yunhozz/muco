INSERT INTO muco.users (created_at, id, updated_at, login_type) VALUES (null, 1, null, 'LOCAL');
INSERT INTO muco.user_role (user_id, roles) VALUES (1, 'USER');
INSERT INTO muco.user_role (user_id, roles) VALUES (1, 'GUEST');
INSERT INTO muco.user_profile (age, created_at, id, nickname, updated_at, user_id, name, email, image_url) VALUES (123, null, 1, 'tester', null, 1, 'yunho', 'qkrdbsgh1121@naver.com', null);
INSERT INTO muco.user_password (retry_count, created_at, id, updated_at, user_id, password) VALUES (0, null, 1, null, 1, '$2a$12$nvohyvmLyWLd33CpoVUAPejYLdVEFZ4haq6sKT/KgmeDbhaPO25ay');
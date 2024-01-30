INSERT INTO category (category_name, created_at, updated_at, active_status) VALUES ('운동', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE');
INSERT INTO category (category_name, created_at, updated_at, active_status) VALUES ('기상', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE');
INSERT INTO category (category_name, created_at, updated_at, active_status) VALUES ('독서', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE');
INSERT INTO category (category_name, created_at, updated_at, active_status) VALUES ('공부', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE');

INSERT INTO users (created_at, updated_at, user_id, email, nickname, password, user_name, active_status, social_type, user_role)
 VALUES('2024-01-01', '2024-01-01', 1, 'test', 'test', '{bcrypt}$2a$10$EKWzqU3GOsgCjOYbKZWYhe4s1.Unz8t6ooyQO6WCP3ksdsn.KLMXe', 'test', 'ACTIVE', 'HARU', 'USER');

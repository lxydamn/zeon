create database db;

use db;

INSERT INTO role (role, role_name, description) VALUES
('ADMIN', '管理员', '系统管理员，拥有所有权限'),
('USER', '普通用户', '普通用户，拥有基本权限');

INSERT INTO users (username, password, role, nickname) VALUES
('admin', 'admin123', 'ADMIN', '系统管理员'),
('user1', 'password1', 'USER', '用户1'),
('user2', 'password2', 'USER', '用户2'),
('user3', 'password3', 'USER', '用户3'),
('user4', 'password4', 'USER', '用户4'),
('user5', 'password5', 'USER', '用户5'),
('user6', 'password6', 'USER', '用户6'),
('user7', 'password7', 'USER', '用户7'),
('user8', 'password8', 'USER', '用户8'),
('user9', 'password9', 'USER', '用户9'),
('user10', 'password10', 'USER', '用户10'),
('user11', 'password11', 'USER', '用户11');


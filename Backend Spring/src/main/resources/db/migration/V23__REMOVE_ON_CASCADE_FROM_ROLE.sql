ALTER TABLE user_roles DROP FOREIGN KEY user_roles_ibfk_2;
-- 2. Добавление нового внешнего ключа без ON DELETE CASCADE
ALTER TABLE user_roles
ADD CONSTRAINT user_roles_ibfk_2
FOREIGN KEY (role_id) REFERENCES role(id);
-- 1. Добавляем новый столбец с нужным типом
ALTER TABLE usere ADD COLUMN number_of_passport_temp VARCHAR(20) NOT NULL;

-- 2. Копируем данные, преобразовав BIGINT в строку
UPDATE usere SET number_of_passport_temp = CAST(number_of_passport AS CHAR(20));

-- 3. Удаляем старый столбец
ALTER TABLE usere DROP COLUMN number_of_passport;

-- 4. Переименовываем новый столбец в старое имя
ALTER TABLE usere CHANGE COLUMN number_of_passport_temp number_of_passport VARCHAR(20) NOT NULL;
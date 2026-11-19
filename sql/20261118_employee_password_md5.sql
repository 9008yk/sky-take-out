-- 将 employee 表中的明文密码一次性转换为 32 位小写 MD5 摘要。
-- 已经是 32 位十六进制摘要的记录会被跳过，因此脚本可以安全地重复执行。
START TRANSACTION;

UPDATE employee
SET password = LOWER(MD5(CONVERT(password USING utf8mb4)))
WHERE password NOT REGEXP '^[0-9a-fA-F]{32}$';

SELECT id,
       username,
       CHAR_LENGTH(password) AS password_length,
       password REGEXP '^[0-9a-f]{32}$' AS is_md5
FROM employee
ORDER BY id;

COMMIT;

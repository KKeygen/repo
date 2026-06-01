-- 最先执行这个，来创建数据库
-- 使用 utf8mb4 字符集（不是 MySQL 的伪 utf8 3 字节实现），并显式指定 utf8mb4_unicode_ci 排序规则
-- 防止中文/Emoji 在不同客户端/连接配置下出现乱码
create database if not exists dismai_base_data character set utf8mb4 collate utf8mb4_unicode_ci;
create database if not exists dismai_customize character set utf8mb4 collate utf8mb4_unicode_ci;
create database if not exists dismai_order_0 character set utf8mb4 collate utf8mb4_unicode_ci;
create database if not exists dismai_order_1 character set utf8mb4 collate utf8mb4_unicode_ci;
create database if not exists dismai_pay_0 character set utf8mb4 collate utf8mb4_unicode_ci;
create database if not exists dismai_pay_1 character set utf8mb4 collate utf8mb4_unicode_ci;
create database if not exists dismai_program_0 character set utf8mb4 collate utf8mb4_unicode_ci;
create database if not exists dismai_program_1 character set utf8mb4 collate utf8mb4_unicode_ci;
create database if not exists dismai_user_0 character set utf8mb4 collate utf8mb4_unicode_ci;
create database if not exists dismai_user_1 character set utf8mb4 collate utf8mb4_unicode_ci;

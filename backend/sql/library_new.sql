CREATE DATABASE IF NOT EXISTS `Library_new`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `Library_new`;

CREATE TABLE IF NOT EXISTS `book_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category_name` VARCHAR(100) NOT NULL,
  `sort` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_category_name` (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `book` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `book_name` VARCHAR(200) NOT NULL,
  `author` VARCHAR(100) NOT NULL,
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `category_id` BIGINT NOT NULL,
  `stock` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_book_category_id` (`category_id`),
  CONSTRAINT `fk_book_category` FOREIGN KEY (`category_id`) REFERENCES `book_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `reader` (`id` BIGINT NOT NULL AUTO_INCREMENT, `reader_no` VARCHAR(50) NOT NULL UNIQUE, `reader_name` VARCHAR(100) NOT NULL, `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS `library_user` (`id` BIGINT NOT NULL AUTO_INCREMENT, `user_name` VARCHAR(50) NOT NULL UNIQUE, `password` VARCHAR(100) NOT NULL, `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT IGNORE INTO `library_user` (`user_name`,`password`) VALUES ('admin','admin123');

CREATE TABLE IF NOT EXISTS `borrow_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `book_id` BIGINT NOT NULL,
  `reader_id` BIGINT NOT NULL,
  `borrow_time` DATETIME NOT NULL,
  `due_time` DATETIME NOT NULL,
  `return_time` DATETIME NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'BORROWED',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_borrow_record_book_id` (`book_id`),
  CONSTRAINT `fk_borrow_record_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`)
  ,CONSTRAINT `fk_borrow_record_reader` FOREIGN KEY (`reader_id`) REFERENCES `reader` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `book_category` (`id`, `category_name`, `sort`) VALUES
  (1, '历史文学', 1), (2, '科幻小说', 2), (3, '推理小说', 3), (4, '言情小说', 4)
ON DUPLICATE KEY UPDATE `sort` = VALUES(`sort`);

INSERT INTO `book` (`book_name`, `author`, `price`, `category_id`, `stock`) VALUES
('史记','司马迁',59.80,1,12), ('资治通鉴','司马光',88.00,1,8), ('人类群星闪耀时','茨威格',42.00,1,15), ('明朝那些事儿','当年明月',39.80,1,10), ('万历十五年','黄仁宇',36.00,1,9), ('国史大纲','钱穆',68.00,1,6), ('苏东坡传','林语堂',45.00,1,11), ('全球通史','斯塔夫里阿诺斯',79.00,1,7), ('中国历代政治得失','钱穆',32.00,1,13), ('叫魂','孔飞力',49.00,1,5),
('三体','刘慈欣',68.00,2,20), ('流浪地球','刘慈欣',35.00,2,14), ('球状闪电','刘慈欣',39.00,2,9), ('基地','阿西莫夫',42.00,2,16), ('沙丘','弗兰克·赫伯特',59.00,2,10), ('银河帝国：机器人五部曲','阿西莫夫',75.00,2,8), ('海伯利安','丹·西蒙斯',55.00,2,7), ('神们自己','阿西莫夫',36.00,2,12), ('北京折叠','郝景芳',29.80,2,15), ('火星救援','安迪·威尔',49.00,2,11),
('无人生还','阿加莎·克里斯蒂',35.00,3,18), ('东方快车谋杀案','阿加莎·克里斯蒂',38.00,3,12), ('尼罗河上的惨案','阿加莎·克里斯蒂',36.00,3,10), ('福尔摩斯探案全集','柯南·道尔',68.00,3,9), ('嫌疑人X的献身','东野圭吾',39.50,3,16), ('白夜行','东野圭吾',49.00,3,13), ('恶意','东野圭吾',35.00,3,8), ('长夜难明','紫金陈',42.00,3,11), ('心理罪','雷米',45.00,3,7), ('模仿犯','宫部美雪',59.00,3,6),
('红楼梦','曹雪芹',59.00,4,13), ('傲慢与偏见','简·奥斯汀',32.00,4,10), ('简·爱','夏洛蒂·勃朗特',39.80,4,12), ('飘','玛格丽特·米切尔',68.00,4,8), ('霍乱时期的爱情','加西亚·马尔克斯',49.00,4,9), ('情书','岩井俊二',35.00,4,14), ('你好，旧时光','八月长安',36.00,4,11), ('致我们终将逝去的青春','辛夷坞',39.80,4,10), ('何以笙箫默','顾漫',29.80,4,15), ('步步惊心','桐华',45.00,4,7);

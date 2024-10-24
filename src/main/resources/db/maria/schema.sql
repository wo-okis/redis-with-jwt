CREATE TABLE IF NOT EXISTS `MEMBER` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(64) UNIQUE NOT NULL,
    `password` varchar(255) NOT NULL,
    `role` varchar(64) NOT NULL,
    `enabled` tinyint(1) DEFAULT 1,
    PRIMARY KEY (`id`)
);
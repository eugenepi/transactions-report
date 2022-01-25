CREATE TABLE IF NOT EXISTS `transactions` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`transaction_id` TEXT NOT NULL COLLATE 'latin1_swedish_ci',
	`currency` CHAR(10) NOT NULL COLLATE 'latin1_swedish_ci',
	`date` TIMESTAMP NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
	`iban` TEXT NOT NULL COLLATE 'latin1_swedish_ci',
	`description` TEXT NOT NULL COLLATE 'latin1_swedish_ci',
	`amount` DECIMAL(20,2) NOT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;

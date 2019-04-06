-- --------------------------------------------------------
-- Servidor:                     localhost
-- Versão do servidor:           5.7.24-log - MySQL Community Server (GPL)
-- OS do Servidor:               Win64
-- HeidiSQL Versão:              9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Copiando estrutura do banco de dados para anjos_crawler
CREATE DATABASE IF NOT EXISTS `anjos_crawler` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `anjos_crawler`;

-- Copiando estrutura para tabela anjos_crawler.category
DROP TABLE IF EXISTS `category`;
CREATE TABLE IF NOT EXISTS `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `image_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKllo73u8jwer0wg1qs3blpxxyw` (`image_id`),
  CONSTRAINT `FKllo73u8jwer0wg1qs3blpxxyw` FOREIGN KEY (`image_id`) REFERENCES `image` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.category: ~0 rows (aproximadamente)
DELETE FROM `category`;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
/*!40000 ALTER TABLE `category` ENABLE KEYS */;

-- Copiando estrutura para tabela anjos_crawler.category_category
DROP TABLE IF EXISTS `category_category`;
CREATE TABLE IF NOT EXISTS `category_category` (
  `category_id` int(11) NOT NULL,
  `children_id` int(11) NOT NULL,
  KEY `FKjtjnxoff00wf3t57nbmvw72s6` (`children_id`),
  KEY `FKd1jj5fnn2w0s8fyu0705r6l4q` (`category_id`),
  CONSTRAINT `FKd1jj5fnn2w0s8fyu0705r6l4q` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `FKjtjnxoff00wf3t57nbmvw72s6` FOREIGN KEY (`children_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.category_category: ~0 rows (aproximadamente)
DELETE FROM `category_category`;
/*!40000 ALTER TABLE `category_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `category_category` ENABLE KEYS */;

-- Copiando estrutura para tabela anjos_crawler.drug
DROP TABLE IF EXISTS `drug`;
CREATE TABLE IF NOT EXISTS `drug` (
  `bula` varchar(255) DEFAULT NULL,
  `how_works` text,
  `indications` text,
  `prescription` bit(1) DEFAULT NULL,
  `type` char(1) DEFAULT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK9ijyesy4731kciunqdl6hyqil` FOREIGN KEY (`id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.drug: ~0 rows (aproximadamente)
DELETE FROM `drug`;
/*!40000 ALTER TABLE `drug` DISABLE KEYS */;
/*!40000 ALTER TABLE `drug` ENABLE KEYS */;

-- Copiando estrutura para tabela anjos_crawler.drug_speciality
DROP TABLE IF EXISTS `drug_speciality`;
CREATE TABLE IF NOT EXISTS `drug_speciality` (
  `drug_id` int(11) NOT NULL,
  `speciality_id` int(11) NOT NULL,
  KEY `FKihg7mq7tj9c2vsbhbjj21dndl` (`speciality_id`),
  KEY `FKgufnt7v9a2gr8y86ds51c84ye` (`drug_id`),
  CONSTRAINT `FKgufnt7v9a2gr8y86ds51c84ye` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`),
  CONSTRAINT `FKihg7mq7tj9c2vsbhbjj21dndl` FOREIGN KEY (`speciality_id`) REFERENCES `specialty` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.drug_speciality: ~0 rows (aproximadamente)
DELETE FROM `drug_speciality`;
/*!40000 ALTER TABLE `drug_speciality` DISABLE KEYS */;
/*!40000 ALTER TABLE `drug_speciality` ENABLE KEYS */;

-- Copiando estrutura para tabela anjos_crawler.drug_substance
DROP TABLE IF EXISTS `drug_substance`;
CREATE TABLE IF NOT EXISTS `drug_substance` (
  `drug_id` int(11) NOT NULL,
  `substance_id` int(11) NOT NULL,
  KEY `FKgsj0p08dw4fx6dy25atp2yb4v` (`substance_id`),
  KEY `FKeldtsp2tx6yisan9klitmf8y4` (`drug_id`),
  CONSTRAINT `FKeldtsp2tx6yisan9klitmf8y4` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`),
  CONSTRAINT `FKgsj0p08dw4fx6dy25atp2yb4v` FOREIGN KEY (`substance_id`) REFERENCES `substance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.drug_substance: ~0 rows (aproximadamente)
DELETE FROM `drug_substance`;
/*!40000 ALTER TABLE `drug_substance` DISABLE KEYS */;
/*!40000 ALTER TABLE `drug_substance` ENABLE KEYS */;

-- Copiando estrutura para tabela anjos_crawler.image
DROP TABLE IF EXISTS `image`;
CREATE TABLE IF NOT EXISTS `image` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data` text,
  `format` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.image: ~0 rows (aproximadamente)
DELETE FROM `image`;
/*!40000 ALTER TABLE `image` DISABLE KEYS */;
/*!40000 ALTER TABLE `image` ENABLE KEYS */;

-- Copiando estrutura para tabela anjos_crawler.presentation
DROP TABLE IF EXISTS `presentation`;
CREATE TABLE IF NOT EXISTS `presentation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(30) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `manual_price` bit(1) DEFAULT NULL,
  `ms` varchar(30) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price_anjos` double DEFAULT NULL,
  `price_max` double DEFAULT NULL,
  `price_pharmacy` double DEFAULT NULL,
  `price_supplier` double DEFAULT NULL,
  `image_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk5y80uub6hn94to4sqkmwut6e` (`image_id`),
  KEY `FK7diu8puys4gc3pusea4d9y1cj` (`product_id`),
  CONSTRAINT `FK7diu8puys4gc3pusea4d9y1cj` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKk5y80uub6hn94to4sqkmwut6e` FOREIGN KEY (`image_id`) REFERENCES `image` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.presentation: ~0 rows (aproximadamente)
DELETE FROM `presentation`;
/*!40000 ALTER TABLE `presentation` DISABLE KEYS */;
/*!40000 ALTER TABLE `presentation` ENABLE KEYS */;

-- Copiando estrutura para tabela anjos_crawler.product
DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
  `product_type` varchar(1) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `featured` bit(1) NOT NULL,
  `featured_priority` int(11) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `image_id` int(11) DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsouy49035ik9r5ojgslbv3i3u` (`image_id`),
  KEY `FK2kxvbr72tmtscjvyp9yqb12by` (`supplier_id`),
  CONSTRAINT `FK2kxvbr72tmtscjvyp9yqb12by` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`),
  CONSTRAINT `FKsouy49035ik9r5ojgslbv3i3u` FOREIGN KEY (`image_id`) REFERENCES `image` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.product: ~0 rows (aproximadamente)
DELETE FROM `product`;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
/*!40000 ALTER TABLE `product` ENABLE KEYS */;

-- Copiando estrutura para tabela anjos_crawler.product_category
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE IF NOT EXISTS `product_category` (
  `product_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  KEY `FKkud35ls1d40wpjb5htpp14q4e` (`category_id`),
  KEY `FK2k3smhbruedlcrvu6clued06x` (`product_id`),
  CONSTRAINT `FK2k3smhbruedlcrvu6clued06x` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKkud35ls1d40wpjb5htpp14q4e` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.product_category: ~0 rows (aproximadamente)
DELETE FROM `product_category`;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;

-- Copiando estrutura para tabela anjos_crawler.specialty
DROP TABLE IF EXISTS `specialty`;
CREATE TABLE IF NOT EXISTS `specialty` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.specialty: ~0 rows (aproximadamente)
DELETE FROM `specialty`;
/*!40000 ALTER TABLE `specialty` DISABLE KEYS */;
/*!40000 ALTER TABLE `specialty` ENABLE KEYS */;

-- Copiando estrutura para tabela anjos_crawler.substance
DROP TABLE IF EXISTS `substance`;
CREATE TABLE IF NOT EXISTS `substance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.substance: ~0 rows (aproximadamente)
DELETE FROM `substance`;
/*!40000 ALTER TABLE `substance` DISABLE KEYS */;
/*!40000 ALTER TABLE `substance` ENABLE KEYS */;

-- Copiando estrutura para tabela anjos_crawler.supplier
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE IF NOT EXISTS `supplier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Copiando dados para a tabela anjos_crawler.supplier: ~0 rows (aproximadamente)
DELETE FROM `supplier`;
/*!40000 ALTER TABLE `supplier` DISABLE KEYS */;
/*!40000 ALTER TABLE `supplier` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

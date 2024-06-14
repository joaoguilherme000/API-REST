create database banco;

use banco;

CREATE TABLE `users` (
  `id` int AUTO_INCREMENT NOT NULL,
  `nome` varchar(40) NOT NULL,
  `email` varchar(40) NOT NULL,
  `senha` varchar(12) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `users` (`id`, `nome`, `email`, `senha`) VALUES
(1, 'admin', 'admin@admin', 'admin');


CREATE TABLE `problemas` (
  `id` int AUTO_INCREMENT NOT NULL,
  `descricao` varchar(100) NOT NULL,
  `foto` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `problemas` (`id`, `descricao`, `foto`) VALUES
(1, 'Acidente de carro', 'https://firebasestorage.googleapis.com/v0/b/mycity-b3f7c.appspot.com/o/images%2Ffoto.jpeg?alt=media&token=23c859fa-798a-456a-9733-83d6c7bcb6c7');


CREATE TABLE `resolvidos` (
  `id` int AUTO_INCREMENT NOT NULL,
  `descricaoResolvida` varchar(100) NOT NULL,
  `fotoResolvida` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `resolvidos` (`id`, `descricaoResolvida`, `fotoResolvida`) VALUES
(1, 'Acidente de carro', 'https://firebasestorage.googleapis.com/v0/b/mycity-b3f7c.appspot.com/o/images%2Ffoto.jpeg?alt=media&token=23c859fa-798a-456a-9733-83d6c7bcb6c7');
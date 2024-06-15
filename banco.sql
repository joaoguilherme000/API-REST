drop database banco;

create database banco;

use banco;

CREATE TABLE `users` (
  `id` int AUTO_INCREMENT NOT NULL,
  `nome` varchar(40) NOT NULL,
  `email` varchar(40) NOT NULL,
  `senha` varchar(12) NOT NULL,
  `fotoPerfil` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `users` (`id`, `nome`, `email`, `senha`, `fotoPerfil`) VALUES
(1, 'admin', 'admin@admin', 'admin', 'https://firebasestorage.googleapis.com/v0/b/mycity-b3f7c.appspot.com/o/images%2FimagemPerfil.png?alt=media&token=a20f53e1-fc9a-4c6b-b74d-d7eb2bae4413');


CREATE TABLE `problemas` (
  `id` int AUTO_INCREMENT NOT NULL,
  `descricao` varchar(100) NOT NULL,
  `foto` VARCHAR(255) NOT NULL,
  `resolveu` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `problemas` (`id`, `descricao`, `foto`, `resolveu`) VALUES
(1, 'Acidente de carro', 'https://firebasestorage.googleapis.com/v0/b/mycity-b3f7c.appspot.com/o/images%2Ffoto.jpeg?alt=media&token=23c859fa-798a-456a-9733-83d6c7bcb6c7', 0),
(2, 'Incêndio em condomínio', 'https://firebasestorage.googleapis.com/v0/b/mycity-b3f7c.appspot.com/o/images%2Fincendio.jpg?alt=media&token=5cf5b9ef-1f63-497b-ae74-e798a76e8fe3', 1);

CREATE TABLE `resolvidos` (
  `id` int AUTO_INCREMENT NOT NULL,
  `problema_id` int NOT NULL,
  `descricaoResolvida` varchar(100) NOT NULL,
  `fotoResolvida` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`problema_id`) REFERENCES `problemas`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `resolvidos` (`problema_id`, `descricaoResolvida`, `fotoResolvida`) VALUES
(2, 'Incêndio apagado', 'https://firebasestorage.googleapis.com/v0/b/mycity-b3f7c.appspot.com/o/images%2Fapagado.jpg?alt=media&token=5b7453d5-20b4-4754-bc99-0504619c6c51');
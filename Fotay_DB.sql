-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Server version: 10.5.12-MariaDB
-- PHP Version: 7.3.32

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- --------------------------------------------------------

--
-- Table structure for table `chat`
--

CREATE TABLE `chat` (
  `id_mensaje` int(3) NOT NULL,
  `usu_id` int(3) NOT NULL,
  `emisor` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `receptor` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `fecha_mensaje` char(50) COLLATE utf8mb4_bin NOT NULL,
  `mensaje` varchar(355) COLLATE utf8mb4_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- --------------------------------------------------------

--
-- Table structure for table `comentarios`
--

CREATE TABLE `comentarios` (
  `coment_id` int(3) NOT NULL,
  `foto_id` int(3) NOT NULL,
  `usu_id` int(3) NOT NULL,
  `usu_nombre` varchar(40) COLLATE utf8mb4_bin NOT NULL,
  `fecha_coment` char(50) COLLATE utf8mb4_bin NOT NULL,
  `txt_coment` varchar(255) COLLATE utf8mb4_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- --------------------------------------------------------

--
-- Table structure for table `fotos`
--

CREATE TABLE `fotos` (
  `foto_id` int(3) NOT NULL,
  `usu_id` int(3) NOT NULL,
  `usu_nombre` varchar(40) NOT NULL,
  `foto_fecha` datetime NOT NULL,
  `foto_coment` varchar(255) DEFAULT NULL,
  `foto_ruta` varchar(255) DEFAULT NULL,
  `total_comentarios` int(3) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- --------------------------------------------------------

--
-- Table structure for table `usuarios`
--

CREATE TABLE `usuarios` (
  `usu_id` int(3) NOT NULL,
  `usu_nombre` varchar(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `usu_contrasena` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `foto_perfil` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `usuarios`
--


--
-- Indexes for dumped tables
--

--
-- Indexes for table `chat`
--
ALTER TABLE `chat`
  ADD PRIMARY KEY (`id_mensaje`),
  ADD KEY `FK_MensajeUsuario` (`usu_id`);

--
-- Indexes for table `comentarios`
--
ALTER TABLE `comentarios`
  ADD PRIMARY KEY (`coment_id`),
  ADD KEY `FK_ComentariosFoto` (`foto_id`),
  ADD KEY `FK_ComentariosUsuario` (`usu_id`);

--
-- Indexes for table `fotos`
--
ALTER TABLE `fotos`
  ADD PRIMARY KEY (`foto_id`),
  ADD KEY `FK_FotosUsuarios` (`usu_id`);

--
-- Indexes for table `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`usu_id`),
  ADD UNIQUE KEY `usu_id` (`usu_id`,`usu_nombre`,`usu_contrasena`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chat`
--
ALTER TABLE `chat`
  MODIFY `id_mensaje` int(3) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=117;

--
-- AUTO_INCREMENT for table `comentarios`
--
ALTER TABLE `comentarios`
  MODIFY `coment_id` int(3) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=150;

--
-- AUTO_INCREMENT for table `fotos`
--
ALTER TABLE `fotos`
  MODIFY `foto_id` int(3) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=237;

--
-- AUTO_INCREMENT for table `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `usu_id` int(3) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chat`
--
ALTER TABLE `chat`
  ADD CONSTRAINT `FK_MensajeUsuario` FOREIGN KEY (`usu_id`) REFERENCES `usuarios` (`usu_id`);

--
-- Constraints for table `comentarios`
--
ALTER TABLE `comentarios`
  ADD CONSTRAINT `FK_ComentariosFoto` FOREIGN KEY (`foto_id`) REFERENCES `fotos` (`foto_id`),
  ADD CONSTRAINT `FK_ComentariosUsuario` FOREIGN KEY (`usu_id`) REFERENCES `usuarios` (`usu_id`);

--
-- Constraints for table `fotos`
--
ALTER TABLE `fotos`
  ADD CONSTRAINT `FK_FotosUsuarios` FOREIGN KEY (`usu_id`) REFERENCES `usuarios` (`usu_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

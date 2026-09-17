-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Servidor: localhost
-- Tiempo de generación: 14-09-2026 a las 07:06:52
-- Versión del servidor: 5.5.24-log
-- Versión de PHP: 5.4.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `dbsistema`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `articulo`
--

CREATE TABLE IF NOT EXISTS `articulo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `categoria_id` int(11) NOT NULL,
  `codigo` varchar(50) COLLATE utf8_spanish_ci DEFAULT NULL,
  `nombre` varchar(100) COLLATE utf8_spanish_ci NOT NULL,
  `precio_venta` decimal(11,2) NOT NULL,
  `stock` int(11) NOT NULL,
  `descripcion` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
  `imagen` varchar(50) COLLATE utf8_spanish_ci DEFAULT NULL,
  `activo` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_UNIQUE` (`nombre`),
  KEY `fk_articulo_categoria_idx` (`categoria_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=12 ;

--
-- Volcado de datos para la tabla `articulo`
--

INSERT INTO `articulo` (`id`, `categoria_id`, `codigo`, `nombre`, `precio_venta`, `stock`, `descripcion`, `imagen`, `activo`) VALUES
(3, 5, '111111', 'Ryzen 9 9950x3d', '7000.00', 5, '24 cores,  4.2 GHz, \n128mb L3 caché', 'ryzen9.jpeg', b'1'),
(4, 5, '222222', 'Ryzen 7 9800X3D', '4500.00', 5, '8 núcleos, 4.7 GHz, 96MB L3 caché, ideal para gaming', 'ryzen79800x3d.jpeg', b'1'),
(5, 5, '333333', 'Ryzen 5 9600X', '3000.00', 5, '6 núcleos, 3.9 GHz, 32MB L3 caché', NULL, b'1'),
(6, 5, '444444', 'Ryzen 9 9900X', '6500.00', 5, '12 núcleos, 4.4 GHz, 64MB L3 caché', NULL, b'1'),
(7, 5, '555555', 'Ryzen 7 7800X3D', '4200.00', 5, '8 núcleos, 4.2 GHz, 96MB L3 caché', NULL, b'1'),
(8, 5, '666666', 'Ryzen 5 7600X', '2400.00', 5, '6 núcleos, 4.7 GHz, 32MB L3 caché', NULL, b'1'),
(9, 5, '777777', 'Ryzen 7 9700X', '3800.00', 5, '8 núcleos, 3.8 GHz, 32MB L3 caché', NULL, b'1'),
(10, 5, '888888', 'Core Ultra 9 285K', '7800.00', 5, '24 núcleos, 3.7 GHz, 36MB Smart Cache', NULL, b'1'),
(11, 5, '999999', 'Core Ultra 7 265K', '5200.00', 5, '20 núcleos, 3.9 GHz, 30MB Smart Cache', NULL, b'0');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria`
--

CREATE TABLE IF NOT EXISTS `categoria` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
  `activo` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_UNIQUE` (`nombre`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=7 ;

--
-- Volcado de datos para la tabla `categoria`
--

INSERT INTO `categoria` (`id`, `nombre`, `descripcion`, `activo`) VALUES
(1, 'Celulares', 'Samsung, Apple, Xiaomi, Honor', b'1'),
(2, 'Discos', 'Solidos de todas las marcas', b'1'),
(3, 'Tablets', 'Apple, Samsung', b'1'),
(4, 'Placas', 'Asus, Gigabyte, AsRock', b'1'),
(5, 'Procesadores', 'AMD, Intel', b'1'),
(6, 'Prueba', 'Prueba', b'1');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_ingreso`
--

CREATE TABLE IF NOT EXISTS `detalle_ingreso` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ingreso_id` int(11) NOT NULL,
  `articulo_id` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio` decimal(11,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_detalle_ingreso_ingreso_idx` (`ingreso_id`),
  KEY `fk_detalle_ingreso_articulo_idx` (`articulo_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=8 ;

--
-- Volcado de datos para la tabla `detalle_ingreso`
--

INSERT INTO `detalle_ingreso` (`id`, `ingreso_id`, `articulo_id`, `cantidad`, `precio`) VALUES
(3, 2, 3, 5, '7000.00'),
(4, 2, 4, 5, '4500.00'),
(5, 3, 3, 5, '7000.00'),
(6, 3, 4, 1, '4500.00'),
(7, 4, 5, 10, '3000.00');

--
-- Disparadores `detalle_ingreso`
--
DROP TRIGGER IF EXISTS `tr_updStockIngreso`;
DELIMITER //
CREATE TRIGGER `tr_updStockIngreso` AFTER INSERT ON `detalle_ingreso`
 FOR EACH ROW BEGIN
UPDATE articulo SET stock = stock + NEW.cantidad
WHERE articulo.id = NEW.articulo_id;
END
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_venta`
--

CREATE TABLE IF NOT EXISTS `detalle_venta` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `venta_id` int(11) NOT NULL,
  `articulo_id` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio` decimal(11,2) NOT NULL,
  `descuento` decimal(11,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_detalle_venta_venta_idx` (`venta_id`),
  KEY `fk_detalle_venta_articulo_idx` (`articulo_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=2 ;

--
-- Volcado de datos para la tabla `detalle_venta`
--

INSERT INTO `detalle_venta` (`id`, `venta_id`, `articulo_id`, `cantidad`, `precio`, `descuento`) VALUES
(1, 1, 10, 2, '7800.00', '600.00');

--
-- Disparadores `detalle_venta`
--
DROP TRIGGER IF EXISTS `tr_updStockVenta`;
DELIMITER //
CREATE TRIGGER `tr_updStockVenta` AFTER INSERT ON `detalle_venta`
 FOR EACH ROW BEGIN
UPDATE articulo SET stock = stock - NEW.cantidad
WHERE articulo.id = NEW.articulo_id;
END
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ingreso`
--

CREATE TABLE IF NOT EXISTS `ingreso` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `persona_id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `tipo_comprobante` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `serie_comprobante` varchar(7) COLLATE utf8_spanish_ci DEFAULT NULL,
  `num_comprobante` varchar(10) COLLATE utf8_spanish_ci NOT NULL,
  `fecha` datetime NOT NULL,
  `impuesto` decimal(4,2) NOT NULL,
  `total` decimal(11,2) NOT NULL,
  `estado` varchar(20) COLLATE utf8_spanish_ci NOT NULL DEFAULT 'Aceptado',
  PRIMARY KEY (`id`),
  KEY `fk_ingreso_persona_idx` (`persona_id`),
  KEY `fk_ingreso_usuario_idx` (`usuario_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=5 ;

--
-- Volcado de datos para la tabla `ingreso`
--

INSERT INTO `ingreso` (`id`, `persona_id`, `usuario_id`, `tipo_comprobante`, `serie_comprobante`, `num_comprobante`, `fecha`, `impuesto`, `total`, `estado`) VALUES
(2, 2, 3, 'FACTURA', '001', '0001', '2026-09-11 17:22:51', '0.18', '57500.00', 'Anulado'),
(3, 2, 3, 'FACTURA', '002', '0002', '2026-09-11 17:31:34', '0.18', '39500.00', 'Anulado'),
(4, 2, 3, 'FACTURA', '003', '0003', '2026-09-12 16:48:53', '0.18', '30000.00', 'Anulado');

--
-- Disparadores `ingreso`
--
DROP TRIGGER IF EXISTS `tr_updStockIngresoAnular`;
DELIMITER //
CREATE TRIGGER `tr_updStockIngresoAnular` AFTER UPDATE ON `ingreso`
 FOR EACH ROW BEGIN
    UPDATE articulo a
        JOIN detalle_ingreso di
        ON di.articulo_id = a.id
        AND di.ingreso_id = new.id
        set a.stock = a.stock - di.cantidad;
end
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona`
--

CREATE TABLE IF NOT EXISTS `persona` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tipo_persona` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `nombre` varchar(70) COLLATE utf8_spanish_ci NOT NULL,
  `tipo_documento` varchar(20) COLLATE utf8_spanish_ci DEFAULT NULL,
  `num_documento` varchar(20) COLLATE utf8_spanish_ci DEFAULT NULL,
  `direccion` varchar(70) COLLATE utf8_spanish_ci DEFAULT NULL,
  `telefono` varchar(15) COLLATE utf8_spanish_ci DEFAULT NULL,
  `email` varchar(50) COLLATE utf8_spanish_ci DEFAULT NULL,
  `activo` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_UNIQUE` (`nombre`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=22 ;

--
-- Volcado de datos para la tabla `persona`
--

INSERT INTO `persona` (`id`, `tipo_persona`, `nombre`, `tipo_documento`, `num_documento`, `direccion`, `telefono`, `email`, `activo`) VALUES
(2, 'Proveedor', 'Grupo Deltron S.A.', 'RUC', '20100642571', 'Av. Manuel Olguín 211, Santiago de Surco, Lima', '014150001', 'ventas@deltron.com.pe', b'1'),
(3, 'Proveedor', 'Impacto Mayorista Tecnológico S.A.C.', 'RUC', '20601426462', 'Av. Garcilaso de la Vega 1348, Cercado de Lima', '014243000', 'informes@impacto.com.pe', b'1'),
(4, 'Proveedor', 'Sercoplus S.A.C.', 'RUC', '20511216102', 'C.C. Cyberplaza, Av. Garcilaso de la Vega 1348, Lima', '014238511', 'ventas@sercoplus.com', b'1'),
(5, 'Proveedor', 'Memory Kings Peru S.A.C.', 'RUC', '20505581190', 'Av. Inca Garcilaso de la Vega 1250, Cercado de Lima', '016196201', 'corporativo@memorykings.com.pe', b'1'),
(6, 'Proveedor', 'Inversiones Hiraoka S.A.C.', 'RUC', '20100028211', 'Av. Abancay 483, Cercado de Lima', '013110200', 'servicioalcliente@hiraoka.com.pe', b'1'),
(7, 'Proveedor', 'Mesajil Hermanos S.A.C.', 'RUC', '20303867699', 'Av. Arenales 661, Jesús María, Lima', '015006600', 'ventas@mesajilhermanos.com', b'1'),
(8, 'Proveedor', 'Compu & Vision Peru S.A.C.', 'RUC', '20516597929', 'Av. Garcilaso de la Vega 1358, Tienda 124, Lima', '014241042', 'ventas@compu-vision.com.pe', b'1'),
(9, 'Proveedor', 'CyC Computer S.A.C.', 'RUC', '20510528470', 'Av. Garcilaso de la Vega 1250, Tienda 115, Lima', '014332306', 'ventas@cyccomputer.pe', b'1'),
(10, 'Proveedor', 'VIPASA S.A.C.', 'RUC', '20502128753', 'Jr. Camaná 1140, Cercado de Lima', '014283131', 'mayoristas@vipasa.com.pe', b'0'),
(11, 'Proveedor', 'Intcomex Peru S.A.C.', 'RUC', '20381615531', 'Av. Materiales 3045, Lima', '017165100', 'ventas.pe@intcomex.com', b'1'),
(12, 'Cliente', 'Mateo Levi Cohen', 'DNI', '45896321', 'Av. Larco 456, Miraflores, Lima', '987654321', 'mateo.levi@gmail.com', b'1'),
(13, 'Cliente', 'Hannah Mizrahi Kaplan', 'DNI', '71236548', 'Jr. Junín 782, Cercado de Lima', '951478236', 'hannah.mizrahi@outlook.com', b'1'),
(14, 'Cliente', 'Samuel David Gabbay', 'RUC', '10423658971', 'Av. La Marina 2310, San Miguel, Lima', '963258147', 'samuel.gabbay@hotmail.com', b'1'),
(15, 'Cliente', 'Daniela Sarai Elbaz', 'DNI', '48569321', 'Calle Los Tulipanes 124, Lince, Lima', '984123567', 'daniela.elbaz@gmail.com', b'1'),
(16, 'Cliente', 'Ezequiel Aarón Dayan', 'DNI', '60215487', 'Av. Arequipa 3450, San Isidro, Lima', '972364158', 'ezequiel.dayan@yahoo.com', b'1'),
(17, 'Cliente', 'Leah Rebeca Hadad', 'DNI', '74125896', 'Urb. Santa Patricia Mz. F Lote 12, La Molina', '910235489', 'leah.hadad@gmail.com', b'1'),
(18, 'Cliente', 'Jonathan Ismael Peretz', 'RUC', '10715482369', 'Av. Javier Prado Este 1120, San Borja, Lima', '965874123', 'j.peretz@outlook.com', b'1'),
(19, 'Cliente', 'Miriam Elizabeth Stern', 'DNI', '09865321', 'Jr. Carabaya 544, Cercado de Lima', '932145698', 'miriam.stern@gmail.com', b'1'),
(20, 'Cliente', 'Caleb Emmanuel Malka', 'DNI', '42365987', 'Av. Universitaria 4500, Los Olivos, Lima', '954781236', 'caleb.malka@hotmail.com', b'1'),
(21, 'Cliente', 'Ruth Noemí Pinto', 'DNI', '46985214', 'Av. Brasil 1820, Jesús María, Lima', '986523147', 'ruth.pinto@gmail.com', b'1');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

CREATE TABLE IF NOT EXISTS `rol` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_UNIQUE` (`nombre`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=4 ;

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`id`, `nombre`, `descripcion`) VALUES
(1, 'Administrador', 'Acceso total'),
(2, 'Vendedor', 'Acceso parcial'),
(3, 'Almacenero', 'Acceso parcial');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE IF NOT EXISTS `usuario` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rol_id` int(11) NOT NULL,
  `nombre` varchar(70) COLLATE utf8_spanish_ci NOT NULL,
  `tipo_documento` varchar(20) COLLATE utf8_spanish_ci DEFAULT NULL,
  `num_documento` varchar(20) COLLATE utf8_spanish_ci DEFAULT NULL,
  `direccion` varchar(70) COLLATE utf8_spanish_ci DEFAULT NULL,
  `telefono` varchar(15) COLLATE utf8_spanish_ci DEFAULT NULL,
  `email` varchar(50) COLLATE utf8_spanish_ci NOT NULL,
  `clave` varchar(128) COLLATE utf8_spanish_ci NOT NULL,
  `activo` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_UNIQUE` (`nombre`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `fk_usuario_rol_idx` (`rol_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=6 ;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id`, `rol_id`, `nombre`, `tipo_documento`, `num_documento`, `direccion`, `telefono`, `email`, `clave`, `activo`) VALUES
(3, 1, 'Jholby Segura', 'DNI', '72736641', 'Lima', '972017585', 'admin@hardtech.pe', '1246bf6cd9311724ead805d20354b354331df540e6e4b7d8d0017ac2ccbadde1', b'1'),
(4, 3, 'Janah Levy', 'DNI', '78716385', 'Lince', '924577885', 'almacen@hardtech.pe', 'a9230eaf4fa0a9ec85b4ac6dcf7e7d1dfc26ddc237d42a9aea33bb5a39e25238', b'1'),
(5, 2, 'Akiva Pinto', 'DNI', '73748512', 'Pueblo Libre', '', 'ventas@hardtech.pe', '48eb38b6b568efa4c5b35392967bd5ac38fde8f9f4a0fdc1350c6bf8cfd75b0e', b'1');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `venta`
--

CREATE TABLE IF NOT EXISTS `venta` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `persona_id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `tipo_comprobante` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `serie_comprobante` varchar(7) COLLATE utf8_spanish_ci DEFAULT NULL,
  `num_comprobante` varchar(10) COLLATE utf8_spanish_ci NOT NULL,
  `fecha` datetime NOT NULL,
  `impuesto` decimal(4,2) NOT NULL,
  `total` decimal(11,2) NOT NULL,
  `estado` varchar(20) COLLATE utf8_spanish_ci NOT NULL DEFAULT 'Aceptado',
  PRIMARY KEY (`id`),
  KEY `fk_ingreso_persona_idx` (`persona_id`),
  KEY `fk_venta_usuario_idx` (`usuario_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=2 ;

--
-- Volcado de datos para la tabla `venta`
--

INSERT INTO `venta` (`id`, `persona_id`, `usuario_id`, `tipo_comprobante`, `serie_comprobante`, `num_comprobante`, `fecha`, `impuesto`, `total`, `estado`) VALUES
(1, 12, 3, 'FACTURA', '0001', '0001', '2026-09-14 01:55:39', '0.18', '15000.00', 'Anulado');

--
-- Disparadores `venta`
--
DROP TRIGGER IF EXISTS `tr_updStockVentaAnular`;
DELIMITER //
CREATE TRIGGER `tr_updStockVentaAnular` AFTER UPDATE ON `venta`
 FOR EACH ROW BEGIN
    UPDATE articulo a
    JOIN detalle_venta dv
    ON dv.articulo_id = a.id
    AND dv.venta_id = new.id
    set a.stock = a.stock + dv.cantidad;
end
//
DELIMITER ;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `articulo`
--
ALTER TABLE `articulo`
  ADD CONSTRAINT `fk_articulo_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categoria` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Filtros para la tabla `detalle_ingreso`
--
ALTER TABLE `detalle_ingreso`
  ADD CONSTRAINT `fk_detalle_ingreso_articulo` FOREIGN KEY (`articulo_id`) REFERENCES `articulo` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_detalle_ingreso_ingreso` FOREIGN KEY (`ingreso_id`) REFERENCES `ingreso` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `detalle_venta`
--
ALTER TABLE `detalle_venta`
  ADD CONSTRAINT `fk_detalle_venta_articulo` FOREIGN KEY (`articulo_id`) REFERENCES `articulo` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_detalle_venta_venta` FOREIGN KEY (`venta_id`) REFERENCES `venta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `ingreso`
--
ALTER TABLE `ingreso`
  ADD CONSTRAINT `fk_ingreso_persona` FOREIGN KEY (`persona_id`) REFERENCES `persona` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_ingreso_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`rol_id`) REFERENCES `rol` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Filtros para la tabla `venta`
--
ALTER TABLE `venta`
  ADD CONSTRAINT `fk_venta_persona` FOREIGN KEY (`persona_id`) REFERENCES `persona` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_venta_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

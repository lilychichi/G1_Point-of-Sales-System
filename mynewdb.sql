-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 06, 2025 at 01:00 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `mydb`
--

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `idCategory` int(10) UNSIGNED NOT NULL,
  `category_name` text NOT NULL COMMENT 'cables, chargers, keyboards, mice, etc.'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`idCategory`, `category_name`) VALUES
(1, 'Cables'),
(2, 'Computer Peripherals'),
(3, 'Audio Devices'),
(4, 'Displays/Monitor'),
(6, 'test');

-- --------------------------------------------------------

--
-- Table structure for table `order`
--

CREATE TABLE `order` (
  `idOrder` int(10) UNSIGNED NOT NULL,
  `orderDate` varchar(45) NOT NULL COMMENT 'timestamp of the sale',
  `totalAmount` float NOT NULL COMMENT 'computed total (from orderdetails)',
  `discountAmount` decimal(10,2) NOT NULL DEFAULT 0.00,
  `vatAmount` decimal(10,2) NOT NULL DEFAULT 0.00,
  `taxAmount` decimal(10,2) NOT NULL DEFAULT 0.00,
  `user_idUser` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `order`
--

INSERT INTO `order` (`idOrder`, `orderDate`, `totalAmount`, `discountAmount`, `vatAmount`, `taxAmount`, `user_idUser`) VALUES
(1, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(2, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(3, '11/05/2025', 11400, 0.00, 0.00, 0.00, 0),
(4, '11/05/2025', 11400, 0.00, 0.00, 0.00, 0),
(5, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(6, '11/05/2025', 11286, 0.00, 0.00, 0.00, 0),
(7, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(8, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(9, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(10, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(11, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(12, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(13, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(14, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(15, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(16, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(19, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(20, '11/05/2025', 0, 0.00, 0.00, 0.00, 0),
(21, '11/11/2025', 0, 0.00, 0.00, 0.00, 0),
(22, '11/12/2025', 0, 0.00, 0.00, 0.00, 0),
(23, '11/12/2025', 0, 0.00, 0.00, 0.00, 0),
(24, '11/18/2025', 0, 0.00, 0.00, 0.00, 0),
(25, '11/18/2025', 0, 0.00, 0.00, 0.00, 0),
(26, '11/18/2025', 0, 0.00, 0.00, 0.00, 0),
(27, '11/18/2025', 0, 0.00, 0.00, 0.00, 0),
(28, '11/18/2025', 0, 0.00, 0.00, 0.00, 0),
(29, '11/18/2025', 0, 0.00, 0.00, 0.00, 0),
(30, '11/18/2025', 0, 0.00, 0.00, 0.00, 0),
(31, '11/26/2025', 0, 0.00, 0.00, 0.00, 0),
(32, '11/26/2025', 0, 0.00, 0.00, 0.00, 0),
(33, '11/26/2025', 0, 0.00, 0.00, 0.00, 0),
(34, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(35, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(36, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(37, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(38, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(39, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(40, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(41, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(42, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(43, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(44, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(45, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(46, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(47, '11/29/2025', 0, 0.00, 0.00, 0.00, 0),
(48, '11/29/2025', 22374, 0.00, 0.00, 0.00, 0),
(49, '11/29/2025', 7.84, 0.00, 0.00, 0.00, 0),
(50, '11/29/2025', 15.68, 0.00, 0.00, 0.00, 0),
(51, '11/29/2025', 926.284, 0.00, 0.00, 0.00, 0),
(52, '11/29/2025', 3356.1, 0.00, 0.00, 0.00, 0),
(54, '11/29/2025', 7.8309, 0.00, 0.00, 0.00, 0),
(55, '12/02/2025', 17.7184, 0.00, 0.00, 0.00, 0),
(56, '12/03/2025', 11.2, 0.00, 0.00, 0.00, 0),
(57, '12/06/2025', 0, 0.00, 0.00, 0.00, 0),
(58, '12/06/2025', 0, 0.00, 0.00, 0.00, 0),
(59, '12/06/2025', 0, 0.00, 0.00, 0.00, 0),
(60, '12/06/2025', 61.6, 0.00, 0.00, 0.00, 0),
(61, '12/06/2025', 123.2, 0.00, 0.00, 0.00, 0),
(62, '12/06/2025', 61.6, 0.00, 0.00, 0.00, 0),
(63, '12/06/2025', 61.6, 0.00, 0.00, 0.00, 0),
(64, '12/06/2025', 492.8, 0.00, 0.00, 0.00, 0),
(65, '12/06/2025', 0, 0.00, 0.00, 0.00, 1),
(66, '12/06/2025', 0, 0.00, 0.00, 0.00, 1),
(67, '12/06/2025', 0, 0.00, 0.00, 0.00, 1),
(68, '12/06/2025', 0, 0.00, 0.00, 0.00, 1),
(69, '12/06/2025', 0, 0.00, 0.00, 0.00, 1),
(70, '12/06/2025', 0, 0.00, 0.00, 0.00, 1),
(71, '12/06/2025', 0, 0.00, 0.00, 0.00, 1),
(72, '12/06/2025', 0, 0.00, 0.00, 0.00, 1),
(73, '12/06/2025', 137.76, 0.00, 0.00, 0.00, 1),
(74, '12/06/2025', 137.76, 0.00, 0.00, 0.00, 1),
(75, '12/06/2025', 137.76, 0.00, 0.00, 0.00, 1),
(76, '12/06/2025', 137.76, 0.00, 0.00, 0.00, 1);

-- --------------------------------------------------------

--
-- Table structure for table `orderdetails`
--

CREATE TABLE `orderdetails` (
  `idOrderDetails` int(10) UNSIGNED NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` float NOT NULL COMMENT 'unit price(from product)',
  `subtotal` int(11) NOT NULL COMMENT 'quantity x price',
  `discount` decimal(10,2) NOT NULL DEFAULT 0.00,
  `vat` decimal(10,2) NOT NULL DEFAULT 0.00,
  `tax` decimal(10,2) NOT NULL DEFAULT 0.00,
  `warranty` varchar(45) DEFAULT NULL,
  `order_idOrder` int(10) UNSIGNED NOT NULL,
  `order_user_idUser` int(10) UNSIGNED NOT NULL,
  `product_idProduct` int(10) UNSIGNED NOT NULL,
  `product_category_idCategory` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `orderdetails`
--

INSERT INTO `orderdetails` (`idOrderDetails`, `quantity`, `price`, `subtotal`, `discount`, `vat`, `tax`, `warranty`, `order_idOrder`, `order_user_idUser`, `product_idProduct`, `product_category_idCategory`) VALUES
(1, 1, 2699.45, 2699, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(2, 1, 2699.45, 2699, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(3, 1, 2300, 2300, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(4, 1, 2299.9, 2300, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(5, 1, 2299.9, 2300, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(6, 1, 1000, 1000, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(7, 1, 1000, 1000, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(8, 1, 2299.9, 2300, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(9, 1, 1000, 1000, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(10, 3, 2000, 6000, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(11, 1, 2000, 2000, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(12, 1, 545, 545, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(13, 2, 434, 868, 0.00, 0.00, 0.00, NULL, 0, 0, 0, 0),
(14, 1, 2000, 1980, 20.00, 237.60, 39.60, NULL, 0, 0, 0, 0),
(15, 1, 2000, 1980, 20.00, 237.60, 39.60, NULL, 0, 0, 0, 0),
(16, 1, 2000, 2000, 0.00, 240.00, 40.00, NULL, 0, 0, 0, 0),
(17, 1, 2000, 2000, 0.00, 240.00, 40.00, NULL, 0, 0, 0, 0),
(18, 1, 2000, 2000, 0.00, 240.00, 40.00, NULL, 0, 0, 0, 0),
(19, 1, 2000, 1980, 20.00, 237.60, 39.60, NULL, 0, 0, 0, 0),
(20, 1, 10000, 9900, 100.00, 1188.00, 198.00, NULL, 0, 0, 0, 0),
(21, 1, 10000, 9900, 100.00, 1188.00, 198.00, NULL, 0, 0, 0, 0),
(22, 1, 3000, 2970, 30.00, 356.40, 59.40, NULL, 0, 0, 0, 0),
(23, 2, 800, 1584, 16.00, 190.08, 31.68, NULL, 0, 0, 0, 0),
(24, 3, 800, 2376, 24.00, 285.12, 47.52, NULL, 0, 0, 0, 0),
(25, 1, 800, 800, 0.00, 96.00, 16.00, NULL, 0, 0, 0, 0),
(26, 1, 10000, 10000, 0.00, 1200.00, 200.00, NULL, 0, 0, 0, 0),
(27, 1, 10000, 10000, 0.00, 1200.00, 200.00, NULL, 0, 0, 0, 0),
(28, 1, 10000, 10000, 0.00, 1200.00, 200.00, NULL, 0, 0, 0, 0),
(29, 1, 10000, 9900, 100.00, 1188.00, 198.00, NULL, 0, 0, 0, 0),
(30, 1, 8, 8, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(31, 1, 7, 7, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(32, 1, 7, 7, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(33, 2, 10000, 20000, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(34, 1, 7, 7, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(35, 2, 7, 14, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(36, 4, 7, 28, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(37, 1, 800, 800, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(38, 1, 3000, 3000, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(39, 1, 7, 7, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(40, 2, 8, 16, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(41, 1, 10, 10, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(42, 1, 55, 55, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(43, 8, 55, 440, 0.00, 0.00, 0.00, '0.0', 0, 0, 0, 0),
(44, 1, 123, 123, 0.00, 0.00, 0.00, NULL, 73, 1, 13, 1),
(45, 1, 123, 123, 0.00, 0.00, 0.00, NULL, 74, 1, 13, 1),
(46, 1, 123, 123, 0.00, 0.00, 0.00, NULL, 75, 1, 13, 1),
(47, 1, 123, 123, 0.00, 0.00, 0.00, NULL, 76, 1, 13, 1);

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `idPayment` int(10) UNSIGNED NOT NULL,
  `payment_type` varchar(45) NOT NULL,
  `payment_Date` varchar(45) NOT NULL,
  `amountPaid` float NOT NULL COMMENT 'amount received',
  `change` float DEFAULT NULL COMMENT 'received amount',
  `order_idOrder` int(10) UNSIGNED NOT NULL,
  `order_user_idUser` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `payment`
--

INSERT INTO `payment` (`idPayment`, `payment_type`, `payment_Date`, `amountPaid`, `change`, `order_idOrder`, `order_user_idUser`) VALUES
(1, 'Cash', '10/26/2025 12:40:00', 2699.45, 0, 0, 0),
(2, 'Cash', '10/26/2025 13:16:36', 2699.45, 0, 0, 0),
(3, 'Cash', '10/26/2025 13:39:56', 3000, 700, 0, 0),
(4, 'Cash', '10/27/2025 22:05:34', 3000, 700.1, 0, 0),
(5, 'Cash', '10/27/2025 22:07:19', 3299.9, 0, 0, 0),
(6, 'Cash', '10/28/2025 16:44:31', 4000, 700.1, 0, 0),
(7, 'Cash', '10/29/2025 09:40:40', 2000, 1000, 0, 0),
(8, 'Cash', '10/29/2025 13:52:21', 7000, 1000, 0, 0),
(9, 'Cash', '10/29/2025 13:55:24', 3500, 87, 0, 0),
(15, 'Cash', '11/02/2025 16:33:10', 2280, 22.8, 0, 0),
(16, 'Cash', '11/02/2025 16:39:46', 2280, 22.8, 0, 0),
(17, 'Cash', '11/02/2025 17:07:42', 2280, 0, 0, 0),
(18, 'Cash', '11/02/2025 17:08:46', 2280, 0, 0, 0),
(19, 'Cash', '11/02/2025 17:15:00', 2280, 0, 0, 0),
(20, 'Cash', '11/02/2025 17:19:19', 2280, 22.8, 0, 0),
(21, 'Cash', '11/02/2025 17:24:16', 11400, 114, 0, 0),
(22, 'Cash', '11/02/2025 17:25:09', 14820, 148.2, 0, 0),
(23, 'Cash', '11/04/2025 17:22:56', 2000, 194.24, 0, 0),
(24, 'Cash', '11/04/2025 17:40:34', 3000, 291.36, 0, 0),
(25, 'Cash', '11/04/2025 17:51:17', 912, 0, 0, 0),
(26, 'Cash', '11/04/2025 23:00:26', 11400, 0, 0, 0),
(27, 'Cash', '11/05/2025 02:04:48', 11400, 0, 0, 0),
(28, 'Cash', '11/05/2025 02:12:39', 11400, 0, 0, 0),
(29, 'Cash', '11/05/2025 02:31:51', 11400, 114, 0, 0),
(30, 'Cash', '11/29/2025 19:23:03', 22500, 126, 0, 0),
(31, 'Cash', '11/29/2025 19:23:32', 10, 2.16, 0, 0),
(32, 'Cash', '11/29/2025 19:23:54', 20, 4.32, 0, 0),
(33, 'Cash', '11/29/2025 19:26:40', 1000, 73.7164, 0, 0),
(34, 'Cash', '11/29/2025 19:36:10', 4000, 643.9, 0, 0),
(35, 'Cash', '11/29/2025 19:37:47', 10, 2.1691, 0, 0),
(36, 'Cash', '12/02/2025 17:18:55', 20, 2.2816, 0, 0),
(37, 'Cash', '12/03/2025 12:54:50', 15, 3.8, 0, 0),
(38, 'Cash', '12/06/2025 16:48:15', 70, 8.4, 0, 0),
(39, 'Cash', '12/06/2025 16:48:52', 70, 8.4, 0, 0),
(40, 'Cash', '12/06/2025 16:53:05', 200, 76.8, 0, 0),
(41, 'Cash', '12/06/2025 16:56:01', 70, 8.4, 0, 0),
(42, 'Cash', '12/06/2025 17:02:17', 100, 38.4, 0, 0),
(43, 'Cash', '12/06/2025 18:25:55', 1000, 507.2, 0, 0),
(44, 'Cash', '12/06/2025 20:33:58', 200, 62.24, 73, 1),
(45, 'Cash', '12/06/2025 20:50:29', 150, 12.24, 74, 1),
(46, 'Cash', '12/06/2025 20:54:45', 200, 62.24, 75, 1),
(47, 'Cash', '12/06/2025 20:58:53', 200, 62.24, 76, 1);

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `idProduct` int(10) UNSIGNED NOT NULL,
  `product_name` text NOT NULL,
  `price` float NOT NULL,
  `StockQuantity` int(11) NOT NULL,
  `barcode` varchar(45) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `category_idCategory` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`idProduct`, `product_name`, `price`, `StockQuantity`, `barcode`, `image_path`, `description`, `category_idCategory`) VALUES
(1, 'Incott G23', 3000, 33, '24-1780', '1761713497497_sg-11134201-7rep4-m1rwmczd8nlib7-2364560986.jpg', 'wireless mouse', 0),
(2, 'Razer Viper Mini', 800, 47, 'PM2138H15023145', '1761713600051_Razer-Viper-Mini-RGB-Gaming-Mouse-61G-Ultra-Light-Black-903961--2274769274.jpg', '8k hz mouse', 0),
(3, 'Coiled Cable', 434, 32, '34324343', '1761713619331_8-1000x1000-1790972885.png', 'A cable to save space and reduce clutter', 0),
(4, 'Gigabit G27QC', 10000, 37, '24-1792', '1762071743488_gigabyte g27qc.jpg', '144hz curved monitor OLED', 0),
(5, 'KZ Castor Pro', 800, 34, '24-1790', '1762277659271_cn-11134208-7ras8-m220z0p6davgd4-3429807670.jpg', 'Bass Edition', 0),
(6, 'Lemon Square Cupcake', 10, 99, '4806018400988', '1764127249820_lemon-square-cupcakr-768x689.jpg', 'cupcake', 0),
(7, 'Butter Coconut', 7, 94, '4807770122170', '1764127341824_buttercoco.jpg', 'butter coconut biscuit', 0),
(8, 'Magic Flakes Cheese', 8, 97, '4800016066153', '1764127410875_Magic_Flakes_Cheese_01.png', 'Cheese flavor', 0),
(9, 'Tiger Chocolate', 7, 95, '7622202374395', '1764411853331_tiger.jpg', 'chocolate biscuit', 0),
(12, 'sfd', 55, 39, '55', '1765007004590_lemon-square-cupcakr-768x689.jpg', 'dsf', 0),
(13, 'magic', 123, 119, '123', '1765020821861_Magic_Flakes_Cheese_01.png', '123', 1);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `idUser` int(10) UNSIGNED NOT NULL,
  `username` varchar(45) NOT NULL,
  `lastName` varchar(100) NOT NULL,
  `firstName` varchar(100) NOT NULL,
  `middleName` varchar(100) DEFAULT NULL,
  `password` varchar(45) NOT NULL,
  `role` text DEFAULT NULL COMMENT 'admin, cashier'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`idUser`, `username`, `lastName`, `firstName`, `middleName`, `password`, `role`) VALUES
(1, 'admin', '', '', NULL, 'admin', 'admin'),
(2, 'test', '', '', NULL, 'test', 'admin'),
(5, 'not', '', '', NULL, 'not', 'admin'),
(9, 'tester', '', '', NULL, 'tester', 'admin'),
(10, 'test1', '', '', NULL, 'test1', 'Cashier'),
(11, 'testq', 'testq', 'testq', 'testq', 'testq', 'Cashier');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`idCategory`);

--
-- Indexes for table `order`
--
ALTER TABLE `order`
  ADD PRIMARY KEY (`idOrder`,`user_idUser`),
  ADD KEY `fk_order_user1_idx` (`user_idUser`);

--
-- Indexes for table `orderdetails`
--
ALTER TABLE `orderdetails`
  ADD PRIMARY KEY (`idOrderDetails`,`order_idOrder`,`order_user_idUser`,`product_idProduct`,`product_category_idCategory`),
  ADD KEY `fk_orderdetails_order1_idx` (`order_idOrder`,`order_user_idUser`),
  ADD KEY `fk_orderdetails_product1_idx` (`product_idProduct`,`product_category_idCategory`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`idPayment`,`order_idOrder`,`order_user_idUser`),
  ADD KEY `fk_payment_order1_idx` (`order_idOrder`,`order_user_idUser`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`idProduct`,`category_idCategory`),
  ADD KEY `fk_product_category_idx` (`category_idCategory`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`idUser`),
  ADD UNIQUE KEY `username_UNIQUE` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `idCategory` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `order`
--
ALTER TABLE `order`
  MODIFY `idOrder` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=77;

--
-- AUTO_INCREMENT for table `orderdetails`
--
ALTER TABLE `orderdetails`
  MODIFY `idOrderDetails` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `idPayment` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
  MODIFY `idProduct` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `idUser` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `order`
--
ALTER TABLE `order`
  ADD CONSTRAINT `fk_order_user1` FOREIGN KEY (`user_idUser`) REFERENCES `user` (`idUser`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `orderdetails`
--
ALTER TABLE `orderdetails`
  ADD CONSTRAINT `fk_orderdetails_order1` FOREIGN KEY (`order_idOrder`,`order_user_idUser`) REFERENCES `order` (`idOrder`, `user_idUser`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_orderdetails_product1` FOREIGN KEY (`product_idProduct`,`product_category_idCategory`) REFERENCES `product` (`idProduct`, `category_idCategory`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `fk_payment_order1` FOREIGN KEY (`order_idOrder`,`order_user_idUser`) REFERENCES `order` (`idOrder`, `user_idUser`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `fk_product_category` FOREIGN KEY (`category_idCategory`) REFERENCES `category` (`idCategory`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

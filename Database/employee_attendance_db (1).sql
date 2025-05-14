-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:4306
-- Generation Time: May 05, 2025 at 09:00 AM
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
-- Database: `employee_attendance_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
  `id` int(11) NOT NULL,
  `employee_id` int(11) DEFAULT NULL,
  `fullname` varchar(255) NOT NULL,
  `date` date DEFAULT NULL,
  `time_in` time DEFAULT NULL,
  `time_out` time DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `total_hours` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `employees`
--

CREATE TABLE `employees` (
  `employee_id` int(11) NOT NULL,
  `fullname` varchar(100) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `position` varchar(50) DEFAULT NULL,
  `qr_code` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `employees`
--

INSERT INTO `employees` (`employee_id`, `fullname`, `phone_number`, `position`, `qr_code`) VALUES
(16, 'Jhade', '1231232131', 'Worker', 'Jhade'),
(17, 'Renzo M. Pacheco', '639123456789', 'Manager', 'Renzo M. Pacheco'),
(18, 'Eriann Ayuban', '639123456789', 'Worker', 'Eriann Ayuban'),
(19, 'Luis Antonio S. Acebo', '639217467275', 'Manager', 'Luis Antonio S. Acebo'),
(20, 'Ivan Kurt Salvador', '09784384384', 'Manager', 'Ivan Kurt Salvador'),
(22, 'Rotsen Serrano', '09352353', 'Worker', 'Rotsen Serrano'),
(28, 'Marco', '09355453535', 'Manager', 'Marco'),
(29, 'Reiner', '09534364464', 'Worker', 'Reiner'),
(30, 'wadawdw', '09534243', 'Manager', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `qrcode`
--

CREATE TABLE `qrcode` (
  `id` int(255) NOT NULL,
  `qrcodedata` varchar(255) NOT NULL,
  `qrcodefilepath` varchar(255) NOT NULL,
  `Created_At` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `qrcode`
--

INSERT INTO `qrcode` (`id`, `qrcodedata`, `qrcodefilepath`, `Created_At`) VALUES
(9, 'Jhade', 'C:\\Users\\Renzo\\OneDrive\\Desktop\\qr codes\\Jhade_QR.png', '2025-04-08 11:33:50'),
(10, 'Renzo M. Pacheco', 'C:\\Users\\Renzo\\OneDrive\\Desktop\\qr codes\\Renzo M. Pacheco_QR.png', '2025-04-08 11:33:57'),
(11, 'Eriann Ayuban', 'C:\\Users\\Renzo\\OneDrive\\Desktop\\qr codes\\Eriann Ayuban_QR.png', '2025-04-08 11:34:10'),
(12, 'Luis Antonio S. Acebo', 'C:\\Users\\Renzo\\OneDrive\\Desktop\\qr codes\\Luis Antonio S. Acebo_QR.png', '2025-04-08 11:34:17'),
(13, 'Ivan Kurt Salvador', 'C:\\Users\\Renzo\\OneDrive\\Desktop\\qr codes\\Ivan Kurt Salvador_QR.png', '2025-04-09 11:57:20'),
(14, 'Rotsen Serrano', 'C:\\Users\\Renzo\\OneDrive\\Desktop\\qr codes\\Rotsen Serrano_QR.png', '2025-04-09 12:15:41'),
(15, 'Ringo Baldivino', 'C:\\Users\\Renzo\\OneDrive\\Desktop\\qr codes\\Ringo Baldivino_QR.png', '2025-04-10 11:22:50'),
(16, 'Marco', 'C:\\Users\\Renzo\\OneDrive\\Desktop\\qr codes\\Marco_QR.png', '2025-04-10 12:44:36'),
(17, 'Reiner', 'C:\\Users\\Renzo\\OneDrive\\Desktop\\qr codes\\Reiner_QR.png', '2025-04-11 02:43:32');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(20) DEFAULT 'admin'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `role`) VALUES
(1, 'Admin', 'admin123', 'admin');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`id`),
  ADD KEY `attendance_ibfk_1` (`employee_id`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`employee_id`);

--
-- Indexes for table `qrcode`
--
ALTER TABLE `qrcode`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=91;

--
-- AUTO_INCREMENT for table `employees`
--
ALTER TABLE `employees`
  MODIFY `employee_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `qrcode`
--
ALTER TABLE `qrcode`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
  ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

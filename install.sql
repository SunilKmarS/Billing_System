DROP DATABASE billing;
CREATE DATABASE billing;
use billing;
CREATE TABLE PRODUCTS(
	productId INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(50),
	originalPrice DECIMAL(8,2) ,
	tax DECIMAL(5,2),
	finalPrice DECIMAL(10,2) GENERATED ALWAYS AS (originalPrice+(originalPrice*tax/100)) STORED,
    stock INT);
CREATE TABLE CUSTOMERS(
	custId INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
    phone VARCHAR(15) NOT NULL);
CREATE TABLE ORDERS(
	orderId INT PRIMARY KEY AUTO_INCREMENT,
	custId INT,
    orderDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    discount DECIMAL(5,2) DEFAULT 0,
    couponCode VARCHAR(20) DEFAULT NULL,
    amount DECIMAL(15,2),
    paymentMethod VARCHAR(10),
	FOREIGN KEY(custId) REFERENCES CUSTOMERS(custId) ON UPDATE CASCADE);
CREATE TABLE ORDER_ITEMS(
	itemId INT PRIMARY KEY AUTO_INCREMENT,
    orderId INT,
    productId INT,
    productPrice DECIMAL(8,2),
    tax DECIMAL(5,2),
    quantity INT,
    itemTotal DECIMAL(15,2) GENERATED ALWAYS AS (quantity*productPrice) STORED,
    FOREIGN KEY(orderId) REFERENCES ORDERS(orderId) ON UPDATE CASCADE,
    FOREIGN KEY(productId) REFERENCES PRODUCTS(productId) ON UPDATE CASCADE
);
CREATE TABLE COUPONS (
    couponId INT PRIMARY KEY AUTO_INCREMENT,
    custId INT,
    couponCode VARCHAR(20) NOT NULL,
    discount DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (custId) REFERENCES CUSTOMERS(custId) ON UPDATE CASCADE
);
INSERT INTO PRODUCTS (name, originalPrice, tax, stock) VALUES
('Apple', 100.00, 5.00, 50),
('Mango', 200.00, 10.00, 30),
('Banana', 150.00, 8.00, 20),
('Orange', 300.00, 12.00, 15),
('Juice', 250.00, 7.50, 25);

INSERT INTO CUSTOMERS (name, phone) VALUES
('Alice Smith', '555-1234'),
('Bob Johnson', '555-5678'),
('Charlie Davis', '555-8765'),
('Dana Lee', '555-4321'),
('Evan Kim', '555-6789');

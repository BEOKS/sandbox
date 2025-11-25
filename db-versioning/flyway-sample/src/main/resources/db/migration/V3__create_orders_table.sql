-- V3: Create orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert sample orders
INSERT INTO orders (user_id, product_name, quantity, total_price) VALUES (1, 'Laptop', 1, 1500000.00);
INSERT INTO orders (user_id, product_name, quantity, total_price) VALUES (1, 'Mouse', 2, 50000.00);
INSERT INTO orders (user_id, product_name, quantity, total_price) VALUES (2, 'Keyboard', 1, 150000.00);

-- Dữ liệu mẫu cho môi trường local/test (MySQL).
-- INSERT IGNORE giúp ứng dụng khởi động lại mà không bị lỗi dữ liệu trùng.

INSERT IGNORE INTO category (id, name) VALUES
    (1, 'Áo nam'),
    (2, 'Áo nữ'),
    (3, 'Quần'),
    (4, 'Phụ kiện');

INSERT IGNORE INTO size (id, name) VALUES
    (1, 'S'),
    (2, 'M'),
    (3, 'L'),
    (4, 'XL');

-- Mật khẩu tài khoản mẫu: password
-- Hash BCrypt tương thích với PasswordEncoder của Spring Security.
INSERT IGNORE INTO user (id, username, password, email, role, created_at) VALUES
    (1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@fashionshop.local', 'ADMIN', NOW()),
    (2, 'testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'testuser@fashionshop.local', 'USER', NOW());

-- Các phương thức thanh toán dùng khi test checkout.
INSERT IGNORE INTO payment (id, method) VALUES
    (1, 'COD'),
    (2, 'VNPAY'),
    (3, 'MoMo');

INSERT IGNORE INTO product (id, name, description, price, stock_quantity, created_at, image, category_id) VALUES
    (1, 'Áo thun basic trắng', 'Áo thun cotton mềm, kiểu dáng cơ bản dễ phối đồ.', 199000, 50, NOW(), 'ts1.webp', 1),
    (2, 'Áo sơ mi linen xanh', 'Áo sơ mi linen thoáng mát, phù hợp đi làm và đi chơi.', 349000, 30, NOW(), 'sm1.webp', 1),
    (3, 'Đầm hoa midi', 'Đầm midi họa tiết hoa nữ tính, chất liệu nhẹ.', 459000, 20, NOW(), 'vd1.webp', 2),
    (4, 'Quần jean ống đứng', 'Quần jean form đứng, màu xanh wash hiện đại.', 399000, 25, NOW(), 'je1.webp', 3),
    (5, 'Túi đeo vai mini', 'Túi đeo vai nhỏ gọn, phù hợp sử dụng hằng ngày.', 259000, 15, NOW(), 'vay1.jpg', 4);

INSERT IGNORE INTO promotion_email (email, subscribed_at) VALUES
    ('newsletter@fashionshop.local', NOW());

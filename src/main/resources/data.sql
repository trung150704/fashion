-- Dữ liệu mẫu cho môi trường local/test. Ảnh dùng các file có sẵn trong static/images.
INSERT IGNORE INTO category (id, name) VALUES
    (1, 'Áo nam'), (2, 'Áo nữ'), (3, 'Quần'), (4, 'Phụ kiện');

INSERT IGNORE INTO size (id, name) VALUES
    (1, 'S'), (2, 'M'), (3, 'L'), (4, 'XL');

-- Mật khẩu mẫu: password
INSERT IGNORE INTO user (id, username, password, email, role, created_at) VALUES
    (1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@fashionshop.local', 'ADMIN', NOW()),
    (2, 'testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'testuser@fashionshop.local', 'USER', NOW());

INSERT IGNORE INTO payment (id, method) VALUES (1, 'COD'), (2, 'VNPAY'), (3, 'MoMo');

INSERT IGNORE INTO product (id, name, description, price, stock_quantity, created_at, image, category_id) VALUES
    (1, 'Áo thun basic trắng', 'Áo thun cotton mềm, phom regular dễ mặc và dễ phối đồ hằng ngày.', 199000, 50, NOW(), 'ts1.webp', 1),
    (2, 'Áo sơ mi linen xanh', 'Áo sơ mi linen thoáng mát, tay dài, phù hợp đi làm và đi chơi.', 349000, 30, NOW(), 'sm1.webp', 1),
    (3, 'Áo polo nam xanh navy', 'Áo polo dệt kim co giãn nhẹ, cổ bẻ thanh lịch cho phong cách smart-casual.', 329000, 28, NOW(), 'ts2.webp', 1),
    (4, 'Áo thun graphic đen', 'Áo thun cotton màu đen in họa tiết tối giản, cá tính và dễ phối.', 229000, 35, NOW(), 'ts3.webp', 1),
    (5, 'Áo blouse tay phồng', 'Blouse nữ cổ tròn, tay phồng nhẹ, chất liệu mềm tạo vẻ nữ tính.', 289000, 24, NOW(), 'qt1.webp', 2),
    (6, 'Đầm hoa midi', 'Đầm midi họa tiết hoa nhã nhặn, dáng xòe nhẹ, thích hợp dạo phố.', 459000, 20, NOW(), 'vd1.webp', 2),
    (7, 'Áo croptop len kem', 'Áo len mỏng dáng croptop, màu kem dễ phối cùng chân váy hoặc quần jean.', 269000, 18, NOW(), 'qt2.webp', 2),
    (8, 'Áo sơ mi nữ trắng', 'Sơ mi trắng phom suông, đường may gọn gàng cho tủ đồ công sở.', 319000, 26, NOW(), 'qt3.webp', 2),
    (9, 'Đầm suông dự tiệc', 'Đầm suông tối giản với chất vải đứng phom, phù hợp sự kiện và tiệc nhẹ.', 529000, 12, NOW(), 'vd2.webp', 2),
    (10, 'Quần jean ống đứng', 'Quần jean denim xanh wash vừa, phom ống đứng hiện đại và tôn dáng.', 399000, 25, NOW(), 'je1.webp', 3),
    (11, 'Quần tây nam đen', 'Quần tây phom slim, màu đen cơ bản, phù hợp đi làm và dịp trang trọng.', 429000, 22, NOW(), 'pl1.webp', 3),
    (12, 'Quần kaki be', 'Quần kaki màu be, chất vải bền nhẹ, phối đẹp với áo thun và sơ mi.', 369000, 20, NOW(), 'pl2.webp', 3),
    (13, 'Chân váy chữ A đen', 'Chân váy chữ A cạp cao, màu đen dễ phối cho phong cách nữ tính.', 299000, 16, NOW(), 'vay1.jpg', 3),
    (14, 'Quần short denim xanh', 'Quần short denim năng động, phù hợp những ngày đi biển hoặc dạo phố.', 279000, 19, NOW(), 'je2.webp', 3),
    (15, 'Túi đeo vai mini', 'Túi đeo vai nhỏ gọn, ngăn chứa vừa đủ cho điện thoại và vật dụng thiết yếu.', 259000, 15, NOW(), 'smc1.1.webp', 4),
    (16, 'Túi tote canvas', 'Túi tote canvas màu kem, quai chắc chắn, tiện dụng khi đi học hoặc đi làm.', 189000, 32, NOW(), 'smc2.1.webp', 4),
    (17, 'Giày sneaker trắng', 'Sneaker trắng thiết kế tối giản, đế cao su êm và dễ phối nhiều phong cách.', 599000, 14, NOW(), 'sh1.webp', 4),
    (18, 'Giày loafer nâu', 'Loafer da tổng hợp màu nâu, kiểu dáng thanh lịch cho outfit công sở.', 549000, 11, NOW(), 'sh2.webp', 4),
    (19, 'Mũ lưỡi trai basic', 'Mũ lưỡi trai unisex, chất liệu nhẹ, phù hợp mặc hằng ngày.', 149000, 40, NOW(), 'scm3.3.webp', 4),
    (20, 'Thắt lưng da đen', 'Thắt lưng da tổng hợp bản vừa, khóa kim loại bền và dễ phối quần jean hoặc quần tây.', 169000, 27, NOW(), 'scm4.1.webp', 4);

INSERT IGNORE INTO promotion_email (email, subscribed_at) VALUES ('newsletter@fashionshop.local', NOW());

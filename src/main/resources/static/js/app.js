var app = angular.module('shopApp', []);

// Thông báo thống nhất cho toàn bộ thao tác; giữ fallback nếu CDN chưa tải.
function showNotice(message, type = 'info', position = 'top') {
	if (window.Swal) {
		Swal.fire({
			toast: true,
			position: position,
			icon: type,
			title: message,
			showConfirmButton: false,
			timer: 2600,
			timerProgressBar: true,
			// Thêm custom class nếu muốn chỉnh style riêng
			customClass: {
				container: 'toast-below-header'
			}
		});
	} else {
		console.info(`[${type.toUpperCase()}] ${message}`);
	}
}

window.alert = function(message, type = 'info') {
	showNotice(message, type, 'top-end');
};

app.controller('CartController', function($scope, $http) {
	$scope.cart = [];
	$scope.selectedSize = null;
	$scope.userLoggedIn = false;

	const sizeMap = {
		"S": 1,
		"M": 2,
		"L": 3,
		"XL": 4
	};
	function normalizeCartItem(item) {
		return {
			productId: item.productId,
			productName: item.productName || item.name,
			price: item.price,
			image: item.image,
			sizeName: item.sizeName || item.size,
			sizeId: sizeMap[item.sizeName || item.size],
			quantity: item.quantity,
		};
	}

	// Lấy user nếu đã đăng nhập
	$http.get("/api/cart/me").then(res => {
		$scope.userLoggedIn = true;

		// Đồng bộ từ localStorage lên server nếu có
		const localCart = localStorage.getItem("cart");
		if (localCart) {
			const items = JSON.parse(localCart);

			// Tạo một mảng Promise cho tất cả POST
			const promises = items.map(item => {
				const size = item.sizeName || item.size;
				const sizeId = sizeMap[size];
				const payload = {
					productId: item.productId,
					sizeId: sizeId,
					quantity: item.quantity,
					productName: item.name || item.productName,
					price: item.price,
					image: item.image,
					sizeName: size
				};
				console.log("Đồng bộ item:", payload);
				return $http.post("/api/cart/add", payload);
			});

			// Đợi tất cả POST hoàn thành
			Promise.all(promises).then(() => {
				localStorage.removeItem("cart");
				// Lấy lại giỏ hàng từ server sau khi đồng bộ xong
				$http.get("/api/cart/me/cart").then(response => {
					$scope.cart = response.data.map(normalizeCartItem);
					$scope.updateCartCount();
				});
			}).catch(err => {
				console.error("Lỗi khi đồng bộ giỏ hàng:", err);
			});
		}
		// Load giỏ hàng từ server
		$http.get("/api/cart/me/cart").then(response => {
			$scope.cart = response.data;
			$scope.updateCartCount();
		});
	}).catch(err => {
		if (err.status === 401) {
			$scope.userLoggedIn = false;
			const localCart = localStorage.getItem("cart");
			$scope.cart = localCart ? JSON.parse(localCart).map(normalizeCartItem) : [];
			$scope.updateCartCount();
			console.clear();
			console.log("Người dùng chưa đăng nhập - không thể tải giỏ hàng từ server");
		}
	});

	$scope.selectSize = function(size) {
		$scope.selectedSize = size;
		const label = document.getElementById("selected-size-label");
		if (label) label.innerText = "— " + size;
		document.querySelectorAll('.size-option').forEach(function(button) {
			button.classList.toggle('active', button.innerText.trim() === size);
		});
	};

	$scope.addToCart = function(productId, productName, price, image) {
		if (!$scope.selectedSize) {
			alert("Vui lòng chọn kích cỡ!");
			return;
		}

		const quantity = parseInt($scope.quantity || 1);
		const item = {
			productId,
			name: productName,
			price,
			image,
			size: $scope.selectedSize,
			quantity
		};

		if (!$scope.userLoggedIn) {
			const existing = $scope.cart.find(i => i.productId === item.productId && i.size === item.size);
			if (existing) {
				existing.quantity += quantity;
			} else {
				$scope.cart.push(item);
			}
			localStorage.setItem("cart", JSON.stringify($scope.cart));
			$scope.updateCartCount();
			showNotice('Thêm vào giỏ hàng thành công! (client)', 'success', 'top-end');
		} else {
			const payload = {
				productId: item.productId,
				sizeId: sizeMap[item.size],
				quantity: item.quantity,
				productName: item.name,
				price: item.price,
				image: item.image,
				sizeName: item.size
			};

			$http.post("/api/cart/add", payload).then(() => {
				// Reload lại cart từ server
				$http.get("/api/cart/me/cart").then(response => {
					$scope.cart = response.data.map(normalizeCartItem);;
					$scope.updateCartCount();
				});
				showNotice('Thêm vào giỏ hàng thành công! (server)', 'success', 'top-end');
			});

		}
	};

	$scope.removeFromCart = function(productId, sizeId) {
		if (!$scope.userLoggedIn) {
			// Xóa ở localStorage
			$scope.cart = $scope.cart.filter(i => !(i.productId === productId && i.sizeId === sizeId));
			localStorage.setItem("cart", JSON.stringify($scope.cart));
			$scope.updateCartCount();
			showNotice('Đã xóa sản phẩm khỏi giỏ hàng (local)', 'success', 'top-end');
		} else {
			const payload = {
				productId: productId,
				sizeId: sizeId
			};
			$http.post("/api/cart/remove", payload).then(() => {
				// Xóa trên giao diện sau khi xóa trên server
				$scope.cart = $scope.cart.filter(i => !(i.productId === productId && i.sizeId === sizeId));
				$scope.updateCartCount();
				showNotice('Đã xóa sản phẩm khỏi giỏ hàng (server)', 'success', 'top-end');
			}).catch(err => {
				console.error("Lỗi khi xóa sản phẩm:", err);
				showNotice('Không thể xóa sản phẩm. Vui lòng thử lại!', 'error', 'top-end');
			});
		}
	};



	$scope.updateCartCount = function() {
		let total = 0;
		for (const item of $scope.cart) {
			total += item.quantity;
		}
		document.getElementById("cart-count").innerText = total;
	};

	/*$scope.getTotal = function() {
		return $scope.cart.reduce((total, item) => total + item.price * item.quantity, 0);
	};*/

	$scope.shippingFee = 0; // phí ship mặc định

	$scope.getSubtotal = function() {
		return $scope.cart.reduce((total, item) => total + item.price * item.quantity, 0);
	};

	$scope.getTotal = function() {
		return $scope.getSubtotal() + $scope.shippingFee;
	};

	function clearLocalCart() {
		localStorage.removeItem("cart");
		$scope.cart = [];
		$scope.updateCartCount();
	}

	//checkout
	$scope.getFullAddress = function() {
		const house = $scope.order.houseNumber || "";
		const province = document.getElementById("province").selectedOptions[0]?.text || "";
		const district = document.getElementById("district").selectedOptions[0]?.text || "";
		const ward = document.getElementById("ward").selectedOptions[0]?.text || "";

		return `${house}, ${ward}, ${district}, ${province}`;
	};
	
	$scope.checkPaid = function(price, content, intervalId, orderData) {
	    $http.get("https://script.google.com/macros/s/AKfycbyEdl0IILtOCmHIoN0elik2K5360NmiWXrQuBL38-KqUw4vQRiyhDbKV_mFbOl0EwLR/exec")
	        .then(function(response) {
	            const data = response.data;
	            if (!data || !data.data || !data.data.length) return;

	            const lastPaid = data.data[data.data.length - 1];
	            const lastPrice = parseFloat(lastPaid["Giá trị"]);
	            const lastContent = lastPaid["Mô tả"];
				
				const expectedContent = content.replace(/[^A-Za-z0-9]/g, "");

				console.log("📌 Checking payment...");
				console.log("Expected Content:", expectedContent);
				console.log("Expected Price:", price);
				console.log("Last Paid Content:", lastContent);
				console.log("Last Paid Price:", lastPrice);
	            if (lastPrice >= price && lastContent.includes(expectedContent)) {
	                clearInterval(intervalId); // dừng kiểm tra

					showNotice('Thanh toán thành công!', 'success', 'top-end');

	                // gọi API tạo đơn hàng
	                $http.post("/order", orderData)
	                    .then(res => {
	                        alert(res.data.message);
	                        const orderId = res.data.orderId;
	                        if (res.data.orderCode) sessionStorage.setItem('lastOrderCode', res.data.orderCode);
	                        $scope.cart = [];
	                        clearLocalCart();
	                        window.location.href = `/order/confirmation/${orderId}`;
	                    })
	                    .catch(err => {
	                        console.error("Lỗi khi tạo đơn sau khi thanh toán", err);
							showNotice('Lỗi khi lưu đơn hàng sau khi thanh toán!', 'error', 'top-end');
	                    });
	            } else {
	                console.log("⛔ Chưa phát hiện thanh toán");
	            }
	        })
	        .catch(function(err) {
	            console.error("Lỗi khi kiểm tra thanh toán", err);
	        });
	};


	let MY_BANK = {
		BANK_ID: "MB",
		ACCOUNT_NO: "0328725537"
	};

	const qr_img = document.querySelector(".qr_img");
	$scope.submitOrder = function() {
		const fullAddress = $scope.getFullAddress();
		let totalPrice = $scope.getTotal();

		// Lấy phương thức thanh toán
		let paymentMethod = $scope.order.paymentMethod;

		// ✅ Kiểm tra nếu chưa chọn phương thức
		if (!paymentMethod) {
			showNotice('Vui lòng chọn phương thức thanh toán!', 'warning', 'top-end');
			return;
		}

		console.log("Phương thức thanh toán:", paymentMethod);
		console.log("Tổng tiền:", totalPrice);

		const orderData = {
			fullName: $scope.order.fullName,
			phone: $scope.order.phone,
			address: fullAddress,
			paymentMethod: $scope.order.paymentMethod,
			shippingFee: $scope.shippingFee,
			cart: $scope.cart.map(item => ({
				productId: item.productId,
				sizeId: item.sizeId,
				quantity: item.quantity,
				price: item.price
			})),
			paid: true
		};
		if (paymentMethod === "Thanh toán bằng Ví điện tử/ QR Code") {
			//const generatedOrderCode = "ORDER_" + Date.now(); // hoặc UUID
			const paidContent = "ACSP" + Date.now();
			sessionStorage.setItem("paidContent", paidContent);
			const paidPrice = totalPrice;
			sessionStorage.setItem("qrOrder", JSON.stringify(orderData));

			let QR = `https://img.vietqr.io/image/${MY_BANK.BANK_ID}-${MY_BANK.ACCOUNT_NO}-compact2.png?amount=${paidPrice}&addInfo=${encodeURIComponent(paidContent)}`;
			sessionStorage.setItem("qrUrl", QR);

			// Kiểm tra sau 20s
			setTimeout(() => {
				const intervalId = setInterval(() => {
					$scope.checkPaid(paidPrice, paidContent, intervalId, orderData);
				}, 1000);
			}, 15000);

			window.location.href = "/qrcode";
			return;
		}


		$http.post("/order", orderData)
			.then(res => {
				alert(res.data.message); // ✅ Lấy message từ JSON
				const orderId = res.data.orderId;
				if (res.data.orderCode) sessionStorage.setItem('lastOrderCode', res.data.orderCode);
				if (res.data.paymentUrl) {
					window.location.href = res.data.paymentUrl;
					return;
				}
				$scope.cart = [];
				if (!$scope.userLoggedIn) {
					// Xóa giỏ hàng local
					clearLocalCart();
					window.location.href = `/order/confirmation/${orderId}`;
				} else {
					// Load lại từ DB sau khi đã được xóa ở backend
					$http.get("/api/cart/me/cart").then(response => {
						$scope.cart = response.data;
						$scope.updateCartCount();
						window.location.href = `/order-list/${orderId}`;
					});
				}
			})
			.catch(err => {
				showNotice('Lỗi khi đặt hàng!', 'error', 'top-end');
				console.error(err);
			});
	};

});


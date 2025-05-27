function selectSize(element) {
    // Bỏ chọn tất cả các ô
    const allSizes = document.querySelectorAll('.size-option');
    allSizes.forEach(size => {
        size.style.border = '1px solid black';
        size.style.color = 'black';
    });

    // Đánh dấu ô được chọn
    element.style.border = '2px solid gold';
    element.style.color = 'gold';

    // Hiển thị kích cỡ đã chọn
    document.getElementById('selected-size').textContent = 'Kích cỡ: ' + element.textContent;
}
window.onscroll = function() {
    const btn = document.getElementById("scrollTopBtn");
    if (document.body.scrollTop > 200 || document.documentElement.scrollTop > 200) {
      btn.style.display = "block";
    } else {
      btn.style.display = "none";
    }
  };

  // Cuộn về đầu trang khi nhấn nút
  function scrollToTop() {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  }
  
  //di chuyen xuong 4 san pham lien quan
  document.addEventListener("DOMContentLoaded", function () {
          if (window.location.hash === "#related-products") {
              const el = document.getElementById("related-products");
              if (el) {
                  el.scrollIntoView({ behavior: "smooth" });
              }
          }
      });
	  
	  
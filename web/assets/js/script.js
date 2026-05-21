// =========================================================
// 1. HIỆU ỨNG CHO THANH MENU (NAVBAR)
// =========================================================
// Lấy phần tử navbar từ HTML
const navbar = document.getElementById('navbar');

// Thêm sự kiện khi người dùng cuộn chuột
window.addEventListener('scroll', function() {
  // Nếu cuộn xuống quá 20px thì thêm class 'scrolled' (để hiện bóng đổ)
  if (window.scrollY > 20) {
    navbar.classList.add('scrolled');
  } else {
    // Ngược lại xóa class đi
    navbar.classList.remove('scrolled');
  }
});

// =========================================================
// 2. TÍNH NĂNG LỌC LỘ TRÌNH (FILTER)
// =========================================================
// Lấy tất cả các nút có class 'filter-btn'
const filterButtons = document.querySelectorAll('.filter-btn');
// Lấy tất cả các thẻ (card) lộ trình
const roadmapCards = document.querySelectorAll('.rcard:not(.rcard-create)');

filterButtons.forEach(function(button) {
  button.addEventListener('click', function() {
    // Bước 1: Xóa màu của tất cả các nút (xóa class 'active')
    filterButtons.forEach(function(btn) {
      btn.classList.remove('active');
    });

    // Bước 2: Tô màu cho nút vừa bấm (thêm class 'active')
    button.classList.add('active');

    // Bước 3: Lấy từ khóa lọc (ví dụ: 'ielts', 'toeic', 'all')
    const filterKeyword = button.getAttribute('data-filter');

    // Bước 4: Kiểm tra từng card để ẩn/hiện
    roadmapCards.forEach(function(card) {
      const cardCategory = card.getAttribute('data-cat');
      
      // Nếu chọn 'all' hoặc đúng với danh mục của card thì hiện lên
      if (filterKeyword === 'all' || filterKeyword === cardCategory) {
        card.style.display = ''; // Hiện
      } else {
        card.style.display = 'none'; // Ẩn
      }
    });
  });
});

// =========================================================
// 3. HIỆU ỨNG NHẢY SỐ (STATISTICS)
// =========================================================
// Hàm để chạy số từ 0 đến con số mong muốn
function animateNum(id, endValue, suffix) {
  const element = document.getElementById(id);
  if (!element) return;

  const duration = 1500; // Thời gian chạy (1.5 giây)
  let startTime = null;

  function updateNumber(currentTime) {
    if (!startTime) startTime = currentTime;
    // Tính toán tiến độ (từ 0 đến 1)
    let progress = (currentTime - startTime) / duration;
    if (progress > 1) progress = 1;

    // Tính toán số hiện tại và hiển thị lên màn hình
    const currentNum = Math.floor(progress * endValue);
    element.textContent = currentNum.toLocaleString('vi-VN') + suffix;

    // Nếu chưa xong thì tiếp tục chạy
    if (progress < 1) {
      requestAnimationFrame(updateNumber);
    }
  }

  // Bắt đầu chạy hiệu ứng sau khi chờ 400ms
  setTimeout(function() {
    requestAnimationFrame(updateNumber);
  }, 400);
}

// Gọi hàm cho 2 con số ở đầu trang
animateNum('cnt-users', 12400, '+');
animateNum('cnt-paths', 3800, '+');

// =========================================================
// 4. HIỂN THỊ THÔNG BÁO (TOAST)
// =========================================================
// Hàm tạo thông báo nổi lên màn hình
function showToast(message) {
  // Xóa thông báo cũ nếu có
  const oldToast = document.getElementById('toast');
  if (oldToast) {
    oldToast.remove();
  }

  // Tạo một thẻ div mới làm thông báo
  const toast = document.createElement('div');
  toast.id = 'toast';
  toast.textContent = message;

  // Code CSS cho hộp thông báo
  toast.style.position = 'fixed';
  toast.style.bottom = '24px';
  toast.style.left = '50%';
  toast.style.transform = 'translate(-50%, 100px)'; // Giấu ở dưới màn hình
  toast.style.backgroundColor = '#0f172a';
  toast.style.color = '#fff';
  toast.style.padding = '14px 24px';
  toast.style.borderRadius = '12px';
  toast.style.fontSize = '14px';
  toast.style.fontWeight = '600';
  toast.style.zIndex = '9999';
  toast.style.boxShadow = '0 8px 32px rgba(0,0,0,0.2)';
  toast.style.transition = 'transform 0.3s ease';

  // Thêm vào trong trang
  document.body.appendChild(toast);

  // Cho thông báo trượt lên
  setTimeout(function() {
    toast.style.transform = 'translate(-50%, 0)';
  }, 10);

  // Sau 3 giây (3000ms) thì thông báo sẽ trượt xuống và biến mất
  setTimeout(function() {
    toast.style.transform = 'translate(-50%, 100px)';
    setTimeout(function() {
      toast.remove();
    }, 300); // Đợi hiệu ứng trượt xong rồi mới xóa
  }, 3000);
}

// =========================================================
// 5. BẮT SỰ KIỆN CLICK CHO CÁC NÚT BẤM
// =========================================================
// Nút tham gia lộ trình
const joinButtons = document.querySelectorAll('.rcard-btn, .btn-join');
joinButtons.forEach(function(button) {
  button.addEventListener('click', function(event) {
    event.preventDefault(); // Ngăn trình duyệt chuyển trang
    showToast('Bạn đã tham gia lộ trình này! Kiểm tra email để xác nhận.');
  });
});

// Nút đăng ký
const signupButton = document.getElementById('btn-signup');
if (signupButton) {
  signupButton.addEventListener('click', function(event) {
    event.preventDefault();
    showToast('🎉 Đăng ký thành công! Chào mừng bạn đến PathShare.');
  });
}

// Nút tạo lộ trình
const createButtons = document.querySelectorAll('#cta-create, #rc-create');
createButtons.forEach(function(button) {
  button.addEventListener('click', function(event) {
    event.preventDefault();
    showToast('✍️ Tính năng tạo lộ trình sắp ra mắt! Bạn sẽ nhận được thông báo sớm.');
  });
});

// =========================================================
// 6. HIỆU ỨNG THANH TIẾN ĐỘ (PROGRESS BAR) KHI CUỘN ĐẾN
// =========================================================
// Dùng IntersectionObserver để biết khi nào thẻ xuất hiện trên màn hình
const observer = new IntersectionObserver(function(entries) {
  entries.forEach(function(entry) {
    // Nếu thẻ tiến độ xuất hiện trên màn hình
    if (entry.isIntersecting) {
      // Chạy hiệu ứng cho từng thanh màu bên trong
      const bars = entry.target.querySelectorAll('.fv-fill');
      bars.forEach(function(bar) {
        const targetWidth = bar.style.width; // Lấy width thực tế (ví dụ: 80%)
        bar.style.width = '0'; // Ban đầu để là 0
        
        // Sau đó chạy dần dần lên targetWidth
        setTimeout(function() {
          bar.style.width = targetWidth;
        }, 100);
      });
      // Chỉ chạy hiệu ứng 1 lần duy nhất
      observer.unobserve(entry.target);
    }
  });
}, { threshold: 0.3 }); // Sẽ chạy hiệu ứng khi hiện được 30% thẻ

// Bắt đầu theo dõi khung tiến độ
const progressCards = document.querySelectorAll('.fv-card');
progressCards.forEach(function(card) {
  observer.observe(card);
});

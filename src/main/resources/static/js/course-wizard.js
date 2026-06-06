document.addEventListener("DOMContentLoaded", function () {
    const basicInfoForm = document.getElementById("basicInfoForm");
    const courseIdInput = document.getElementById("courseId");

    // Lấy thông tin từ Form
    const getFormData = () => {
        return {
            title: document.getElementById("courseTitle").value.trim(),
            subtitle: document.getElementById("courseSubtitle").value.trim(),
            description: document.getElementById("courseDescription").value.trim(),
            price: parseFloat(document.getElementById("coursePrice").value) || 0,
            thumbnailFileId: document.getElementById("thumbnailFileId").value || null // Dành cho upload ảnh sau này
        };
    };
//xử lý nút lưu ở tab 1 lúc tạo khóa học
    basicInfoForm.addEventListener("submit", function (e) {
        e.preventDefault(); // Ngăn trình duyệt tải lại trang khi bấm form

        const courseId = courseIdInput.value;
        const formData = getFormData();

        // 1. Lấy thẻ bài CSRF từ trên thẻ <meta>
        const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
        const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

        // 2. Gói thẻ bài vào Headers
        const headers = {
            "Content-Type": "application/json"
        };
        if (csrfTokenMeta && csrfHeaderMeta) {
            headers[csrfHeaderMeta.content] = csrfTokenMeta.content;
        }
// sửa lỗi chỉ hiện lưu mỗi titile khi ấn tạo khóa hoc
        if (!courseId) {
            // Nhịp 1: Gọi API POST /draft để tạo khóa học (chỉ nhận title)
            fetch("/api/instructor/courses/draft", {
                method: "POST",
                headers: headers,
                body: JSON.stringify({ title: formData.title })
            })
                .then(res => {
                    if (!res.ok) throw new Error("Lỗi khi tạo khóa học nháp");
                    return res.json(); // Lấy data.courseId từ Backend trả về
                })
                .then(data => {
                    // Nhịp 2: Ép nó gọi tiếp API PUT ngay lập tức để lưu nốt Phụ đề, Mô tả, Giá tiền
                    return fetch(`/api/instructor/courses/${data.courseId}`, {
                        method: "PUT",
                        headers: headers,
                        body: JSON.stringify(formData) // Gửi full mọi thứ Sếp gõ trên form
                    }).then(res2 => {
                        if (!res2.ok) throw new Error("Lỗi khi cất thêm thông tin phụ");
                        // Lưu thành công cả 2 nhịp rồi mới chuyển trang!
                        alert("Tạo khóa học thành công! Hệ thống sẽ chuyển sang chế độ chỉnh sửa.");
                        window.location.href = `/instructor/courses/${data.courseId}/edit`;
                    });
                })
                .catch(error => {
                    console.error(error);
                    alert("Có lỗi xảy ra khi tạo khóa học!");
                });

        } else {
            // Trường hợp Cập nhật: Gọi API PUT (Phase 3)
            fetch(`/api/instructor/courses/${courseId}`, {
                method: "PUT",
                headers: headers, // <-- Gắn thẻ bài vào đây
                body: JSON.stringify(formData)
            })
                .then(res => {
                    if (!res.ok) throw new Error("Lỗi khi cập nhật khóa học");
                    return res.text();
                })
                .then(message => {
                    alert("Đã lưu thông tin khóa học!");

                    const curriculumTabBtn = document.getElementById("curriculum-tab");
                    curriculumTabBtn.disabled = false;

                    const bsTab = new bootstrap.Tab(curriculumTabBtn);
                    bsTab.show();
                })
                .catch(error => {
                    console.error(error);
                    alert("Có lỗi xảy ra khi lưu khóa học!");
                });
        }
    });

    // Mock logic cho nút Upload Ảnh (Chờ Sếp viết API Backend upload ảnh sau)
    const uploadArea = document.querySelector(".upload-area");
    if (uploadArea) {
        uploadArea.addEventListener("click", function() {
            alert("Sếp chưa viết API Upload Ảnh cho Khóa học ở Backend! Chỗ này tạm thời để trống nhé.");
        });
    }
});

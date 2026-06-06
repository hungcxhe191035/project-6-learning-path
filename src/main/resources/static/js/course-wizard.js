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

        // sửa lỗi chỉ hiện lưu mỗi title khi ấn tạo khóa học
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
                        body: JSON.stringify(formData) // Gửi full mọi thứ bạn gõ trên form
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
                headers: headers,
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

    // Logic cho nút Upload Ảnh (Phase 8)
    const uploadArea = document.querySelector(".upload-area");
    const thumbnailUploadInput = document.getElementById("thumbnailUploadInput");
    const thumbnailFileIdInput = document.getElementById("thumbnailFileId");
    const thumbnailPreviewContainer = document.getElementById("thumbnailPreviewContainer");
    const thumbnailPreview = document.getElementById("thumbnailPreview");
    const thumbnailUploadPrompt = document.getElementById("thumbnailUploadPrompt");

    if (uploadArea && thumbnailUploadInput) {
        uploadArea.addEventListener("click", function() {
            thumbnailUploadInput.click();
        });

        thumbnailUploadInput.addEventListener("change", function() {
            const file = this.files[0];
            if (!file) return;

            const formData = new FormData();
            formData.append("file", file);

            const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
            const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
            const headers = {};
            if (csrfTokenMeta && csrfHeaderMeta) headers[csrfHeaderMeta.content] = csrfTokenMeta.content;

            // Đổi con trỏ chuột thành wait để user biết đang load
            uploadArea.style.cursor = "wait";
            uploadArea.style.opacity = "0.7";

            fetch("/api/instructor/courses/thumbnail", {
                method: "POST",
                headers: headers,
                body: formData
            })
            .then(res => {
                if (!res.ok) throw new Error("Lỗi khi tải ảnh lên server");
                return res.json();
            })
            .then(data => {
                // data = { fileId: ..., fileUrl: ... }
                thumbnailFileIdInput.value = data.fileId;
                thumbnailPreview.src = data.fileUrl;
                
                thumbnailUploadPrompt.style.display = "none";
                thumbnailPreviewContainer.style.display = "block";
            })
            .catch(err => {
                console.error(err);
                alert("Tải ảnh thất bại! Vui lòng thử lại.");
            })
            .finally(() => {
                // Khôi phục giao diện
                uploadArea.style.cursor = "pointer";
                uploadArea.style.opacity = "1";
                thumbnailUploadInput.value = ""; // Clear input
            });
        });
    }

    // Logic Xuất bản khóa học (Phase 7)
    const btnPublishCourse = document.getElementById("btnPublishCourse");
    if (btnPublishCourse) {
        btnPublishCourse.addEventListener("click", function() {
            const courseId = courseIdInput.value;
            if (!courseId) {
                return alert("Vui lòng hoàn thành Thông tin cơ bản trước khi xuất bản!");
            }

            if (confirm("Bạn có chắc chắn muốn Xuất bản khóa học này không? Khóa học sẽ được hiển thị ngay lập tức cho học viên!")) {
                const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
                const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
                const headers = {};
                if (csrfTokenMeta && csrfHeaderMeta) headers[csrfHeaderMeta.content] = csrfTokenMeta.content;

                fetch(`/api/instructor/courses/${courseId}/publish`, {
                    method: 'PUT',
                    headers: headers
                })
                .then(res => {
                    if (!res.ok) return res.text().then(text => { throw new Error(text) });
                    return res.text();
                })
                .then(msg => {
                    alert(msg);
                    window.location.href = '/instructor/courses'; // Đá về danh sách khóa học
                })
                .catch(err => {
                    console.error(err);
                    alert(err.message || "Có lỗi xảy ra khi xuất bản khóa học! Vui lòng kiểm tra lại nội dung.");
                });
            }
        });
    }
});
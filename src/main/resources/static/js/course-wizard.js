document.addEventListener("DOMContentLoaded", function () {
    const basicInfoForm = document.getElementById("basicInfoForm");
    const courseIdInput = document.getElementById("courseId");

    // Khởi tạo Select2 cho tags
    if (typeof $ !== 'undefined' && $('#courseTags').length) {
        $('#courseTags').select2({
            placeholder: "Chọn thẻ chủ đề cho khóa học...",
            allowClear: true
        });
    }

    // Lấy thông tin từ Form
    const getFormData = () => {
        const selectedTags = $('#courseTags').val() || [];
        const tagIds = selectedTags.map(id => parseInt(id));
        return {
            title: document.getElementById("courseTitle").value.trim(),
            subtitle: document.getElementById("courseSubtitle").value.trim(),
            description: document.getElementById("courseDescription").value.trim(),
            price: parseFloat(document.getElementById("coursePrice").value) || 0,
            thumbnailFileId: document.getElementById("thumbnailFileId").value || null,
            tagIds: tagIds
        };
    };

    // Toastr Helper
    const showSuccessToast = (msg) => {
        if (typeof toastr !== 'undefined') {
            toastr.success(msg);
        } else {
            alert(msg);
        }
    };

    const showErrorToast = (msg) => {
        if (typeof toastr !== 'undefined') {
            toastr.error(msg);
        } else {
            alert(msg);
        }
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
                        showSuccessToast("Tạo khóa học thành công!");
                        setTimeout(() => {
                            window.location.href = `/instructor/courses/${data.courseId}/edit`;
                        }, 1000);
                    });
                })
                .catch(error => {
                    console.error(error);
                    showErrorToast("Có lỗi xảy ra khi tạo khóa học!");
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
                    showSuccessToast("Đã lưu thông tin khóa học!");

                    const curriculumTabBtn = document.getElementById("curriculum-tab");
                    curriculumTabBtn.disabled = false;

                    const bsTab = new bootstrap.Tab(curriculumTabBtn);
                    bsTab.show();
                })
                .catch(error => {
                    console.error(error);
                    showErrorToast("Có lỗi xảy ra khi lưu khóa học!");
                });
        }
    });

    // Logic cho nút Upload Ảnh (Phase 8)
    const uploadArea = document.querySelector(".cinematic-upload-area");
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
                showSuccessToast("Tải ảnh bìa thành công!");
            })
            .catch(err => {
                console.error(err);
                showErrorToast("Tải ảnh thất bại! Vui lòng thử lại.");
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
                return showErrorToast("Vui lòng hoàn thành Thông tin cơ bản trước khi xuất bản!");
            }

            if (typeof Swal !== 'undefined') {
                Swal.fire({
                    title: 'Xuất bản khóa học?',
                    text: 'Khóa học sẽ lập tức hiển thị công khai cho học viên!',
                    icon: 'question',
                    showCancelButton: true,
                    confirmButtonColor: '#198754',
                    cancelButtonColor: '#6c757d',
                    confirmButtonText: '<i class="bi bi-send-fill me-1"></i> Xuất bản ngay',
                    cancelButtonText: 'Hủy'
                }).then((result) => {
                    if (result.isConfirmed) {
                        doPublishCourse(courseId);
                    }
                });
            } else {
                if (confirm("Bạn có chắc chắn muốn Xuất bản khóa học này không?")) {
                    doPublishCourse(courseId);
                }
            }
        });

        function doPublishCourse(courseId) {
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
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        title: 'Thành công!',
                        text: msg,
                        icon: 'success',
                        confirmButtonColor: '#198754'
                    }).then(() => {
                        window.location.href = '/instructor/courses';
                    });
                } else {
                    alert(msg);
                    window.location.href = '/instructor/courses';
                }
            })
            .catch(err => {
                console.error(err);
                if (typeof Swal !== 'undefined') {
                    Swal.fire('Lỗi xuất bản!', err.message || "Có lỗi xảy ra khi xuất bản khóa học!", 'error');
                } else {
                    alert(err.message || "Có lỗi xảy ra khi xuất bản khóa học!");
                }
            });
        }
    }
});
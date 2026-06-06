document.addEventListener("DOMContentLoaded", function () {
    const btnAddSection = document.getElementById("btnAddSection");
    const curriculumAccordion = document.getElementById("curriculumAccordion");
    const emptyState = document.getElementById("emptyCurriculumState");
    const courseId = document.getElementById("courseId").value;
    const sectionTemplate = document.getElementById("sectionTemplate");
    const lessonTemplate = document.getElementById("lessonTemplate");

    const lessonModalElement = document.getElementById('lessonModal');
    const lessonModal = lessonModalElement ? new bootstrap.Modal(lessonModalElement) : null;
    const btnConfirmAddLesson = document.getElementById('btnConfirmAddLesson');

    const lessonContentModalElement = document.getElementById('lessonContentModal');
    const lessonContentModal = lessonContentModalElement ? new bootstrap.Modal(lessonContentModalElement) : null;
    const btnSaveLessonContent = document.getElementById('btnSaveLessonContent');

    let editorInstance = null;

    if (document.getElementById('ckeditorContent')) {
        
        // CÔNG NGHỆ CHẤT: Plugin tự chế bắn ảnh thẳng lên S3 thông qua API
        function S3UploadAdapterPlugin(editor) {
            editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
                return {
                    upload: () => {
                        return loader.file.then(file => new Promise((resolve, reject) => {
                            const data = new FormData();
                            data.append('upload', file);
                            
                            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
                            const csrfToken = document.querySelector('meta[name="_csrf"]').content;

                            fetch('/api/instructor/article-image-upload', {
                                method: 'POST',
                                headers: { [csrfHeader]: csrfToken },
                                body: data
                            })
                            .then(response => response.json())
                            .then(result => {
                                if(result.error) reject(result.error);
                                else resolve({ default: result.url }); // Nhận link S3 về chèn vào HTML
                            })
                            .catch(error => reject(error));
                        }));
                    },
                    abort: () => {}
                };
            };
        }

        ClassicEditor
            .create(document.querySelector('#ckeditorContent'), {
                extraPlugins: [S3UploadAdapterPlugin]
            })
            .then(editor => {
                editorInstance = editor;
            })
            .catch(error => {
                console.error("Lỗi khởi tạo CKEditor:", error);
            });
            
        // SỬA LỖI BOOTSTRAP: Tranh giành quyền Click chuột với Modal
        document.addEventListener('focusin', function(e) {
            if (e.target.closest('.ck-balloon-panel')) {
                e.stopImmediatePropagation();
            }
        });
    }

    const getHeaders = () => {
        const headers = { "Content-Type": "application/json" };
        const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
        const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
        if (csrfTokenMeta && csrfHeaderMeta) {
            headers[csrfHeaderMeta.content] = csrfTokenMeta.content;
        }
        return headers;
    };

    function attachLessonEvents(lessonNode, lessonId) {
        lessonNode.querySelector('.btn-delete-lesson').addEventListener('click', function() {
            if(!confirm("Bạn có chắc muốn xóa bài giảng này không?")) return;
            fetch(`/api/instructor/lessons/${lessonId}`, {
                method: "DELETE",
                headers: getHeaders()
            }).then(() => lessonNode.remove());
        });

        lessonNode.querySelector('.btn-edit-lesson').addEventListener('click', function() {
            if(!lessonContentModal) return alert("Thiếu HTML của Modal nội dung!");

            const lessonTypeText = lessonNode.querySelector('.lesson-type-badge').textContent;
            const rawLessonType = lessonTypeText.includes('VIDEO') ? 'VIDEO' : 'ARTICLE';

            document.getElementById('editLessonId').value = lessonId;
            document.getElementById('editLessonType').value = rawLessonType;

            // Kéo nội dung cũ từ Backend về trước khi mở Modal
            fetch(`/api/instructor/lessons/${lessonId}`)
                .then(res => res.json())
                .then(data => {
                    if(rawLessonType === 'VIDEO') {
                        document.getElementById('videoUploadContainer').classList.remove('d-none');
                        document.getElementById('articleEditorContainer').classList.add('d-none');

                        const progressBar = document.getElementById('videoProgressBar');
                        document.getElementById('videoFileInput').value = "";

                        if(data.videoUrl) {
                            progressBar.style.width = '100%';
                            progressBar.textContent = 'Đã có video trên hệ thống (Tải file mới để ghi đè)';
                            document.getElementById('videoProgressBarContainer').classList.remove('d-none');
                            
                            const videoPlayer = document.getElementById('videoPlayer');
                            if(videoPlayer) {
                                videoPlayer.src = data.videoUrl;
                                document.getElementById('videoPreviewContainer').classList.remove('d-none');
                            }
                        } else {
                            progressBar.style.width = '0%';
                            progressBar.textContent = '0%';
                            document.getElementById('videoProgressBarContainer').classList.add('d-none');
                            if(document.getElementById('videoPreviewContainer')) document.getElementById('videoPreviewContainer').classList.add('d-none');
                        }
                    } else {
                        document.getElementById('videoUploadContainer').classList.add('d-none');
                        document.getElementById('articleEditorContainer').classList.remove('d-none');

                        if(editorInstance) {
                            editorInstance.setData(data.articleContent || "");
                        }
                    }
                    lessonContentModal.show();
                });
        });
    }

    function attachSectionEvents(sectionNode, sectionId) {
        sectionNode.querySelector('.btn-delete-section').addEventListener('click', function() {
            if(!confirm("Bạn có chắc muốn xóa Chương này không?")) return;
            fetch(`/api/instructor/sections/${sectionId}`, { method: "DELETE", headers: getHeaders() })
                .then(() => {
                    sectionNode.remove();
                    if (document.querySelectorAll(".section-item").length === 0 && emptyState) emptyState.style.display = "block";
                });
        });

        sectionNode.querySelector('.btn-edit-section').addEventListener('click', function() {
            const currentTitle = sectionNode.querySelector('.section-title').textContent;
            const newTitle = prompt("Nhập Tên mới cho Chương:", currentTitle);
            if(!newTitle) return;
            fetch(`/api/instructor/sections/${sectionId}`, { method: "PUT", headers: getHeaders(), body: JSON.stringify({ title: newTitle, displayOrder: 1 }) })
                .then(() => sectionNode.querySelector('.section-title').textContent = newTitle);
        });

        sectionNode.querySelector('.btn-add-lesson').addEventListener('click', function() {
            if(!lessonModal) return;
            document.getElementById('modalTargetSectionId').value = sectionId;
            document.getElementById('modalLessonTitle').value = "";
            document.getElementById('modalLessonType').value = "VIDEO";
            lessonModal.show();
        });

        sectionNode.querySelectorAll('.lesson-item').forEach(lessonItem => attachLessonEvents(lessonItem, lessonItem.dataset.lessonId));
    }

    if(btnConfirmAddLesson) {
        btnConfirmAddLesson.addEventListener("click", function() {
            const sectionId = document.getElementById('modalTargetSectionId').value;
            const lessonTitle = document.getElementById('modalLessonTitle').value.trim();
            const lessonType = document.getElementById('modalLessonType').value;
            if(!lessonTitle) return alert("Vui lòng nhập tên bài giảng!");

            const sectionNode = document.querySelector(`.section-item[data-section-id="${sectionId}"]`);
            const lessonList = sectionNode.querySelector('.lesson-list');

            fetch(`/api/instructor/sections/${sectionId}/lessons`, {
                method: "POST", headers: getHeaders(),
                body: JSON.stringify({ title: lessonTitle, lessonType: lessonType, displayOrder: lessonList.children.length + 1 })
            }).then(res => res.json()).then(lessonData => {
                const newLessonNode = lessonTemplate.content.cloneNode(true);
                const lessonItem = newLessonNode.querySelector('.lesson-item');
                lessonItem.dataset.lessonId = lessonData.lessonId;
                newLessonNode.querySelector('.lesson-title').textContent = lessonTitle;
                newLessonNode.querySelector('.lesson-type-badge').textContent = lessonType;
                if(lessonType === 'VIDEO') newLessonNode.querySelector('.lesson-icon').classList.add('bi-play-circle-fill', 'text-primary');
                else newLessonNode.querySelector('.lesson-icon').classList.add('bi-file-text-fill', 'text-success');

                attachLessonEvents(lessonItem, lessonData.lessonId);
                lessonList.appendChild(newLessonNode);
                lessonModal.hide();
            }).catch(err => alert("Lỗi tạo bài giảng!"));
        });
    }

    document.querySelectorAll(".section-item").forEach(sectionNode => attachSectionEvents(sectionNode, sectionNode.dataset.sectionId));

    if (btnAddSection) {
        btnAddSection.addEventListener("click", function () {
            const title = prompt("Nhập Tên Chương mới:");
            if (!title) return;
            fetch(`/api/instructor/courses/${courseId}/sections`, {
                method: "POST", headers: getHeaders(),
                body: JSON.stringify({ title: title, displayOrder: document.querySelectorAll(".section-item").length + 1 })
            }).then(res => res.json()).then(data => {
                if (emptyState) emptyState.style.display = "none";
                const newSectionNode = sectionTemplate.content.cloneNode(true);
                const sectionItem = newSectionNode.querySelector('.section-item');
                sectionItem.dataset.sectionId = data.sectionId;
                newSectionNode.querySelector('.section-title').textContent = title;
                newSectionNode.querySelector('.accordion-header').id = `heading-${data.sectionId}`;
                newSectionNode.querySelector('.accordion-collapse').id = `collapse-${data.sectionId}`;
                newSectionNode.querySelector('.accordion-button').dataset.bsTarget = `#collapse-${data.sectionId}`;
                attachSectionEvents(sectionItem, data.sectionId);
                curriculumAccordion.appendChild(newSectionNode);
            });
        });
    }

    const videoFileInput = document.getElementById('videoFileInput');
    if(videoFileInput) {
        videoFileInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if(!file) return;

            const lessonId = document.getElementById('editLessonId').value;
            const formData = new FormData();
            formData.append("file", file);

            document.getElementById('videoProgressBarContainer').classList.remove('d-none');
            const progressBar = document.getElementById('videoProgressBar');

            const xhr = new XMLHttpRequest();
            xhr.open("POST", `/api/instructor/lessons/${lessonId}/video`, true);

            const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
            const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
            if (csrfTokenMeta && csrfHeaderMeta) {
                xhr.setRequestHeader(csrfHeaderMeta.content, csrfTokenMeta.content);
            }

            xhr.upload.onprogress = function(event) {
                if (event.lengthComputable) {
                    const percentComplete = Math.round((event.loaded / event.total) * 100);
                    progressBar.style.width = percentComplete + '%';
                    progressBar.textContent = percentComplete + '%';
                }
            };

            xhr.onload = function() {
                if (xhr.status === 200) {
                    alert("Tải video lên S3 thành công!");

                    const lessonItem = document.querySelector(`.lesson-item[data-lesson-id="${lessonId}"]`);
                    if(lessonItem) {
                        const badge = lessonItem.querySelector('.lesson-type-badge');
                        badge.classList.remove('bg-secondary-subtle', 'text-secondary');
                        badge.classList.add('bg-success-subtle', 'text-success', 'fw-bold');
                        badge.innerHTML = '<i class="bi bi-check-circle-fill me-1"></i>ĐÃ CÓ VIDEO';
                    }

                    lessonContentModal.hide();
                } else {
                    alert("Lỗi tải video: " + xhr.responseText);
                }
            };

            xhr.send(formData);
        });
    }

    if(btnSaveLessonContent) {
        btnSaveLessonContent.addEventListener('click', function() {
            const lessonType = document.getElementById('editLessonType').value;
            const lessonId = document.getElementById('editLessonId').value;

            if (lessonType.includes('ARTICLE')) {
                const htmlContent = editorInstance ? editorInstance.getData() : "";

                fetch(`/api/instructor/lessons/${lessonId}/article`, {
                    method: "PUT",
                    headers: getHeaders(),
                    body: JSON.stringify({ content: htmlContent })
                }).then(res => {
                    if(!res.ok) throw new Error("Lỗi lưu bài viết");
                    alert("Lưu bài viết thành công rực rỡ!");

                    const lessonItem = document.querySelector(`.lesson-item[data-lesson-id="${lessonId}"]`);
                    if(lessonItem) {
                        const badge = lessonItem.querySelector('.lesson-type-badge');
                        badge.classList.remove('bg-secondary-subtle', 'text-secondary');
                        badge.classList.add('bg-success-subtle', 'text-success', 'fw-bold');
                        badge.innerHTML = '<i class="bi bi-check-circle-fill me-1"></i>ĐÃ CÓ BÀI VIẾT';
                    }

                    lessonContentModal.hide();
                }).catch(err => {
                    console.error(err);
                    alert("Lỗi khi lưu bài viết!");
                });
            } else {
                alert("Đối với Video thì hệ thống tự động tải lên khi bạn chọn File rồi, không cần bấm Lưu nữa!");
            }
        });
    }
});
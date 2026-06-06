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

    function createQuestionHTML(questionIndex, qData = { questionText: '', answers: [] }) {
        let answersHTML = '';
        const answers = qData.answers && qData.answers.length > 0 ? qData.answers : [
            { answerText: '', isCorrect: true },
            { answerText: '', isCorrect: false },
            { answerText: '', isCorrect: false },
            { answerText: '', isCorrect: false }
        ];

        answers.forEach((a, aIndex) => {
            answersHTML += `
                <div class="input-group mb-2 answer-item">
                    <div class="input-group-text bg-white">
                        <input class="form-check-input mt-0 is-correct-radio" type="radio" name="correctAnswer_q${questionIndex}" value="${aIndex}" ${a.isCorrect ? 'checked' : ''} title="Chọn làm đáp án đúng">
                    </div>
                    <input type="text" class="form-control answer-text" placeholder="Nhập đáp án..." value="${a.answerText}">
                    <button class="btn btn-outline-danger btn-remove-answer" type="button" title="Xóa đáp án"><i class="bi bi-x"></i></button>
                </div>
            `;
        });

        return `
            <div class="card shadow-sm border-0 bg-light question-block mb-4">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h6 class="fw-bold mb-0 text-secondary">Câu hỏi <span class="q-index">${questionIndex + 1}</span></h6>
                        <button type="button" class="btn btn-sm btn-outline-danger btn-remove-question"><i class="bi bi-trash"></i> Xóa câu này</button>
                    </div>
                    <div class="mb-3">
                        <textarea class="form-control question-text fw-medium" rows="2" placeholder="Nhập nội dung câu hỏi...">${qData.questionText}</textarea>
                    </div>
                    <div class="answers-container ps-3 border-start border-3 border-primary">
                        ${answersHTML}
                    </div>
                    <div class="mt-3 ps-3">
                        <button type="button" class="btn btn-sm btn-outline-primary btn-add-answer"><i class="bi bi-plus"></i> Thêm đáp án</button>
                    </div>
                </div>
            </div>
        `;
    }

    // Gắn sự kiện cho các nút thêm/xóa trong form Quiz
    const btnAddQuizQuestion = document.getElementById('btnAddQuizQuestion');
    if (btnAddQuizQuestion) {
        btnAddQuizQuestion.addEventListener('click', function() {
            const quizList = document.getElementById('quizQuestionsList');
            const qCount = quizList.querySelectorAll('.question-block').length;
            quizList.insertAdjacentHTML('beforeend', createQuestionHTML(qCount));
        });
    }

    const quizQuestionsList = document.getElementById('quizQuestionsList');
    if (quizQuestionsList) {
        quizQuestionsList.addEventListener('click', function(e) {
            if(e.target.closest('.btn-remove-question')) {
                if(confirm('Xóa câu hỏi này?')) {
                    e.target.closest('.question-block').remove();
                    document.querySelectorAll('.question-block .q-index').forEach((el, idx) => el.textContent = idx + 1);
                }
            }
            if(e.target.closest('.btn-remove-answer')) {
                e.target.closest('.answer-item').remove();
            }
            if(e.target.closest('.btn-add-answer')) {
                const answersContainer = e.target.closest('.card-body').querySelector('.answers-container');
                const qBlock = e.target.closest('.question-block');
                const qIndex = Array.from(document.getElementById('quizQuestionsList').children).indexOf(qBlock);
                
                const newAnswerHTML = `
                    <div class="input-group mb-2 answer-item">
                        <div class="input-group-text bg-white">
                            <input class="form-check-input mt-0 is-correct-radio" type="radio" name="correctAnswer_q${qIndex}" value="new" title="Chọn làm đáp án đúng">
                        </div>
                        <input type="text" class="form-control answer-text" placeholder="Nhập đáp án..." value="">
                        <button class="btn btn-outline-danger btn-remove-answer" type="button" title="Xóa đáp án"><i class="bi bi-x"></i></button>
                    </div>
                `;
                answersContainer.insertAdjacentHTML('beforeend', newAnswerHTML);
            }
        });
    }

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
            let rawLessonType = 'ARTICLE'; // Mặc định là Bài viết
            if (lessonTypeText.includes('VIDEO')) {
                rawLessonType = 'VIDEO';
            } else if (lessonTypeText.includes('QUIZ') || lessonTypeText.includes('TRẮC NGHIỆM')) {
                rawLessonType = 'QUIZ';
            }

            document.getElementById('editLessonId').value = lessonId;
            document.getElementById('editLessonType').value = rawLessonType;

            // Kéo nội dung cũ từ Backend về trước khi mở Modal
            fetch(`/api/instructor/lessons/${lessonId}`)
                .then(res => res.json())
                .then(data => {
                    if(rawLessonType === 'VIDEO') {
                        document.getElementById('videoUploadContainer').classList.remove('d-none');
                        document.getElementById('articleEditorContainer').classList.add('d-none');
                        document.getElementById('quizBuilderContainer').classList.add('d-none');

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
                    } else if (rawLessonType === 'ARTICLE') {
                        document.getElementById('videoUploadContainer').classList.add('d-none');
                        document.getElementById('articleEditorContainer').classList.remove('d-none');
                        document.getElementById('quizBuilderContainer').classList.add('d-none');

                        if(editorInstance) {
                            editorInstance.setData(data.articleContent || "");
                        }
                    } else if (rawLessonType === 'QUIZ') {
                        document.getElementById('videoUploadContainer').classList.add('d-none');
                        document.getElementById('articleEditorContainer').classList.add('d-none');
                        document.getElementById('quizBuilderContainer').classList.remove('d-none');

                        const quizList = document.getElementById('quizQuestionsList');
                        quizList.innerHTML = '';
                        if(data.questions && data.questions.length > 0) {
                            data.questions.forEach((q, index) => {
                                quizList.insertAdjacentHTML('beforeend', createQuestionHTML(index, q));
                            });
                        } else {
                            quizList.insertAdjacentHTML('beforeend', createQuestionHTML(0));
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
                else if(lessonType === 'QUIZ') newLessonNode.querySelector('.lesson-icon').classList.add('bi-patch-question-fill', 'text-warning');
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
            } else if (lessonType.includes('QUIZ')) {
                const payload = [];
                document.querySelectorAll('.question-block').forEach((qBlock, qIdx) => {
                    const qText = qBlock.querySelector('.question-text').value.trim();
                    const answers = [];
                    qBlock.querySelectorAll('.answer-item').forEach((aItem, aIdx) => {
                        answers.push({
                            answerText: aItem.querySelector('.answer-text').value.trim(),
                            isCorrect: aItem.querySelector('.is-correct-radio').checked,
                            displayOrder: aIdx + 1
                        });
                    });
                    if(qText) {
                        payload.push({
                            questionText: qText,
                            displayOrder: qIdx + 1,
                            answers: answers
                        });
                    }
                });

                fetch(`/api/instructor/lessons/${lessonId}/quiz`, {
                    method: "PUT",
                    headers: getHeaders(),
                    body: JSON.stringify(payload)
                }).then(res => {
                    if(!res.ok) throw new Error("Lỗi lưu trắc nghiệm");
                    alert("Lưu trắc nghiệm thành công rực rỡ!");
                    
                    const lessonItem = document.querySelector(`.lesson-item[data-lesson-id="${lessonId}"]`);
                    if(lessonItem) {
                        const badge = lessonItem.querySelector('.lesson-type-badge');
                        badge.classList.remove('bg-secondary-subtle', 'text-secondary');
                        badge.classList.add('bg-success-subtle', 'text-success', 'fw-bold');
                        badge.innerHTML = '<i class="bi bi-check-circle-fill me-1"></i>ĐÃ CÓ TRẮC NGHIỆM';
                    }
                    lessonContentModal.hide();
                }).catch(err => {
                    console.error(err);
                    alert("Lỗi khi lưu trắc nghiệm!");
                });
            } else {
                alert("Đối với Video thì hệ thống tự động tải lên khi bạn chọn File rồi, không cần bấm Lưu nữa!");
            }
        });
    }
});
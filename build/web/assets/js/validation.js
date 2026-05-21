document.addEventListener("DOMContentLoaded", function() {
    const regForm = document.querySelector("form[action='register']");
    if(regForm) {
        regForm.addEventListener("submit", function(e) {
            const pwd = document.querySelector("input[name='password']").value;
            const confirm = document.querySelector("input[name='confirm']").value;
            if(pwd.length < 6) {
                alert("Password must be at least 6 characters.");
                e.preventDefault();
            } else if(pwd !== confirm) {
                alert("Passwords do not match.");
                e.preventDefault();
            }
        });
    }
});
const active = 'bi-eye';
const inactive = 'bi-eye-slash';
const passType = 'password'
const textType = 'text'


function togglePasswordVisibility() {
    const passwordInput = document.getElementById('inputPassword');
    const toggleIcon = document.getElementById('toggleIcon');
    if (passwordInput.type === passType) {
        passwordInput.type = textType;
        toggleIcon.classList.remove(active);
        toggleIcon.classList.add(inactive);
    } else {
        passwordInput.type = passType;
        toggleIcon.classList.remove(inactive);
        toggleIcon.classList.add(active);
    }
}

if (typeof module !== "undefined" && typeof module.exports !== "undefined") {
    module.exports = {
        togglePasswordVisibility, active, inactive, passType, textType
    };
}

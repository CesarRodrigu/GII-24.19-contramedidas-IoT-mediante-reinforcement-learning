const fileName = "formsToggle.js";

const {
    togglePasswordVisibility, active, inactive, passType, textType
} = require("../../main/resources/static/js/" + fileName);

if (!togglePasswordVisibility || !active || !inactive || !passType || !textType) {
    console.error("Error: cant load functions from " + fileName + " file");
    process.exit(1);
}

test("togglePasswordVisibility works correctly", () => {
    document.body.innerHTML = `
        <input type="${passType}" id="inputPassword" />
        <i id="toggleIcon" class=active></i>
    `;

    const passwordInput = document.getElementById('inputPassword');
    const toggleIcon = document.getElementById('toggleIcon');

    togglePasswordVisibility();

    expect(passwordInput.type).toBe(textType);
    expect(toggleIcon.classList.contains(active)).toBe(false);
    expect(toggleIcon.classList.contains(inactive)).toBe(true);

    togglePasswordVisibility();

    expect(passwordInput.type).toBe(passType);
    expect(toggleIcon.classList.contains(active)).toBe(true);
    expect(toggleIcon.classList.contains(inactive)).toBe(false);
});


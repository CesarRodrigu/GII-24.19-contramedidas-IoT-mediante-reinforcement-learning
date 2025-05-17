function addRole() {
    const select = document.getElementById('availableRolesSelect');
    const role = select.value;
    const rolesContainer = document.getElementById('rolesContainer');

    if ([...rolesContainer.children].some(el => el.dataset.role === role)) return;

    const wrapper = document.createElement('div');
    wrapper.className = 'd-inline-flex align-items-center bg-primary text-white rounded-pill me-2 mb-2 px-3 py-1';
    wrapper.dataset.role = role;

    const text = document.createElement('span');
    text.textContent = role;

    const button = document.createElement('button');
    button.type = 'button';
    button.className = 'btn-close btn-close-white ms-2';
    button.setAttribute('aria-label', 'Remove');
    button.onclick = () => removeRole(button);

    wrapper.appendChild(text);
    wrapper.appendChild(button);
    rolesContainer.appendChild(wrapper);

    updateHiddenInput();
}

function removeRole(buttonElement) {
    const wrapper = buttonElement.closest('[data-role]');
    if (wrapper) {
        wrapper.remove();
        updateHiddenInput();
    }
}

function updateHiddenInput() {
    const badges = document.querySelectorAll('#rolesContainer [data-role]');
    const roles = [...badges].map(b => b.dataset.role);
    document.getElementById('hiddenRoles').value = roles.join(',');
}

function addHoverEffect() {
    const buttons = document.querySelectorAll('.btn-close');

    buttons.forEach(btn => {
        btn.addEventListener('mouseover', () => {
            btn.querySelector('.hover-icon')?.classList.remove('d-none');
        });
        btn.addEventListener('mouseout', () => {
            btn.querySelector('.hover-icon')?.classList.add('d-none');
        });
    });

}

addHoverEffect()

if (typeof module !== "undefined" && typeof module.exports !== "undefined") {
    module.exports = {
        addRole, removeRole, updateHiddenInput, addHoverEffect
    };
}

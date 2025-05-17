const fileName = "editUser.js";

const {
    addRole, removeRole, updateHiddenInput, addHoverEffect
} = require("../../main/resources/static/js/" + fileName);

if (!addRole || !removeRole || !updateHiddenInput || !addHoverEffect) {
    console.error("Error: cant load functions from " + fileName + " file");
    process.exit(1);
}
const role = "ADMIN";

test("addRole must add the role to the set", () => {
    document.body.innerHTML = `
    <select id="availableRolesSelect">
        <option value="${role}">${role}</option>
    </select>
    <div id="rolesContainer"></div>
    <input type="hidden" id="hiddenRoles" />
`;

    const select = document.getElementById("availableRolesSelect");
    select.value = role;

    addRole();

    const badge = document.querySelector(`[data-role="${role}"]`);
    const hiddenInput = document.getElementById("hiddenRoles");

    expect(badge).not.toBeNull();
    expect(badge.textContent).toContain(role);
    expect(hiddenInput.value).toBe(role);
});

test("removeRole must remove the role from the set", () => {
    document.body.innerHTML = `
        <div id="rolesContainer">
            <div class="d-inline-flex" data-role="${role}">
                <span>${role}</span>
                <button type="button" class="btn-close" aria-label="Remove"></button>
            </div>
        </div>
        <input type="hidden" id="hiddenRoles" />
    `;

    const badge = document.querySelector(`[data-role="${role}"]`);
    const button = badge.querySelector('button');


    document.getElementById("hiddenRoles").value = role;

    removeRole(button);

    expect(document.querySelector('[data-role="${role}"]')).toBeNull();
    expect(document.getElementById("hiddenRoles").value).toBe("");
});

test("addHoverEffect must change the class to add hover effect", () => {
    document.body.innerHTML = `
        <button class="btn-close">
            <span class="hover-icon d-none">👁</span>
        </button>
    `;

    const button = document.querySelector(".btn-close");
    const icon = button.querySelector(".hover-icon");

    addHoverEffect();

    const mouseOverEvent = new Event("mouseover");
    button.dispatchEvent(mouseOverEvent);
    expect(icon.classList.contains("d-none")).toBe(false);

    const mouseOutEvent = new Event("mouseout");
    button.dispatchEvent(mouseOutEvent);
    expect(icon.classList.contains("d-none")).toBe(true);
});

test("addRole must only add role only if not exists", () => {
    document.body.innerHTML = `
        <select id="availableRolesSelect">
            <option value="${role}">${role}</option>
        </select>
        <div id="rolesContainer">
            <div data-role="${role}"></div>
        </div>
        <input type="hidden" id="hiddenRoles" />
    `;

    const select = document.getElementById("availableRolesSelect");
    select.value = role;

    const initialCount = document.getElementById("rolesContainer").children.length;


    const finalCount = document.getElementById("rolesContainer").children.length;

    expect(finalCount).toBe(initialCount);
});

test("removeRole only remove role if is in the set", () => {
    document.body.innerHTML = `
        <div id="rolesContainer">
            <div class="not-a-role">
                <button type="button" class="btn-close" aria-label="Remove"></button>
            </div>
        </div>
        <input type="hidden" id="hiddenRoles" value="${role}" />
    `;

    const button = document.querySelector('button');
    const rolesContainerBefore = document.getElementById("rolesContainer").innerHTML;
    const hiddenBefore = document.getElementById("hiddenRoles").value;

    removeRole(button);

    const rolesContainerAfter = document.getElementById("rolesContainer").innerHTML;
    const hiddenAfter = document.getElementById("hiddenRoles").value;

    expect(rolesContainerAfter).toBe(rolesContainerBefore);
    expect(hiddenAfter).toBe(hiddenBefore);
});

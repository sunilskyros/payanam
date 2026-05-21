let login = document.getElementById("login");         // Home & Login page
let register = document.getElementById("register");   // Register page
let aboutussection = document.getElementById("aboutus-section"); // About Us section
let container = document.getElementById("container"); // Container for home and login/register

let loginLink = document.getElementById("login-link"); // Login link in nav bar
let registerLink = document.getElementById("register-link"); // Register link in nav bar
let aboutUsLink = document.getElementById("aboutus-link"); // About Us link in nav bar

function updateActiveNav(activeLink) {
    document.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));
    activeLink.classList.add('active');
}

loginLink.addEventListener("click", function() {
    aboutussection.style.display = "none";
    container.style.display = "flex";
    login.style.display = "flex";
    register.style.display = "none";
    updateActiveNav(loginLink);
});

registerLink.addEventListener("click", function() {
    aboutussection.style.display = "none";
    container.style.display = "flex";
    login.style.display = "none";
    register.style.display = "flex";
    updateActiveNav(registerLink);
});

aboutUsLink.addEventListener("click", function() {
    container.style.display = "none";
    aboutussection.style.display = "block";
    updateActiveNav(aboutUsLink);
});

// Show beautiful alerts based on URL query parameters from backend redirects
const urlParams = new URLSearchParams(window.location.search);
if (urlParams.has('error')) {
    alert("Invalid credentials or unauthorized access!");
} else if (urlParams.has('registered')) {
    alert("Registration successful! Please Sign In.");
} else if (urlParams.has('logged_out')) {
    alert("Successfully logged out.");
} else if (urlParams.has('reg_error')) {
    alert("Registration failed. The phone number might already be registered.");
}

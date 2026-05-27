// Elements
const loginForm = document.getElementById("login");
const registerForm = document.getElementById("register");
const aboutUsSection = document.getElementById("aboutus-section");
const formContainer = document.getElementById("container");

const loginLink = document.getElementById("login-link");
const registerLink = document.getElementById("register-link");
const aboutUsLink = document.getElementById("aboutus-link");

const loadingOverlay = document.getElementById("loading-overlay");
const toastContainer = document.getElementById("toast-container");
const themeToggle = document.getElementById("theme-toggle");

// 1. Navigation Tabs
function updateActiveNav(activeLink) {
    document.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));
    activeLink.classList.add('active');
}

loginLink.addEventListener("click", function() {
    aboutUsSection.style.display = "none";
    formContainer.style.display = "grid";
    loginForm.style.display = "block";
    registerForm.style.display = "none";
    updateActiveNav(loginLink);
});

registerLink.addEventListener("click", function() {
    aboutUsSection.style.display = "none";
    formContainer.style.display = "grid";
    loginForm.style.display = "none";
    registerForm.style.display = "block";
    updateActiveNav(registerLink);
});

aboutUsLink.addEventListener("click", function() {
    formContainer.style.display = "none";
    aboutUsSection.style.display = "block";
    updateActiveNav(aboutUsLink);
});

// 2. Light / Dark Mode Toggle
themeToggle.addEventListener("click", function() {
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
    
    // Save theme to a persistent cookie valid for 1 year
    document.cookie = "theme=" + newTheme + "; max-age=" + (365*24*60*60) + "; path=/";
    
    // Toggle Icon class
    const icon = themeToggle.querySelector('i');
    if (newTheme === 'dark') {
        icon.className = 'fas fa-sun';
    } else {
        icon.className = 'fas fa-moon';
    }
    showToast("Theme switched successfully!", "success");
});

// Set initial theme icon
if (document.documentElement.getAttribute('data-theme') === 'dark') {
    themeToggle.querySelector('i').className = 'fas fa-sun';
}

// 3. Slide-in Toast Notifications
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    let iconClass = 'fa-info-circle';
    if (type === 'success') iconClass = 'fa-check-circle';
    if (type === 'error') iconClass = 'fa-exclamation-circle';
    if (type === 'warning') iconClass = 'fa-exclamation-triangle';
    
    toast.innerHTML = `
        <i class="fas ${iconClass}"></i>
        <span>${message}</span>
    `;
    
    toastContainer.appendChild(toast);
    
    // Remove toast after 4 seconds
    setTimeout(() => {
        toast.style.animation = 'slideIn 0.3s forwards reverse';
        setTimeout(() => {
            toast.remove();
        }, 300);
    }, 4000);
}

// 4. Show loading overlay
function showLoader(visible) {
    if (visible) {
        loadingOverlay.classList.add('active');
    } else {
        loadingOverlay.classList.remove('active');
    }
}

// 5. AJAX Form Submissions
loginForm.onsubmit = function(e) {
    e.preventDefault();
    showLoader(true);
    
    const formData = new URLSearchParams(new FormData(loginForm));
    
    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: formData.toString()
    })
    .then(r => {
        showLoader(false);
        if (r.url.includes("error=true")) {
            showToast("Invalid credentials! Please try again.", "error");
        } else {
            showToast("Login successful! Redirecting...", "success");
            setTimeout(() => {
                window.location.href = r.url;
            }, 1000);
        }
    })
    .catch(err => {
        showLoader(false);
        showToast("Server connection error. Please try again.", "error");
    });
};

registerForm.onsubmit = function(e) {
    e.preventDefault();
    
    const p1 = document.getElementById("register-password").value;
    const p2 = document.getElementById("confirm-password").value;
    
    if (p1 !== p2) {
        showToast("Passwords do not match!", "warning");
        return;
    }
    
    showLoader(true);
    const formData = new URLSearchParams(new FormData(registerForm));
    
    fetch('/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: formData.toString()
    })
    .then(r => {
        showLoader(false);
        if (r.url.includes("reg_error=true")) {
            showToast("Registration failed! Phone number might be registered.", "error");
        } else {
            showToast("Registration successful! Switching to login tab.", "success");
            setTimeout(() => {
                loginLink.click();
                registerForm.reset();
            }, 1200);
        }
    })
    .catch(err => {
        showLoader(false);
        showToast("Server connection error. Please try again.", "error");
    });
};

// Check for redirection parameters
const urlParams = new URLSearchParams(window.location.search);
if (urlParams.has('error')) {
    showToast("Unauthorized access or invalid session!", "error");
} else if (urlParams.has('logged_out')) {
    showToast("Logged out successfully.", "success");
}

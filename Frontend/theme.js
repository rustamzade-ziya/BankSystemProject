const savedTheme = localStorage.getItem("theme");

if (savedTheme === "dark") {
    document.body.classList.add("dark-theme");
}

const toggleBtn = document.getElementById("themeToggle");

if (toggleBtn) {
    toggleBtn.addEventListener("click", () => {
        document.body.classList.toggle("dark-theme");

        if (document.body.classList.contains("dark-theme")) {
            localStorage.setItem("theme", "dark");
            toggleBtn.innerText = "☀️";
        } else {
            localStorage.setItem("theme", "light");
            toggleBtn.innerText = "🌙";
        }
    });

    if (document.body.classList.contains("dark-theme")) {
        toggleBtn.innerText = "☀️";
    } else {
        toggleBtn.innerText = "🌙";
    }
}

const phoneInput = document.getElementById('phoneInput');
phoneInput.value = '+994';

    phoneInput.addEventListener('input', function() {
        // Сохраняем начало +994
        let value = this.value;

        // Если пользователь удалил +994, восстанавливаем
        if (!value.startsWith('+994')) {
            value = '+994' + value.replace(/[^0-9]/g, '');
        } else {
        // Если +994 есть, оставляем как есть но удаляем лишние символы после
            value = '+994' + value.substring(4).replace(/[^0-9]/g, '');
        }

    this.value = value;
});



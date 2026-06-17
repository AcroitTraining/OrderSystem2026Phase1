/**
 * ページリロード後も画面全体のスクロール位置を維持するスクリプト
 */
document.addEventListener("DOMContentLoaded", function() {
    // 1. localStorageに保存されたスクロール位置があれば復元する
    const savedScrollY = localStorage.getItem("itemDetailsScrollY");
    if (savedScrollY !== null) {
        window.scrollTo(0, parseInt(savedScrollY, 10));
    }

    // 2. ユーザーが画面をスクロールするたびに、現在の位置を保存する
    window.addEventListener("scroll", function() {
        localStorage.setItem("itemDetailsScrollY", window.scrollY);
    });
    
    // 3. トッピングの数量フォームが送信された瞬間の正確な位置を確実に保存する
    const quantityForms = document.querySelectorAll(".quantity-form");
    quantityForms.forEach(function(form) {
        form.addEventListener("submit", function() {
            localStorage.setItem("itemDetailsScrollY", window.scrollY);
        });
    });
});
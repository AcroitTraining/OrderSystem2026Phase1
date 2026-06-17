/**
 * ページリロード後もカテゴリの横スクロール位置を維持するスクリプト
 */
document.addEventListener("DOMContentLoaded", function() {
    const categoryWrap = document.getElementById("categoryWrap");
    
    if (categoryWrap) {
        // 1. localStorageに保存されたスクロール位置があれば復元する
        const savedScrollLeft = localStorage.getItem("categoryScrollLeft");
        if (savedScrollLeft !== null) {
            categoryWrap.scrollLeft = parseInt(savedScrollLeft, 10);
        }

        // 2. ユーザーが横スクロールするたびに、現在の位置を保存する
        categoryWrap.addEventListener("scroll", function() {
            localStorage.setItem("categoryScrollLeft", categoryWrap.scrollLeft);
        });
        
        // 3. カテゴリボタンがクリックされた瞬間の正確な位置を確実に保存する
        const inputs = categoryWrap.querySelectorAll("input[type='submit']");
        inputs.forEach(function(input) {
            input.addEventListener("click", function() {
                localStorage.setItem("categoryScrollLeft", categoryWrap.scrollLeft);
            });
        });
    }
});
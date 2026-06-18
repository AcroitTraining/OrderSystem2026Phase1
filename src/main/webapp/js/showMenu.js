/**
 * カテゴリの横スクロール位置を完全に制御するスクリプト
 */
document.addEventListener("DOMContentLoaded", function() {
    const categoryWrap = document.getElementById("categoryWrap");
    
    if (categoryWrap) {
        // --- 最重要：画面の描画が完全に終わった後にスクロール位置を復元する ---
        const savedScrollLeft = localStorage.getItem("categoryScrollLeft");
        if (savedScrollLeft !== null) {
            // ブラウザの描画タイミングのズレを防ぐため、少しだけタイミングを遅らせて実行する
            requestAnimationFrame(function() {
                setTimeout(function() {
                    categoryWrap.scrollLeft = parseInt(savedScrollLeft, 10);
                }, 50); // 50ミリ秒だけ待ってから位置を完全に固定
            });
        }

        // 2. ユーザーが横スクロールするたびに、現在の位置を常に保存する
        categoryWrap.addEventListener("scroll", function() {
            localStorage.setItem("categoryScrollLeft", categoryWrap.scrollLeft);
        });
        
        // 3. カテゴリボタン（お好み焼き等）をクリックした瞬間の位置を確実に保存する
        const categoryForm = categoryWrap.querySelector("form");
        if (categoryForm) {
            categoryForm.addEventListener("submit", function() {
                localStorage.setItem("categoryScrollLeft", categoryWrap.scrollLeft);
            });
        }
    }

    // 4. フッターのボタン（履歴・お会計 / 注文リスト）を押して別画面に行くときは記憶を消す
    const footerForms = document.querySelectorAll("footer form");
    footerForms.forEach(function(form) {
        form.addEventListener("submit", function() {
            localStorage.removeItem("categoryScrollLeft");
        });
    });
});

// 5. カート画面や履歴画面から、ブラウザの「←（戻る）」でこの画面に戻ってきたときの処理
window.addEventListener("pageshow", function(event) {
    const categoryWrap = document.getElementById("categoryWrap");
    if (!categoryWrap) return;

    const navEntries = performance.getEntriesByType("navigation");
    if (navEntries.length > 0) {
        const navType = navEntries[0].type;
        
        // ブラウザの「戻る」ボタンで戻ってきた場合
        if (navType === "back_forward") {
            const referrer = document.referrer;
            // 商品詳細（ItemDetails）から戻ってきた場合「以外」は完全にリセットする
            if (!referrer.includes("ItemDetails") && !referrer.includes("itemDetails")) {
                localStorage.removeItem("categoryScrollLeft");
                categoryWrap.scrollLeft = 0;
            }
        }
    }
});
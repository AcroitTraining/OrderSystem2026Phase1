
/*        
// 一定時間ごとにセッションの状態をチェック
setInterval(function() {
    fetch('OrderListServlet') 
        .then(response => response.text())
        .then(status => {
            if (status.trim() === 'invalid') {
                // セッションが切れていたらエラー画面へ遷移
                window.location.href = 'error.jsp'; 
                console.log("invalid反応")
            }
        })
        .catch(error => console.error('通信エラー:', error));
}, 10000); // 10000ミリ秒 = 10秒*/

document.addEventListener("DOMContentLoaded", function () {
    
    // =================================================================
    // 削除確認ポップアップの制御
    // =================================================================
    const deleteForms = document.querySelectorAll('.delete-form');
    const deleteModal = document.getElementById('deleteModal');
    const confirmDeleteBtn = document.getElementById('confirmDelete');
    const cancelDeleteBtn = document.getElementById('cancelDelete');
    let targetedForm = null; // どの商品のゴミ箱が押されたかを保持する変数

    // すべてのゴミ箱フォームに対してイベントを設定
    deleteForms.forEach(form => {
        form.addEventListener('submit', function (e) {
            e.preventDefault(); // 一旦、Java（サーブレット）への即時リダイレクトをブロック
            targetedForm = this; // クリックされたフォームを保管
            deleteModal.classList.add('active'); // ポップアップを展開
        });
    });

    // 「いいえ」が押されたら閉じる
    cancelDeleteBtn.addEventListener('click', function () {
        deleteModal.classList.remove('active');
        targetedForm = null;
    });

    // 「はい」が押されたら対象のフォームをサブミット（OrderRemoveServletを実行）
    confirmDeleteBtn.addEventListener('click', function () {
        if (targetedForm) {
            targetedForm.submit();
        }
    });


    // =================================================================
    // 注文確定確認ポップアップの制御
    // =================================================================
    const orderBtn = document.getElementById('orderBtn');
    const orderModal = document.getElementById('orderModal');
    const confirmOrderBtn = document.getElementById('confirmOrder');
    const cancelOrderBtn = document.getElementById('cancelOrder');

    if (orderBtn) {
        orderBtn.addEventListener('click', function (e) {
            e.preventDefault(); // 通常遷移をストップ
            orderModal.classList.add('active'); // ポップアップを展開
        });
    }

    // 「いいえ」が押されたら閉じる
    cancelOrderBtn.addEventListener('click', function () {
        orderModal.classList.remove('active');
    });

    // 「はい」が押されたら、本来の宛先である注文完了サーブレット（OrderCompleteServlet）に直接転送
    confirmOrderBtn.addEventListener('click', function () {
        location.href = 'OrderCompleteServlet';
    });
});
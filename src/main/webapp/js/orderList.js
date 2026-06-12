
        
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
}, 10000); // 10000ミリ秒 = 10秒
// ajax-loader.js
class AutoRefresher {
    /**
     * @param {string} servletUrl - 通信先サーブレットのURL
     * @param {string} targetId - 書き換えたいHTML要素のID
     * @param {Object} baseParams - 送信したい基本パラメータ（Object形式）
     * @param {number} intervalMs - 更新間隔（ミリ秒）
     */
    constructor(servletUrl, targetId, baseParams = {}, intervalMs = 10000) {
        this.servletUrl = servletUrl;
        this.targetId = targetId;
        this.baseParams = baseParams;
        this.intervalMs = intervalMs;
    }

    // 自動更新を開始するメソッド
    start() {
        setInterval(() => this.refresh(), this.intervalMs);
    }

    // 通信を実行して要素を書き換える
    refresh() {
        const params = new URLSearchParams();
        
        // 渡されたパラメータをすべてセット
        for (const [key, value] of Object.entries(this.baseParams)) {
            params.append(key, value);
        }
        // 共通で「これはAjax通信だよ」という目印を送る
        params.append('ajax', 'true');

        fetch(this.servletUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params.toString()
        })
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.text(); // サーブレットから返ってきた最新テキスト/HTML
        })
        .then(htmlOrText => {
            const targetEl = document.getElementById(this.targetId);
            if (targetEl) {
                targetEl.innerHTML = htmlOrText; // 指定されたエリアを上書き
            }
        })
        .catch(error => console.error('Auto refresh failed:', error));
    }
}

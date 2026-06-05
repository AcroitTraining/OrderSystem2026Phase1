function updateScreenSize() {
            // 画面の幅と高さを取得
            const width = window.innerWidth;
            const height = window.innerHeight;
            
            // 取得したサイズをHTMLに表示
            document.getElementById('screen-size').textContent = width + ' x ' + height + ' px';
        }

        // ページ読み込み時に実行
        window.onload = updateScreenSize;

        // 画面の向きが変わった（縦・横の切り替え）時にも更新
        window.onresize = updateScreenSize;
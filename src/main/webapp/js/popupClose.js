// ポップアップを閉じる関数
    function closeModal() {
      const modal = document.getElementById('modalOverlay');
      if (modal) {
        modal.style.opacity = '0';
        setTimeout(() => {
          modal.classList.remove('active');
        }, 300);
      }
    }
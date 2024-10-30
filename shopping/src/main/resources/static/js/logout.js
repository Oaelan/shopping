//로그아웃 버
let logoutButton = document.getElementById("logout_container");

// 페이지 로드 시 이벤트 리스너 등록
document.addEventListener("DOMContentLoaded", function () {
	setupLogoutButton();
});

// 로그아웃 버튼 클릭 이벤트 처리 함수
function setupLogoutButton() {
    logoutButton.addEventListener('click', handleLogout);
}

// 로그아웃 요청 처리
async function handleLogout() {
    let accessToken = localStorage.getItem('Authorization');

    try {
        let response = await axios.post('/logout', {}, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        });

        localStorage.removeItem('Authorization');
        alert('로그아웃 되었습니다.');
		
		// 로그아웃 후 이벤트 리스너 제거
		logoutButton.removeEventListener('click', handleLogout);
        window.location.href = '/';
    } catch (error) {
        console.error('Error:', error);
        alert('로그아웃에 실패했습니다. 다시 시도해주세요.');
    }
}

// 폼 요소를 변수에 저장
let loginForm = document.querySelector('#loginForm');
let logoutButton = document.getElementById("logoutButton");

// 페이지 로드 시 이벤트 리스너 등록
document.addEventListener("DOMContentLoaded", function () {
    setupLoginForm();
    setupLogoutButton();
});

// 로그인 폼 제출 이벤트 처리 함수
function setupLoginForm() {
    loginForm.addEventListener('submit', handleLogin);
}

// 로그아웃 버튼 클릭 이벤트 처리 함수
function setupLogoutButton() {
    logoutButton.addEventListener('click', handleLogout);
}

// 로그인 요청 처리
async function handleLogin(event) {
    event.preventDefault(); // 기본 새로고침 방지

    let email = loginForm.querySelector('input[name="email"]').value;
    let password = loginForm.querySelector('input[name="password"]').value;

    let loginData = { email, password };

    try {
        let response = await axios.post('/login', loginData, {
            headers: { 'Content-Type': 'application/json' }
        });

        let accessToken = response.headers['authorization'].replace('Bearer ', '');
        localStorage.setItem('Authorization', accessToken);
        console.log('로그인 성공:', accessToken);

        window.location.href = "/";
    } catch (error) {
        console.error('Error:', error);
        alert('로그인에 실패했습니다. 다시 시도해주세요.');
    }
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
        window.location.href = '/';
    } catch (error) {
        console.error('Error:', error);
        alert('로그아웃에 실패했습니다. 다시 시도해주세요.');
    }
}

document.addEventListener("DOMContentLoaded", function() {
	showLoginBorLogOutB();
	loginType();	
});
// 로그인한 사용자 정보를 화면에 표시하는 함수

async function fetchUserInfo(accessToken) {
	try {
		const response = await axios.post('/api/user/login/userInfo', {}, {
			headers: {
				'Content-Type': 'application/json',
				'Authorization': `Bearer ${accessToken}` // Authorization 헤더에 Access Token 추가
			}
		});

		if (response.status === 200) {
			const email = response.data; // 응답 본문에서 이메일 정보 추출
			//document.getElementById("loginTrueOrFalse").innerText ="로그아웃"
			//document.getElementById("userEmail").innerText = `현재 로그인된 사용자: ${email}`; // 이메일 표시
		} else {
			throw new Error('사용자 정보를 가져오는 데 실패했습니다.');
		}
	} catch (error) {
		console.error('에러 발생:', error);
		document.getElementById("userEmail").innerText = `사용자 정보를 가져오는 데 실패했습니다.`;
	}
}

// 소셜 로그인 후 Access Token을 가져오는 함수
async function fetchSocialLoginToken() {
	try {
		const response = await axios.post('/api/user/login/socialLogined', {}, {
			headers: {
				'Content-Type': 'application/json'
			}
		});

		if (response.status === 200) {
			const accessToken = response.headers['authorization'].replace('Bearer ', '');
			localStorage.setItem('Authorization', accessToken); // 로컬 스토리지에 저장된 토큰
			console.log("소셜 로그인 후 발급받은 액세스 토큰을 저장했습니다.");
			window.location.href = "/"; 
			return accessToken; // 토큰 반환
		} else {
			throw new Error('사용자 정보를 가져오는 데 실패했습니다.');
		}
	} catch (error) {
		console.error('에러 발생:', error);
		return null;
	}
}

// 로그인 유형에 따라 사용자 정보를 불러오는 함수
async function loginType() {
	let accessToken = localStorage.getItem('Authorization'); // 로컬 스토리지에 저장된 토큰

	if (accessToken) {
		console.log("accessToken 존재함");
		await fetchUserInfo(accessToken); // 사용자 정보 불러오기
	} else {
		console.log("accessToken 존재하지 않음");
		accessToken = await fetchSocialLoginToken(); // 소셜 로그인 후 Access Token 가져오기
		if (accessToken) {
			await fetchUserInfo(accessToken); // 사용자 정보 불러오기
		}
	}
}

function showLoginBorLogOutB() {
	let accessToken = localStorage.getItem('Authorization'); // 로컬 스토리지에 저장된 토큰
	if (accessToken) {
		console.log(accessToken);
		// 로그인 후
		showLogout();
	} else {
		// 로그인 전
		showLogin();
	}
}

//로그인 전후에 따라 메인 헤더의 버튼 보이게 안보이게 하기
function showLogin() {
	//액세스 토큰 존재하지 않을 시 로그인 버튼이 보이게
	document.getElementById("login_container").style.display = "block";
	document.getElementById("logout_container").style.display = "none";

}

function showLogout() {
	//액세스 토큰 존재할 때 로그아웃 버튼이 보이게
	document.getElementById("login_container").style.display = "none";
	document.getElementById("logout_container").style.display = "block";
}
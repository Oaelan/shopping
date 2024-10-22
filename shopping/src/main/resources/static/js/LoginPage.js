function backMainpage() {
    window.location.href = '../index.html';
}


let vh = window.innerHeight * 0.01; // 초기 vh 값 계산
document.documentElement.style.setProperty("--vh", `${vh}px`); // CSS 변수로 설정

window.addEventListener("resize", () => {
    // console.log("resizing"); // 리사이즈 이벤트 발생 시 로그 출력
    let vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty("--vh", `${vh}px`); // CSS 변수로 업데이트
});

const pwInput = document.getElementById('password_input');
const inputTypeConversionBtn = document.getElementById('view_pw');

inputTypeConversionBtn.addEventListener("change", () => {
    //비밀번호 보기 스크립트
    if (inputTypeConversionBtn.checked) {
        pwInput.setAttribute("type", "text");
    } else {
        pwInput.setAttribute("type", "password");
    };
});

const imageSection = document.querySelector('.loginform_imageSection')
//회원가입 폼 변경 애니메이션, 색상변경
const mainText = document.querySelector('.main_text');
//메인 타이틀 로그인 <-> 회원가입 폼 변경 시 문구 변경
const subText = document.querySelector('.sub_text');
//서브 타이틀 로그인 <-> 회원가입 폼 변경 시 문구 변경
const iconColor = document.querySelector('.goBack_btn_icon');
const iconColor2 = document.querySelector('.arrow_head_child1');
const iconColor3 = document.querySelector('.arrow_head_child2');
const iconColor4 = document.querySelector('.arrow_body');
const iconColor5 = document.querySelector('.btn_text');
//아이콘 색상 변경
const loginFormImage = document.querySelector('.view_login_image');
const membershipFormImage = document.querySelector('.view_membership_image');
let switchingTrigger = true
//true = 로그인 폼, false = 회원가입 폼

function switchingForm() {
    if (switchingTrigger) {
        imageSection.style.left = '0';
        imageSection.style.backgroundColor = '#31514F';
        switchingTrigger = false;
        mainText.innerHTML = '로그인';
        subText.innerHTML = '회원에게만 제공되는 다양한 혜택을 누려보세요 :&#41;';
        loginFormImage.style.display = 'none';
        membershipFormImage.style.display = 'block';
        iconColor.style.border = '4px solid #fff';
        iconColor2.style.backgroundColor = '#fff';
        iconColor3.style.backgroundColor = '#fff';
        iconColor4.style.backgroundColor = '#fff';
        iconColor5.style.color = '#fff'
    } else {
        imageSection.style.left = '50%';
        imageSection.style.backgroundColor = '#514031';
        switchingTrigger = true;
        mainText.innerHTML = '회원가입';
        subText.innerHTML = '가입을 환영합니다! 특별한 경험을 기대해 주세요 XD';
        loginFormImage.style.display = 'block';
        membershipFormImage.style.display = 'none';
        iconColor.style.border = '4px solid #000';
        iconColor2.style.backgroundColor = '#000';
        iconColor3.style.backgroundColor = '#000';
        iconColor4.style.backgroundColor = '#000';
        iconColor5.style.color = '#000'
    }
};

switchingForm()

const loginIdInput = document.getElementById('id_input');
const loginPasswordInput = document.getElementById('password_input');
const membershipEmailInput = document.getElementById('membership_email_input');
const membershipPasswordInput = document.getElementById('membership_pw_input');
const rePwInput = document.getElementById('re_pw_input');

const loginIdCapsLock = document.getElementById('login_id_capsLock_warning');
const loginPasswordCapsLock = document.getElementById('login_pw_capsLock_warning');
const membershipEmailCapsLock = document.getElementById('email_capsLock_warning');
const membershipPasswordCapsLock = document.getElementById('membership_pw_capsLcok_warning');
const rePwCapsLock = document.getElementById('membership_re_pw_capsLcok_warning');

const passwordWarningMessage = document.querySelector('.pw_necessary_condition');

function detectCapsLock(event, warningElement) {
    const isCapsLockOn = event.getModifierState('CapsLock');
    warningElement.style.display = isCapsLockOn ? 'block' : 'none';
    warningElement.style.opacity = isCapsLockOn ? '1' : '0';
}

function hideCapsLockWarning(warningElement) {
    warningElement.style.display = 'none';
}

membershipPasswordInput.addEventListener("focus", () => {
    passwordWarningMessage.style.height = '100%';
    passwordWarningMessage.style.opacity = '1';

});

membershipPasswordInput.addEventListener("focusout", () => {
    passwordWarningMessage.style.height = '0';
    passwordWarningMessage.style.opacity = '0';

});

loginIdInput.addEventListener('keydown', (event) => detectCapsLock(event, loginIdCapsLock));
loginIdInput.addEventListener('focusout', () => hideCapsLockWarning(loginIdCapsLock));

loginPasswordInput.addEventListener('keydown', (event) => detectCapsLock(event, loginPasswordCapsLock));
loginPasswordInput.addEventListener('focusout', () => hideCapsLockWarning(loginPasswordCapsLock));

membershipEmailInput.addEventListener('keydown', (event) => detectCapsLock(event, membershipEmailCapsLock));
membershipEmailInput.addEventListener('focusout', () => hideCapsLockWarning(membershipEmailCapsLock));

membershipPasswordInput.addEventListener('keydown', (event) => detectCapsLock(event, membershipPasswordCapsLock));
membershipPasswordInput.addEventListener('focusout', () => hideCapsLockWarning(membershipPasswordCapsLock));

rePwInput.addEventListener('keydown', (event) => detectCapsLock(event, rePwCapsLock));
rePwCapsLock.addEventListener('focusout', () => hideCapsLockWarning(rePwCapsLock));

const warningMessages = [
    document.querySelector('.warning_message_1'),
    document.querySelector('.warning_message_2'),
    document.querySelector('.warning_message_3'),
    document.querySelector('.warning_message_4'),
    document.querySelector('.warning_message_5'),
    document.querySelector('.warning_message_6'),
];

const warningMessagesContainers = [
    document.querySelector('.pw_necessary_1'),
    document.querySelector('.pw_necessary_2'),
    document.querySelector('.pw_necessary_3'),
    document.querySelector('.pw_necessary_4'),
    document.querySelector('.pw_necessary_5'),
];

document.querySelector('.membership_form').addEventListener('submit', function(event){
    event.preventDefault();


    const password = membershipPasswordInput.value;
    const confirmPassword = rePwInput.value;
    const firstName = document.querySelector('.firstName_input');
    const lastName = document.querySelector('.lastName_input');

    // 비밀번호 유효성 검사
    const isPasswordValid = validatePassword(password);
    const emailValid = validateEmail(email);

    // 비밀번호 일치 여부 확인
    if (isPasswordValid && password === confirmPassword && emailValid && firstName !== '' && lastName !=='') {
        // 모든 조건이 충족되면 폼을 제출
        // this.submit();
        console.log("submit");
    }else{
        console.log("submitError");
    }
});

const emailNecessaryWarningMessage = document.querySelector('.email_necessary_warning_message');

// 이메일 유효성 검사 경고 메시지 제어 이벤트리스너
membershipEmailInput.addEventListener('focus', () =>{
    emailNecessaryWarningMessage.style.height = '100%';
    emailNecessaryWarningMessage.style.opacity = '1';
});

membershipEmailInput.addEventListener('focusout', () =>{
    emailNecessaryWarningMessage.style.height = '0';
    emailNecessaryWarningMessage.style.opacity = '0';
});

// 이메일 유효성 검사
function validateEmail(email) {
    const emailCondition = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email);

    if (emailCondition) {
        emailNecessaryWarningMessage.style.color = '#62b62e'; // 초록색 통과 메시지
        emailNecessaryWarningMessage.innerHTML = '멋진 이메일이네요!';
    } else {
        emailNecessaryWarningMessage.style.color = '#ff0000'; // 빨간색 불통 메시지
        emailNecessaryWarningMessage.innerHTML = '이메일이 유효하지 않아요.';
    }
}

// 이메일 입력 시 유효성 검사 이벤트 리스너 추가
membershipEmailInput.addEventListener('input', function(event) {
    validateEmail(event.target.value); // 입력값을 유효성 검사에 전달
});

const verificationCode = document.getElementById('verificationCode_input');

verificationCode.addEventListener('input', ()=>{
    verificationCode.value = verificationCode.value.replace(/[^0-9]/g, '');
});
   

function validatePassword(password) {
    // 각 조건 체크
    const lengthCondition = password.length >= 8 && password.length <= 20; // 1. 길이
    const upperCaseCondition = /[A-Z]/.test(password);                     // 2. 대문자
    const lowerCaseCondition = /[a-z]/.test(password);                     // 3. 소문자
    const numberCondition = /\d/.test(password);                            // 4. 숫자
    const specialCharCondition = /[@$%^]/.test(password);                  // 5. 특수 문자
    const validCharsCondition = /^[A-Za-z0-9@$%^]*$/.test(password);       // 6. 허용된 문자만 사용

    // 각 조건에 맞는 메시지 업데이트
    const conditions = [lengthCondition, upperCaseCondition, lowerCaseCondition, numberCondition, specialCharCondition, validCharsCondition];

    for (let i = 0; i < conditions.length; i++) {
        if (conditions[i]) {
            warningMessages[i].style.color = '#62b62e'; // 조건이 맞으면 초록색
        } else {
            warningMessages[i].style.color = '#ff0000'; // 조건이 맞지 않으면 빨간색
        }
    }

    // 모든 조건(1~5번)이 만족하고, 6번 조건도 만족하면 경고 메시지 초록색
    const allConditionsMet = conditions.slice(0, 5).every(condition => condition) && validCharsCondition;

    // 6번 조건을 포함하여 모든 조건을 만족할 경우에만 녹색으로 변경
    if (allConditionsMet) {
        warningMessages[5].style.color = '#62b62e'; // 모든 조건이 만족할 때
        warningMessages[5].innerHTML = '완벽해요!';
        warningMessagesContainers.forEach(container => container.style.display = 'none'); // 모든 컨테이너 숨기기
        rePwInput.disabled = false;
        rePwSection.style.display = 'block';
    } else {
        warningMessages[5].style.color = '#ff0000'; // 조건이 만족하지 않으면
        warningMessages[5].innerHTML = '위 조건 이외의 문자는 사용 할 수 없어요.';
        warningMessagesContainers.forEach(container => container.style.display = 'flex'); // 모든 컨테이너 보이기
        rePwInput.disabled = true;
        rePwSection.style.display = 'none';
    }

    return allConditionsMet;
}

membershipPasswordInput.addEventListener('input', (event) => {
    const password = event.target.value;
    validatePassword(password);
});

const rePwWarningMessage = document.querySelector('.re_pw_warning_message'); // 일치 여부를 보여줄 경고 메시지 요소
const rePwSection = document.querySelector('.re_pw')

membershipPasswordInput.addEventListener("focus", () => {
    passwordWarningMessage.style.height = '100%';
    passwordWarningMessage.style.opacity = '1';

    // 비밀번호 확인 메시지 숨기기
    rePwWarningMessage.style.display = 'none'; // 비밀번호 입력란에 포커스할 때 비밀번호 확인 경고 메시지 숨김
});

// focus 이벤트로 경고 메시지 표시
rePwInput.addEventListener('focus', () => {
    rePwWarningMessage.style.display = 'flex'; // 비밀번호 확인란에 포커스할 때만 표시
});

// input 이벤트로 비밀번호 일치 여부 실시간 검사
rePwInput.addEventListener('input', validatePasswordMatch);
membershipPasswordInput.addEventListener('input', validatePasswordMatch);

function validatePasswordMatch() {
    const password = membershipPasswordInput.value;
    const confirmPassword = rePwInput.value;

    if (password === confirmPassword) {
        rePwWarningMessage.style.color = '#62b62e'; // 초록색
        rePwWarningMessage.innerHTML = '비밀번호가 일치해요.';
    } else {
        rePwWarningMessage.style.color = '#ff0000'; // 빨간색
        rePwWarningMessage.innerHTML = '비밀번호가 일치하지 않아요.';
    }
}

// focus 이벤트로 경고 메시지 표시
rePwInput.addEventListener('focus', () => {
    rePwWarningMessage.style.opacity = '1';
});

// focusout 이벤트로 경고 메시지 숨기기
rePwInput.addEventListener('focusout', () => {
    rePwWarningMessage.style.opacity = '0';
});

// input 이벤트로 비밀번호 일치 여부 실시간 검사
rePwInput.addEventListener('input', validatePasswordMatch);
membershipPasswordInput.addEventListener('input', validatePasswordMatch);
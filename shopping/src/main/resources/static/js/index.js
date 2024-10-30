let vh = window.innerHeight * 0.01; // 초기 vh 값 계산
document.documentElement.style.setProperty("--vh", `${vh}px`); // CSS 변수로 설정

window.addEventListener("resize", () => {
    // console.log("resizing"); // 리사이즈 이벤트 발생 시 로그 출력
    let vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty("--vh", `${vh}px`); // CSS 변수로 업데이트
});

// 로그인 버튼 클릭시 로그인 페이지로 이동
document.querySelector('.login_container').addEventListener("click" , function(){
    window.location.href = "http://localhost:8080/login";
})

const  carouselContainer = document.querySelector('.carosel_image_container');
const imageIndex =  carouselContainer.children;

let currentIndex = 0; // 현재 인덱스 초기화

const caroselTotalWidthIndex = imageIndex.length - 1;
const navigaterContainer = document.querySelector('.navigater_container');

// 모든 슬라이드에 대한 <li> 요소를 생성하여 navigater_container에 추가하는 함수
function createNavigationItems() {
    // navigater_container 초기화
    navigaterContainer.innerHTML = ''; // 기존의 <li> 요소를 지웁니다.

    // 각 이미지에 대해 <li> 요소 생성
    for (let i = 0; i < imageIndex.length; i++) {
        const li = document.createElement('li');

        // div 요소 생성 및 클래스 설정
        const div = document.createElement('div');
        div.className = `nav_icon_${i + 1}`; // 클래스 이름 설정

        // div를 li에 추가
        li.appendChild(div);
        // li를 navigater_container에 추가
        navigaterContainer.appendChild(li);
    }
}

// 슬라이드가 로드될 때 호출
createNavigationItems();

// 네비게이션 아이템의 스타일을 업데이트하는 함수
function updateNavigationStyles() {
    const navigaterItems = navigaterContainer.children; // 네비게이터의 모든 li 요소를 가져옴

    // 모든 네비게이터 아이템에 대해 기본 스타일을 적용
    for (let i = 0; i < navigaterItems.length; i++) {
        navigaterItems[i].style.opacity = '0.5'; // 기본적으로 반투명
    }

    // 현재 인덱스에 해당하는 아이템을 강조
    if (navigaterItems[currentIndex]) {
        navigaterItems[currentIndex].style.opacity = '1'; // 현재 아이템은 완전 불투명
    }
}

updateNavigationStyles()


function nextSlide() {
    if (currentIndex >= caroselTotalWidthIndex) {
        // 현재 인덱스가 마지막 슬라이드에 도달했을 때
        currentIndex = 0; // 첫 슬라이드로 이동
         carouselContainer.style.transform = `translateX(0)`; // 위치 초기화
        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                updateCarousel(); // 첫 슬라이드로 업데이트
                updateNavigationStyles();
                resetAutoRevolvCooldown();
            });
        });
    } else {
        currentIndex++;
        updateCarousel();
        updateNavigationStyles();
        resetAutoRevolvCooldown();
    }
}

function prevSlide() {
    if (currentIndex <= 0) {
        // 첫 슬라이드에서 이전 버튼을 클릭했을 때
        currentIndex = caroselTotalWidthIndex; // 마지막 슬라이드로 이동
         carouselContainer.style.transform = `translateX(-${(currentIndex) * 100}vw)`; // 위치 초기화
        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                updateCarousel(); // 마지막 슬라이드로 업데이트
                updateNavigationStyles();
                resetAutoRevolvCooldown();
            });
        });
    } else {
        currentIndex--;
        updateCarousel();
        updateNavigationStyles();
        resetAutoRevolvCooldown();
    }
}

function updateCarousel() {
    // 슬라이드 이동: currentIndex * 100vw로 변경
    carouselContainer.style.transform = `translateX(-${currentIndex * 100}vw)`;
    carouselContainer.style.animation = 'none'; // 초기화
    carouselContainer.offsetHeight; // 강제로 리플로우(reflow)
    carouselContainer.style.animation = 'fade_animation 0.5s ease-in-out';
}


let revolvIndex = true; //슬라이드 자동 실행 트리거 - true = 실행 / false = 멈춤
let startAutoRevolv;

const caroselIconON = document.querySelector('.on');
const caroselIconOFF = document.querySelector('.off');

function caroselAutorevolv() {
    startAutoRevolv = setInterval(() => {
        nextSlide();
    }, 5000);
}

caroselAutorevolv();

function autoRevolvControl() {
    if (revolvIndex) {
        clearInterval(startAutoRevolv);
        revolvIndex = false;
        console.log("Revolv OFF");
        caroselIconON.style.display = '';
        caroselIconOFF.style.display = 'none';
    } else {
        caroselAutorevolv();
        revolvIndex = true;
        console.log("Strat Revolving");
        caroselIconON.style.display = 'none';
        caroselIconOFF.style.display = '';
    }
}

function resetAutoRevolvCooldown() {
    clearInterval(startAutoRevolv);
    caroselAutorevolv();
    if(revolvIndex == false){
        revolvIndex = true
        caroselIconON.style.display = 'none';
        caroselIconOFF.style.display = '';
    }
};


// 드래그 상태와 관련된 변수
let dragController = false;
let startPosition = 0;
let currentTranslate = 0;
let prevTranslate = 0;
let animationID = 0;

const itemPreview = document.querySelector('.item_container');
const itemPreviewScroll = document.querySelector('.preview_scroll');
const scrollHandle = document.querySelector('.scroll_handle');
const children = itemPreview.children;
const gap = 20; // 요소 간의 간격 설정

// 자식 요소의 총 너비 계산
let totalWidth = 0;
for (let i = 0; i < children.length; i++) {
    totalWidth += children[i].offsetWidth;
}
totalWidth += (children.length - 1) * gap; // 마지막 요소 뒤의 간격은 포함하지 않음

// 드래그 시작 함수
function startDrag(e) {
    dragController = true;
    startPosition = e.type.includes('mouse') ? e.clientX : e.touches[0].clientX; // 마우스 또는 터치 위치
    animationID = requestAnimationFrame(animation);
    itemPreview.style.transition = `none`;
    scrollHandle.style.transition = `none`;
}

// 드래그 종료 함수
function endDrag() {
    dragController = false;
    cancelAnimationFrame(animationID);
    prevTranslate = currentTranslate; // 드래그 종료 시 현재 위치 저장
}

// 드래그 중인 애니메이션 함수
function animation() {
    setitemPreview(); // 드래그 중 위치 업데이트
    if (dragController) {
        requestAnimationFrame(animation);
    }
}

// 이벤트 리스너 등록
itemPreview.addEventListener('mousedown', startDrag);
itemPreview.addEventListener('mouseup', endDrag);
itemPreview.addEventListener('mousemove', (e) => {
    if (dragController) {
        const currentPosition = e.clientX;
        currentTranslate = prevTranslate + (currentPosition - startPosition);
    }
});

// 터치 이벤트 리스너 추가
itemPreview.addEventListener('touchstart', startDrag);
itemPreview.addEventListener('touchend', endDrag);
itemPreview.addEventListener('touchmove', (e) => {
    if (dragController) {
        const currentPosition = e.touches[0].clientX; // 터치 위치
        currentTranslate = prevTranslate + (currentPosition - startPosition);
        itemPreview.style.transition = `none`;
        scrollHandle.style.transition = `none`;
    }
});

function setitemPreview() {
    // 범위 제한: currentTranslate가 0px 미만이거나 totalWidth를 초과하지 않도록 설정
    currentTranslate = Math.max(Math.min(currentTranslate, 0), -totalWidth + itemPreview.offsetWidth);

    itemPreview.style.transform = `translateX(${currentTranslate}px)`; // X축 이동 적용
    updateScrollHandle(); // 스크롤 핸들 업데이트
}

function updateScrollHandle() {
    // 스크롤 핸들의 위치를 캐러셀 이동 비율에 따라 조정
    const maxScrollHandleWidth = itemPreviewScroll.offsetWidth; // 스크롤 핸들이 최대 너비
    const scrollPercentage = (currentTranslate / (-totalWidth + itemPreview.offsetWidth)); // 캐러셀의 이동 비율
    const scrollHandleWidth = maxScrollHandleWidth * scrollPercentage; // 스크롤 핸들의 새로운 너비
    scrollHandle.style.width = `${scrollHandleWidth}px`; // 너비 조정
}

function item_preview_control(direction) {
    const item_preview_child_width = document.querySelector('.item_preview_container').offsetWidth;

    // direction에 따라 currentTranslate 업데이트 (next: 음수, prev: 양수)
    currentTranslate += direction * (item_preview_child_width + gap);

    // 범위 제한: currentTranslate가 0px 미만이거나 totalWidth를 초과하지 않도록 설정
    currentTranslate = Math.max(Math.min(currentTranslate, 0), -totalWidth + itemPreview.offsetWidth);

    // X축 이동 적용
    itemPreview.style.transform = `translateX(${currentTranslate}px)`;
    itemPreview.style.transition = `0.5s`;
    scrollHandle.style.transition = `0.5s`;

    // 스크롤 핸들 업데이트
    updateScrollHandle();
}

// 다음으로 이동
function item_preview_control_next() {
    item_preview_control(-1); // 음수 방향으로 이동
}

// 이전으로 이동
function item_preview_control_prev() {
    item_preview_control(1); // 양수 방향으로 이동
}


function animation() {
    setitemPreview();
    if (dragController) {
        requestAnimationFrame(animation);
    }
}
// IntersectionObserver 생성
const observer = new IntersectionObserver(
    (entries) => {
        entries.forEach(entry => {
            // viewport에 보일 때
            if (entry.isIntersecting) {
                entry.target.style.opacity = 1;
                // entry.target.style.transform = 'translateY(0px)';
            }
        });
    },
    {
        threshold: 1
    }
);

// 감시할 요소 선택
let ecoProjectEvent = document.querySelector('.eco_project');
ecoProjectEvent.style.opacity = 0;
// ecoProjectEvent.style.transform = 'translateY(-100px)';
ecoProjectEvent.style.transition = 'all 0.75s';

// 요소 관찰 시작
observer.observe(ecoProjectEvent);



//Chrome에서 북 마크 바에 따라 height값이 변동되어도 full Size 화면을 유지하기 위한 스크립트
let vh = window.innerHeight * 0.01; // 초기 vh 값 계산
document.documentElement.style.setProperty("--vh", `${vh}px`); // CSS 변수로 설정

window.addEventListener("resize", () => {
    // console.log("resizing"); // 리사이즈 이벤트 발생 시 로그 출력
    let vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty("--vh", `${vh}px`); // CSS 변수로 업데이트
});

// 로그인 클릭 시 로그인 페이지로 이동하기 위한 함수
document.querySelector('.login_container').addEventListener("click", function () {
    window.location.href = "http://localhost:8080/login";
})

// 캐러셀, 무한으로 회전하는 슬라이드 애니메이션을 위한 함수
const carouselContainer = document.querySelector('.carousel_image_container'); // 캐러셀의 배경이미지
const firstChild = carouselContainer.firstElementChild; // 캐러셀 슬라이드의 첫 번째 자식요소
const lastChild = carouselContainer.lastElementChild; // 캐러셀 슬라이드의 마지막 자식요소
const cloneFirst = firstChild.cloneNode(true); //캐러셀 슬라이드 첫 번째 자식요소와 같은 클론 노드
const cloneLast = lastChild.cloneNode(true); // 캐러셀 슬라이드 마지막 자식요소와 같은 클론 노드
const imageIndex = carouselContainer.children; // 캐러셀 배경 이미지의 자식요소, 현재 슬라이드가 몆 번째 슬라이드인지 확인하기 위한 요소
const slideSpeed = 500; //밀리초 단위의 슬라이드 재생 속도 100ms = 1초

let currentIndex = 1; // 현재 인덱스 초기화, 0은 클론이기 때문에 1이 첫 번째 슬라이드임

const carouselTotalWidthIndex = imageIndex.length; // 클론을 포함한 이미지 슬라이드의 자식 요소 개수.
const pagenationContainer = document.querySelector('.pagenation_container'); // 캐러셀 페이지네이션

carouselContainer.appendChild(cloneFirst); // 첫 번째 슬라이드 앞에 클론 슬라이드 추가.
carouselContainer.insertBefore(cloneLast, firstChild) // 마지막 슬라이드 뒤에 클론 슬라이드 추가

// 모든 슬라이드에 대한 <li> 요소를 생성하여 pagenation_container에 추가하는 함수
function createNavigationItems() {
    // pagenation_container 초기화
    pagenationContainer.innerHTML = ''; // 기존의 임시로 추가한 <li> 요소를 지움.
    carouselContainer.style.transition = `transform ${slideSpeed}ms`; //캐러셀 페이지가 변경 될 때 이동하는 속도, slideSpeed 변수로 속도 결정
    carouselContainer.style.transform = `translatex(-${(currentIndex) * 100}vw)`; // 슬라이드 width = 100vw이기 때문에 현재 currentIndex에 *100을 하여 한 칸씩 이동하도록 함

    //  슬라이드 개수에 따라 페이지네이션 <li>요소 생성
    for (let i = 0; i < imageIndex.length - 2; i++) { // 클론 슬라이드를 제외한 전체 슬라이드 개수 표시, 클론을 포함한 자식요소 개수 - 2
        const li = document.createElement('li');

        // div 요소 생성 및 클래스 설정
        const div = document.createElement('div');
        div.className = `nav_icon_${i + 1}`; // 클래스 이름 설정

        // div를 li에 추가
        li.appendChild(div);
        // li를 pagenation_container에 추가
        pagenationContainer.appendChild(li);
    }
}

// 슬라이드가 로드될 때 호출
createNavigationItems();


function updateCarousel() {
    // 슬라이드 이동
    carouselContainer.style.transform = `translateX(-${currentIndex * 100}vw)`;
}

// 네비게이션 아이템의 스타일을 업데이트하는 함수
const pagenationItems = pagenationContainer.children; // 페이지네이션의 모든 li 요소를 가져옴
function updateNavigationStyles() {
    // 모든 네비게이터 아이템에 대해 기본 스타일을 적용
    setTimeout(function () {
        for (let i = 0; i < pagenationItems.length; i++) {
            pagenationItems[i].style.opacity = '0.5'; // 현재 슬라이드를 제외한 페이지네이션은 투명도 0.5
        }

        // 현재 인덱스에 해당하는 아이템을 강조
        if (pagenationItems[currentIndex - 1]) {
            pagenationItems[currentIndex - 1].style.opacity = '1'; // 현재 슬라이드는 1로 설정
        }
    }, slideSpeed)//슬라이드 변경 시 페이지네이션 변경 딜레이
    // 버그로 인해 마지막 슬라이드에서 첫 번째 슬라이드 이동 시 slideSpeed만큼 딜레이가 있어서 설정함.
}
// 슬라이드가 로드될 때 페이지네이션 호출
updateNavigationStyles()

// 다음 슬라이드버튼을 눌렀을 때 슬라이드가 오른쪽으로 이동
function nextSlide() {
    // 다음 버튼 실행에 slideSpeed만큼 재사용 대기 시간 생성
    document.getElementById('next').onclick = 'null';
    setTimeout(function () {
        document.getElementById('next').onclick = nextSlide;
    }, slideSpeed);

    if (currentIndex >= carouselTotalWidthIndex) {
        // 현재 인덱스가 마지막 슬라이드에 도달했을 때
        carouselContainer.style.transform = `translatex(-${(currentIndex + 1) * 100}vw)`; // 마지막 슬라이드에서 첫 번째 슬라이드의 클론으로 이동
        updateNavigationStyles();//페이지네이션 업데이트

        // 마지막 슬라이드->클론 슬라이드->첫 번째 슬라이드로 자연스럽게 이동하기 위한 함수
        setTimeout(function () {
            carouselContainer.style.transition = 'none'; // 슬라이드 이동 시 애니메이션을 제거
            currentIndex = 1; // 첫 슬라이드로 이동
            carouselContainer.style.transform = `translatex(-${(currentIndex) * 100}vw)`; // 위치 초기화
            requestAnimationFrame(() => {
                updateCarousel(); // 첫 슬라이드로 업데이트
                resetAutoRevolvCooldown(); // 다음 버튼 클릭 시 슬라이드 자동 이동 대기시간 초기화
                setTimeout(() => {
                    carouselContainer.style.transition = `transform ${slideSpeed}ms`;
                }, 1); // 약간의 지연 시간(1ms)을 추가해 부드럽게 전환
            });
        }, slideSpeed - 1);
    } else {
        currentIndex++; // index + 1
        updateCarousel(); // 슬라이드 이동 실행
        updateNavigationStyles(); // 페이지네이션 업데이트
        resetAutoRevolvCooldown();   // 다음 버튼 클릭 시 슬라이드 자동 이동 대기시간 초기화
    }
}

// 이전 슬라이드 버튼을 눌렀을 때 슬라이드가 왼쪽으로 이동, 다음 슬라이드 버튼과 반대
function prevSlide() {
    document.getElementById('prev').onclick = 'null';
    setTimeout(function () {
        document.getElementById('prev').onclick = prevSlide;
    }, slideSpeed);
    if (currentIndex <= 1) {
        carouselContainer.style.transform = `translatex(-${(currentIndex - 1) * 100}vw)`;
        updateNavigationStyles();
        setTimeout(function () {
            carouselContainer.style.transition = 'none';
            currentIndex = carouselTotalWidthIndex;
            carouselContainer.style.transform = `translatex(-${(currentIndex) * 100}vw)`;
            requestAnimationFrame(() => {
                updateCarousel();
                resetAutoRevolvCooldown();
                setTimeout(() => {
                    carouselContainer.style.transition = `transform ${slideSpeed}ms`;
                }, 1);
            });
        }, slideSpeed - 1);
    } else {
        currentIndex--;
        updateCarousel();
        updateNavigationStyles();
        resetAutoRevolvCooldown();
    }
}


let revolvIndex = true; // 슬라이드 자동 실행 트리거 - true = 실행 / false = 멈춤

const carouselIconON = document.querySelector('.on');
const carouselIconOFF = document.querySelector('.off');

function carouselAutorevolv() {
    startAutoRevolv = setInterval(() => {
        nextSlide();
    }, 5000);
}

carouselAutorevolv();

function autoRevolvControl() {
    if (revolvIndex) {
        clearInterval(startAutoRevolv);
        revolvIndex = false;
        // console.log("Revolv OFF");
        carouselIconON.style.display = '';
        carouselIconOFF.style.display = 'none';
    } else {
        carouselAutorevolv();
        revolvIndex = true;
        // console.log("Strat Revolving");
        carouselIconON.style.display = 'none';
        carouselIconOFF.style.display = '';
    }
}

function resetAutoRevolvCooldown() {
    clearInterval(startAutoRevolv);
    carouselAutorevolv();
    if (revolvIndex == false) {
        revolvIndex = true
        carouselIconON.style.display = 'none';
        carouselIconOFF.style.display = '';
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
        threshold: 0.5
        // threshold 0~1까지 설정
        // 요소가 0%~100%만큼 보일때 실행
    }
);

// 감시할 요소 선택
let ecoProjectEvent = document.querySelector('.eco_project');
ecoProjectEvent.style.opacity = 0;
// ecoProjectEvent.style.transform = 'translateY(-100px)';
ecoProjectEvent.style.transition = 'all 0.75s';

// 요소 관찰 시작
observer.observe(ecoProjectEvent);
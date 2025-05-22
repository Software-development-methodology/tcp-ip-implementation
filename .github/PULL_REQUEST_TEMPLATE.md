<!-------------------------- Group Spike Root -------------------------->

<!-- Title: [Spike] issueX descrition -->
<!-- Label: Group spike -->


### 🌲 Header construction
- Resolve #9
---
### 🧩 포함된 브랜치 목록 (총 5)
- `bracn-name` #PR-Number
---
### ✅ 통합 목적
- description
---
### 🔎 주요 내용 요약

#### Something
- description

<!-------------------------- Individual Spike -------------------------->

<!-- Title: [Spike] issueX descrition -->
<!-- Label: Group Spike or Individual spike-->

### 🧪 Spike 목표
> #Issue-Number or description

### 🔨 작업 내용
#### Something
- description

### 🧹 기타
#### Something
- description


<!-------------------------- Feature -------------------------->

<!-- Title: [Feature] description -->
<!-- Label: enhancement -->
# Feature 이름 (ex. Data 추상화 및 abstract 작성)
- reference(ex. #Issue-Number, # PR-Number) or description

<!-- # Custom 항목 -->
---
# 클래스 다이어그램 ()
>[!note]
> Safari에서 깨집니다. Chrome으로 보시면 됩니다.
```mermaid

```
---
# 🤔 고민 했던 것

---
# 생각해볼 것
## Something
- description
## 아이디어 or 조금 미흡한 것
### ...
- description

<!-------------------------- Fix bug -------------------------->

<!-- Title: [Fix] description -->
<!-- Label: bug -->


## 🐞 Bug Fix PR

### 📌 관련 이슈
<!-- 해당 버그 이슈 번호를 연결. 없다면 description -->
- Fixes #123

---

### ❗ 버그 내용 요약
<!-- 어떤 문제가 발생했는지 요약 설명 -->
- (예) 로그인 시 유효하지 않은 토큰에도 페이지가 로드됨
- (예) 사용자 목록에서 null 포인터 발생

---

### ✅ 수정한 내용
<!-- 어떤 방식으로 버그를 해결했는지 구체적으로 작성 -->
- `AuthInterceptor`에서 토큰 null 체크 추가
- 예외 발생 시 401 응답 처리
- 테스트 코드 작성 (`AuthInterceptorTest`)

---

### 🧪 테스트 내역
- [x] 버그 재현 테스트 작성
- [x] 정상 동작 테스트 작성
- [x] 기존 테스트 모두 통과

---

### 📝 기타 참고 사항
<!-- 코드 리뷰어가 알아야 할 추가 정보가 있다면 작성 -->
- (예) 이 PR은 리팩토링이 아닌 **핫픽스**입니다.
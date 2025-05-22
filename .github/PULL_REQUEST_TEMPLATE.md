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





<!-------------------------- Refactor -------------------------->
<!-- Title: [Refactor] description -->
<!-- Label:  -->
## 🔧 Refactoring PR

### 📌 목적
<!-- 어떤 의도로 리팩토링을 진행했는지 작성 -->
- 클래스 책임 분리 및 메서드 단일 책임 원칙 적용
- 중복 로직 제거 및 가독성 향상
- 네이밍 통일 및 불필요한 의존 제거

---

### 🧩 변경 요약
<!-- 주요 변경 사항 요약 (기능 변경 없어야 함) -->
- `UserService`에서 인증 관련 로직 분리 (`AuthService`로 이동)
- if/else 중첩 로직 메서드 분리 (`extract*` 등)
- 불필요한 주석 및 dead code 제거

---

### ✅ 영향도
- [x] 기존 동작과 동일
- [x] 테스트 코드 변경 없음
- [x] API 응답 및 외부 인터페이스 변경 없음

---

### 🔍 리뷰 포인트
<!-- 리뷰어가 집중해서 보면 좋을 변경 지점 -->
- 서비스 계층의 역할 재배분 방식 적절한지
- 분리된 메서드명/모듈 구조 적절한지
- 불필요하게 제거된 부분 없는지

---

### 🧪 테스트
- [x] 기존 단위 테스트 전체 통과
- [ ] 리팩토링 대상 커버리지 보강 (필요 시)

---

### 📝 기타 참고 사항
- 추가 리팩토링은 후속 PR에서 이어갈 예정
- 정적 분석 도구 추천 규칙 기반으로 정리





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







<!-------------------------- 회의록 -------------------------->

<!-- Title: [회의록] YY-MM-DD 회의록 -->
<!-- Label: documentation -->

### 📁 업로드 문서
- [`docs/meetings/yyyy-mm-dd.md`](docs/meetings/yyyy-mm-dd.md)
# Todo-List 실습 1차
RMS 실습 1차 과제

## 기술스택
### Back-end
- SpringBoot 3.5.x
- Java 21
- MariaDB
- Swagger 2.x.x
### Front-end
- JavaScript
- HTML/CSS

## 주요 기능
- todo 등록, 수정, 삭제, 읽기
- 검색 (상태 토글 선택 가능)

<img width="1578" height="1319" alt="Image" src="https://github.com/user-attachments/assets/19ced4c3-497e-48df-8edc-f66281438635" />

## ERD
<img width="852" height="260" alt="Image" src="https://github.com/user-attachments/assets/c89a72d2-a9b5-4a3c-a25c-7c7e3aab1fb3" />

## DB
<img width="941" height="332" alt="Image" src="https://github.com/user-attachments/assets/a15ea73f-808b-467b-9f28-5d37b01878e7" />

## Swagger
<img width="1361" height="424" alt="Image" src="https://github.com/user-attachments/assets/8c4e491f-b765-442f-8e4f-ff6bbd636624" />

## 트러블 슈팅
1. 상황
- DB에서는 새 Todo가 보이는데, 웹 화면 목록에는 추가한 할 일이 안 보임
- SOFT_DELETE 적용, REGIST_DATE 자동 입력
2. 원인
- BOARD 테이블 정의에서 DELETE_FLAG_YN CHAR(1) DEFAULT NULL로 되어 있어서 새로 INSERT된 행의 DELETE_FLAG_YN이 전부 NULL로 들어감
- List 쿼리 WHERE에 DELETE_FLAG_YN = 'N' 조건이 있어서, 새로 추가한 행들이 전부 결과에서 필터링됨
- 프론트에서는 그 쿼리 결과만 받아서 그려서 등록 성공했는데 화면에는 안 나오는 현상이 발생함
3. 해결
> DDL 수정: ALTER TABLE BOARD MODIFY COLUMN DELETE_FLAG_YN CHAR(1) NOT NULL DEFAULT 'N';
- INSERT 쿼리는 DELETE_FLAG_YN을 건드리지 않고 그대로 두어도, 이제부터는 자동으로 ‘N’이 들어가고 목록에도 바로 보임
4. 배운점
- Soft Delete를 도입할 때는 “컬럼 추가 + 기본값 설정 + 기존 데이터 마이그레이션 + WHERE 조건” 네 가지를 항상 한 세트로 봐야 함
- 새 기능(DELETE_FLAG_YN) 추가 후에는 “INSERT → SELECT” 흐름까지 실제로 테스트해봐야 한다 (특히 WHERE 조건과 DEFAULT 값이 맞는지)

## 아쉬운 점
1. 상황
- ExceptionCode를 Service에서는 사용하고 있지만, 프론트에서는 모든 에러를 동일한 alert("오류가 발생했습니다!")로 처리
- fetch 사용으로 서버에서 내려준 상세 에러코드(CONTENT001, TODO001 등)를 활용하지 못함
2. 원인
- 서버는 CONTENT001("할일 입력 필수"), TODO001("존재하지 않는 할일"), TODO002("수정 실패") 등으로 구분했는데, 프론트에서는 모두 "오류 발생"으로 통일 처리
- fetch만 사용하여 에러처리가 단순화됨
3. 개선방안
- jQuery AJAX를 도입하고 ExceptionCode별 분기 처리 로직을 구현
  - success/error 콜백으로 네트워크/서버 에러 자동 구분
  - 자동 JSON 파싱
  - 타임아웃, 재시도 로직 내장

## 알게된 점
- HashMap vs Map.of
  - HashMap
    - 대량의 데이터를 처리할 때
    - 삽입, 검색 작업에서 효율적
    - 가변적. 데이터 추가, 수정, 삭제 가능
  - Map.of
    - 소규모 맵을 간결하게 생성
    - map.of로 생성된 맵은 불변. 생성후엔 수정x
    - 데이터의 안정성과 보안 향상
- 예외처리
  - 흐름; service에서 throw -> GlobalExceptionHandler 자동 변환
  - ExceptionCode enum
    - **예외 정의**
    - 정적 상수로 미리 컴파일되어 메모리 사용량이 적고, GC 부하가 낮음
    - 확장성 유연, enum 항목 추가만 하면 됨
  - GlobalExceptionHandler
    - **HTTP 응답 변환**
    - ExceptionCode를 분석해서 JSON + HTTP 상태코드 생성
    - thorw 한 예외를 자동으로 잡음
- SRP: 단일 책임 원칙 (single responsibility principle)
  - 객체는 단 하나의 책임만 가져야 한다
  - 하나의 클래스는 하나의 기능을 담당하여 하나의 책임을 수행
  - 응집도는 높게, 결합도는 낮게. 생각하며 구성

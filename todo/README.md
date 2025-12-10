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
<img width="831" height="256" alt="Image" src="https://github.com/user-attachments/assets/f6fdd291-be70-4ced-beae-e34f3ec155c7" />

## 트러블 슈팅
1. properties에서 DB 정보를 못 읽는 이슈
> DataBaseConfig 파일 생성 (추가예정)
## 아쉬운 점
### Front
1. Fetch 사용
<br/> JS에 시간을 많이 할애하지 못해서 fetch를 사용하였는데 Json 변환도 가능하고, 가독성, 예외처리, 유지보수 방면에서 ajax가 성능이 좋다고 생각하여 다음엔 ajax를 사용하여 개발 할 예정
2. 에러 처리
<br/> 백엔드에서 ExceptionCode 커스텀하여 에러 관리 파일 생성했는데 활용을 못함. 다음엔 error.code로 에러처리도 적용하고 싶다
### Back
1. 상태(STATUS) 컬럼 DB에 0/1 저장되게 했는데 다른 개발자가 봤을 땐 뭔지 모를 수 있으니 "Y"/"N"으로 받는게 어떠냐는 의견 받음
<br/> -> 컬럼 설정 INT->CHAR로 변환
<br/> -> .xml 수정 STATUS = CASE WHEN #{status} = 1 THEN 'Y' ELSE 'N' END
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
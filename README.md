## Expense-Tracker-API

[REST API / Mysql / JWT 실습 프로젝트](https://www.youtube.com/watch?v=5VUjP1wMqoE)

----

[API TEST](./src/test/java/com/pairlearning/expensetracker/ExpenseTrackerApiApplicationTests.java)
* 유저 등록 성공 테스트
* 유저 등록 실패 테스트(중복 이메일)
* 유저 등록 실패 테스트(잘못된 이메일 형식)
* 로그인 실패(패스워드 불일치)
* 로그인 성공
* 카테고리 조회 실패(토큰 누락)
* 카테고리 조회 실패(Bearer 누락)
* 카테고리 생성 성공
* 카테고리 전체 조회 성공
* 카테고리 단일 조회
* 카테고리 수정 실패(잘못된 카테고리 ID)
* 카테고리 수정 성공
* 트랜잭션 생성 성공
* 트랜잭션 전체 조회 성공
* 트랜잭션 단일 조회 성공
* 트랜잭션 수정 성공

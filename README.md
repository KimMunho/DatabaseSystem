<div align="center">
<h2>2020039012 김문호 TermProject</h2>
</div>

## 목차

- [개요](#개요)
- [운영개념](#운영개념)
- [요구사항](#요구사항)
- [E-R Diagram](#E-R-Diagram)
- [Relation Schema](#Relation-Schema)

## 개요

- 프로젝트 이름 : 소프트웨어학부 학부 동아리 관리시스템 시스템
- 개발 언어 : Java

## 운영개념

1. MySQL DBMS Database를 생성 한다.
2. 기본환경은 가상머신에 CentOS + MySQL이 설치된다.
3. 프로그램을 통해 데이터베이스에 접속하여 사용자가 요청을 수행한다.

## 요구사항

1. 프로젝트의 엔티티로 'User', 'Club', ‘Club_members’가 있다.
2. 'User'은 Primary key인 Ssn을 가지고 Name, Address, ID, Role, Pnum, Gender이라는 속성을 가진다.

- 사용자는 Name, Address, ID, Pnum, Gender를 통해 회원가입 할 수 있다.
  - 회원가입을 진행한 사용자의 Ssn은 auto_increment하다.
  - 회원가입을 진행한 사용자의 Role의 default 값은 General이다.
  - 회원가입 후에는 프로그램 진입 초기화면으로 이동한다.
- 사용자는 Name, ID를 통해 로그인을 할 수 있다.

3. 'Club'은 Primary key인 Club_name (중복 불가)을 가지고 Webpage, Location, Type, Number_of_members, PresidentID, Club_Uid 라는 속성을 가진다.
4. 'User'과 'Club'는 'Join_club', 'Manage_club', 'Visit_club', ‘Participate_club’, ‘Create_club’이라는 관계를 가지고 있다.

- Role이 해당 ‘Club’의 President인 ‘User’인 경우에만 ‘Manage_club’에 진입할 수 있다.
- Role이 General인 ‘User’인 경우에만 ‘Join_club’, ‘Create_club’에 진입할 수 있다.
  - ‘Create_club’에 진입한 경우 해당 ‘User’의 Role이 해당 ‘Club’의 President로 변경된다.
- Role에 상관없이 ‘Visit_club’에 진입할 수 있다.
- Role이 어떤 ‘Club’의 President이거나 Member인 ‘User’인 경우 다른 ‘Club’의 President이거나 Member의 Role일 수 없다.
- Role이 해당 ‘Club’의 Member인 ‘User’인 경우에만 ‘Participate_club’에 진입할 수 있다.
- Role이 해당 ‘Club’의 President인 ‘User’인 경우에만 ‘Manage_club’에 진입하여 ‘Club’의 attribute를 수정, 삭제할 수 있다.
  - ‘Club’이 삭제된 경우 해당 ‘Club’의 President와 모든 Member인 ‘User’의 Role은 General로 변경된다.
- 진입이 불가하다면 이전 페이지로 돌아가고 그러한 이유를 터미널을 통해 출력한다.

5. 'Club_members'은 Primary key인 C_Name, UserID를 가지고 UserName, Role 라는 속성을 가진다.

- ‘Club_members’를 통해 Role이 President이거나 Member인 ‘User’를 저장한다.

## E-R Diagram

![E-R Diagram](./E-R%20Diagram.png)

## Relation Schema

![Relation Schema](./Relation%20Schema.png)

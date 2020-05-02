# 채팅 프로그램

 ##   TCP socket을 활용한 간단 채팅 프로그램

##   구성

* SimpleClient.java

* SimpleServer.java

  

##  업데이트 내역

<2020-05-02> - ver1.1

### 에러

* 서버와 클라이언트과 통신 중에는 `portField`와 `portBtn`을 비활성화하여 port 번호 변경을 할 수 없도록 수정



### 기능

* 서버 port 를 지정해서 채팅을 수행하도록 로직 변경 (서버는 특정 port를 열고 클라이언트는 열려진 port를 통해서 채팅하도록)

* Thread 클래스를 상속하는 대신 Runnable 인터페이스를 구현해서 쓰레드 실행하도록 수정(ReadCMessageThread, ReadSMessageThread 는 각각 클라, 서버의 메시지를 읽어오는 쓰레드)

* 쓰레드 종료를 `stop 플래그`를 사용하는 대신 `interrupt`를 주는 방식으로 변경

  

<2020-05-01> - ver1.0

### 기능

- 클라이언트 ID 입력 로직 추가(ID 미입력시 메시지창 띄우기)

- 닫기 클릭시 종료 이벤트 추가(WindowListener)

- 채팅 시 서버, 클라이언트 ID를 메시지에 포함시키도록 수정

  

### 에러

- 서버가 accept을 성공하기 전에 창을 닫을 시, `s`만 close하도록 수정

- 서버가 accpet을 성공한 후에는 `dis`, `dos`, `s`, `s1`을 close 하도록 수정

  

  
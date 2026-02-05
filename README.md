# PII Masker (Logback Custom Converter)

`pii-masker`는 Java 애플리케이션 로그 출력 시 발생할 수 있는 개인정보 유출을 방지하기 위해 개발된 **Logback Custom Message Converter**입니다. 

정규표현식을 기반으로 로그 메시지 내의 **사용자 이름, UUID, 우리은행 계좌번호** 패턴을 자동으로 감지하여 안전하게 마스킹 처리합니다.

## 배포 링크
https://repo1.maven.org/maven2/io/github/song-dela-moon/pii-masker/1.0.0/

<br>

## ✨ 주요 기능
이 라이브러리는 다음과 같은 개인정보 패턴을 인식하고 마스킹합니다.


### 1. 사용자 이름 마스킹
- **인식 조건**: 특정 성씨로 시작하는 2~4글자 이름 뒤에 '님'이 붙어 있는 경우
- **마스킹 방식**:
    - 2글자: `성*` (예: 김현 -> 김*)
    - 3글자: `성*명` (예: 홍길동 -> 홍*동)
    - 4글자: `성**명` (예: 남궁사길 -> 남**길)

### 2. UUID 마스킹
- **인식 조건**: 표준 UUID (8-4-4-4-12) 형식 매칭
- **마스킹 방식**: 중간 마디와 마지막 마디의 일부를 가림
    - 예: `550e8400-****-****-***-af45a4******`

### 3. 우리은행 계좌번호 마스킹 (다양한 포맷 대응)
- **통합 우리 계좌**: `0000-000-000000` -> 앞 두 마디 유지 후 일련번호 마스킹
- **한일/연계 계좌**: `000-000000-00-00-0` -> 두 번째 마디(6자리) 마스킹
- **상업/평화 계좌**: `000-00-000000-0` -> 세 번째 마디(일련번호) 마스킹
![alt text](<스크린샷 2026-02-04 오후 4.40.54.png>)

<br>

## 🚀 설치 방법 (Maven)

`pom.xml`에 아래 의존성을 추가하세요.

```xml
<dependency>
    <groupId>io.github.song-dela-moon</groupId>
    <artifactId>pii-masker</artifactId>
    <version>1.0.0</version>
</dependency>
```

<br>

## 🛠 설정 방법 (logback.xml)
프로젝트의 src/main/resources/logback.xml 파일에 conversionRule을 등록하고 로그 패턴에 적용합니다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <conversionRule conversionWord="mask" converterClass="io.github.songdelamoon.masker.PiiMasker" />

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %mask%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

---

### 📝 사용 예시

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Example {
    private static final Logger logger = LoggerFactory.getLogger(Example.class);

    public static void main(String[] args) {
        logger.info("송성혁님께서 시스템에 접속하였습니다.");
        logger.info("생성된 토큰 ID: 550e8400-e29b-41d4-a716-446655440000");
        logger.info("환불 계좌 정보: 1002-123-456789");
    }
}
``` 


### 💻 콘솔 출력
```c
12:00:00.001 [main] INFO - 송*혁님께서 시스템에 접속하였습니다.
12:00:00.002 [main] INFO - 생성된 토큰 ID: 550e8400-****-****-***-446655******
12:00:00.003 [main] INFO - 환불 계좌 정보: 1002-123-******
```

---

### ⚠️ 주의사항
* 본 라이브러리는 MessageConverter를 상속받아 구현되었습니다.
* 따라서 Logback 설정 시 %mask(%msg) 가 아닌 %mask 단독 키워드로 사용해야 정상적으로 동작합니다.
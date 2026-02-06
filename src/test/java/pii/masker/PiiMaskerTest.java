package pii.masker;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.qos.logback.classic.spi.ILoggingEvent;

@ExtendWith(MockitoExtension.class)
public class PiiMaskerTest {

	@InjectMocks
	private PiiMasker masker; // 테스트할 컨버터 클래스

	@Mock
	private ILoggingEvent mockEvent;

	@Test
	@DisplayName("사용자 이름 2글자 & 계좌번호 A 타입 검사")
	void test01() {
		// given: 마스킹 대상이 포함된 로그 메시지 설정
		String originalMessage = "유정님의 계좌번호는 4444-333-666666";
		String expect = "유*님의 계좌번호는 4444-333-******";
		

		// ILoggingEvent가 불렸을 때 위 메시지를 주도록 설정
		when(mockEvent.getFormattedMessage()).thenReturn(originalMessage);

		// when: 컨버터 실행
		String result = masker.convert(mockEvent);

		// then: 마스킹 결과 확인 (각 마스킹 전략에 맞는 예상 결과값 작성)
		assertEquals(expect, result);
	}
	
	@Test
	@DisplayName("사용자 이름 3글자 & 계좌번호 B 타입 검사")
	void test02() {
		// given: 마스킹 대상이 포함된 로그 메시지 설정
		String originalMessage = "유정호님의 계좌번호는 333-666666-22-22-1";
		String expect = "유*호님의 계좌번호는 333-******-22-22-1";

		// ILoggingEvent가 불렸을 때 위 메시지를 주도록 설정
		when(mockEvent.getFormattedMessage()).thenReturn(originalMessage);

		// when: 컨버터 실행
		String result = masker.convert(mockEvent);

		// then: 마스킹 결과 확인 (각 마스킹 전략에 맞는 예상 결과값 작성)
		assertEquals(expect, result);
	}
	
	@Test
	@DisplayName("사용자 이름 4글자 & 계좌번호 C 타입 검사")
	void test03() {
		// given: 마스킹 대상이 포함된 로그 메시지 설정
		String originalMessage = "유정호호님의 계좌번호는 333-22-55555-1";
		String expect = "유**호님의 계좌번호는 333-22-*****-1";

		// ILoggingEvent가 불렸을 때 위 메시지를 주도록 설정
		when(mockEvent.getFormattedMessage()).thenReturn(originalMessage);

		// when: 컨버터 실행
		String result = masker.convert(mockEvent);

		// then: 마스킹 결과 확인 (각 마스킹 전략에 맞는 예상 결과값 작성)
		assertEquals(expect, result);
	}
	
	@Test
	@DisplayName("사용자의 UUID 검사")
	void test04() {
		// given: 마스킹 대상이 포함된 로그 메시지 설정
		String originalMessage = "유정호님의 UUID: 550e8400-e29b-41d4-a716-446655440000";
		String expect = "유*호님의 UUID: 550e8400-****-****-****-446655******";

		// ILoggingEvent가 불렸을 때 위 메시지를 주도록 설정
		when(mockEvent.getFormattedMessage()).thenReturn(originalMessage);

		// when: 컨버터 실행
		String result = masker.convert(mockEvent);

		// then: 마스킹 결과 확인 (각 마스킹 전략에 맞는 예상 결과값 작성)
		assertEquals(expect, result);
	}
	
	@Test
	@DisplayName("사용자의 UUID 검사")
	void test05() {
		// given: 마스킹 대상이 포함된 로그 메시지 설정
//		String originalMessage = "유정호님의 UUID: 550e8400-e29b-41d4-a716-446655440000";
		String expect = "유*호님의 UUID: 550e8400-****-****-****-446655******";

		// ILoggingEvent가 불렸을 때 위 메시지를 주도록 설정
		when(mockEvent.getFormattedMessage()).thenReturn(null);

		// when: 컨버터 실행
		String result = masker.convert(mockEvent);

		// then: 마스킹 결과 확인 (각 마스킹 전략에 맞는 예상 결과값 작성)
		assertNull(result);
	}
	
	@Test
	@DisplayName("마스킹 없는 경우 검사")
	void test06() {
		// given: 마스킹 대상이 포함된 로그 메시지 설정
//		
		String originalMessage = "강사님 집에 가고 싶어요.";
		String expect = "강사님 집에 가고 싶어요.";

		// ILoggingEvent가 불렸을 때 위 메시지를 주도록 설정
		when(mockEvent.getFormattedMessage()).thenReturn(originalMessage);

		// when: 컨버터 실행
		String result = masker.convert(mockEvent);

		// then: 마스킹 결과 확인 (각 마스킹 전략에 맞는 예상 결과값 작성)
		assertEquals(expect, result);
	}
}

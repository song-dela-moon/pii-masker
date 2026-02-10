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

	/**
	 * [이름 2글자 & 계좌번호 Type A] 마스킹 테스트
	 * 이름이 2글자인 경우(성+이름) 뒷글자 마스킹 여부와, 
	 * '0000-000-000000' 형식의 계좌번호 뒷부분 마스킹 처리 기능을 검증합니다.
	 */
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
	
	/**
	 * [이름 3글자 & 계좌번호 Type B] 마스킹 테스트
	 * 일반적인 3글자 이름의 가운데 글자 마스킹과,
	 * 하이픈이 4개 포함된 복합적인 계좌번호 형식의 특정 구간 마스킹 여부를 검증합니다.
	 */
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
	
	/**
	 * [이름 4글자 & 계좌번호 Type C] 마스킹 테스트
	 * 외자 성이 아닌 4글자 이름(예: 복성 등)의 중간 글자들 마스킹과,
	 * 하이픈이 3개 포함된 또 다른 계좌번호 형식의 마스킹 규칙을 검증합니다.
	 */
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
	
	/**
	 * [UUID 표준 형식] 마스킹 테스트
	 * 8-4-4-4-12 자릿수의 표준 UUID 형식에서 
	 * 중간 세그먼트들과 마지막 세그먼트의 일부가 규정에 맞게 마스킹되는지 검증합니다.
	 */
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
	
	/**
	 * [Null 입력 예외 처리] 테스트
	 * 로그 이벤트의 메시지가 null인 경우, 컨버터가 에러를 발생시키지 않고 
	 * 안전하게 null을 반환하는지(Null-safe) 확인합니다.
	 */
	@Test
	@DisplayName("메세지가 null 입력 시 검사")
	void test05() {
		// given: 마스킹 대상이 포함된 로그 메시지 설정

		// ILoggingEvent가 불렸을 때 위 메시지를 주도록 설정
		when(mockEvent.getFormattedMessage()).thenReturn(null);

		// when: 컨버터 실행
		String result = masker.convert(mockEvent);

		// then: 마스킹 결과 확인 (각 마스킹 전략에 맞는 예상 결과값 작성)
		assertNull(result);
	}
	
	/**
	 * [마스킹 비대상 메시지 유지] 테스트
	 * 개인정보 패턴이 포함되지 않은 일반 텍스트 로그의 경우, 
	 * 변조 없이 원본 메시지를 그대로 유지하는지 확인합니다.
	 */
	@Test
	@DisplayName("마스킹 안되는 대상 검사")
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

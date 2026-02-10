package pii.masker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
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
     * [개인정보 마스킹 통합 검증]
     * 외부 CSV 파일(test-data.csv)로부터 원본 메시지와 예상 마스킹 메시지 쌍을 읽어와서
     * PiiMasker의 변환 로직이 각 케이스별 마스킹 전략에 맞게 동작하는지 일괄 검증합니다.
     * * @param originalMessage  CSV 파일의 첫 번째 컬럼: 마스킹 전 원본 로그 메시지
     * @param expectedMessage  CSV 파일의 두 번째 컬럼: 마스킹 정책이 적용된 예상 결과 메시지
     * @see <a href="file:src/test/resources/test-data.csv">테스트 데이터셋</a>
     */
    @ParameterizedTest(name = "[{index}] {0} => {1}")
    @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
    @DisplayName("CSV 데이터를 이용한 개인정보 마스킹 통합 검사")
    void piiMaskingIntegrationTest(String originalMessage, String expectedMessage) {
        // ILoggingEvent가 불렸을 때 CSV에서 읽어온 원본 메시지를 반환하도록 설정
        when(mockEvent.getFormattedMessage()).thenReturn(originalMessage);

        // 컨버터 실행
        String result = masker.convert(mockEvent);

        // 실제 결과와 CSV의 예상 결과 메시지 비교
        assertEquals(expectedMessage, result, "마스킹 결과가 데이터셋의 기대치와 일치하지 않습니다.");
    }
	
	/**
	 * [UUID 표준 형식] 마스킹 테스트
	 * 8-4-4-4-12 자릿수의 표준 UUID 형식에서 
	 * 중간 세그먼트들과 마지막 세그먼트의 일부가 규정에 맞게 마스킹되는지 검증합니다.
	 */
	@Test
	@DisplayName("사용자의 UUID 정보의 중간 4글자짜리와 마지막 6글자를 마스킹한다.")
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
	@DisplayName("메세지로 null 입력 시 빈 문자열을 반환한다.")
	void test05() {
		// given: 마스킹 대상이 포함된 로그 메시지 설정

		// ILoggingEvent가 불렸을 때 위 메시지를 주도록 설정
		when(mockEvent.getFormattedMessage()).thenReturn(null);
		String expect = "";
		
		// when: 컨버터 실행
		String result = masker.convert(mockEvent);

		// then: 마스킹 결과 확인 (각 마스킹 전략에 맞는 예상 결과값 작성)
		assertEquals(expect, result);
	}
	
	/**
	 * [마스킹 비대상 메시지 유지] 테스트
	 * 개인정보 패턴이 포함되지 않은 일반 텍스트 로그의 경우, 
	 * 변조 없이 원본 메시지를 그대로 유지하는지 확인합니다.
	 */
	@Test
	@DisplayName("개인정보가 아닌 데이터는 마스킹하지 않는다.")
	void test06() {
		// given: 마스킹 대상이 포함된 로그 메시지 설정
//		
		String originalMessage = "집에 가고 싶어요.";
		String expect = "집에 가고 싶어요.";

		// ILoggingEvent가 불렸을 때 위 메시지를 주도록 설정
		when(mockEvent.getFormattedMessage()).thenReturn(originalMessage);

		// when: 컨버터 실행
		String result = masker.convert(mockEvent);

		// then: 마스킹 결과 확인 (각 마스킹 전략에 맞는 예상 결과값 작성)
		assertEquals(expect, result);
	}
}

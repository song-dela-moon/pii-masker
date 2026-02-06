package pii.masker;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
	void 마스킹_통합_테스트() {
		// given: 마스킹 대상이 포함된 로그 메시지 설정
		String originalMessage = "유정호님";
		String expect = "유*호님";

		// ILoggingEvent가 불렸을 때 위 메시지를 주도록 설정
		when(mockEvent.getFormattedMessage()).thenReturn(originalMessage);

		// when: 컨버터 실행
		String result = masker.convert(mockEvent);

		// then: 마스킹 결과 확인 (각 마스킹 전략에 맞는 예상 결과값 작성)
		assertEquals(expect, result);
	}
}

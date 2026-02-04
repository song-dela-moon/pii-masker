package pii.masker;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PiiMasker extends MessageConverter {

    // 예시: 전화번호 패턴 (010-xxxx-xxxx)
    private static final String PHONE_PATTERN = "(01[016789])[-](\\d{3,4})[-](\\d{4})";
    private static final Pattern pattern = Pattern.compile(PHONE_PATTERN);

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        
        // 메시지 내에서 패턴 매칭 및 마스킹
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            // 가운데 자리를 마스킹 처리
            return matcher.replaceAll("$1-****-$3");
        }
        
        return message;
    }
}
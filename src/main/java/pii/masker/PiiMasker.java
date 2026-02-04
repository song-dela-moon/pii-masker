package pii.masker;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PiiMasker extends MessageConverter {// 1. 각 개인정보별 정규표현식 정의
    private static final String PHONE = "(?<phone>01[016789]-\\d{3,4}-\\d{4})";
    private static final String RRN = "(?<rrn>\\d{6}-[1-4]\\d{6})"; // 주민번호
    private static final String ACCOUNT = "(?<acc>\\d{2,6}-\\d{2,6}-\\d{2,6})"; // 일반적인 계좌번호 형태

    // 2. 모든 패턴을 OR(|)로 결합
    private static final Pattern COMBINED_PATTERN = Pattern.compile(
        String.join("|", PHONE, RRN, ACCOUNT)
    );

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        if (message == null) return message;

        StringBuilder sb = new StringBuilder();
        Matcher matcher = COMBINED_PATTERN.matcher(message);

        while (matcher.find()) {
            String replacement;
            // 매칭된 그룹에 따라 마스킹 전략 분기
            if (matcher.group("phone") != null) {
            	replacement = maskPhone(matcher.group("phone"));
            } else if (matcher.group("rrn") != null) {
                replacement = matcher.group().replaceAll("(\\d{6})-([1-4])\\d{6}", "$1-$2******");
            } else if (matcher.group("acc") != null) {
            	replacement = maskAccount(matcher.group("acc"));
            } else {
                replacement = matcher.group();
            }
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
    
    private String maskPhone(String text) {
        Matcher m = Pattern.compile("(\\d{3})-(\\d{3,4})-(\\d{4})").matcher(text);
        if (m.find()) {
            String middle = m.group(2);
            String stars = "";
            for (int i = 0; i < middle.length(); i++) {
                stars += "*";
            }
            return m.group(1) + "-" + stars + "-" + m.group(3);
        }
        return text;
    }
    
    private String maskAccount(String text) {
        Matcher m = Pattern.compile("(\\d{2,6})-(\\d{2,6})-(\\d{2,6})").matcher(text);
        if (m.find()) {
            String middle = m.group(2);
            String stars = "";
            for (int i = 0; i < middle.length(); i++) {
                stars += "*";
            }
            return m.group(1) + "-" + stars + "-" + m.group(3);
        }
        return text;
    }
}
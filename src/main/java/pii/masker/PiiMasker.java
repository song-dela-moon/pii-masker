package pii.masker;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PiiMasker extends MessageConverter {// 1. 각 개인정보별 정규표현식 정의    
    private static final String NAME = "(?<name>[고김남류박백선송오유이전정조하][가-힣]{1,3})(?=님)";
    private static final String UUID = "(?<uuid>[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})"; // 주민번호
    private static final String ACCOUNT_A = "(?<accA>\\d{4}-\\d{3}-\\d{6})"; // 통합우리 계좌번호 형태
    private static final String ACCOUNT_B = "(?<accB>\\d{3}-\\d{6}-\\d{2}-\\d{2}-\\d{1})"; // 한일, 연계 계좌번호 형태
    private static final String ACCOUNT_C = "(?<accC>\\d{3}-\\d{2}-\\d{5,6}-\\d{1})"; // 상업, 평화 계좌번호 형태

    // 2. 모든 패턴을 OR(|)로 결합
    private static final Pattern COMBINED_PATTERN = Pattern.compile(
        String.join("|", NAME, UUID, ACCOUNT_A, ACCOUNT_B, ACCOUNT_C)
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
            if (matcher.group("name") != null) {
            	replacement = maskName(matcher.group("name"));
            } else if (matcher.group("uuid") != null) {
            	replacement = maskUuid(matcher.group("uuid"));
            } else if (matcher.group("accA") != null) {
            	replacement = maskAccountA(matcher.group("accA"));
            } else if (matcher.group("accB") != null) {
            	replacement = maskAccountB(matcher.group("accB"));
            } else if (matcher.group("accC") != null) {
            	replacement = maskAccountC(matcher.group("accC"));
            } else {
                replacement = matcher.group();
            }
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
    
    private String maskName(String text) {
    	int length = text.length();
    	Character front = text.charAt(0);
    	Character back = text.charAt(length-1);
    	
    	String result = "";
    	switch(length) {
    	case 2:
    		result = front + "*";
    		break;
    	case 3:
    		result = front + "*" + back;
    		break;
    	case 4:
    		result = front + "**" + back;
    		break;
    	default:
    		result = "***";
    	}
    	return result;
    }
    
    private String maskUuid(String text) {
    	String regex = "([0-9a-fA-F]{8})-([0-9a-fA-F]{4})-(4[0-9a-fA-F]{3})-([89abAB][0-9a-fA-F]{3})-([0-9a-fA-F]{12})";
    	Matcher m = Pattern.compile(regex).matcher(text);
        if (m.find()) {
            return m.group(1) + "-****-****-***-" + m.group(5).substring(0, 6) + "******" ;
        }
        return text;
    }
    
    private String maskAccountA(String text) {
    	String regex = "(\\d{4})-(\\d{3})-(\\d{6})";
        Matcher m = Pattern.compile(regex).matcher(text);
        if (m.find()) {
            return m.group(1) + "-" + m.group(2) + "-******";
        }
        return text;
    }
     
    private String maskAccountB(String text) {
    	String regex = "(\\d{3})-(\\d{6})-(\\d{2})-(\\d{2})-(\\d{1})";
        Matcher m = Pattern.compile(regex).matcher(text);
        if (m.find()) {
            return m.group(1) + "-" + "******" + "-" + m.group(3) + "-" + m.group(4) + "-" + m.group(5);
        }
        return text;
    }
    
    private String maskAccountC(String text) {
    	String regex = "(\\d{3})-(\\d{2})-(\\d{5,6})-(\\d{1})";
        Matcher m = Pattern.compile(regex).matcher(text);
        if (m.find()) {
        	StringBuilder stars = new StringBuilder();
            for (int i = 0; i < m.group(3).length(); i++) {
                stars.append("*");
            }
            
            return m.group(1) + "-" + m.group(2) + "-" + stars.toString() + "-" + m.group(4);
        }
        return text;
    }
}
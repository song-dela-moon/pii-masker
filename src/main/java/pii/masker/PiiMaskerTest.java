package pii.masker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PiiMaskerTest {
    private static final Logger logger = LoggerFactory.getLogger(PiiMaskerTest.class);

    public static void main(String[] args) {
        logger.info("테스트를 시작합니다.");
        
        // 마스킹 대상 데이터
        String userName = "송민혁";
        String userAccount = "938002-00-916329";
        
        // 로그 출력
//        logger.info("사용자 접속 확인: {}", customerPhone);
        logger.warn("민감 정보 노출 주의: 고객 연락처는 {} 입니다.", "010-987-5432");
        logger.warn("민감 정보 노출 주의: 고객 주민번호는 {} 입니다.", "010724-3047113");
        logger.warn("민감 정보 노출 주의: 고객 계좌번호는 {} 입니다.", "938002-001234-916329");
        
        logger.info("테스트가 완료되었습니다.");
    }
}
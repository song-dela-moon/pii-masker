package pii.masker;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PiiMaskerLog {
    private static final Logger logger = LoggerFactory.getLogger(PiiMaskerLog.class);

    public static void main(String[] args) {
    	
//    	Logger logger = LoggerFactory.getLogger(PiiMaskerTest.class);
        
        // 마스킹 대상 데이터
        String userID = UUID.randomUUID().toString();
        String userName = "유정호";
        String wooriAccount = "1111-222-333333";
        String tempAccount1 = "111-222222-33-44-5";
        String tempAccount2 = "111-22-33333-4";
        
        
        
        // 로그 출력        
        logger.info("민감 정보 노출 주의: 고객 아이디는 {} 입니다.", userID);
        logger.info("민감 정보 노출 주의: 고객 성함은 {}님 입니다.", userName);
        logger.info("민감 정보 노출 주의: 고객 계좌번호1은 {} 입니다.", wooriAccount);
        logger.info("민감 정보 노출 주의: 고객 계좌번호2은 {} 입니다.", tempAccount1);
        logger.info("민감 정보 노출 주의: 고객 계좌번호3은 {} 입니다.", tempAccount2);
        
        logger.info("민감 정보 노출 주의: 이체금액은 {}원 입니다.", 1000000);
        logger.info("민감 정보 노출 주의: 상대 우리은행 계좌번호는 {} 입니다.", "1234-321-098765");
        logger.info("민감 정보 노출 주의: 상대 타행 계좌번호는 {} 입니다.", "1234-098765");
        
    }
}
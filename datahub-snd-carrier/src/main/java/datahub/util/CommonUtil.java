package datahub.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * 共通処理クラス
 *  
 */
@Component
public class CommonUtil {
	
	/**
	 * イベントIDを生成する
	 * 
	 */
	public String createEventId() {
		return DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
	}
	
	/**
	 * プロセスIDを生成する
	 * 
	 */
	public String createProcessId() {
		return DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()); 
	}
	
	/**
	 * UUIDを生成する
	 * 
	 */
	public UUID createUUID() {
		return UUID.randomUUID();
	}
}

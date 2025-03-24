package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * ヘルスチェックのリクエスト
 * 
 */
@Data
public class HealthCheckRequest {
	
	/** 送信元企業ID */
	@JsonProperty("from_id")
	private String fromId;
	
	/** イベントID */
	@JsonProperty("event_id")
	private String eventId;
	
	/** DataHub ユーザーID */
	@JsonProperty("dh_user_id")
	private String dhUserId;
	
	/** DataHub パスワード */
	@JsonProperty("dh_password")
	private String dhPassword;
}

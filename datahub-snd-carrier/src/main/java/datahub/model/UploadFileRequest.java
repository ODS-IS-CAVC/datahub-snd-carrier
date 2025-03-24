package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * ファイルアップロードリクエスト
 * 
 */
@Data
public class UploadFileRequest {
	
	/** 送信元企業ID */
	@JsonProperty("from_id")
	private String fromId;
	
	/** 情報区分 */
	@JsonProperty("data_type")
	private String dataType;
	
	/** イベントID */
	@JsonProperty("event_id")
	private String eventId;
	
	/** ZIPファイルコンテンツ */
	@JsonProperty("zipfile_content")
	private String zipfileContent;
	
	/** プロセスID */
	@JsonProperty("process_id")
	private String processId;
	
	/** DataHub ユーザーID */
	@JsonProperty("dh_user_id")
	private String dhUserId;
	
	/** DataHub パスワード */
	@JsonProperty("dh_password")
	private String dhPassword;
}

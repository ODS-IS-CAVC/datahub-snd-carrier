package datahub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.Getter;
import lombok.Setter;

/**
 * バッチ設定
 * 
 */
@Configuration
@ConfigurationProperties(prefix = "app.api")
@Getter
@Setter
@Primary
public class ApiConfig {
	
	/** コネクトタイムアウト (ミリ秒) */
    private String connectionTimeout;

    /** リクエストタイムアウト (ミリ秒) */
    private String requestTimeout;

    /** 最大リトライ回数 */
    private String maxRetryCount;

    /** リトライ待機時間 (秒) */
    private String retryWaitingTime;

    /** キャリアAPI URL 認証 */
    private String carrierUrlAuth;
    
    /** キャリアAPI URL データチャネル */
    private String carrierUrlDataChannel;

    /** キャリアAPI キー */
    private String carrierKey;
    
    /** キャリアAPI ID */
    private String carrierClientId;
    
    /** キャリアAPI シークレット */
    private String carrierClientSecret;

    /** DataHubAPI URL */
    private String datahubUrl;

    /** DataHubAPI キー */
    private String datahubKey;
    
    /** CSV出力先のベースパス */
    private String baseCsvOutputPath;
    
    /** CSV出力制御 */
    @Value("${output.csv.enabled}")
    private boolean isCsvOutputEnabled;
    
    /** JSON出力制御 */
    @Value("${output.json.enabled}")
    private boolean isJsonOutputEnabled;
}

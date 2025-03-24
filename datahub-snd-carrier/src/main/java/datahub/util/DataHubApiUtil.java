package datahub.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import datahub.common.Constants;
import datahub.config.ApiConfig;
import datahub.exception.DataHubException;
import datahub.model.HealthCheckRequest;
import datahub.model.UploadFileRequest;

/**
 * DataHubAPI操作クラス
 * 
 */
@Component
public class DataHubApiUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(DataHubApiUtil.class);
	
	private ApiConfig config;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	/** エラーメッセージ */
	private final String MSG_API_ERROR = "DataHub APIへのアクセスに失敗しました。";
	
	/** ヘッダー接頭辞 コンテンツタイプ */
	private static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
	
	/** ヘッダー json */
	private static final String HEADER_APPLICATION_JSON = "application/json";
	
	/** エンドポイント ヘルスチェック */
	private final String ENDPOINT_HEATH_CHECK = "/healthcheck";
	
	/** エンドポイント ファイル送信 */
	private final String ENDPOINT_FILE_UPLOAD = "/files/upload";
	
	/** コネクトタイムアウト（ミリ秒）*/
	private Duration connectTimeout;
	
	/** リクエストタイムアウト（ミリ秒）*/
	private Duration requestTimeout;
	
	/** 最大リトライ回数 */
	private int maxRetryCount;
	
	/** リトライ待機時間 （秒）*/
	private long retryWaitingTime;
	
	/**
	 * コンストラクタ
	 * 
	 */
	public DataHubApiUtil(final ApiConfig config) {
		this.config = config;
		this.connectTimeout = Duration.ofMinutes(Long.parseLong(this.config.getConnectionTimeout()));
		this.requestTimeout = Duration.ofMinutes(Long.parseLong(this.config.getRequestTimeout()));
		this.maxRetryCount = Integer.parseInt(this.config.getMaxRetryCount());
		this.retryWaitingTime = Integer.parseInt(this.config.getRetryWaitingTime());
	}
	
	/**
	 * 活殺チェック
	 * 
	 */
	public void healthcheck(final HealthCheckRequest healthCheckRequest) throws Exception{
		
		String url = this.config.getDatahubUrl() + ENDPOINT_HEATH_CHECK;
		String requestBody = objectMapper.writeValueAsString(healthCheckRequest);
		
		logger.debug("URL: " + url);
		logger.debug("Request Body:\n" + requestBody);
		
		//リクエストの作成
		HttpRequest request = HttpRequest.newBuilder(URI.create(url))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(HEADER_KEY_CONTENT_TYPE, HEADER_APPLICATION_JSON)
				.timeout(this.requestTimeout)
				.build();
		
		//リクエストの送信
		this.sendHttp(request);
	}
	
	/**
	 * DataHubにZIPファイル送信
	 * 
	 */
	public String sendFileToDataHub(final UploadFileRequest uploadFileRequest) throws Exception {
		
		String url = this.config.getDatahubUrl() + ENDPOINT_FILE_UPLOAD;
		String requestBody = objectMapper.writeValueAsString(uploadFileRequest);
		
		logger.debug("URL: " + url);
		logger.debug("Request Body:\n" + requestBody);
		
		//リクエストの作成
		HttpRequest request = HttpRequest.newBuilder(URI.create(url))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(HEADER_KEY_CONTENT_TYPE, HEADER_APPLICATION_JSON)
				.timeout(this.requestTimeout)
				.build();
		
		HttpResponse<String> httpResponse = null;
		//リクエストの送信、レスポンスの受取
		httpResponse = this.sendHttp(request);
		
		JsonNode responseBodyJson = objectMapper.readTree(httpResponse.body());
	    String processId = responseBodyJson.path("process_id").asText();
		
		return processId;
	}
	
	/** 
	 * HTTPリクエスト
	 * 
	 */
	private HttpResponse<String> sendHttp(final HttpRequest httpRequest) throws Exception {
		
		HttpClient httpClient = HttpClient.newBuilder()
				.connectTimeout(this.connectTimeout)
				.build();
		
		HttpResponse<String> httpResponse = null;
		
		//最大リトライ回数に達するかレスポンスが返ってくるまでリクエスト実行
		int r = 0;
		while(true) {
			try {
				httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
				r++;
				
				if(HttpsURLConnection.HTTP_OK == httpResponse.statusCode() ||
						HttpsURLConnection.HTTP_CREATED == httpResponse.statusCode()) {
					break;
				}
				
				if(HttpURLConnection.HTTP_OK != httpResponse.statusCode()) {
					logger.warn("ステータスコード200以外を受信: " + httpResponse.statusCode());
					logger.warn("メッセージ: " + httpResponse.body());
				}
				
				Thread.sleep(this.retryWaitingTime);
			} catch (IOException | InterruptedException e) {
				throw new DataHubException(Constants.ERR_MSG, e);
			}
			if (r == this.maxRetryCount) {
				throw new DataHubException(MSG_API_ERROR);
			}
		}
		
		return httpResponse;
	}
}

package datahub.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import datahub.common.CarrierApiEndpoint;
import datahub.common.DataTypeModelMapper;
import datahub.config.ApiConfig;
import datahub.exception.DataHubException;
import datahub.model.CarrierApiRequest;
import datahub.model.CarrierTransCapacitySearchRequest;
import datahub.model.CarrierTransRequestSearchRequest;
import datahub.model.InputParameters;
import datahub.model.ShipperTransCapacitySearchRequest;
import datahub.model.VehicleSearchRequest;

/**
 * API操作クラス
 * 
 */
@Component
public class CarrierApiUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(CarrierApiUtil.class);
	
	private ApiConfig config;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private CommonUtil commonUtil;
	
	/** ヘッダー接頭辞 認証認可 */
	private final String HEADER_KEY_AUTHORIZATION = "Authorization";
	
	/** ヘッダー接頭辞 APIKey */
	private final String HEADER_KEY_API_KEY = "apiKey";
	
	/** ヘッダー接頭辞 コンテンツタイプ */
	private final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
	
	/** ヘッダー接頭辞 X-Tracking */
	private final String HEADER_KEY_X_TRACKING = "X-Tracking";
	
	/** ヘッダー json */
	private final String HEADER_APPLICATION_JSON = "application/json";
	
	/** ボディ operatorAccountId */
	private final String BODY_CLIENT_ID = "clientId";
	
	/** ボディ accountPassword */
	private final String BODY_CLIENT_SECRET = "clientSecret";
	
	/** APIエラーメッセージ */
	private static final String MSG_API_ERROR = "外部APIへのアクセスに失敗しました。";
	
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
	public CarrierApiUtil(ApiConfig config) {
		this.config = config;
		this.connectTimeout = Duration.ofMinutes(Long.parseLong(this.config.getConnectionTimeout()));
		this.requestTimeout = Duration.ofMinutes(Long.parseLong(this.config.getRequestTimeout()));
		this.maxRetryCount = Integer.parseInt(this.config.getMaxRetryCount());
		this.retryWaitingTime = Integer.parseInt(this.config.getRetryWaitingTime());
	}
	
	/**
	 * ログイン
	 * 
	 */
	public String login() throws Exception {
		
		Map<String, String> loginInfo = new HashMap<>();
		loginInfo.put(BODY_CLIENT_ID, config.getCarrierClientId());
		loginInfo.put(BODY_CLIENT_SECRET, config.getCarrierClientSecret());
		
		String requestBody = objectMapper.writeValueAsString(loginInfo);
		String url = config.getCarrierUrlAuth();
		
		logger.debug("URL: " + url);
		
		//リクエストボディの作成
		HttpRequest request = HttpRequest.newBuilder(URI.create(url))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(HEADER_KEY_API_KEY, config.getCarrierKey())
				.header(HEADER_KEY_CONTENT_TYPE, HEADER_APPLICATION_JSON)
				.timeout(this.requestTimeout)
				.build();
		
		//リクエストの送信、レスポンスの受取
		HttpResponse<String> httpResponse = this.sendHttp(request);
		
		return httpResponse.body();
		
	}
		
	/**
	 * JSONデータを取得
	 * 
	 */
	public String fetchJson(final String url, final CarrierApiRequest carrierApiRequest, final String accessToken) throws Exception {
		
		String queryParams = this.buildQueryParams(carrierApiRequest);
		String xTracking = commonUtil.createUUID().toString();
		
		logger.debug("URL: " + url);
		logger.info("QueryParams: " + queryParams);
		logger.info("X-Tracking: " + xTracking);
		
		//リクエストボディの作成
		HttpRequest request = HttpRequest.newBuilder(URI.create(url + "?" + queryParams))
				.GET()
				.header(HEADER_KEY_AUTHORIZATION, "Bearer " + accessToken)
				.header(HEADER_KEY_API_KEY, config.getCarrierKey())
				.header(HEADER_KEY_X_TRACKING, xTracking)
				.header(HEADER_KEY_CONTENT_TYPE, HEADER_APPLICATION_JSON)
				.timeout(this.requestTimeout)
				.build();
		
		logger.info("request:" + request);
		
		//リクエストの送信、レスポンスの受取
		HttpResponse<String> httpResponse = this.sendHttp(request);
		
		String contentType = httpResponse.headers().firstValue("Content-Type").orElse("");
	    if (!contentType.contains("application/json")) {
	        throw new DataHubException("APIからJSONデータ以外が返されました: " + contentType);
	    }
		
		return httpResponse.body();
		
	}
	
	/**
	 * HTTP送信
	 */
	private HttpResponse<String> sendHttp(final HttpRequest httpRequest) throws Exception {
		
		//Httpクライアントの作成
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
				throw e;
			}
			if (r == this.maxRetryCount) {
				throw new DataHubException(MSG_API_ERROR);
			}
		}
		
		return httpResponse;
	}
	
	/**
	 * URLを取得
	 * 
	 */
	public String getUrl(final String dataType) {
		return this.config.getCarrierUrlDataChannel() + 
				CarrierApiEndpoint.fromCode(dataType).getEndpoint();
	}
	
	/**
	 * リクエストを作成
	 * 
	 */
	public CarrierApiRequest createRequest(final InputParameters inputParameters) {
		
		String dataType = inputParameters.getDataType();
		DataTypeModelMapper type = DataTypeModelMapper.fromCode(dataType);
		
		switch(type) {
			case VEHICLE:
				return this.createVehicleRequest(inputParameters);
			case SHIPPER_TRANS_CAPACISTY:
				return this.createShipperTransCapacitySearchRequest(inputParameters);
			case CARRIER_TRANS_REQUEST:
				return this.createCarrierTransRequestSearchRequest(inputParameters);
			case CARRIER_TRANS_CAPACITY:
				return this.createCarrierTransCapacitySearchRequest(inputParameters);
			default:
				throw new DataHubException("存在しない情報区分が選択されています。情報区分: " + dataType);
		}
	}
	
	/**
	 * 車輛マスタのリクエスト作成
	 */
	private CarrierApiRequest createVehicleRequest(final InputParameters inputParameters) {
	    return VehicleSearchRequest.builder()
	            .vehicleType(this.getOrDefault(inputParameters.getVehicleType()))
	            .build();
	}
	
	/**
	 * 運送能力情報のリクエスト作成
	 */
	private CarrierApiRequest createShipperTransCapacitySearchRequest(final InputParameters inputParameters) {
		return ShipperTransCapacitySearchRequest.builder()
	            .serviceNo(this.getOrDefault(inputParameters.getServiceNo()))
	            .serviceName(this.getOrDefault(inputParameters.getServiceName()))
	            .carMaxLoadCapacity1Meas(this.convertStrToInteger(inputParameters.getCarMaxLoadCapacity1Meas()))
	            .trspOpStrtAreaLineOneTxt(this.getOrDefault(inputParameters.getTrspOpStrtAreaLineOneTxt()))
	            .trspOpEndAreaLineOneTxt(this.getOrDefault(inputParameters.getTrspOpEndAreaLineOneTxt()))
	            .maxTrspOpDateTrmStrtDate(this.getOrDefault(inputParameters.getMaxTrspOpDateTrmStrtDate()))
	            .minTrspOpDateTrmStrtDate(this.getOrDefault(inputParameters.getMinTrspOpDateTrmStrtDate()))
	            .maxTrspOpDateTrmEndDate(this.getOrDefault(inputParameters.getMaxTrspOpDateTrmEndDate()))
	            .minTrspOpDateTrmEndDate(this.getOrDefault(inputParameters.getMinTrspOpDateTrmEndDate()))
	            .maxTrspOpPlanDateTrmStrtTime(this.getOrDefault(inputParameters.getMaxTrspOpPlanDateTrmStrtTime()))
	            .minTrspOpPlanDateTrmStrtTime(this.getOrDefault(inputParameters.getMinTrspOpPlanDateTrmStrtTime()))
	            .maxTrspOpPlanDateTrmEndTime(this.getOrDefault(inputParameters.getMaxTrspOpPlanDateTrmEndTime()))
	            .minTrspOpPlanDateTrmEndTime(this.getOrDefault(inputParameters.getMinTrspOpPlanDateTrmEndTime()))
	            .build();
	}
	
	/**
	 * 運送計画情報（明細型）キャリア向け運行依頼案件のリクエスト作成
	 */
	private CarrierApiRequest createCarrierTransRequestSearchRequest(final InputParameters inputParameters) {
		return CarrierTransRequestSearchRequest.builder()
				.serviceNo(this.getOrDefault(inputParameters.getServiceNo()))
	            .serviceName(this.getOrDefault(inputParameters.getServiceName()))
	            .carMaxLoadCapacity1Meas(this.convertStrToInteger(inputParameters.getCarMaxLoadCapacity1Meas()))
	            .trspOpStrtAreaLineOneTxt(this.getOrDefault(inputParameters.getTrspOpStrtAreaLineOneTxt()))
	            .trspOpEndAreaLineOneTxt(this.getOrDefault(inputParameters.getTrspOpEndAreaLineOneTxt()))
	            .maxTrspOpDateTrmStrtDate(this.getOrDefault(inputParameters.getMaxTrspOpDateTrmStrtDate()))
	            .minTrspOpDateTrmStrtDate(this.getOrDefault(inputParameters.getMinTrspOpDateTrmStrtDate()))
	            .maxTrspOpDateTrmEndDate(this.getOrDefault(inputParameters.getMaxTrspOpDateTrmEndDate()))
	            .minTrspOpDateTrmEndDate(this.getOrDefault(inputParameters.getMinTrspOpDateTrmEndDate()))
	            .maxTrspOpPlanDateTrmStrtTime(this.getOrDefault(inputParameters.getMaxTrspOpPlanDateTrmStrtTime()))
	            .minTrspOpPlanDateTrmStrtTime(this.getOrDefault(inputParameters.getMinTrspOpPlanDateTrmStrtTime()))
	            .maxTrspOpPlanDateTrmEndTime(this.getOrDefault(inputParameters.getMaxTrspOpPlanDateTrmEndTime()))
	            .minTrspOpPlanDateTrmEndTime(this.getOrDefault(inputParameters.getMinTrspOpPlanDateTrmEndTime()))
	            .build();
	}
	
	/**
	 * 運送計画情報（明細型）キャリア向け運行依頼案件のリクエスト作成
	 */
	private CarrierApiRequest createCarrierTransCapacitySearchRequest(final InputParameters inputParameters) {
		return CarrierTransCapacitySearchRequest.builder()
				.serviceNo(this.getOrDefault(inputParameters.getServiceNo()))
	            .serviceName(this.getOrDefault(inputParameters.getServiceName()))
	            .carMaxLoadCapacity1Meas(this.convertStrToInteger(inputParameters.getCarMaxLoadCapacity1Meas()))
	            .trspOpStrtAreaLineOneTxt(this.getOrDefault(inputParameters.getTrspOpStrtAreaLineOneTxt()))
	            .trspOpEndAreaLineOneTxt(this.getOrDefault(inputParameters.getTrspOpEndAreaLineOneTxt()))
	            .maxTrspOpDateTrmStrtDate(this.getOrDefault(inputParameters.getMaxTrspOpDateTrmStrtDate()))
	            .minTrspOpDateTrmStrtDate(this.getOrDefault(inputParameters.getMinTrspOpDateTrmStrtDate()))
	            .maxTrspOpDateTrmEndDate(this.getOrDefault(inputParameters.getMaxTrspOpDateTrmEndDate()))
	            .minTrspOpDateTrmEndDate(this.getOrDefault(inputParameters.getMinTrspOpDateTrmEndDate()))
	            .maxTrspOpPlanDateTrmStrtTime(this.getOrDefault(inputParameters.getMaxTrspOpPlanDateTrmStrtTime()))
	            .minTrspOpPlanDateTrmStrtTime(this.getOrDefault(inputParameters.getMinTrspOpPlanDateTrmStrtTime()))
	            .maxTrspOpPlanDateTrmEndTime(this.getOrDefault(inputParameters.getMaxTrspOpPlanDateTrmEndTime()))
	            .minTrspOpPlanDateTrmEndTime(this.getOrDefault(inputParameters.getMinTrspOpPlanDateTrmEndTime()))
	            .build();
	}
	
	/**
	 * クエリパラメータの生成
	 */
	private String buildQueryParams(Object obj) throws Exception {
	    return Stream.of(obj.getClass().getDeclaredFields())
	            .peek(field -> field.setAccessible(true))
	            .map(field -> toQueryParam(field, obj))
	            .filter(param -> !param.isEmpty())
	            .collect(Collectors.joining("&"));
	}
	
	/**
	 * フィールドと値をクエリパラメータ形式に変換
	 */
	private String toQueryParam(Field field, Object obj){
		try {
			Object value = field.get(obj);
	        if (value != null) {
	            // @JsonPropertyの値を取得
	            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
	            String paramName = (jsonProperty != null && !jsonProperty.value().isEmpty())
	                    ? jsonProperty.value()
	                    : field.getName();

	            return URLEncoder.encode(paramName, StandardCharsets.UTF_8) + "=" +
	                    URLEncoder.encode(value.toString(), StandardCharsets.UTF_8);
	        }
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("クエリパラメータ生成中にエラーが発生しました。", e);
		}
        return "";
	}
	
	/**
	 * nullの場合はブランクを設定する
	 */
	private String getOrDefault(final String value) {
		return (value.isBlank()) ? null : value;
	}
	
	/**
	 * 文字列をIntegerに変換
	 */
	private Integer convertStrToInteger(final String value) {
		if(value == null || value.isBlank()) {
			return null;
		}
		
		try {
	        // 文字列を Integer に変換
	        return Integer.parseInt(value);
	    } catch (NumberFormatException e) {
	        throw new IllegalArgumentException("不正な数値データです。 value: " + value, e);
	    }
	}
}

package datahub.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import datahub.config.ApiConfig;
import datahub.model.CarrierApiRequest;
import datahub.model.InputParameters;
import datahub.util.CarrierApiUtil;

/**
 * CarrierAPIサービス
 * 
 */
@Service
public class CarrierApiService {
	
	private static final Logger logger = LoggerFactory.getLogger(CarrierApiService.class);
	
	@Autowired
	private CarrierApiUtil carrierApiUtil;
	
	@Autowired
	private ApiConfig apiConfig;
	
	private final String RESPONSE_KEY_ACCESS_TOKEN = "accessToken";
	
	private final String RESPONSE_KEY_ATTRIBUTE = "attribute";
	
	/**
	 * ログイン
	 */
	public String login() throws Exception{
		logger.info("認証開始");
		
		//レスポンスデータの取得
		String responseData = carrierApiUtil.login();
		
		// アクセストークンの抽出
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode tokenData = objectMapper.readTree(responseData).get(RESPONSE_KEY_ACCESS_TOKEN);
		
		logger.info("認証終了: OK");
		return tokenData.asText();
	}
	
	/**
	 * JSONデータ取得
	 * 
	 */
	public Optional<String> fetchJson(final InputParameters inputParameters, final String accessToken) throws Exception {
		logger.info("JSONデータ取得開始");
		
		//URLの取得
		String url  = carrierApiUtil.getUrl(inputParameters.getDataType());

		CarrierApiRequest carrierApiRequest = carrierApiUtil.createRequest(inputParameters);
		
		//リクエスト送信、レスポンス取得
		String responseData = carrierApiUtil.fetchJson(url, carrierApiRequest, accessToken);
		logger.info("response: \n" + responseData);
		
		// 情報区分に応じたデータを取得
		Optional<String> jsonData = this.getValueFromKey(inputParameters.getDataType(), responseData);		
		if (jsonData.isPresent() && apiConfig.isJsonOutputEnabled()) {
			logger.info("JSONデータ\n" + jsonData.get());
		}
		
		logger.info("JSONデータ取得終了");
		return jsonData;
	}
	
	/**
	 * 情報区分に応じたレスポンスデータを受け取る
	 *  
	 */
	private Optional<String> getValueFromKey(String dataType, String responseData) throws Exception {
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(responseData);
		
		//attributeの値を抽出
		JsonNode jsonData = rootNode.get(RESPONSE_KEY_ATTRIBUTE);
		String jsonDataStr = jsonData.toString();
		if (jsonData == null || jsonData.toString().isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.of(jsonDataStr);
	}
}

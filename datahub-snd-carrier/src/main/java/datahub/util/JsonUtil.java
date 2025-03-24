package datahub.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import datahub.common.Constants;
import datahub.common.DataTypeModelMapper;
import datahub.exception.DataHubException;

/**
 * JSON操作クラス
 * 
 */
@Component
public class JsonUtil {
	
	private ObjectMapper objectMapper = new ObjectMapper();
		
	/**
	 * JSONデータを読み込んでオブジェクトを生成
	 * 
	 */
	public Object convertJsonToObject(final String jsonData, final String dataType) {
		DataTypeModelMapper jsonType = DataTypeModelMapper.fromCode(dataType);
		Class<?> targetClass = jsonType.getClazz();
		
        try {
			return objectMapper.readValue(jsonData, targetClass);
		} catch (JsonProcessingException e) {
			throw new DataHubException(Constants.ERR_MSG,e);
		}
    }
}

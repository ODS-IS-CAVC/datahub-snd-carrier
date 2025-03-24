package datahub.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datahub.common.DataTypeModelMapper;
import datahub.exception.DataHubException;
import datahub.model.CarrierTrans;
import datahub.model.InputParameters;
import datahub.model.ShipperTransCapacity;
import datahub.model.Vehicle;
import datahub.util.JsonUtil;

/**
 * コンバートサービス
 *  
 */
@Service
public class ConvertService {
	
	private static final Logger logger = LoggerFactory.getLogger(ConvertService.class);
	
	@Autowired
	private JsonUtil jsonUtil;
	
	@Autowired
	private ConvertShipperTransCapacityToCsvService convertShipperTransCapacityToCsvService;
	
	@Autowired
	private ConvertVehicleToCsvService convertVehicleToCsvService;
	
	@Autowired
	private ConvertCarrierTransToCsvService convertCarrierTransToCsvService;
	
	/**
	 * CSVファイル出力
	 * 
	 */
	public List<String> writeJsonToCsv(final String jsonData, final InputParameters userInfo, String eventId) {
		
		String dataType = userInfo.getDataType();
		DataTypeModelMapper jsonType = DataTypeModelMapper.fromCode(dataType);
		//情報区分により個別JsonからCSVへ変換クラスに条件分岐
		switch(jsonType){
			case VEHICLE:
				Vehicle vehicle = (Vehicle) jsonUtil.convertJsonToObject(jsonData, dataType);
				return convertVehicleToCsvService.convertJsonToCsv(vehicle);
			case SHIPPER_TRANS_CAPACISTY:
				ShipperTransCapacity shipperTransCapacity = 
					(ShipperTransCapacity) jsonUtil.convertJsonToObject(jsonData, dataType);
				return convertShipperTransCapacityToCsvService.convertJsonToCsv(shipperTransCapacity);
			case CARRIER_TRANS_REQUEST:
				CarrierTrans carrierTransRequest = 
					(CarrierTrans) jsonUtil.convertJsonToObject(jsonData, dataType);
				return convertCarrierTransToCsvService.convertJsonToCsv(carrierTransRequest);
			case CARRIER_TRANS_CAPACITY:
				CarrierTrans carrierTransCapacity = 
					(CarrierTrans) jsonUtil.convertJsonToObject(jsonData, dataType);
			return convertCarrierTransToCsvService.convertJsonToCsv(carrierTransCapacity);
			default:
				throw new DataHubException("存在しない情報区分が選択されています。情報区分: " + dataType);
		}
	}
}

package datahub.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import datahub.exception.DataHubException;
import datahub.model.HazardousMaterialInfo;
import datahub.model.MaxCarryingCapacity;
import datahub.model.MotasInfo;
import datahub.model.Vehicle;
import datahub.model.VehicleDetails;
import datahub.model.VehicleInfo;

/**
 * 車輛マスタコンバートサービス
 *  
 */
@Service
public class ConvertVehicleToCsvService {
	
	private static final Logger logger = LoggerFactory.getLogger(ConvertVehicleToCsvService.class);
	
	private final String VEHICLE_INFO_HEADER = "\"registration_number\","
			+ "\"giai\","
            + "\"registration_transport_office_name\","
            + "\"registration_vehicle_type\","
            + "\"registration_vehicle_use\","
            + "\"registration_vehicle_id\","
            + "\"chassis_number\","
            + "\"vehicle_id\","
            + "\"operator_corporate_number\","
            + "\"operator_business_code\","
            + "\"owner_corporate_number\","
            + "\"owner_business_code\","
            + "\"vehicle_type\","
            + "\"hazardous_material_vehicle_type\","
            + "\"tractor\","
            + "\"trailer\"";
	
	private final String MOTAS_INFO_HEADER = "\"max_payload_1\","
            + "\"max_payload_2\","
            + "\"vehicle_weight\","
            + "\"vehicle_length\","
            + "\"vehicle_width\","
            + "\"vehicle_height\","
            + "\"hazardous_material_volume\","
            + "\"hazardous_material_specific_gravity\","
            + "\"expiry_date\","
            + "\"deregistration_status\"";
	
	private final String VEHICLE_DETAILS_HEADER = "\"bed_height\","
            + "\"cargo_height\","
            + "\"cargo_width\","
            + "\"cargo_length\","
            + "\"max_cargo_capacity\","
            + "\"body_type\","
            + "\"power_gate\","
            + "\"wing_doors\","
            + "\"refrigeration_unit\","
            + "\"temperature_range_min\","
            + "\"temperature_range_max\","
            + "\"crane_equipped\","
            + "\"vehicle_equipment_notes\","
            + "\"master_data_start_date\","
            + "\"master_data_end_date\"";
	
	private final String MAX_CARRYING_CAPACITY_HEADER = "\"package_code\","
            + "\"package_name_kanji\","
            + "\"width\","
            + "\"height\","
            + "\"depth\","
            + "\"dimension_unit_code\","
            + "\"max_load_quantity\"";
	
	private final String HAZARDOUS_MATERIAL_INFO_HEADER = "\"hazardous_material_item_code\","
            + "\"hazardous_material_text_info\"";
	
	private final String HEADER = String.join(",",
			VEHICLE_INFO_HEADER,
			MOTAS_INFO_HEADER,
			VEHICLE_DETAILS_HEADER,
			MAX_CARRYING_CAPACITY_HEADER,
			HAZARDOUS_MATERIAL_INFO_HEADER
			);
	
	/**
	 * JSONからCSVに変換
	 * 
	 */
	public List<String> convertJsonToCsv(final Vehicle vehicle) {
		
		logger.info("JSON->CSV変換開始");
		
	    List<String> csvRecords = new ArrayList<>();

	    // vehicle_info（車輛情報）のデータを取得
	    // 基本情報なので、持ちまわる
	    String vehicleInfoData = this.createVehicleInfoAsCsv(vehicle);
	    
	    // motas_info（MOTAS情報）のデータを取得
	    String motasInfoData = this.getMotasInfoAsCsv(vehicle.getMotasInfo(), vehicleInfoData);
	    csvRecords.add(motasInfoData);
	    
	    // vehicle_details（車輌情報詳細）のデータを取得
	    String vehicleDetailsData = this.getVehicleDetailsAsCsv(vehicle.getVehicleDetails(), vehicleInfoData);
	    csvRecords.add(vehicleDetailsData);
	    
	    // max_carrying_capacity（最大積載能力）のデータを取得
	    Optional<List<String>> maxCarryingCapacityData = this.getMaxCarryingCapacityAsCsv(
	    		vehicle.getMaxCarryingCapacityList(),
	    		vehicleInfoData
	    );
	    if (maxCarryingCapacityData.isPresent()) {
	    	csvRecords.addAll(maxCarryingCapacityData.get());
	    }
	    
	    // hazardous_material_info（危険物情報）のデータを取得
	    Optional<List<String>> hazardousMaterialInfoData = this.getHazardousMaterialInfoAsCsv(
	    		vehicle.getHazardousMaterialInfoList(),
	    		vehicleInfoData
	    );
	    if (hazardousMaterialInfoData.isPresent()) {
	    	csvRecords.addAll(hazardousMaterialInfoData.get());
	    }
	    
	    logger.info("JSON->CSV変換終了");

	    return csvRecords;
	}
	
	/**
	 * MOTAS情報をCSVレコード用に取得
	 */
	private String getMotasInfoAsCsv(final MotasInfo motasInfo, final String vehicleInfoData) {
		if (motasInfo == null || this.areAllFieldsNull(motasInfo)) {
	        throw new DataHubException("motas_info（MOTAS情報）のデータは0件です。");
	    }
		
		String motasInfoData = this.createMotasInfoAsCsv(motasInfo);
		
		int requiredBlankSize = 
				HEADER.split(",").length -
				VEHICLE_INFO_HEADER.split(",").length -
				MOTAS_INFO_HEADER.split(",").length;
		String blank = this.generateEmptyCsvRow(requiredBlankSize);
		
		return String.join(",", 
				vehicleInfoData,
				motasInfoData,
				blank
				);
	}
	
	/**
	 * 車輌情報詳細情報をCSVレコード用に取得
	 */
	private String getVehicleDetailsAsCsv(final VehicleDetails vehicleDetails, final String vehicleInfoData) {
		if (vehicleDetails == null || this.areAllFieldsNull(vehicleDetails)) {
	        throw new DataHubException("vehicle_details（車輛情報詳細）のデータは0件です。");
	    }
		
		String vehicleDetailsData = this.createVehicleDetailsAsCsv(vehicleDetails);
		
		int requiredBlankSize = 
				MOTAS_INFO_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(requiredBlankSize);
		requiredBlankSize =
				HEADER.split(",").length -
				VEHICLE_INFO_HEADER.split(",").length -
				MOTAS_INFO_HEADER.split(",").length -
				VEHICLE_DETAILS_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(requiredBlankSize);
		
		return String.join(",", 
				vehicleInfoData,
				blank1,
				vehicleDetailsData,
				blank2
				);
	}
	
	/**
	 * 最大積載能力をCSVレコード用に取得
	 */
	private Optional<List<String>> getMaxCarryingCapacityAsCsv(
			final List<MaxCarryingCapacity> maxCarryingCapacityList, 
			final String vehicleInfoData
			) {
		
		if (maxCarryingCapacityList == null) {
			logger.warn("max_carrying_capacity（最大積載能力）が0件です。");
			return Optional.empty();
	    }
		
		
		List<String> record = new ArrayList<>();
		for (MaxCarryingCapacity item : maxCarryingCapacityList) {
			
			if (this.areAllFieldsNull(item)) {
				logger.warn("max_carrying_capacity（最大積載能力）が0件です。");
				return Optional.empty();
			}
			
			// 最大積載能力の設定
			logger.debug("package_code（荷姿コード）: " + item.getPackageCode());
			String maxCarryingCapacityData = this.createMaxCarryingCapacity(item);
			
			// 空文字の設定
			int requiredBlankSize = 
					MOTAS_INFO_HEADER.split(",").length +
					VEHICLE_DETAILS_HEADER.split(",").length;
			String blank1 = this.generateEmptyCsvRow(requiredBlankSize);
			requiredBlankSize = HAZARDOUS_MATERIAL_INFO_HEADER.split(",").length;
			String blank2 = this.generateEmptyCsvRow(requiredBlankSize);

			String csvData = String.join(",", 
					vehicleInfoData,
					blank1,
					maxCarryingCapacityData,
					blank2
				);
			
			record.add(csvData);
		}
		
		return Optional.of(record);
	}
	
	/**
	 * 危険物情報をCSVレコード用に取得
	 */
	private Optional<List<String>> getHazardousMaterialInfoAsCsv(
			final List<HazardousMaterialInfo> hazardousMaterialInfoList, 
			final String vehicleInfoData
			) {
		
		if (hazardousMaterialInfoList == null) {
			logger.warn("hazardous_material_info（危険物情報）が0件です。");
			return Optional.empty();
	    }
		
		
		List<String> record = new ArrayList<>();
		for (HazardousMaterialInfo item : hazardousMaterialInfoList) {
			
			if (this.areAllFieldsNull(item)) {
				logger.warn("hazardous_material_info（危険物情報）が0件です。");
				return Optional.empty();
			}
			
			// 最大積載能力の設定
			logger.debug("hazardous_material_item_code（危険物積載物品名コード）: " + 
					item.getHazardousMaterialItemCode());
			String hazardousMaterialInfoData = this.createHazardousMaterialInfo(item);
			
			// 空文字の設定
			int requiredBlankSize = 
					MOTAS_INFO_HEADER.split(",").length +
					VEHICLE_DETAILS_HEADER.split(",").length +
					MAX_CARRYING_CAPACITY_HEADER.split(",").length;
			String blank = this.generateEmptyCsvRow(requiredBlankSize);
			
			String csvData = String.join(",", 
					vehicleInfoData,
					blank,
					hazardousMaterialInfoData
				);
			
			record.add(csvData);
		}
		
		return Optional.of(record);
	}
	
	/**
	 * 車輛情報をCSVデータを生成
	 */
	private String createVehicleInfoAsCsv(final Vehicle vehicle) {
		VehicleInfo vehicleInfo = vehicle.getVehicleInfo();
		if (vehicleInfo == null || this.areAllFieldsNull(vehicleInfo)) {
	        throw new DataHubException("vehicle_info（車輛情報）のデータは0件です。");
	    }
		
		return String.join(",",
				this.getOrDefault(vehicleInfo.getRegistrationNumber()),
				this.getOrDefault(vehicleInfo.getGiai()),
				this.getOrDefault(vehicleInfo.getRegistrationTransportOfficeName()),
	            this.getOrDefault(vehicleInfo.getRegistrationVehicleType()),
	            this.getOrDefault(vehicleInfo.getRegistrationVehicleUse()),
	            this.getOrDefault(vehicleInfo.getRegistrationVehicleId()),
	            this.getOrDefault(vehicleInfo.getChassisNumber()),
	            this.getOrDefault(vehicleInfo.getVehicleId()),
	            this.getOrDefault(vehicleInfo.getOperatorCorporateNumber()),
	            this.getOrDefault(vehicleInfo.getOperatorBusinessCode()),
	            this.getOrDefault(vehicleInfo.getOwnerCorporateNumber()),
	            this.getOrDefault(vehicleInfo.getOwnerBusinessCode()),
	            this.getOrDefault(vehicleInfo.getVehicleType()),
	            this.getOrDefault(vehicleInfo.getHazardousMaterialVehicleType()),
	            this.getOrDefault(vehicleInfo.getTractor()),
	            this.getOrDefault(vehicleInfo.getTrailer())
            );
	}
	
	/**
	 * MOTAS情報をCSVデータを生成
	 */
	private String createMotasInfoAsCsv(final MotasInfo motasInfo) {
		return String.join(",",
		        this.convertToStringOrEmpty(motasInfo.getMaxPayload1()),
		        this.convertToStringOrEmpty(motasInfo.getMaxPayload2()),
		        this.convertToStringOrEmpty(motasInfo.getVehicleWeight()),
		        this.convertToStringOrEmpty(motasInfo.getVehicleLength()),
		        this.convertToStringOrEmpty(motasInfo.getVehicleWidth()),
		        this.convertToStringOrEmpty(motasInfo.getVehicleHeight()),
		        this.convertToStringOrEmpty(motasInfo.getHazardousMaterialVolume()),
		        this.convertToStringOrEmpty(motasInfo.getHazardousMaterialSpecificGravity()),
		        this.getOrDefault(motasInfo.getExpiryDate()),
		        this.getOrDefault(motasInfo.getDeregistrationStatus())
	        );
	}
	
	/**
	 * 車輛情報詳細をCSVデータを生成
	 */
	private String createVehicleDetailsAsCsv(final VehicleDetails vehicleDetails) {
		return String.join(",",
		        this.convertToStringOrEmpty(vehicleDetails.getBedHeight()),
		        this.convertToStringOrEmpty(vehicleDetails.getCargoHeight()),
		        this.convertToStringOrEmpty(vehicleDetails.getCargoWidth()),
		        this.convertToStringOrEmpty(vehicleDetails.getCargoLength()),
		        this.convertToStringOrEmpty(vehicleDetails.getMaxCargoCapacity()),
		        this.getOrDefault(vehicleDetails.getBodyType()),
		        this.getOrDefault(vehicleDetails.getPowerGate()),
		        this.getOrDefault(vehicleDetails.getWingDoors()),
		        this.getOrDefault(vehicleDetails.getRefrigerationUnit()),
		        this.convertToStringOrEmpty(vehicleDetails.getTemperatureRangeMin()),
		        this.convertToStringOrEmpty(vehicleDetails.getTemperatureRangeMax()),
		        this.getOrDefault(vehicleDetails.getCraneEquipped()),
		        this.getOrDefault(vehicleDetails.getVehicleEquipmentNotes()),
		        this.getOrDefault(vehicleDetails.getMasterDataStartDate()),
		        this.getOrDefault(vehicleDetails.getMasterDataEndDate())
		    );
	}
	
	/**
	 * 最大積載能力をCSVデータを生成
	 */
	private String createMaxCarryingCapacity(final MaxCarryingCapacity maxCarryingCapacity) {
		return String.join(",",
		        this.getOrDefault(maxCarryingCapacity.getPackageCode()),
		        this.getOrDefault(maxCarryingCapacity.getPackageNameKanji()),
		        this.convertToStringFromBigDecimalOrEmpty(maxCarryingCapacity.getWidth()),
		        this.convertToStringFromBigDecimalOrEmpty(maxCarryingCapacity.getHeight()),
		        this.convertToStringFromBigDecimalOrEmpty(maxCarryingCapacity.getDepth()),
		        this.getOrDefault(maxCarryingCapacity.getDimensionUnitCode()),
		        this.convertToStringOrEmpty(maxCarryingCapacity.getMaxLoadQuantity())
		    );
	}
	
	/**
	 * 危険物情報をCSVデータを生成
	 */
	private String createHazardousMaterialInfo(final HazardousMaterialInfo hazardousMaterialInfo) {
		return String.join(",",
		        this.getOrDefault(hazardousMaterialInfo.getHazardousMaterialItemCode()),
		        this.getOrDefault(hazardousMaterialInfo.getHazardousMaterialTextInfo())
		    );
	}
	
	/**
	 * ダブルクォートを追加（String）
	 *
	 */
	private String getOrDefault(final String value) {
	    return (value == null) ? "\"\"" : "\"" + value + "\"";
	}

	/**
	 * ダブルクォートを追加（Object）
	 * 
	 */
	private String convertToStringOrEmpty(Object obj) {
	    return (obj != null) ? "\"" + String.valueOf(obj) + "\"" : "\"\"";
	}
	
	/**
	 * ダブルクォートを追加（BigDecimal）
	 * 
	 */
	private String convertToStringFromBigDecimalOrEmpty(BigDecimal dec) {
		return (dec != null) ? "\"" + dec.toPlainString() + "\"" : "\"\"";
	}
	
	/**
	 * 空白のレコード行を作成
	 * 
	 */
	private String generateEmptyCsvRow(final int size) {
	    return IntStream.range(0, size)
	            .mapToObj(i -> "\"\"") // ダブルクォートのみの文字列を生成
	            .collect(Collectors.joining(","));
	}
	
	/**
	 * 全てのフィールドがnullかどうかをチェック
	 * @throws IllegalAccessException 
	 */
	private boolean areAllFieldsNull(Object object) {
	    if (object == null) {
	        return true;
	    }
	    try {
	        for (Field field : object.getClass().getDeclaredFields()) {
	            field.setAccessible(true);
	            Object value = field.get(object);
	            if (value instanceof List) {
                    continue;
                }
	            
	            if (value != null) {
	            	return false;
	            }
	        }
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }
	    return true;
	}
}

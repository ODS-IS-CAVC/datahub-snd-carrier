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
import datahub.model.CarInfo;
import datahub.model.CutOffInfo;
import datahub.model.DrvAvbTime;
import datahub.model.DrvInfo;
import datahub.model.FreeTimeInfo;
import datahub.model.LogsSrvcPrv;
import datahub.model.MsgInfo;
import datahub.model.RoadCarr;
import datahub.model.ShipperTransCapacity;
import datahub.model.TrspAbilityLineItem;
import datahub.model.VehicleAvbResource;

/**
 * 運送能力情報用コンバートサービス
 * 
 */
@Service
public class ConvertShipperTransCapacityToCsvService {
	
	private static final Logger logger = LoggerFactory.getLogger(ConvertShipperTransCapacityToCsvService.class);
	
	private final String MSG_INFO_HEADER = "\"msg_id\","
				+ "\"msg_info_cls_typ_cd\","
				+ "\"msg_date_iss_dttm\","
				+ "\"msg_time_iss_dttm\","
				+ "\"msg_fn_stas_cd\","
				+ "\"note_dcpt_txt\"";

	private final String ROAD_CARR_HEADER = "\"trsp_cli_prty_head_off_id\","
		        + "\"trsp_cli_prty_brnc_off_id\","
		        + "\"trsp_cli_prty_name_txt\","
		        + "\"road_carr_depa_sped_org_id\","
		        + "\"road_carr_depa_sped_org_name_txt\","
		        + "\"trsp_cli_tel_cmm_cmp_num_txt\","
		        + "\"road_carr_arr_sped_org_id\","
		        + "\"road_carr_arr_sped_org_name_txt\"";
	
	private final String LOGS_SRVC_PRV_HEADER = "\"logs_srvc_prv_prty_head_off_id\","
		        + "\"logs_srvc_prv_prty_brnc_off_id\","
		        + "\"logs_srvc_prv_prty_name_txt\","
		        + "\"logs_srvc_prv_sct_sped_org_id\","
		        + "\"logs_srvc_prv_sct_sped_org_name_txt\","
		        + "\"logs_srvc_prv_sct_prim_cnt_pers_name_txt\","
		        + "\"logs_srvc_prv_sct_tel_cmm_cmp_num_txt\"";
	
	private final String CAR_INFO_HEADER = "\"service_no\","
				+ "\"service_name\","
				+ "\"service_strt_date\","
				+ "\"service_strt_time\","
				+ "\"service_end_date\","
				+ "\"service_end_time\","
				+ "\"freight_rate\","
				+ "\"car_ctrl_num_id\","
		        + "\"car_license_plt_num_id\","
		        + "\"giai\","
		        + "\"car_body_num_cd\","
		        + "\"car_cls_of_size_cd\","
		        + "\"tractor_idcr\","
		        + "\"trailer_license_plt_num_id\","
		        + "\"car_weig_meas\","
		        + "\"car_lngh_meas\","
		        + "\"car_wid_meas\","
		        + "\"car_hght_meas\","
		        + "\"car_max_load_capacity1_meas\","
		        + "\"car_max_load_capacity2_meas\","
		        + "\"car_vol_of_hzd_item_meas\","
		        + "\"car_spc_grv_of_hzd_item_meas\","
		        + "\"car_trk_bed_hght_meas\","
		        + "\"car_trk_bed_wid_meas\","
		        + "\"car_trk_bed_lngh_meas\","
		        + "\"car_trk_bed_grnd_hght_meas\","
		        + "\"car_max_load_vol_meas\","
		        + "\"pcke_frm_cd\","
		        + "\"pcke_frm_name_cd\","
		        + "\"car_max_untl_cp_quan\","
		        + "\"car_cls_of_shp_cd\","
		        + "\"car_cls_of_tlg_lftr_exst_cd\","
		        + "\"car_cls_of_wing_body_exst_cd\","
		        + "\"car_cls_of_rfg_exst_cd\","
		        + "\"trms_of_lwr_tmp_meas\","
		        + "\"trms_of_upp_tmp_meas\","
		        + "\"car_cls_of_crn_exst_cd\","
		        + "\"car_rmk_about_eqpm_txt\","
		        + "\"car_cmpn_name_of_gtp_crtf_exst_txt\"";
	
	private final String VEHICLE_AVB_RESOURCE_HEADER = "\"trsp_op_strt_area_line_one_txt\","
		        + "\"trsp_op_strt_area_cty_jis_cd\","
		        + "\"trsp_op_date_trm_strt_date\","
		        + "\"trsp_op_plan_date_trm_strt_time\","
		        + "\"trsp_op_end_area_line_one_txt\","
		        + "\"trsp_op_end_area_cty_jis_cd\","
		        + "\"trsp_op_date_trm_end_date\","
		        + "\"trsp_op_plan_date_trm_end_time\","
		        + "\"clb_area_txt\","
		        + "\"trms_of_clb_area_cd\","
		        + "\"avb_date_cll_date\","
		        + "\"avb_from_time_of_cll_time\","
		        + "\"avb_to_time_of_cll_time\","
		        + "\"delb_area_txt\","
		        + "\"trms_of_delb_area_cd\","
		        + "\"esti_del_date_prfm_dttm\","
		        + "\"avb_from_time_of_del_time\","
		        + "\"avb_to_time_of_del_time\","
		        + "\"avb_load_cp_of_car_meas\","
		        + "\"avb_load_vol_of_car_meas\","
		        + "\"pcke_frm_cd\","
		        + "\"avb_num_of_retb_cntn_of_car_quan\","
		        + "\"trk_bed_stas_txt\"";
	
	private final String CUT_OFF_INFO_HEADER = "\"cut_off_time\","
				+ "\"cut_off_fee\"";
	
	private final String FREE_TIME_INFO_HEADER = "\"free_time\","
			+ "\"free_time_fee\"";
	
	private final String DRV_INFO_HEADER = "\"drv_ctrl_num_id\","
		        + "\"drv_cls_of_drvg_license_cd\","
		        + "\"drv_cls_of_fkl_license_exst_cd\","
		        + "\"drv_rmk_about_drv_txt\","
		        + "\"drv_cmpn_name_of_gtp_crtf_exst_txt\"";
	
	private final String DRV_AVB_TIME_HEADER = "\"drv_avb_from_date\","
		        + "\"drv_avb_from_time_of_wrkg_time\","
		        + "\"drv_avb_to_date\","
		        + "\"drv_avb_to_time_of_wrkg_time\","
		        + "\"drv_wrkg_trms_txt\","
		        + "\"drv_frmr_optg_date\","
		        + "\"drv_frmr_op_end_time\"";
	
	private final String HEADER = String.join(",",
			MSG_INFO_HEADER,
			ROAD_CARR_HEADER,
			LOGS_SRVC_PRV_HEADER,
			CAR_INFO_HEADER,
			VEHICLE_AVB_RESOURCE_HEADER,
			CUT_OFF_INFO_HEADER,
			FREE_TIME_INFO_HEADER,
			DRV_INFO_HEADER,
			DRV_AVB_TIME_HEADER
			);
	
	/**
	 * JSONからCSVに変換
	 * 
	 */
	public List<String> convertJsonToCsv(final ShipperTransCapacity shipperTransCapacity) {
		
		logger.info("JSON->CSV変換開始");
		
	    List<String> csvRecords = new ArrayList<>();

	    // msg_info（メッセージ情報）のデータを取得
	    String msgInfoData = this.createMsgInfoAsCsv(shipperTransCapacity);
	    
	    // trsp_ability_line_item（運送能力明細）のデータを取得
	    List<String> trspAbilityLineItemData = this.getTrspAbilityLineItemAsCsv(shipperTransCapacity, msgInfoData);
	    if (trspAbilityLineItemData != null) {
	    	csvRecords.addAll(trspAbilityLineItemData);
	    }
	    	    
	    logger.info("JSON->CSV変換終了");

	    return csvRecords;
	}
	
	/**
	 * 運送能力明細をCSVレコード用に取得
	 */
	private List<String> getTrspAbilityLineItemAsCsv(final ShipperTransCapacity shipperTransCapacity, final String msgInfoData) {
		if (shipperTransCapacity.getTrspAbilityLineItem() == null) {
	        throw new DataHubException("trsp_ability_line_item（運送能力明細）のデータは0件です。");
	    }
		
		List<String> record = new ArrayList<>();
		for (TrspAbilityLineItem item : shipperTransCapacity.getTrspAbilityLineItem()) {
			
			// road_carr（運送事業者）を設定
			record.add(this.getRoadCarrAsCsv(item.getRoadCarr(), msgInfoData));
			
			// logs_srvc_prv（物流サービス提供者）のを設定
			Optional<String> logsSrvcPrvData = this.getLogsSrvcPrvAsCsv(item.getLogsSrvcPrv(), msgInfoData);
			if (logsSrvcPrvData.isPresent()) {
				record.add(logsSrvcPrvData.get());				
			}
			
			// car_info（車輛情報）のデータを取得
			List<CarInfo> carInfoList = item.getCarInfo();
			if (carInfoList == null) {
				throw new DataHubException("car_info（車輛情報）のデータが0件です。");
			}
			for (CarInfo carInfo : carInfoList) {
				record.addAll(this.getCarInfoAsCsv(carInfo, msgInfoData));
			}
			
			// drv_info（運転手情報）のデータを取得
			Optional<List<String>> dvrInfoList = this.getDvrInfoAsCsv(item.getDrvInfo(), msgInfoData);
		    if (dvrInfoList.isPresent()) {
		    	record.addAll(dvrInfoList.get());
		    }
		}
		
		return record;
	}
	
	/**
	 * 運転手情報をCSVレコード用に取得
	 */
	private Optional<List<String>> getDvrInfoAsCsv(final List<DrvInfo> drvInfoList, final String msgInfoData) {
		if (drvInfoList == null) {
	        logger.warn("drv_info（運転手情報）のデータは0件です。");
	        return Optional.empty();
	    }
		
		List<String> record = new ArrayList<>();
		for (DrvInfo drvInfo : drvInfoList) {
			
			if (drvInfo == null || this.areAllFieldsNull(drvInfo) ) {
				return Optional.empty();
			}
						
			logger.debug("drv_ctrl_num_id（運転手管理コード）: " + drvInfo.getDrvCtrlNumId());
			
			String drvInfoData = this.createDrvInfoAsCsv(drvInfo);
			
			List<String> drvInfoCsvData = this.getDrvAvbTimeAsCsv(
					drvInfo.getDrvAvbTime(), 
					msgInfoData, 
					drvInfoData);
			record.addAll(drvInfoCsvData);
		}
		
		return Optional.of(record);
	}
	
	/**
	 * 運送事業者をCSVレコード用に取得
	 */
	private String getRoadCarrAsCsv(final RoadCarr roadCarr, final String msgInfoData) {
		if (roadCarr == null || this.areAllFieldsNull(roadCarr)) {
	        throw new DataHubException("road_carr（運送事業者）が0件です。");
	    }
		String trspCliPrtyHeadOffId = roadCarr.getTrspCliPrtyHeadOffId();
		logger.debug("trsp_cli_prty_head_off_id（運送事業者コード）: " + trspCliPrtyHeadOffId);
		
		String roadCarrData = this.createRoadCarrAsCsv(roadCarr);
		int requiredBlankSize = 
				HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				ROAD_CARR_HEADER.split(",").length;
		
		return String.join(",",
				msgInfoData,
				roadCarrData,
				this.generateEmptyCsvRow(requiredBlankSize)
				);
	}
	
	/**
	 * 物流サービス提供者をCSVレコード用に取得
	 */
	private Optional<String> getLogsSrvcPrvAsCsv(final LogsSrvcPrv logsSrvcPrv, final String msgInfoData) {
		if (logsSrvcPrv == null || this.areAllFieldsNull(logsSrvcPrv)) {
	        logger.warn("logs_srvc_prv（物流サービス提供者）のデータは0件です。");
	        return Optional.empty();
	    } 
		String logsSrvcPrvPrtyHeadOffId = logsSrvcPrv.getLogsSrvcPrvPrtyHeadOffId();
		logger.debug("logs_srvc_prv_prty_head_off_id（物流サービス提供者コード（本社））: " + logsSrvcPrvPrtyHeadOffId);
		
		int requiredBlankSize = ROAD_CARR_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(requiredBlankSize);
		requiredBlankSize =
				HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				ROAD_CARR_HEADER.split(",").length -
				LOGS_SRVC_PRV_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(requiredBlankSize);
		
		String logsSrvcPrvData = this.createLogsSrvcPrvAsCsv(logsSrvcPrv);
		
		return Optional.of(String.join(",",
				msgInfoData,
				blank1,
				logsSrvcPrvData,
				blank2
				));
	}
	
	/**
	 * 車輌情報をCSVレコード用に取得
	 */
	private List<String> getCarInfoAsCsv(final CarInfo carInfo, final String msgInfoData) {
		
		if (carInfo == null) {
			throw new DataHubException("car_info（車輛情報）が0件です。");
	    }
		
		// 車輛情報の設定
		logger.debug("service_no（便・ダイヤ番号）: " + carInfo.getServiceNo());
		String carInfoData = this.createCarInfoAsCsv(carInfo);
		
		List<String> record = new ArrayList<>();
		
		for (VehicleAvbResource vehicleAvbResource : carInfo.getVehicleAvbResource()) {
			
			// 車輛情報可能リソース（cut_off_info、free_time_info以外)
			String vehicleAvbResourceData = this.getVehicleAvbResourceAsCsv(vehicleAvbResource, msgInfoData, carInfoData);		
			
			// cut_off_info（カットオフ情報）を取得し、レコードを生成
			Optional<List<String>> cutOffInfoCsvRecord = this.createCarInfoWithCutOffRecord(
					vehicleAvbResource.getCutOffInfoList(),
					msgInfoData,
					carInfoData,
					vehicleAvbResourceData
					);
			
			if (cutOffInfoCsvRecord.isPresent()) {
				record.addAll(cutOffInfoCsvRecord.get());
			}
			
			// free_time_info（フリータイム情報）を取得し、レコードを生成
			// 空データの場合は空文字を設定
			Optional<List<String>> freeTimeInfoCsvRecord = this.createCarInfoWithFreeTimeRecord(
					vehicleAvbResource.getFreeTimeInfoList(),
					msgInfoData,
					carInfoData,
					vehicleAvbResourceData
					);
			
			if (freeTimeInfoCsvRecord.isPresent()) {
				record.addAll(freeTimeInfoCsvRecord.get());
			}
			
			// cut_off_infoとfree_time_infoの双方がデータなしの場合、
			// cut_off_infoとfree_time_info以外の項目をレコードとして出力
			if (cutOffInfoCsvRecord.isEmpty() && freeTimeInfoCsvRecord.isEmpty()) {
				int requiredBlankSize = 
						ROAD_CARR_HEADER.split(",").length +
						LOGS_SRVC_PRV_HEADER.split(",").length;
				String blank1 = this.generateEmptyCsvRow(requiredBlankSize);
				
				requiredBlankSize = 
						HEADER.split(",").length -
						MSG_INFO_HEADER.split(",").length -
						ROAD_CARR_HEADER.split(",").length -
						LOGS_SRVC_PRV_HEADER.split(",").length -
						CAR_INFO_HEADER.split(",").length -
						VEHICLE_AVB_RESOURCE_HEADER.split(",").length;
				String blank2 = this.generateEmptyCsvRow(requiredBlankSize);
				
				record.add(String.join(",", 
						msgInfoData,
						blank1,				
						carInfoData,				
						vehicleAvbResourceData,
						blank2
						));
			}
		}
		
		return record;
	}
	
	/**
	 * 車輌稼働可能リソースをCSVレコード用に取得
	 */
	private String getVehicleAvbResourceAsCsv(
			final VehicleAvbResource vehicleAvbResource,
			final String msgInfoData,
			final String carInfoData) {
		
		if (vehicleAvbResource == null) {
			throw new DataHubException("vehicle_avb_resource（車輌稼働可能リソース）が0件です。");
		}
		
		String trspOpStrtAreaLineOneTxt = vehicleAvbResource.getTrspOpStrtAreaLineOneTxt();
		logger.debug("trsp_op_strt_area_line_one_txt（運行開始地域）: " + trspOpStrtAreaLineOneTxt);
		
		return this.createVehicleAvbResourceAsCsv(vehicleAvbResource);
	}
	
	/**
	 * カットオフ情報をCSVレコード用に取得
	 */
	private Optional<List<String>> createCarInfoWithCutOffRecord(
			final List<CutOffInfo> cutOffInfoList,
			final String msgInfoData,
			final String carInfoData,
			final String vehicleAvbResourceData) {
		
		// 空項目の設定
		int requiredBlankSize = 
				ROAD_CARR_HEADER.split(",").length +
				LOGS_SRVC_PRV_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(requiredBlankSize);
		
		List<String> csvRecord = new ArrayList<>();
		
		requiredBlankSize = 
				HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				ROAD_CARR_HEADER.split(",").length -
				LOGS_SRVC_PRV_HEADER.split(",").length -
				CAR_INFO_HEADER.split(",").length -
				VEHICLE_AVB_RESOURCE_HEADER.split(",").length -
				CUT_OFF_INFO_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(requiredBlankSize);
		
		if (cutOffInfoList == null) {
			logger.warn("cut_off_info（カットオフ情報）が0件です。");
			return Optional.empty();
		}
		
		// CSVレコード生成
		
		for (CutOffInfo cutOffInfo : cutOffInfoList) {
			String cutOffInfoData = this.createCutOffInfoAsCsv(cutOffInfo);
			String carInfoWithCutOffRecord = String.join(",",
					msgInfoData,
					blank1,				
					carInfoData,				
					vehicleAvbResourceData,
					cutOffInfoData,
					blank2
					);
			
			csvRecord.add(carInfoWithCutOffRecord);
		}
		
		return Optional.of(csvRecord);
	}
	
	/**
	 * フリータイム情報をCSVレコード用に取得
	 */
	private Optional<List<String>> createCarInfoWithFreeTimeRecord(
			final List<FreeTimeInfo> freeTimeInfoList,
			final String msgInfoData,
			final String carInfoData,
			final String vehicleAvbResourceData) {
		
		// 空項目の設定
		int requiredBlankSize = 
				ROAD_CARR_HEADER.split(",").length +
				LOGS_SRVC_PRV_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(requiredBlankSize);
		
		String blank2 = this.generateEmptyCsvRow(CUT_OFF_INFO_HEADER.split(",").length);
		
		requiredBlankSize = 
				HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				ROAD_CARR_HEADER.split(",").length -
				LOGS_SRVC_PRV_HEADER.split(",").length -
				CAR_INFO_HEADER.split(",").length -
				VEHICLE_AVB_RESOURCE_HEADER.split(",").length -
				CUT_OFF_INFO_HEADER.split(",").length -
				FREE_TIME_INFO_HEADER.split(",").length;
		String blank3 = this.generateEmptyCsvRow(requiredBlankSize);
		
		List<String> csvRecord = new ArrayList<>();
		
		if (freeTimeInfoList == null) {
			logger.warn("free_time_info（フリータイム情報）が0件です。");
			return Optional.empty();
		}
		
		// CSVレコード生成
		for (FreeTimeInfo freeTimeInfo : freeTimeInfoList) {
			String freeTimeInfoData = this.createFreeTimeInfoAsCsv(freeTimeInfo);
			String carInfoWithFreeTimeRecord = String.join(",",
					msgInfoData,
					blank1,				
					carInfoData,				
					vehicleAvbResourceData,
					blank2,
					freeTimeInfoData,
					blank3
					);
			
			csvRecord.add(carInfoWithFreeTimeRecord);
		}
		
		return Optional.of(csvRecord);
	}
	
	/**
	 * 運転手稼働可能時間をCSVレコード用に取得
	 */
	private List<String> getDrvAvbTimeAsCsv(
			final List<DrvAvbTime> drvAvbTimeList, 
			final String msgInfoData, 
			final String drvInfoData) {
		
		// 空項目を取得
		int requiredBlankSize = 
				HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				DRV_INFO_HEADER.split(",").length -
				DRV_AVB_TIME_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(requiredBlankSize);
		String blank2 = this.generateEmptyCsvRow(DRV_AVB_TIME_HEADER.split(",").length);
		
		List<String> record = new ArrayList<>();
		if (drvAvbTimeList == null) {
			logger.warn("drv_avb_time（運転手稼働可能時間）は0件です。");
			record.add(String.join(",",
					msgInfoData,
					blank1,
					drvInfoData,
					blank2
					));
			return record;
		}
		
		for (DrvAvbTime drvAvbTime : drvAvbTimeList) {
			record.add(String.join(",",
					msgInfoData,
					blank1,
					drvInfoData,
					this.createDrvAvbTimeAsCsv(drvAvbTime)
					));
		}
		
		return record;
	}
	
	//----------------------------------------------------------------------
	// CSVデータ生成メソッド
	//----------------------------------------------------------------------

	/**
	 * メッセージ情報をCSVデータを生成
	 */
	private String createMsgInfoAsCsv(final ShipperTransCapacity shipperTransCapacity) {
		MsgInfo msgInfo = shipperTransCapacity.getMsgInfo();
		if (msgInfo == null || this.areAllFieldsNull(msgInfo)) {
	        logger.warn("msg_info（メッセージ情報）のデータは0件です。");
	        int requiredBlankSize = MSG_INFO_HEADER.split(",").length;
	        return this.generateEmptyCsvRow(requiredBlankSize);
	    }
		return String.join(",",
			this.convertToStringOrEmpty(msgInfo.getMsgId()),
			this.getOrDefault(msgInfo.getMsgInfoClsTypCd()),
			this.getOrDefault(msgInfo.getMsgDateIssDttm()),
			this.convertToStringOrEmpty(msgInfo.getMsgTimeIssDttm()),
			this.getOrDefault(msgInfo.getMsgFnStasCd()),
			this.getOrDefault(msgInfo.getNoteDcptTxt()));
	}
	
	/**
	 * 運送事業者のCSVデータを生成
	 */
	private String createRoadCarrAsCsv(final RoadCarr roadCarr) {
	    return String.join(",",
	        this.getOrDefault(roadCarr.getTrspCliPrtyHeadOffId()),
	        this.getOrDefault(roadCarr.getTrspCliPrtyBrncOffId()),
	        this.getOrDefault(roadCarr.getTrspCliPrtyNameTxt()),
	        this.getOrDefault(roadCarr.getRoadCarrDepaSpedOrgId()),
	        this.getOrDefault(roadCarr.getRoadCarrArrSpedOrgNameTxt()),
	        this.getOrDefault(roadCarr.getTrspCliTelCmmCmpNumTxt()),
	        this.getOrDefault(roadCarr.getRoadCarrArrSpedOrgId()),
	        this.getOrDefault(roadCarr.getRoadCarrArrSpedOrgNameTxt())
	    );
	}
	
	/**
	 * 物流サービス提供者のCSVデータを生成
	 */
	private String createLogsSrvcPrvAsCsv(final LogsSrvcPrv logsSrvcPrv) {
	    return String.join(",",
	        this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvPrtyHeadOffId()),
	        this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvPrtyBrncOffId()),
	        this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvPrtyNameTxt()),
	        this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvSctSpedOrgId()),
	        this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvSctSpedOrgNameTxt()),
	        this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvSctPrimCntPersNameTxt()),
	        this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvSctTelCmmCmpNumTxt())
	    );
	}
	
	/**
	 * 車輛情報のCSVデータを生成
	 */
	private String createCarInfoAsCsv(final CarInfo carInfo) {
		return String.join(",",
			this.getOrDefault(carInfo.getServiceNo()),
	        this.getOrDefault(carInfo.getServiceName()),
	        this.getOrDefault(carInfo.getServiceStrtDate()),
	        this.getOrDefault(carInfo.getServiceStrtTime()),
	        this.getOrDefault(carInfo.getServiceEndDate()),
	        this.getOrDefault(carInfo.getServiceEndTime()),
	        this.convertToStringOrEmpty(carInfo.getFreightRate()),
	        this.getOrDefault(carInfo.getCarCtrlNumId()),
	        this.getOrDefault(carInfo.getCarLicensePltNumId()),
	        this.getOrDefault(carInfo.getGiai()),
	        this.getOrDefault(carInfo.getCarBodyNumCd()),
	        this.getOrDefault(carInfo.getCarClsOfSizeCd()),
	        this.getOrDefault(carInfo.getTractorIdcr()),
	        this.getOrDefault(carInfo.getTrailerLicensePltNumId()),
		    this.convertToStringOrEmpty(carInfo.getCarWeigMeas()),
		    this.convertToStringOrEmpty(carInfo.getCarLnghMeas()),
		    this.convertToStringOrEmpty(carInfo.getCarWidMeas()),
		    this.convertToStringOrEmpty(carInfo.getCarHghtMeas()),
		    this.convertToStringOrEmpty(carInfo.getCarMaxLoadCapacity1Meas()),
		    this.convertToStringOrEmpty(carInfo.getCarMaxLoadCapacity2Meas()),
		    this.convertToStringOrEmpty(carInfo.getCarVolOfHzdItemMeas()),
		    this.convertToStringOrEmpty(carInfo.getCarSpcGrvOfHzdItemMeas()),
		    this.convertToStringOrEmpty(carInfo.getCarTrkBedHghtMeas()),
		    this.convertToStringOrEmpty(carInfo.getCarTrkBedWidMeas()),
		    this.convertToStringOrEmpty(carInfo.getCarTrkBedLnghMeas()),
		    this.convertToStringOrEmpty(carInfo.getCarTrkBedGrndHghtMeas()),
		    this.convertToStringOrEmpty(carInfo.getCarMaxLoadVolMeas()),
		    this.getOrDefault(carInfo.getPckeFrmCd()),
	        this.getOrDefault(carInfo.getPckeFrmNameCd()),
	        this.convertToStringOrEmpty(carInfo.getCarMaxUntlCpQuan()),
	        this.getOrDefault(carInfo.getCarClsOfShpCd()),
	        this.getOrDefault(carInfo.getCarClsOfTlgLftrExstCd()),
	        this.getOrDefault(carInfo.getCarClsOfWingBodyExstCd()),
	        this.getOrDefault(carInfo.getCarClsOfRfgExstCd()),
	        this.convertToStringOrEmpty(carInfo.getTrmsOfLwrTmpMeas()),
	        this.convertToStringOrEmpty(carInfo.getTrmsOfUppTmpMeas()),
	        this.getOrDefault(carInfo.getCarClsOfCrnExstCd()),
	        this.getOrDefault(carInfo.getCarRmkAboutEqpmTxt()),
	        this.getOrDefault(carInfo.getCarCmpnNameOfGtpCrtfExstTxt())
			);
	}
	
	/**
	 * 車輌稼働可能リソースのCSVデータを生成
	 */
	private String createVehicleAvbResourceAsCsv(final VehicleAvbResource vehicleAvbResource) {
		return String.join(",",
			this.getOrDefault(vehicleAvbResource.getTrspOpStrtAreaLineOneTxt()),
	        this.getOrDefault(vehicleAvbResource.getTrspOpStrtAreaCtyJisCd()),
	        this.getOrDefault(vehicleAvbResource.getTrspOpDateTrmStrtDate()),
	        this.getOrDefault(vehicleAvbResource.getTrspOpPlanDateTrmStrtTime()),
	        this.getOrDefault(vehicleAvbResource.getTrspOpEndAreaLineOneTxt()),
	        this.getOrDefault(vehicleAvbResource.getTrspOpEndAreaCtyJisCd()),
	        this.getOrDefault(vehicleAvbResource.getTrspOpDateTrmEndDate()),
	        this.getOrDefault(vehicleAvbResource.getTrspOpPlanDateTrmEndTime()),
	        this.getOrDefault(vehicleAvbResource.getClbAreaTxt()),
	        this.getOrDefault(vehicleAvbResource.getTrmsOfClbAreaCd()),
	        this.getOrDefault(vehicleAvbResource.getAvbDateCllDate()),
	        this.getOrDefault(vehicleAvbResource.getAvbFromTimeOfCllTime()),
	        this.getOrDefault(vehicleAvbResource.getAvbToTimeOfCllTime()),
	        this.getOrDefault(vehicleAvbResource.getDelbAreaTxt()),
	        this.getOrDefault(vehicleAvbResource.getTrmsOfDelbAreaCd()),
	        this.getOrDefault(vehicleAvbResource.getEstiDelDatePrfmDttm()),
	        this.getOrDefault(vehicleAvbResource.getAvbFromTimeOfDelTime()),
	        this.getOrDefault(vehicleAvbResource.getAvbToTimeOfDelTime()),
	        this.convertToStringFromBigDecimalOrEmpty(vehicleAvbResource.getAvbLoadCpOfCarMeas()),
	        this.convertToStringOrEmpty(vehicleAvbResource.getAvbLoadVolOfCarMeas()),
	        this.getOrDefault(vehicleAvbResource.getPckeFrmCd()),
	        this.convertToStringOrEmpty(vehicleAvbResource.getAvbNumOfRetbCntnOfCarQuan()),
	        this.getOrDefault(vehicleAvbResource.getTrkBedStasTxt())
		    );
	}
	
	/**
	 * カットオフ情報のCSVデータを生成
	 */
	private String createCutOffInfoAsCsv(final CutOffInfo cutOffInfo) {
		return String.join(",",
			this.convertToStringOrEmpty(cutOffInfo.getCutOffTime()),
			this.convertToStringOrEmpty(cutOffInfo.getCutOffFee())
			);
	}
	
	/**
	 * フリータイム情報のCSVデータを生成
	 */
	private String createFreeTimeInfoAsCsv(final FreeTimeInfo freeTimeInfo) {
		return String.join(",",
			this.convertToStringOrEmpty(freeTimeInfo.getFreeTime()),
			this.convertToStringOrEmpty(freeTimeInfo.getFreeTimeFee())
			);
	}
	
	/**
	 * 運転手情報のCSVデータを生成
	 */
	private String createDrvInfoAsCsv(final DrvInfo drvInfo) {
		return String.join(",",
	        this.getOrDefault(drvInfo.getDrvCtrlNumId()),
	        this.getOrDefault(drvInfo.getDrvClsOfDrvgLicenseCd()),
	        this.getOrDefault(drvInfo.getDrvClsOfFklLicenseExstCd()),
	        this.getOrDefault(drvInfo.getDrvRmkAboutDrvTxt()),
	        this.getOrDefault(drvInfo.getDrvCmpnNameOfGtpCrtfExstTxt())
		    );
	}
	
	/**
	 * 運転手稼働可能時間のCSVデータを生成
	 */
	private String createDrvAvbTimeAsCsv(final DrvAvbTime drvAvbTime) {
		return String.join(",",
	        this.getOrDefault(drvAvbTime.getDrvAvbFromDate()),
	        this.getOrDefault(drvAvbTime.getDrvAvbFromTimeOfWrkgTime()),
	        this.getOrDefault(drvAvbTime.getDrvAvbToDate()),
	        this.getOrDefault(drvAvbTime.getDrvAvbToTimeOfWrkgTime()),
	        this.getOrDefault(drvAvbTime.getDrvWrkgTrmsTxt()),
	        this.getOrDefault(drvAvbTime.getDrvFrmrOptgDate()),
	        this.getOrDefault(drvAvbTime.getDrvFrmrOpEndTime())
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
	 * 
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

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
import datahub.model.CarrierTrans;
import datahub.model.CneePrty;
import datahub.model.Cns;
import datahub.model.CnsLineItem;
import datahub.model.CnsgPrty;
import datahub.model.CutOffInfo;
import datahub.model.DelInfo;
import datahub.model.FreeTimeInfo;
import datahub.model.FretClimToPrty;
import datahub.model.LogsSrvcPrv;
import datahub.model.MsgInfo;
import datahub.model.RoadCarr;
import datahub.model.ShipFromPrty;
import datahub.model.ShipFromPrtyRqrm;
import datahub.model.ShipToPrty;
import datahub.model.ShipToPrtyRqrm;
import datahub.model.TrspIsr;
import datahub.model.TrspPlan;
import datahub.model.TrspPlanLineItem;
import datahub.model.TrspRqrPrty;
import datahub.model.TrspSrvc;
import datahub.model.TrspVehicleTrms;

/**
 * 運送計画情報（明細型）コンバートサービス
 * 
 */
@Service
public class ConvertCarrierTransToCsvService {

	private static final Logger logger = LoggerFactory.getLogger(ConvertCarrierTransToCsvService.class);

	private final String MSG_INFO_HEADER = "\"msg_id\","
			+ "\"msg_info_cls_typ_cd\","
			+ "\"msg_date_iss_dttm\","
			+ "\"msg_time_iss_dttm\","
			+ "\"msg_fn_stas_cd\","
			+ "\"note_dcpt_txt\""
			+ "\"giai\"";

	private final String TRSP_PLAN_HEADER = "\"trsp_plan_stas_cd\"";

	private final String TRSP_ISR_HEADER = "\"trsp_instruction_id\","
			+ "\"trsp_instruction_date_subm_dttm\","
			+ "\"inv_num_id\","
			+ "\"cmn_inv_num_id\","
			+ "\"mix_load_num_id\"";

	private final String TRSP_SRVC_HEADER = "\"service_no\","
			+ "\"service_name\","
			+ "\"service_strt_date\","
			+ "\"service_strt_time\","
			+ "\"service_end_date\","
			+ "\"service_end_time\","
			+ "\"freight_rate\","
			+ "\"trsp_means_typ_cd\","
			+ "\"trsp_srvc_typ_cd\","
			+ "\"road_carr_srvc_typ_cd\","
			+ "\"trsp_root_prio_cd\","
			+ "\"car_cls_prio_cd\","
			+ "\"cls_of_carg_in_srvc_rqrm_cd\","
			+ "\"cls_of_pkg_up_srvc_rqrm_cd\","
			+ "\"pyr_cls_srvc_rqrm_cd\","
			+ "\"trms_of_mix_load_cnd_cd\","
			+ "\"dsed_cll_from_date\","
			+ "\"dsed_cll_to_date\","
			+ "\"dsed_cll_from_time\","
			+ "\"dsed_cll_to_time\","
			+ "\"dsed_cll_time_trms_srvc_rqrm_cd\","
			+ "\"aped_arr_from_date\","
			+ "\"aped_arr_to_date\","
			+ "\"aped_arr_from_time_prfm_dttm\","
			+ "\"aped_arr_to_time_prfm_dttm\","
			+ "\"aped_arr_time_trms_srvc_rqrm_cd\","
			+ "\"trms_of_mix_load_txt\","
			+ "\"trsp_srvc_note_one_txt\","
			+ "\"trsp_srvc_note_two_txt\"";

	private final String TRSP_VEHICLE_TRMS_HEADER = "\"car_cls_of_size_cd\","
			+ "\"car_cls_of_shp_cd\","
			+ "\"car_cls_of_tlg_lftr_exst_cd\","
			+ "\"car_cls_of_wing_body_exst_cd\","
			+ "\"car_cls_of_rfg_exst_cd\","
			+ "\"trms_of_lwr_tmp_meas\","
			+ "\"trms_of_upp_tmp_meas\","
			+ "\"car_cls_of_crn_exst_cd\","
			+ "\"car_rmk_about_eqpm_txt\"";

	private final String DEL_INFO_HEADER = "\"del_note_id\","
			+ "\"shpm_num_id\","
			+ "\"rced_ord_num_id\"";

	private final String CNS_HEADER = "\"istd_totl_pcks_quan\","
			+ "\"num_unt_cd\","
			+ "\"istd_totl_weig_meas\","
			+ "\"weig_unt_cd\","
			+ "\"istd_totl_vol_meas\","
			+ "\"vol_unt_cd\","
			+ "\"istd_totl_untl_quan\"";

	private final String CNS_LINE_ITEM_HEADER = "\"line_item_num_id\","
			+ "\"sev_ord_num_id\","
			+ "\"cnsg_crg_item_num_id\","
			+ "\"buy_assi_item_cd\","
			+ "\"sell_assi_item_cd\","
			+ "\"wrhs_assi_item_cd\","
			+ "\"item_name_txt\","
			+ "\"gods_idcs_in_ots_pcke_name_txt\","
			+ "\"num_of_istd_untl_quan\","
			+ "\"num_of_istd_quan\","
			+ "\"sev_num_unt_cd\","
			+ "\"istd_pcke_weig_meas\","
			+ "\"sev_weig_unt_cd\","
			+ "\"istd_pcke_vol_meas\","
			+ "\"sev_vol_unt_cd\","
			+ "\"istd_quan_meas\","
			+ "\"cnte_num_unt_cd\","
			+ "\"dcpv_trpn_pckg_txt\","
			+ "\"pcke_frm_cd\","
			+ "\"pcke_frm_name_cd\","
			+ "\"crg_hnd_trms_spcl_isrs_txt\","
			+ "\"glb_retb_asse_id\","
			+ "\"totl_rti_quan_quan\","
			+ "\"chrg_of_pcke_ctrl_num_unt_amnt\"";

	private final String CNSG_PRTY_HEADER = "\"cnsg_prty_head_off_id\","
			+ "\"cnsg_prty_brnc_off_id\","
			+ "\"cnsg_prty_name_txt\","
			+ "\"cnsg_sct_sped_org_id\","
			+ "\"cnsg_sct_sped_org_name_txt\","
			+ "\"cnsg_tel_cmm_cmp_num_txt\","
			+ "\"cnsg_pstl_adrs_line_one_txt\","
			+ "\"cnsg_pstc_cd\"";

	private final String TRSP_RQR_PRTY_HEADER = "\"trsp_rqr_prty_head_off_id\","
			+ "\"trsp_rqr_prty_brnc_off_id\","
			+ "\"trsp_rqr_prty_name_txt\","
			+ "\"trsp_rqr_sct_sped_org_id\","
			+ "\"trsp_rqr_sct_sped_org_name_txt\","
			+ "\"trsp_rqr_sct_tel_cmm_cmp_num_txt\","
			+ "\"trsp_rqr_pstl_adrs_line_one_txt\","
			+ "\"trsp_rqr_pstc_cd\"";

	private final String CNEE_PRTY_HEADER = "\"cnee_prty_head_off_id\","
			+ "\"cnee_prty_brnc_off_id\","
			+ "\"cnee_prty_name_txt\","
			+ "\"cnee_sct_id\","
			+ "\"cnee_sct_name_txt\","
			+ "\"cnee_prim_cnt_pers_name_txt\","
			+ "\"cnee_tel_cmm_cmp_num_txt\","
			+ "\"cnee_pstl_adrs_line_one_txt\","
			+ "\"cnee_pstc_cd\"";

	private final String LOGS_SRVC_PRV_HEADER = "\"logs_srvc_prv_prty_head_off_id\","
			+ "\"logs_srvc_prv_prty_brnc_off_id\","
			+ "\"logs_srvc_prv_prty_name_txt\","
			+ "\"logs_srvc_prv_sct_sped_org_id\","
			+ "\"logs_srvc_prv_sct_sped_org_name_txt\","
			+ "\"logs_srvc_prv_sct_prim_cnt_pers_name_txt\","
			+ "\"logs_srvc_prv_sct_tel_cmm_cmp_num_txt\"";

	private final String ROAD_CARR_HEADER = "\"trsp_cli_prty_head_off_id\","
			+ "\"trsp_cli_prty_brnc_off_id\","
			+ "\"trsp_cli_prty_name_txt\","
			+ "\"road_carr_depa_sped_org_id\","
			+ "\"road_carr_depa_sped_org_name_txt\","
			+ "\"trsp_cli_tel_cmm_cmp_num_txt\","
			+ "\"road_carr_arr_sped_org_id\","
			+ "\"road_carr_arr_sped_org_name_txt\"";

	private final String FRET_CLIM_TO_PRTY_HEADER = "\"fret_clim_to_prty_head_off_id\","
			+ "\"fret_clim_to_prty_brnc_off_id\","
			+ "\"fret_clim_to_prty_name_txt\","
			+ "\"fret_clim_to_sct_sped_org_id\","
			+ "\"fret_clim_to_sct_sped_org_name_txt\"";

	private final String SHIP_FROM_PRTY_HEADER = "\"ship_from_prty_head_off_id\","
			+ "\"ship_from_prty_brnc_off_id\","
			+ "\"ship_from_prty_name_txt\","
			+ "\"ship_from_sct_id\","
			+ "\"ship_from_sct_name_txt\","
			+ "\"ship_from_tel_cmm_cmp_num_txt\","
			+ "\"ship_from_pstl_adrs_cty_id\","
			+ "\"ship_from_pstl_adrs_id\","
			+ "\"ship_from_pstl_adrs_line_one_txt\","
			+ "\"ship_from_pstc_cd\","
			+ "\"plc_cd_prty_id\","
			+ "\"gln_prty_id\","
			+ "\"jpn_uplc_cd\","
			+ "\"jpn_van_srvc_cd\","
			+ "\"jpn_van_vans_cd\"";

	private final String SHIP_FROM_PRTY_RQRM_HEADER = "\"trms_of_car_size_cd\","
			+ "\"trms_of_car_hght_meas\","
			+ "\"trms_of_gtp_cert_txt\","
			+ "\"trms_of_cll_txt\","
			+ "\"trms_of_gods_hnd_txt\","
			+ "\"anc_wrk_of_cll_txt\","
			+ "\"spcl_wrk_txt\"";

	private final String CUT_OFF_INFO_HEADER = "\"cut_off_time\","
			+ "\"cut_off_fee\"";

	private final String SHIP_TO_PRTY_HEADER = "\"ship_to_prty_head_off_id\","
			+ "\"ship_to_prty_brnc_off_id\","
			+ "\"ship_to_prty_name_txt\","
			+ "\"ship_to_sct_id\","
			+ "\"ship_to_sct_name_txt\","
			+ "\"ship_to_prim_cnt_id\","
			+ "\"ship_to_prim_cnt_pers_name_txt\","
			+ "\"ship_to_tel_cmm_cmp_num_txt\","
			+ "\"ship_to_pstl_adrs_cty_id\","
			+ "\"ship_to_pstl_adrs_id\","
			+ "\"ship_to_pstl_adrs_line_one_txt\","
			+ "\"ship_to_pstc_cd\","
			+ "\"plc_cd_prty_id\","
			+ "\"gln_prty_id\","
			+ "\"jpn_uplc_cd\","
			+ "\"jpn_van_srvc_cd\","
			+ "\"jpn_van_vans_cd\"";

	private final String FREE_TIME_INFO_HEADER = "\"free_time\","
			+ "\"free_time_fee\"";

	private final String SHIP_TO_PRTY_RQRM_HEADER = "\"trms_of_car_size_cd\","
			+ "\"trms_of_car_hght_meas\","
			+ "\"trms_of_gtp_cert_txt\","
			+ "\"trms_of_del_txt\","
			+ "\"trms_of_gods_hnd_txt\","
			+ "\"anc_wrk_of_del_txt\","
			+ "\"spcl_wrk_txt\"";

	private final String HEADER = String.join(",",
			MSG_INFO_HEADER,
			TRSP_PLAN_HEADER,
			TRSP_ISR_HEADER,
			TRSP_SRVC_HEADER,
			TRSP_VEHICLE_TRMS_HEADER,
			DEL_INFO_HEADER,
			CNS_HEADER,
			CNS_LINE_ITEM_HEADER,
			CNSG_PRTY_HEADER,
			TRSP_RQR_PRTY_HEADER,
			CNEE_PRTY_HEADER,
			LOGS_SRVC_PRV_HEADER,
			ROAD_CARR_HEADER,
			FRET_CLIM_TO_PRTY_HEADER,
			SHIP_FROM_PRTY_HEADER,
			SHIP_FROM_PRTY_RQRM_HEADER,
			CUT_OFF_INFO_HEADER,
			SHIP_TO_PRTY_HEADER,
			FREE_TIME_INFO_HEADER,
			SHIP_TO_PRTY_RQRM_HEADER);

	/**
	 * JSONからCSVに変換
	 */
	public List<String> convertJsonToCsv(final CarrierTrans carrierTrans) {

		logger.info("JSON->CSV変換開始");

		List<String> csvRecords = new ArrayList<>();

		// msg_info（メッセージ情報）のデータを取得
		String msgInfoData = this.createMsgInfoAsCsv(carrierTrans);

		// trsp_plan（運送計画）のデータを取得
		String trspPlanData = this.createTrspPlanAsCsv(carrierTrans);

		// trsp_plan_line_item（運送計画明細）のデータを取得
		List<String> trspPlanLineItemList = this.getTrspPlanLineItemAsCsv(carrierTrans, msgInfoData, trspPlanData);

		//trsp_plan_line_item（運送計画明細）のデータをレコードに追加
		csvRecords.addAll(trspPlanLineItemList);

		logger.info("JSON->CSV変換終了");

		return csvRecords;
	}

	/**
	 * 運転計画明細をCSVレコード用に取得
	 */
	private List<String> getTrspPlanLineItemAsCsv(
			final CarrierTrans carrierTrans,
			final String msgInfoData,
			final String trspPlanData) {
		//trsp_plan_line_item（運送計画明細）の取得
		List<TrspPlanLineItem> trspPlanLineItemList = carrierTrans.getTrspPlanLineItem();
		//データがない場合は異常終了
		if (trspPlanLineItemList == null) {
			throw new DataHubException("trsp_plan_line_item（運送計画明細）のデータは0件です。");
		}

		List<String> record = new ArrayList<>();
		//繰り返し処理
		for (TrspPlanLineItem item : trspPlanLineItemList) {

			// trsp_isr（運送依頼）の取得
			String trspIsrData = this.getTrspIsrAsCsv(item.getTrspIsr(), msgInfoData, trspPlanData);
			record.add(trspIsrData);

			// trsp_srvc（運送サービス）の取得
			String trspSrvcData = this.getTrspSrvcAsCsv(item.getTrspSrvc(), msgInfoData, trspPlanData);
			record.add(trspSrvcData);

			// trsp_vehicle_trms（運送車輌条件）の取得
			Optional<String> trspVehicleTrmsData = this.getTrspVehicleTrmsAsCsv(item.getTrspVehicleTrms(), msgInfoData,
					trspPlanData);
			//データがあればレコード追加
			if (!trspVehicleTrmsData.isEmpty()) {
				record.add(trspVehicleTrmsData.get());
			}

			// del_info（納品情報）の取得
			Optional<String> delInfoData = this.getDelInfoAsCsv(item.getDelInfo(), msgInfoData, trspPlanData);
			//データがあればレコード追加
			if (!delInfoData.isEmpty()) {
				record.add(delInfoData.get());
			}

			// cns（委託貨物）の取得
			Optional<String> cnsRecord = this.getCnsAsCsv(item.getCns(), msgInfoData, trspPlanData);
			record.add(cnsRecord.get());

			// cns_line_item（貨物明細）の取得
			Optional<List<String>> cnsLineItemList = this.getCnsLineItemAsCsv(item.getCnsLineItemList(), item.getCns(),
					msgInfoData, trspPlanData);
			if (cnsLineItemList.isPresent()) {
				record.addAll(cnsLineItemList.get());
			}

			// cnsg_prty（荷送人）の取得
			Optional<String> cnsgPrtyData = this.getCnsgPrtyAsCsv(item.getCnsgPrty(), msgInfoData, trspPlanData);
			if (cnsgPrtyData.isPresent()) {
				record.add(cnsgPrtyData.get());
			}

			// trsp_rqr_prty（運送依頼者）の取得
			Optional<String> trspRqrPrtyData = this.getTrspRqrPrtyAsCsv(item.getTrspRqrPrty(), msgInfoData,
					trspPlanData);
			if (trspRqrPrtyData.isPresent()) {
				record.add(trspRqrPrtyData.get());
			}

			// cnee_prty（荷受人）の取得
			Optional<String> cneePrty = this.getCneePrtyAsCsv(item.getCneePrty(), msgInfoData, trspPlanData);
			if (cneePrty.isPresent()) {
				record.add(cneePrty.get());
			}

			// logs_srvc_prv（物流サービス提供者）の取得
			Optional<String> logsSrvcPrv = this.getLogsSrvcPrvAsCsv(item.getLogsSrvcPrv(), msgInfoData, trspPlanData);
			if (logsSrvcPrv.isPresent()) {
				record.add(logsSrvcPrv.get());
			}

			// road_carr（運送事業者）の取得
			Optional<String> roadCarr = this.getRoadCarrAsCsv(item.getRoadCarr(), msgInfoData, trspPlanData);
			if (logsSrvcPrv.isPresent()) {
				record.add(roadCarr.get());
			}

			// fret_clim_to_prty（運賃請求先）の取得
			Optional<String> fretClimToPrty = this.getFretClimToPrtyAsCsv(item.getFretClimToPrty(), msgInfoData,
					trspPlanData);
			if (fretClimToPrty.isPresent()) {
				record.add(fretClimToPrty.get());
			}

			// ship_from_prty（出荷場所）の取得
			Optional<List<String>> shipFromPrtyList = this.getShipFromPrtyAsCsv(item.getShipFromPrtyList(), msgInfoData,
					trspPlanData);
			if (shipFromPrtyList.isPresent()) {
				record.addAll(shipFromPrtyList.get());
			}

			// ship_to_prty（荷届先）の取得
			Optional<List<String>> shipToPrtyList = this.getShipToPrtyAsCsv(item.getShipToPrtyList(), msgInfoData,
					trspPlanData);
			if (shipToPrtyList.isPresent()) {
				record.addAll(shipToPrtyList.get());
			}
		}

		return record;
	}

	/**
	 * 運送依頼をCSVレコード用に取得
	 */
	private String getTrspIsrAsCsv(
			final TrspIsr trspIsr,
			final String msgInfoData,
			final String trspPlanData) {
		//データがない場合は異常終了
		if (trspIsr == null || this.areAllFieldsNull(trspIsr)) {
			throw new DataHubException("trsp_isr（運送依頼）のデータは0件です。");
		}
		logger.debug("trsp_instruction_id（運送依頼番号）: " + trspIsr.getTrspInstructionId());
		
		//trsp_isr（運送依頼）の取得
		String trspIsrData = this.createTrspIsrAsCsv(trspIsr);
		
		//空白の作成
		int requiredBlankSize = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length;
		String blank = this.generateEmptyCsvRow(requiredBlankSize);
		
		//レコードの作成
		return String.join(",",
				msgInfoData,
				trspPlanData,
				trspIsrData,
				blank);
	}

	/**
	 * 運送サービスをCSVレコード用に取得
	 */
	private String getTrspSrvcAsCsv(
			final TrspSrvc trspSrvc,
			final String msgInfoData,
			final String trspPlanData) {
		//データがない場合は異常終了
		if (trspSrvc == null || this.areAllFieldsNull(trspSrvc)) {
			throw new DataHubException("trsp_srvc（運送サービス）のデータは0件です。");
		}
		logger.debug("service_no（便・ダイヤ番号）: " + trspSrvc.getServiceNo());
		
		//trsp_srvc（運送サービス）の取得
		String trspSrvcData = this.createTrspSrvcAsCsv(trspSrvc);
		
		//空白の作成
		int requiredBlankSize = TRSP_ISR_HEADER.split(",").length;
		String balnk1 = this.generateEmptyCsvRow(requiredBlankSize);

		requiredBlankSize = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(requiredBlankSize);
		
		//レコードの作成
		return String.join(",",
				msgInfoData,
				trspPlanData,
				balnk1,
				trspSrvcData,
				blank2);
	}

	/**
	 * 運送車輌条件をCSVレコード用に取得
	 */
	private Optional<String> getTrspVehicleTrmsAsCsv(
			final TrspVehicleTrms trspVehicleTrms,
			final String msgInfoData,
			final String trspPlanData) {
		//データがない場合はログを出力
		if (trspVehicleTrms == null || this.areAllFieldsNull(trspVehicleTrms)) {
			logger.warn("trsp_vehicle_trms（運送車輌条件）のデータは0件です。");
			return Optional.empty();
		}

		logger.debug("car_cls_of_size_cd（車輌種別）: " + trspVehicleTrms.getCarClsOfSizeCd());
		
		//TrspVehicleTrms（運送車輌条件）の取得
		String trspVehicleTrmsData = this.createTrspVehicleTrmsAsCsv(trspVehicleTrms);

		//空白の作成
		int requiredBlankSize = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(requiredBlankSize);
		requiredBlankSize = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length -
				TRSP_VEHICLE_TRMS_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(requiredBlankSize);

		//レコードの作成
		return Optional.of(String.join(",",
				msgInfoData,
				trspPlanData,
				blank1,
				trspVehicleTrmsData,
				blank2));
	}

	/**
	 * 納品情報をCSVレコード用に取得
	 */
	private Optional<String> getDelInfoAsCsv(
			final DelInfo delInfo,
			final String msgInfoData,
			final String trspPlanData) {
		//データがない場合はログを出力
		if (delInfo == null || this.areAllFieldsNull(delInfo)) {
			logger.warn("del_info（納品情報）のデータは0件です。");
			return Optional.empty();
		}

		logger.debug("del_note_id（納品伝票ID）: " + delInfo.getDelNoteId());
		
		//del_info（納品情報）の取得
		String delInfoData = this.createDelInfoAsCsv(delInfo);

		//空白の作成
		int blank1Size = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length +
				TRSP_VEHICLE_TRMS_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(blank1Size);

		int blank2Size = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length -
				TRSP_VEHICLE_TRMS_HEADER.split(",").length -
				DEL_INFO_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(blank2Size);

		//レコードの作成
		return Optional.of(String.join(",",
				msgInfoData,
				trspPlanData,
				blank1,
				delInfoData,
				blank2));
	}

	/**
	 * 委託貨物をCSVレコード用に取得
	 */
	private Optional<String> getCnsAsCsv(
			final Cns cns,
			final String msgInfoData,
			final String trspPlanData) {

		//データがない場合は異常終了
		if (cns == null || this.areAllFieldsNull(cns)) {
			throw new DataHubException("cns（委託貨物）のデータは0件です。");
		}

		logger.debug("istd_totl_pcks_quan（運送梱包総個数（依頼））: " + cns.getIstdTotlPcksQuan());

		//cns（委託貨物）の取得
		String cnsData = this.createCnsAsCsv(cns);

		//空白の作成
		int blank1Size = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length +
				TRSP_VEHICLE_TRMS_HEADER.split(",").length +
				DEL_INFO_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(blank1Size);

		int blank2Size = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length -
				TRSP_VEHICLE_TRMS_HEADER.split(",").length -
				DEL_INFO_HEADER.split(",").length -
				CNS_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(blank2Size);

		//レコードの作成
		return Optional.of(String.join(",",
				msgInfoData,
				trspPlanData,
				blank1,
				cnsData,
				blank2));
	}

	/**
	 * 貨物明細をCSVレコード用に取得
	 */
	private Optional<List<String>> getCnsLineItemAsCsv(
			final List<CnsLineItem> cnsLineItemList,
			final Cns cnsData,
			final String msgInfoData,
			final String trspPlanData) {
		
		//空白の作成
		int blank1Size = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length +
				TRSP_VEHICLE_TRMS_HEADER.split(",").length +
				DEL_INFO_HEADER.split(",").length +
				CNS_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(blank1Size);

		int blank2Size = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length -
				TRSP_VEHICLE_TRMS_HEADER.split(",").length -
				DEL_INFO_HEADER.split(",").length -
				CNS_HEADER.split(",").length -
				CNS_LINE_ITEM_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(blank2Size);

		List<String> record = new ArrayList<>();

		//データがない場合はログを出力
		if (cnsLineItemList == null) {
			logger.warn("cns_line_item（貨物明細）のデータは0件です。");
			return Optional.empty();
		}
		
		//繰り返し処理
		for (CnsLineItem cnsLineItem : cnsLineItemList) {

			//データがない場合はログを出力
			if (this.areAllFieldsNull(cnsLineItem)) {
				logger.debug("cns_line_item（貨物明細）のデータは0件です。");
				return Optional.empty();
			}

			//cns_line_item（貨物明細）の取得
			String cnsLineItemData = this.createCnsLineItemAsCsv(cnsLineItem);

			//レコードの作成
			String csvData = String.join(",",
					msgInfoData,
					trspPlanData,
					blank1,
					cnsLineItemData,
					blank2);

			record.add(csvData);
		}

		return Optional.of(record);
	}

	/**
	 * 荷送人をCSVレコード用に取得
	 */
	private Optional<String> getCnsgPrtyAsCsv(
			final CnsgPrty cnsgPrty,
			final String msgInfoData,
			final String trspPlanData) {
		
		//データがない場合はログを出力
		if (cnsgPrty == null || this.areAllFieldsNull(cnsgPrty)) {
			logger.warn("cnsg_prty（荷送人）のデータは0件です。");
			return Optional.empty();
		}

		logger.debug("cnsg_prty_head_off_id（荷送人コード（本社））: " + cnsgPrty.getCnsgPrtyHeadOffId());

		//ccnsg_prty（荷送人）の取得
		String cnsgPrtyData = this.createCnsgPrtyAsCsv(cnsgPrty);
		
		//空白の作成
		int blank1Size = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length +
				TRSP_VEHICLE_TRMS_HEADER.split(",").length +
				DEL_INFO_HEADER.split(",").length +
				CNS_HEADER.split(",").length +
				CNS_LINE_ITEM_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(blank1Size);

		int blank2Size = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length -
				TRSP_VEHICLE_TRMS_HEADER.split(",").length -
				DEL_INFO_HEADER.split(",").length -
				CNS_HEADER.split(",").length -
				CNS_LINE_ITEM_HEADER.split(",").length -
				CNSG_PRTY_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(blank2Size);

		//レコードの作成
		return Optional.of(String.join(",",
				msgInfoData,
				trspPlanData,
				blank1,
				cnsgPrtyData,
				blank2));
	}

	/**
	 * 運送依頼者をCSVレコード用に取得
	 */
	private Optional<String> getTrspRqrPrtyAsCsv(
			final TrspRqrPrty trspRqrPrty,
			final String msgInfoData,
			final String trspPlanData) {
		
		//データがない場合はログを出力
		if (trspRqrPrty == null || this.areAllFieldsNull(trspRqrPrty)) {
			logger.warn("trsp_rqr_prty（運送依頼者）のデータは0件です。");
			return Optional.empty();

		}

		logger.debug("trsp_rqr_prty_head_off_id（運送依頼者コード（本社））: " + trspRqrPrty.getTrspRqrPrtyHeadOffId());

		//trsp_rqr_prty（運送依頼者）の取得
		String cnsgPrtyData = this.createTrspRqrPrtyAsCsv(trspRqrPrty);
		
		//空白の作成
		int blank1Size = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length +
				TRSP_VEHICLE_TRMS_HEADER.split(",").length +
				DEL_INFO_HEADER.split(",").length +
				CNS_HEADER.split(",").length +
				CNS_LINE_ITEM_HEADER.split(",").length +
				CNSG_PRTY_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(blank1Size);

		int blank2Size = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length -
				TRSP_VEHICLE_TRMS_HEADER.split(",").length -
				DEL_INFO_HEADER.split(",").length -
				CNS_HEADER.split(",").length -
				CNS_LINE_ITEM_HEADER.split(",").length -
				CNSG_PRTY_HEADER.split(",").length -
				TRSP_RQR_PRTY_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(blank2Size);

		//レコードの作成
		return Optional.of(String.join(",",
				msgInfoData,
				trspPlanData,
				blank1,
				cnsgPrtyData,
				blank2));
	}

	/**
	 * 荷受人をCSVレコード用に取得
	 */
	private Optional<String> getCneePrtyAsCsv(
			final CneePrty cneePrty,
			final String msgInfoData,
			final String trspPlanData) {
		
		//データがない場合はログを出力
		if (cneePrty == null || this.areAllFieldsNull(cneePrty)) {
			logger.warn("cnee_prty（荷受人）のデータは0件です。");
			return Optional.empty();
		}

		logger.debug("cnee_prty_head_off_id（荷受人コード（本社））: " + cneePrty.getCneePrtyHeadOffId());
		
		//cnee_prty（荷受人）の取得
		String cnsgPrtyData = this.createCneePrtyAsCsv(cneePrty);

		//空白の作成
		int blank1Size = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length +
				TRSP_VEHICLE_TRMS_HEADER.split(",").length +
				DEL_INFO_HEADER.split(",").length +
				CNS_HEADER.split(",").length +
				CNS_LINE_ITEM_HEADER.split(",").length +
				CNSG_PRTY_HEADER.split(",").length +
				TRSP_RQR_PRTY_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(blank1Size);

		int blank2Size = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length -
				TRSP_VEHICLE_TRMS_HEADER.split(",").length -
				DEL_INFO_HEADER.split(",").length -
				CNS_HEADER.split(",").length -
				CNS_LINE_ITEM_HEADER.split(",").length -
				CNSG_PRTY_HEADER.split(",").length -
				TRSP_RQR_PRTY_HEADER.split(",").length -
				CNEE_PRTY_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(blank2Size);

		//レコードの作成
		return Optional.of(String.join(",",
				msgInfoData,
				trspPlanData,
				blank1,
				cnsgPrtyData,
				blank2));
	}

	/**
	 * 物流サービス提供者をCSVレコード用に取得
	 */
	private Optional<String> getLogsSrvcPrvAsCsv(
			final LogsSrvcPrv logsSrvcPrv,
			final String msgInfoData,
			final String trspPlanData) {
		
		//データがない場合はログを出力
		if (logsSrvcPrv == null || this.areAllFieldsNull(logsSrvcPrv)) {
			logger.warn("logs_srvc_prv（物流サービス提供者）のデータは0件です。");
			return Optional.empty();
		}

		logger.debug("logs_srvc_prv_prty_head_off_id（物流サービス提供者コード（本社））: " + logsSrvcPrv.getLogsSrvcPrvPrtyHeadOffId());

		//logs_srvc_prv（物流サービス提供者）の取得
		String cnsgPrtyData = this.createLogsSrvcPrvAsCsv(logsSrvcPrv);
		
		//空白の作成
		int blank1Size = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length +
				TRSP_VEHICLE_TRMS_HEADER.split(",").length +
				DEL_INFO_HEADER.split(",").length +
				CNS_HEADER.split(",").length +
				CNS_LINE_ITEM_HEADER.split(",").length +
				CNSG_PRTY_HEADER.split(",").length +
				TRSP_RQR_PRTY_HEADER.split(",").length +
				CNEE_PRTY_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(blank1Size);

		int blank2Size = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length -
				TRSP_VEHICLE_TRMS_HEADER.split(",").length -
				DEL_INFO_HEADER.split(",").length -
				CNS_HEADER.split(",").length -
				CNS_LINE_ITEM_HEADER.split(",").length -
				CNSG_PRTY_HEADER.split(",").length -
				TRSP_RQR_PRTY_HEADER.split(",").length -
				CNEE_PRTY_HEADER.split(",").length -
				LOGS_SRVC_PRV_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(blank2Size);

		//レコードの作成
		return Optional.of(String.join(",",
				msgInfoData,
				trspPlanData,
				blank1,
				cnsgPrtyData,
				blank2));
	}

	/**
	 * 運送事業者提供者をCSVレコード用に取得
	 */
	private Optional<String> getRoadCarrAsCsv(
			final RoadCarr roadCarr,
			final String msgInfoData,
			final String trspPlanData) {
		
		//データがない場合はログを出力
		if (roadCarr == null || this.areAllFieldsNull(roadCarr)) {
			logger.warn("road_carr（運送事業者）のデータは0件です。");
			return Optional.empty();
		}

		logger.debug("trsp_cli_prty_head_off_id（運送事業者コード（本社））: " + roadCarr.getTrspCliPrtyHeadOffId());

		//road_carr（運送事業者）の取得
		String cnsgPrtyData = this.createRoadCarrAsCsv(roadCarr);

		//空白の作成
		int blank1Size = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length +
				TRSP_VEHICLE_TRMS_HEADER.split(",").length +
				DEL_INFO_HEADER.split(",").length +
				CNS_HEADER.split(",").length +
				CNS_LINE_ITEM_HEADER.split(",").length +
				CNSG_PRTY_HEADER.split(",").length +
				TRSP_RQR_PRTY_HEADER.split(",").length +
				CNEE_PRTY_HEADER.split(",").length +
				LOGS_SRVC_PRV_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(blank1Size);

		int blank2Size = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length -
				TRSP_VEHICLE_TRMS_HEADER.split(",").length -
				DEL_INFO_HEADER.split(",").length -
				CNS_HEADER.split(",").length -
				CNS_LINE_ITEM_HEADER.split(",").length -
				CNSG_PRTY_HEADER.split(",").length -
				TRSP_RQR_PRTY_HEADER.split(",").length -
				CNEE_PRTY_HEADER.split(",").length -
				LOGS_SRVC_PRV_HEADER.split(",").length -
				ROAD_CARR_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(blank2Size);

		//レコードの作成
		return Optional.of(String.join(",",
				msgInfoData,
				trspPlanData,
				blank1,
				cnsgPrtyData,
				blank2));
	}

	/**
	 * 運賃請求先をCSVレコード用に取得
	 */
	private Optional<String> getFretClimToPrtyAsCsv(
			final FretClimToPrty fretClimToPrty,
			final String msgInfoData,
			final String trspPlanData) {
		
		//データがない場合はログを出力
		if (fretClimToPrty == null || this.areAllFieldsNull(fretClimToPrty)) {
			logger.warn("fret_clim_to_prty（運賃請求先）のデータは0件です。");
			return Optional.empty();
		}

		logger.debug("fret_clim_to_prty_head_off_id（運賃請求先コード（本社））: " + fretClimToPrty.getFretClimToPrtyHeadOffId());

		//fret_clim_to_prty（運賃請求先）の取得
		String cnsgPrtyData = this.createFretClimToPrtyAsCsv(fretClimToPrty);

		//空白の作成
		int blank1Size = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length +
				TRSP_VEHICLE_TRMS_HEADER.split(",").length +
				DEL_INFO_HEADER.split(",").length +
				CNS_HEADER.split(",").length +
				CNS_LINE_ITEM_HEADER.split(",").length +
				CNSG_PRTY_HEADER.split(",").length +
				TRSP_RQR_PRTY_HEADER.split(",").length +
				CNEE_PRTY_HEADER.split(",").length +
				LOGS_SRVC_PRV_HEADER.split(",").length +
				ROAD_CARR_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(blank1Size);

		int blank2Size = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length -
				TRSP_VEHICLE_TRMS_HEADER.split(",").length -
				DEL_INFO_HEADER.split(",").length -
				CNS_HEADER.split(",").length -
				CNS_LINE_ITEM_HEADER.split(",").length -
				CNSG_PRTY_HEADER.split(",").length -
				TRSP_RQR_PRTY_HEADER.split(",").length -
				CNEE_PRTY_HEADER.split(",").length -
				LOGS_SRVC_PRV_HEADER.split(",").length -
				ROAD_CARR_HEADER.split(",").length -
				FRET_CLIM_TO_PRTY_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(blank2Size);

		//レコードの作成
		return Optional.of(String.join(",",
				msgInfoData,
				trspPlanData,
				blank1,
				cnsgPrtyData,
				blank2));
	}

	/**
	 * 出荷場所をCSVレコード用に取得
	 */
	private Optional<List<String>> getShipFromPrtyAsCsv(
			final List<ShipFromPrty> shipFromPrtyList,
			final String msgInfoData,
			final String trspPlanData) {

		//データがない場合はログを出力
		if (shipFromPrtyList == null) {
			logger.warn("ship_from_prty（出荷場所）のデータは0件です。");
			return Optional.empty();
		}

		List<String> record = new ArrayList<>();

		//繰り返し処理
		for (ShipFromPrty shipFromPrty : shipFromPrtyList) {
			//データがない場合はログを出力
			if (this.areAllFieldsNull(shipFromPrty)) {
				logger.warn("ship_from_prty（出荷場所）のデータは0件です。");
				return Optional.empty();
			}
			
			//ship_from_prty（出荷場所）の取得
			String shipFromPrtyData = this.createShipFromPrtyAsCsv(shipFromPrty);

			// ship_from_prty_rqrm（出荷場所要件）を取得
			String shipFromPrtyRqrmData = "";
			ShipFromPrtyRqrm shipFromPrtyRqrm = shipFromPrty.getShipFromPrtyRqrm();
			
			//データがない場合は空白の作成
			if (this.areAllFieldsNull(shipFromPrtyRqrm)) {
				shipFromPrtyRqrmData = this.generateEmptyCsvRow(SHIP_FROM_PRTY_RQRM_HEADER.split(",").length);
			}
			
			// ship_from_prty_rqrm（出荷場所要件）を取得
			shipFromPrtyRqrmData = this.getShipFromPrtyRqrmAsCsv(shipFromPrtyRqrm);

			// cut_off_info（カットオフ情報）を取得し、CSVレコード作成
			Optional<List<String>> cutOffInfoRecord = this.getShipFromPrtyRecord(
					shipFromPrty.getCutOffInfo(),
					msgInfoData,
					trspPlanData,
					shipFromPrtyData,
					shipFromPrtyRqrmData);

			//データがある場合はレコードの追加
			if (cutOffInfoRecord.isPresent()) {
				record.addAll(cutOffInfoRecord.get());
			}
		}
		logger.debug(String.join(",", record));

		//レコードの作成
		return Optional.of(record);

	}

	/**
	 * 出荷場所要件をCSVレコード用に取得
	 */
	private String getShipFromPrtyRqrmAsCsv(final ShipFromPrtyRqrm shipFromPrtyRqrm) {

		//データがない場合はログを出力
		if (shipFromPrtyRqrm == null || this.areAllFieldsNull(shipFromPrtyRqrm)) {
			logger.warn("ship_from_prty_rqrm（出荷場所要件）のデータは0件です。");
			return this.generateEmptyCsvRow(SHIP_FROM_PRTY_RQRM_HEADER.split(",").length);
		}

		logger.debug("trms_of_car_size_cd（車輌種別制限）: " + shipFromPrtyRqrm.getTrmsOfCarSizeCd());

		//ship_from_prty_rqrm（出荷場所要件）の取得
		return this.createShipFromPrtyRqrmAsCsv(shipFromPrtyRqrm);
	}

	/**
	 * 出荷場所をCSVレコード用に取得
	 */
	private Optional<List<String>> getShipFromPrtyRecord(
			final List<CutOffInfo> cutOffInfoList,
			final String msgInfoData,
			final String trspPlanData,
			final String shipFromPrtyData,
			final String shipFromPrtyRqrmData) {

		// 空項目の設定
		int blank1Size = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length +
				TRSP_VEHICLE_TRMS_HEADER.split(",").length +
				DEL_INFO_HEADER.split(",").length +
				CNS_HEADER.split(",").length +
				CNS_LINE_ITEM_HEADER.split(",").length +
				CNSG_PRTY_HEADER.split(",").length +
				TRSP_RQR_PRTY_HEADER.split(",").length +
				CNEE_PRTY_HEADER.split(",").length +
				LOGS_SRVC_PRV_HEADER.split(",").length +
				ROAD_CARR_HEADER.split(",").length +
				FRET_CLIM_TO_PRTY_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(blank1Size);

		int blank2Size = HEADER.split(",").length -
				MSG_INFO_HEADER.split(",").length -
				TRSP_PLAN_HEADER.split(",").length -
				TRSP_ISR_HEADER.split(",").length -
				TRSP_SRVC_HEADER.split(",").length -
				TRSP_VEHICLE_TRMS_HEADER.split(",").length -
				DEL_INFO_HEADER.split(",").length -
				CNS_HEADER.split(",").length -
				CNS_LINE_ITEM_HEADER.split(",").length -
				CNSG_PRTY_HEADER.split(",").length -
				TRSP_RQR_PRTY_HEADER.split(",").length -
				CNEE_PRTY_HEADER.split(",").length -
				LOGS_SRVC_PRV_HEADER.split(",").length -
				ROAD_CARR_HEADER.split(",").length -
				FRET_CLIM_TO_PRTY_HEADER.split(",").length -
				SHIP_FROM_PRTY_HEADER.split(",").length -
				SHIP_FROM_PRTY_RQRM_HEADER.split(",").length -
				CUT_OFF_INFO_HEADER.split(",").length;
		String blank2 = this.generateEmptyCsvRow(blank2Size);

		List<String> csvRecord = new ArrayList<>();

		//データがない場合は空白の作成、レコードの作成
		if (cutOffInfoList == null) {
			logger.warn("cut_off_info（カットオフ情報）が0件です。");
			String shipFromPrtyRecord = String.join(",",
					msgInfoData,
					trspPlanData,
					blank1,
					shipFromPrtyData,
					shipFromPrtyRqrmData,
					this.generateEmptyCsvRow(CUT_OFF_INFO_HEADER.split(",").length),
					blank2);
			csvRecord.add(shipFromPrtyRecord);
			return Optional.of(csvRecord);
		}

		//繰り返し処理
		for (CutOffInfo cutOffInfo : cutOffInfoList) {
			//cut_off_info（カットオフ情報）の取得、レコードの作成
			String cutOffInfoData = this.createCutOffInfoAsCsv(cutOffInfo);
			String shipFromPrtyRecord = String.join(",",
					msgInfoData,
					trspPlanData,
					blank1,
					shipFromPrtyData,
					shipFromPrtyRqrmData,
					cutOffInfoData,
					blank2);
			csvRecord.add(shipFromPrtyRecord);
		}
		return Optional.of(csvRecord);
	}

	/**
	 * 荷届先のCSVレコードを生成
	 */
	private Optional<List<String>> getShipToPrtyAsCsv(
			final List<ShipToPrty> shipToPrtyList,
			final String msgInfoData,
			final String trspPlanData) {

		//データがない場合はログを出力
		if (shipToPrtyList == null) {
			logger.warn("ship_to_prty（荷届先）のデータは0件です。");
			return Optional.empty();
		}

		List<String> record = new ArrayList<>();

		//繰り返し処理
		for (ShipToPrty shipToPrty : shipToPrtyList) {
			
			//データがない場合はログを出力
			if (this.areAllFieldsNull(shipToPrty)) {
				logger.warn("ship_to_prty（荷届先）のデータは0件です。");
				return Optional.empty();
			}

			// ship_to_prty（荷届先）を取得
			String shipToPrtyData = this.createShipToPrtyAsCsv(shipToPrty);

			// ship_to_prty_rqrm（荷届先要件）を取得
			String shipToPrtyRqrm = this.getShipToPrtyRqrmAsCsv(shipToPrty.getShipToPrtyRqrm());

			// free_time_info（フリータイム情報）を取得し、CSVレコードを作成
			Optional<List<String>> freeTimeInfoRecord = this.createShipToPrtyRecord(
					shipToPrty.getFreeTimeInfoList(),
					msgInfoData,
					trspPlanData,
					shipToPrtyData,
					shipToPrtyRqrm);

			if (freeTimeInfoRecord.isPresent()) {
				record.addAll(freeTimeInfoRecord.get());
			}
		}
		return Optional.of(record);
	}

	/**
	 * 荷届先要件をCSVレコード用に取得
	 */
	private String getShipToPrtyRqrmAsCsv(final ShipToPrtyRqrm shipToPrtyRqrm) {
		
		//データがない場合はログを出力
		if (shipToPrtyRqrm == null || this.areAllFieldsNull(shipToPrtyRqrm)) {
			logger.warn("ship_to_prty_rqrm（荷届先要件）のデータは0件です。");
			return this.generateEmptyCsvRow(SHIP_TO_PRTY_RQRM_HEADER.split(",").length);
		}

		logger.debug("trms_of_car_size_cd（車輌種別制限）: " + shipToPrtyRqrm.getTrmsOfCarSizeCd());
		//ship_to_prty_rqrm（荷届先要件）の取得
		return this.createShipToPrtyRqrmAsCsv(shipToPrtyRqrm);
	}

	/**
	 * 荷届先のCSVレコードを生成
	 */
	private Optional<List<String>> createShipToPrtyRecord(
			final List<FreeTimeInfo> freeTimeInfoList,
			final String msgInfoData,
			final String trspPlanData,
			final String shipToPrtyData,
			final String shipToPrtyRqrmData) {

		// 空項目の設定
		int blank1Size = TRSP_ISR_HEADER.split(",").length +
				TRSP_SRVC_HEADER.split(",").length +
				TRSP_VEHICLE_TRMS_HEADER.split(",").length +
				DEL_INFO_HEADER.split(",").length +
				CNS_HEADER.split(",").length +
				CNS_LINE_ITEM_HEADER.split(",").length +
				CNSG_PRTY_HEADER.split(",").length +
				TRSP_RQR_PRTY_HEADER.split(",").length +
				CNEE_PRTY_HEADER.split(",").length +
				LOGS_SRVC_PRV_HEADER.split(",").length +
				ROAD_CARR_HEADER.split(",").length +
				FRET_CLIM_TO_PRTY_HEADER.split(",").length +
				SHIP_FROM_PRTY_HEADER.split(",").length +
				SHIP_FROM_PRTY_RQRM_HEADER.split(",").length +
				CUT_OFF_INFO_HEADER.split(",").length;
		String blank1 = this.generateEmptyCsvRow(blank1Size);
		List<String> csvRecord = new ArrayList<>();

		//データがない場合は空白の作成
		if (freeTimeInfoList == null) {
			logger.warn("free_time_info（フリータイム情報）が0件です。");
			String shipFromPrtyRecord = String.join(",",
					msgInfoData,
					trspPlanData,
					blank1,
					shipToPrtyData,
					this.generateEmptyCsvRow(CUT_OFF_INFO_HEADER.split(",").length),
					shipToPrtyRqrmData);
			csvRecord.add(shipFromPrtyRecord);
			return Optional.of(csvRecord);
		}

		//繰り返し処理
		for (FreeTimeInfo freeTimeInfo : freeTimeInfoList) {
			
			//free_time_info（フリータイム情報）の取得、レコードの作成
			String freeTimeInfoData = this.createFreeTimeInfoAsCsv(freeTimeInfo);
			String shipFromPrtyRecord = String.join(",",
					msgInfoData,
					trspPlanData,
					blank1,
					shipToPrtyData,
					freeTimeInfoData,
					shipToPrtyRqrmData);

			csvRecord.add(shipFromPrtyRecord);
		}

		return Optional.of(csvRecord);
	}

	//----------------------------------------------------------------------
	// CSVデータ生成メソッド
	//----------------------------------------------------------------------

	/**
	 * メッセージ情報をCSVデータを生成
	 */
	private String createMsgInfoAsCsv(final CarrierTrans carrierTrans) {
		MsgInfo msgInfo = carrierTrans.getMsgInfo();
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
	 * 運送計画をCSVデータを生成
	 */
	private String createTrspPlanAsCsv(final CarrierTrans carrierTrans) {
		TrspPlan trspPlan = carrierTrans.getTrspPlan();
		if (trspPlan == null || this.areAllFieldsNull(trspPlan)) {
			logger.warn("trsp_plan（運送計画）のデータは0件です。");
			int requiredBlankSize = TRSP_PLAN_HEADER.split(",").length;
			return this.generateEmptyCsvRow(requiredBlankSize);
		}
		return this.getOrDefault(trspPlan.getTrspPlanStasCd());
	}

	/**
	 * 運送依頼をCSVデータを生成
	 */
	private String createTrspIsrAsCsv(final TrspIsr trspIsr) {
		return String.join(",",
				this.getOrDefault(trspIsr.getTrspInstructionId()),
				this.getOrDefault(trspIsr.getTrspInstructionDateSubmDttm()),
				this.getOrDefault(trspIsr.getInvNumId()),
				this.getOrDefault(trspIsr.getCmnInvNumId()),
				this.getOrDefault(trspIsr.getMixLoadNumId()));
	}

	/**
	 * 運送サービスをCSVデータとして生成
	 */
	private String createTrspSrvcAsCsv(final TrspSrvc trspSrvc) {
		return String.join(",",
				this.getOrDefault(trspSrvc.getServiceNo()),
				this.getOrDefault(trspSrvc.getServiceName()),
				this.getOrDefault(trspSrvc.getServiceStrtDate()),
				this.getOrDefault(trspSrvc.getServiceStrtTime()),
				this.getOrDefault(trspSrvc.getServiceEndDate()),
				this.getOrDefault(trspSrvc.getServiceEndTime()),
				this.convertToStringOrEmpty(trspSrvc.getFreightRate()),
				this.getOrDefault(trspSrvc.getTrspMeansTypCd()),
				this.getOrDefault(trspSrvc.getTrspSrvcTypCd()),
				this.getOrDefault(trspSrvc.getRoadCarrSrvcTypCd()),
				this.getOrDefault(trspSrvc.getTrspRootPrioCd()),
				this.getOrDefault(trspSrvc.getCarClsPrioCd()),
				this.getOrDefault(trspSrvc.getClsOfCargInSrvcRqrmCd()),
				this.getOrDefault(trspSrvc.getClsOfPkgUpSrvcRqrmCd()),
				this.getOrDefault(trspSrvc.getPyrClsSrvcRqrmCd()),
				this.getOrDefault(trspSrvc.getTrmsOfMixLoadCndCd()),
				this.getOrDefault(trspSrvc.getDsedCllFromDate()),
				this.getOrDefault(trspSrvc.getDsedCllToDate()),
				this.getOrDefault(trspSrvc.getDsedCllFromTime()),
				this.getOrDefault(trspSrvc.getDsedCllToTime()),
				this.getOrDefault(trspSrvc.getDsedCllTimeTrmsSrvcRqrmCd()),
				this.getOrDefault(trspSrvc.getApedArrFromDate()),
				this.getOrDefault(trspSrvc.getApedArrToDate()),
				this.getOrDefault(trspSrvc.getApedArrFromTimePrfmDttm()),
				this.getOrDefault(trspSrvc.getApedArrToTimePrfmDttm()),
				this.getOrDefault(trspSrvc.getApedArrTimeTrmsSrvcRqrmCd()),
				this.getOrDefault(trspSrvc.getTrmsOfMixLoadTxt()),
				this.getOrDefault(trspSrvc.getTrspSrvcNoteOneTxt()),
				this.getOrDefault(trspSrvc.getTrspSrvcNoteTwoTxt()));
	}

	/**
	 * 運送車輌条件をCSVデータとして生成
	 */
	private String createTrspVehicleTrmsAsCsv(final TrspVehicleTrms trspVehicleTrms) {
		return String.join(",",
				this.getOrDefault(trspVehicleTrms.getCarClsOfSizeCd()),
				this.getOrDefault(trspVehicleTrms.getCarClsOfShpCd()),
				this.getOrDefault(trspVehicleTrms.getCarClsOfTlgLftrExstCd()),
				this.getOrDefault(trspVehicleTrms.getCarClsOfWingBodyExstCd()),
				this.getOrDefault(trspVehicleTrms.getCarClsOfRfgExstCd()),
				this.convertToStringOrEmpty(trspVehicleTrms.getTrmsOfLwrTmpMeas()),
				this.convertToStringOrEmpty(trspVehicleTrms.getTrmsOfUppTmpMeas()),
				this.getOrDefault(trspVehicleTrms.getCarClsOfCrnExstCd()),
				this.getOrDefault(trspVehicleTrms.getCarRmkAboutEqpmTxt()));
	}

	/**
	 * 納品情報をCSVデータとして生成
	 */
	private String createDelInfoAsCsv(final DelInfo delInfo) {
		return String.join(",",
				this.getOrDefault(delInfo.getDelNoteId()),
				this.getOrDefault(delInfo.getShpmNumId()),
				this.getOrDefault(delInfo.getRcedOrdNumId()));
	}

	/**
	 * 委託貨物をCSVデータとして生成
	 */
	private String createCnsAsCsv(final Cns cns) {
		logger.info("BigDecimal:"+ cns.getIstdTotlWeigMeas());
		logger.info("BigDecimalStr:"+ this.convertToStringFromBigDecimalOrEmpty(cns.getIstdTotlWeigMeas()));
		
		return String.join(",",
				this.convertToStringOrEmpty(cns.getIstdTotlPcksQuan()),
				this.getOrDefault(cns.getNumUntCd()),
				this.convertToStringFromBigDecimalOrEmpty(cns.getIstdTotlWeigMeas()),
				this.getOrDefault(cns.getWeigUntCd()),
				this.convertToStringFromBigDecimalOrEmpty(cns.getIstdTotlVolMeas()),
				this.getOrDefault(cns.getVolUntCd()),
				this.convertToStringOrEmpty(cns.getIstdTotlUntlQuan()));
	}

	/**
	 * 貨物明細をCSVデータとして生成
	 */
	private String createCnsLineItemAsCsv(final CnsLineItem cnsLineItem) {
		return String.join(",",
				this.getOrDefault(cnsLineItem.getLineItemNumId()),
				this.getOrDefault(cnsLineItem.getSevOrdNumId()),
				this.getOrDefault(cnsLineItem.getCnsgCrgItemNumId()),
				this.getOrDefault(cnsLineItem.getBuyAssiItemCd()),
				this.getOrDefault(cnsLineItem.getSellAssiItemCd()),
				this.getOrDefault(cnsLineItem.getWrhsAssiItemCd()),
				this.getOrDefault(cnsLineItem.getItemNameTxt()),
				this.getOrDefault(cnsLineItem.getGodsIdcsInOtsPckeNameTxt()),
				this.convertToStringOrEmpty(cnsLineItem.getNumOfIstdUntlQuan()),
				this.convertToStringOrEmpty(cnsLineItem.getNumOfIstdQuan()),
				this.getOrDefault(cnsLineItem.getSevNumUntCd()),
				this.convertToStringOrEmpty(cnsLineItem.getIstdPckeWeigMeas()),
				this.getOrDefault(cnsLineItem.getSevWeigUntCd()),
				this.convertToStringOrEmpty(cnsLineItem.getIstdPckeVolMeas()),
				this.getOrDefault(cnsLineItem.getSevVolUntCd()),
				this.convertToStringFromBigDecimalOrEmpty(cnsLineItem.getIstdQuanMeas()),
				this.getOrDefault(cnsLineItem.getCnteNumUntCd()),
				this.getOrDefault(cnsLineItem.getDcpvTrpnPckgTxt()),
				this.getOrDefault(cnsLineItem.getPckeFrmCd()),
				this.getOrDefault(cnsLineItem.getPckeFrmNameCd()),
				this.getOrDefault(cnsLineItem.getCrgHndTrmsSpclIsrsTxt()),
				this.getOrDefault(cnsLineItem.getGlbRetbAsseId()),
				this.convertToStringOrEmpty(cnsLineItem.getTotlRtiQuanQuan()),
				this.convertToStringOrEmpty(cnsLineItem.getChrgOfPckeCtrlNumUntAmnt()));
	}

	/**
	 * 荷送人をCSVデータとして生成
	 */
	private String createCnsgPrtyAsCsv(final CnsgPrty cnsgPrty) {
		return String.join(",",
				this.getOrDefault(cnsgPrty.getCnsgPrtyHeadOffId()),
				this.getOrDefault(cnsgPrty.getCnsgPrtyBrncOffId()),
				this.getOrDefault(cnsgPrty.getCnsgPrtyNameTxt()),
				this.getOrDefault(cnsgPrty.getCnsgSctSpedOrgId()),
				this.getOrDefault(cnsgPrty.getCnsgSctSpedOrgNameTxt()),
				this.getOrDefault(cnsgPrty.getCnsgTelCmmCmpNumTxt()),
				this.getOrDefault(cnsgPrty.getCnsgPstlAdrsLineOneTxt()),
				this.getOrDefault(cnsgPrty.getCnsgPstcCd()));
	}

	/**
	 * 運送依頼者をCSVデータとして生成
	 */
	private String createTrspRqrPrtyAsCsv(final TrspRqrPrty trspRqrPrty) {

		return String.join(",",
				this.getOrDefault(trspRqrPrty.getTrspRqrPrtyHeadOffId()),
				this.getOrDefault(trspRqrPrty.getTrspRqrPrtyBrncOffId()),
				this.getOrDefault(trspRqrPrty.getTrspRqrPrtyNameTxt()),
				this.getOrDefault(trspRqrPrty.getTrspRqrSctSpedOrgId()),
				this.getOrDefault(trspRqrPrty.getTrspRqrSctSpedOrgNameTxt()),
				this.getOrDefault(trspRqrPrty.getTrspRqrSctTelCmmCmpNumTxt()),
				this.getOrDefault(trspRqrPrty.getTrspRqrPstlAdrsLineOneTxt()),
				this.getOrDefault(trspRqrPrty.getTrspRqrPstcCd()));
	}

	/**
	 * 荷受人をCSVデータとして生成
	 */
	private String createCneePrtyAsCsv(final CneePrty cneePrty) {
		return String.join(",",
				this.getOrDefault(cneePrty.getCneePrtyHeadOffId()),
				this.getOrDefault(cneePrty.getCneePrtyBrncOffId()),
				this.getOrDefault(cneePrty.getCneePrtyNameTxt()),
				this.getOrDefault(cneePrty.getCneeSctId()),
				this.getOrDefault(cneePrty.getCneeSctNameTxt()),
				this.getOrDefault(cneePrty.getCneePrimCntPersNameTxt()),
				this.getOrDefault(cneePrty.getCneeTelCmmCmpNumTxt()),
				this.getOrDefault(cneePrty.getCneePstlAdrsLineOneTxt()),
				this.getOrDefault(cneePrty.getCneePstcCd()));
	}

	/**
	 * 物流サービス提供者をCSVデータとして生成
	 */
	private String createLogsSrvcPrvAsCsv(final LogsSrvcPrv logsSrvcPrv) {
		return String.join(",",
				this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvPrtyHeadOffId()),
				this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvPrtyBrncOffId()),
				this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvPrtyNameTxt()),
				this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvSctSpedOrgId()),
				this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvSctSpedOrgNameTxt()),
				this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvSctPrimCntPersNameTxt()),
				this.getOrDefault(logsSrvcPrv.getLogsSrvcPrvSctTelCmmCmpNumTxt()));
	}

	/**
	 * 運送事業者をCSVデータとして生成
	 */
	private String createRoadCarrAsCsv(final RoadCarr roadCarr) {
		return String.join(",",
				this.getOrDefault(roadCarr.getTrspCliPrtyHeadOffId()),
				this.getOrDefault(roadCarr.getTrspCliPrtyBrncOffId()),
				this.getOrDefault(roadCarr.getTrspCliPrtyNameTxt()),
				this.getOrDefault(roadCarr.getRoadCarrDepaSpedOrgId()),
				this.getOrDefault(roadCarr.getRoadCarrDepaSpedOrgNameTxt()),
				this.getOrDefault(roadCarr.getTrspCliTelCmmCmpNumTxt()),
				this.getOrDefault(roadCarr.getRoadCarrArrSpedOrgId()),
				this.getOrDefault(roadCarr.getRoadCarrArrSpedOrgNameTxt()));
	}

	/**
	 * 運賃請求先をCSVデータとして生成
	 */
	private String createFretClimToPrtyAsCsv(final FretClimToPrty fretClimToPrty) {
		return String.join(",",
				this.getOrDefault(fretClimToPrty.getFretClimToPrtyHeadOffId()),
				this.getOrDefault(fretClimToPrty.getFretClimToPrtyBrncOffId()),
				this.getOrDefault(fretClimToPrty.getFretClimToPrtyNameTxt()),
				this.getOrDefault(fretClimToPrty.getFretClimToSctSpedOrgId()),
				this.getOrDefault(fretClimToPrty.getFretClimToSctSpedOrgNameTxt()));
	}

	/**
	 * 出荷場所をCSVデータとして生成
	 */
	private String createShipFromPrtyAsCsv(final ShipFromPrty shipFromPrty) {
		return String.join(",",
				this.getOrDefault(shipFromPrty.getShipFromPrtyHeadOffId()),
				this.getOrDefault(shipFromPrty.getShipFromPrtyBrncOffId()),
				this.getOrDefault(shipFromPrty.getShipFromPrtyNameTxt()),
				this.getOrDefault(shipFromPrty.getShipFromSctId()),
				this.getOrDefault(shipFromPrty.getShipFromSctNameTxt()),
				this.getOrDefault(shipFromPrty.getShipFromTelCmmCmpNumTxt()),
				this.getOrDefault(shipFromPrty.getShipFromPstlAdrsCtyId()),
				this.getOrDefault(shipFromPrty.getShipFromPstlAdrsId()),
				this.getOrDefault(shipFromPrty.getShipFromPstlAdrsLineOneTxt()),
				this.getOrDefault(shipFromPrty.getShipFromPstcCd()),
				this.getOrDefault(shipFromPrty.getPlcCdPrtyId()),
				this.getOrDefault(shipFromPrty.getGlnPrtyId()),
				this.getOrDefault(shipFromPrty.getJpnUplcCd()),
				this.getOrDefault(shipFromPrty.getJpnVanSrvcCd()),
				this.getOrDefault(shipFromPrty.getJpnVanVansCd()));
	}

	/**
	 * 出荷場所要件をCSVデータとして生成
	 */
	private String createShipFromPrtyRqrmAsCsv(final ShipFromPrtyRqrm shipFromPrtyRqrm) {
		return String.join(",",
				this.getOrDefault(shipFromPrtyRqrm.getTrmsOfCarSizeCd()),
				this.getOrDefault(shipFromPrtyRqrm.getTrmsOfCarHghtMeas()),
				this.getOrDefault(shipFromPrtyRqrm.getTrmsOfGtpCertTxt()),
				this.getOrDefault(shipFromPrtyRqrm.getTrmsOfCllTxt()),
				this.getOrDefault(shipFromPrtyRqrm.getTrmsOfGodsHndTxt()),
				this.getOrDefault(shipFromPrtyRqrm.getAncWrkOfCllTxt()),
				this.getOrDefault(shipFromPrtyRqrm.getSpclWrkTxt()));
	}

	/**
	 * カットオフ情報のCSVデータを生成
	 */
	private String createCutOffInfoAsCsv(final CutOffInfo cutOffInfo) {
		return String.join(",",
				this.convertToStringOrEmpty(cutOffInfo.getCutOffTime()),
				this.convertToStringOrEmpty(cutOffInfo.getCutOffFee()));
	}

	/**
	 * 荷届先をCSVデータとして生成
	 */
	private String createShipToPrtyAsCsv(final ShipToPrty shipToPrty) {
		return String.join(",",
				this.getOrDefault(shipToPrty.getShipToPrtyHeadOffId()),
				this.getOrDefault(shipToPrty.getShipToPrtyBrncOffId()),
				this.getOrDefault(shipToPrty.getShipToPrtyNameTxt()),
				this.getOrDefault(shipToPrty.getShipToSctId()),
				this.getOrDefault(shipToPrty.getShipToSctNameTxt()),
				this.getOrDefault(shipToPrty.getShipToPrimCntId()),
				this.getOrDefault(shipToPrty.getShipToPrimCntPersNameTxt()),
				this.getOrDefault(shipToPrty.getShipToTelCmmCmpNumTxt()),
				this.getOrDefault(shipToPrty.getShipToPstlAdrsCtyId()),
				this.getOrDefault(shipToPrty.getShipToPstlAdrsId()),
				this.getOrDefault(shipToPrty.getShipToPstlAdrsLineOneTxt()),
				this.getOrDefault(shipToPrty.getShipToPstcCd()),
				this.getOrDefault(shipToPrty.getPlcCdPrtyId()),
				this.getOrDefault(shipToPrty.getGlnPrtyId()),
				this.getOrDefault(shipToPrty.getJpnUplcCd()),
				this.getOrDefault(shipToPrty.getJpnVanSrvcCd()),
				this.getOrDefault(shipToPrty.getJpnVanVansCd()));
	}

	/**
	 * フリータイム情報のCSVデータを生成
	 */
	private String createFreeTimeInfoAsCsv(final FreeTimeInfo freeTimeInfo) {
		return String.join(",",
				this.convertToStringOrEmpty(freeTimeInfo.getFreeTime()),
				this.convertToStringOrEmpty(freeTimeInfo.getFreeTimeFee()));
	}

	/**
	 * 荷届先要件をCSVデータとして生成
	 */
	private String createShipToPrtyRqrmAsCsv(final ShipToPrtyRqrm shipToPrtyRqrm) {
		return String.join(",",
				this.getOrDefault(shipToPrtyRqrm.getTrmsOfCarSizeCd()),
				this.getOrDefault(shipToPrtyRqrm.getTrmsOfCarHghtMeas()),
				this.getOrDefault(shipToPrtyRqrm.getTrmsOfGtpCertTxt()),
				this.getOrDefault(shipToPrtyRqrm.getTrmsOfDelTxt()),
				this.getOrDefault(shipToPrtyRqrm.getTrmsOfGodsHndTxt()),
				this.getOrDefault(shipToPrtyRqrm.getAncWrkOfDelTxt()),
				this.getOrDefault(shipToPrtyRqrm.getSpclWrkTxt()));
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

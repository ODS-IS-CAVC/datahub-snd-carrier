package datahub.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * VehicleAvbResource（車輌稼働可能リソース）Model
 * 
 */
@Data
public class VehicleAvbResource {
	@JsonProperty("trsp_op_strt_area_line_one_txt")
    private String trspOpStrtAreaLineOneTxt;

    @JsonProperty("trsp_op_strt_area_cty_jis_cd")
    private String trspOpStrtAreaCtyJisCd;

    @JsonProperty("trsp_op_date_trm_strt_date")
    private String trspOpDateTrmStrtDate;

    @JsonProperty("trsp_op_plan_date_trm_strt_time")
    private String trspOpPlanDateTrmStrtTime;

    @JsonProperty("trsp_op_end_area_line_one_txt")
    private String trspOpEndAreaLineOneTxt;

    @JsonProperty("trsp_op_end_area_cty_jis_cd")
    private String trspOpEndAreaCtyJisCd;

    @JsonProperty("trsp_op_date_trm_end_date")
    private String trspOpDateTrmEndDate;

    @JsonProperty("trsp_op_plan_date_trm_end_time")
    private String trspOpPlanDateTrmEndTime;

    // 以下は新たに追加されたフィールド
    @JsonProperty("clb_area_txt")
    private String clbAreaTxt;

    @JsonProperty("trms_of_clb_area_cd")
    private String trmsOfClbAreaCd;

    @JsonProperty("avb_date_cll_date")
    private String avbDateCllDate;

    @JsonProperty("avb_from_time_of_cll_time")
    private String avbFromTimeOfCllTime;

    @JsonProperty("avb_to_time_of_cll_time")
    private String avbToTimeOfCllTime;

    @JsonProperty("delb_area_txt")
    private String delbAreaTxt;

    @JsonProperty("trms_of_delb_area_cd")
    private String trmsOfDelbAreaCd;

    @JsonProperty("esti_del_date_prfm_dttm")
    private String estiDelDatePrfmDttm;

    @JsonProperty("avb_from_time_of_del_time")
    private String avbFromTimeOfDelTime;

    @JsonProperty("avb_to_time_of_del_time")
    private String avbToTimeOfDelTime;

    @JsonProperty("avb_load_cp_of_car_meas")
    private BigDecimal avbLoadCpOfCarMeas;

    @JsonProperty("avb_load_vol_of_car_meas")
    private Double avbLoadVolOfCarMeas;

    @JsonProperty("pcke_frm_cd")
    private String pckeFrmCd;

    @JsonProperty("avb_num_of_retb_cntn_of_car_quan")
    private Integer avbNumOfRetbCntnOfCarQuan;

    @JsonProperty("trk_bed_stas_txt")
    private String trkBedStasTxt;
    
    @JsonProperty("cut_off_info")
    private List<CutOffInfo> cutOffInfoList;
    
    @JsonProperty("free_time_info")
    private List<FreeTimeInfo> freeTimeInfoList;
}

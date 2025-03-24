package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 運送能力情報パラメータ
 * 
 */
@Data
@Builder
@AllArgsConstructor
public class ShipperTransCapacitySearchRequest implements CarrierApiRequest {
	/** 便・ダイヤ番号 */
    @JsonProperty("service_no")
    private String serviceNo;
    
    /** 便・ダイヤ名称 */
    @JsonProperty("service_name")
    private String serviceName;
    
    /** 最大積載量1 */
    @JsonProperty("car_max_load_capacity1_meas")
    private Integer carMaxLoadCapacity1Meas;
    
    /** 運行開始地域 */
    @JsonProperty("trsp_op_strt_area_line_one_txt")
    private String trspOpStrtAreaLineOneTxt;
    
    /** 運行終了地域 */
    @JsonProperty("trsp_op_end_area_line_one_txt")
    private String trspOpEndAreaLineOneTxt;
    
    /** 運行開始日最大値 */
    @JsonProperty("max_trsp_op_date_trm_strt_date")
    private String maxTrspOpDateTrmStrtDate;
    
    /** 運行開始日最小値 */
    @JsonProperty("min_trsp_op_date_trm_strt_date")
    private String minTrspOpDateTrmStrtDate;
    
    /** 運行終了日最大値 */
    @JsonProperty("max_trsp_op_date_trm_end_date")
    private String maxTrspOpDateTrmEndDate;
    
    /** 運行終了日最小値 */
    @JsonProperty("min_trsp_op_date_trm_end_date")
    private String minTrspOpDateTrmEndDate;
    
    /** 運行開始希望時刻最大値 */
    @JsonProperty("max_trsp_op_plan_date_trm_strt_time")
    private String maxTrspOpPlanDateTrmStrtTime;
    
    /** 運行開始希望時刻最小値 */
    @JsonProperty("min_trsp_op_plan_date_trm_strt_time")
    private String minTrspOpPlanDateTrmStrtTime;
    
    /** 運行終了希望時刻最大値 */
    @JsonProperty("max_trsp_op_plan_date_trm_end_time")
    private String maxTrspOpPlanDateTrmEndTime;
    
    /** 運行終了希望時刻最小値 */
    @JsonProperty("min_trsp_op_plan_date_trm_end_time")
    private String minTrspOpPlanDateTrmEndTime;
}

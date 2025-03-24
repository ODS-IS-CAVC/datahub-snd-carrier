package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 *DrvAvbTime（運転手稼働可能時間）Model
 * 
 */
@Data
public class DrvAvbTime {
	@JsonProperty("drv_avb_from_date")
    private String drvAvbFromDate;

    @JsonProperty("drv_avb_from_time_of_wrkg_time")
    private String drvAvbFromTimeOfWrkgTime;

    @JsonProperty("drv_avb_to_date")
    private String drvAvbToDate;

    @JsonProperty("drv_avb_to_time_of_wrkg_time")
    private String drvAvbToTimeOfWrkgTime;

    @JsonProperty("drv_wrkg_trms_txt")
    private String drvWrkgTrmsTxt;

    @JsonProperty("drv_frmr_optg_date")
    private String drvFrmrOptgDate;

    @JsonProperty("drv_frmr_op_end_time")
    private String drvFrmrOpEndTime;
}

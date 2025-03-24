package datahub.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * CarInfo（車輌情報）Model
 */
@Data
public class CarInfo {
	@JsonProperty("service_no")
	private String serviceNo;
	
	@JsonProperty("service_name")
	private String serviceName;
	
	@JsonProperty("service_strt_date")
	private String serviceStrtDate;
	
	@JsonProperty("service_strt_time")
	private String serviceStrtTime;
	
	@JsonProperty("service_end_date")
	private String serviceEndDate;
	
	@JsonProperty("service_end_time")
	private String serviceEndTime;
	
	@JsonProperty("freight_rate")
	private Integer freightRate;
	
	@JsonProperty("car_ctrl_num_id")
    private String carCtrlNumId;
	
	@JsonProperty("car_license_plt_num_id")
    private String carLicensePltNumId;
	
	@JsonProperty("giai")
    private String giai;

    @JsonProperty("car_body_num_cd")
    private String carBodyNumCd;

    @JsonProperty("car_cls_of_size_cd")
    private String carClsOfSizeCd;

    @JsonProperty("tractor_idcr")
    private String tractorIdcr;

    @JsonProperty("trailer_license_plt_num_id")
    private String trailerLicensePltNumId;

    @JsonProperty("car_weig_meas")
    private Integer carWeigMeas;

    @JsonProperty("car_lngh_meas")
    private Integer carLnghMeas;

    @JsonProperty("car_wid_meas")
    private Integer carWidMeas;

    @JsonProperty("car_hght_meas")
    private Integer carHghtMeas;

    @JsonProperty("car_max_load_capacity1_meas")
    private Integer carMaxLoadCapacity1Meas;

    @JsonProperty("car_max_load_capacity2_meas")
    private Integer carMaxLoadCapacity2Meas;

    @JsonProperty("car_vol_of_hzd_item_meas")
    private Integer carVolOfHzdItemMeas;

    @JsonProperty("car_spc_grv_of_hzd_item_meas")
    private Double carSpcGrvOfHzdItemMeas;

    @JsonProperty("car_trk_bed_hght_meas")
    private Integer carTrkBedHghtMeas;

    @JsonProperty("car_trk_bed_wid_meas")
    private Integer carTrkBedWidMeas;

    @JsonProperty("car_trk_bed_lngh_meas")
    private Integer carTrkBedLnghMeas;

    @JsonProperty("car_trk_bed_grnd_hght_meas")
    private Integer carTrkBedGrndHghtMeas;

    @JsonProperty("car_max_load_vol_meas")
    private Double carMaxLoadVolMeas;

    @JsonProperty("pcke_frm_cd")
    private String pckeFrmCd;

    @JsonProperty("pcke_frm_name_cd")
    private String pckeFrmNameCd;

    @JsonProperty("car_max_untl_cp_quan")
    private Integer carMaxUntlCpQuan;

    @JsonProperty("car_cls_of_shp_cd")
    private String carClsOfShpCd;

    @JsonProperty("car_cls_of_tlg_lftr_exst_cd")
    private String carClsOfTlgLftrExstCd;

    @JsonProperty("car_cls_of_wing_body_exst_cd")
    private String carClsOfWingBodyExstCd;

    @JsonProperty("car_cls_of_rfg_exst_cd")
    private String carClsOfRfgExstCd;

    @JsonProperty("trms_of_lwr_tmp_meas")
    private Double trmsOfLwrTmpMeas;

    @JsonProperty("trms_of_upp_tmp_meas")
    private Double trmsOfUppTmpMeas;

    @JsonProperty("car_cls_of_crn_exst_cd")
    private String carClsOfCrnExstCd;

    @JsonProperty("car_rmk_about_eqpm_txt")
    private String carRmkAboutEqpmTxt;

    @JsonProperty("car_cmpn_name_of_gtp_crtf_exst_txt")
    private String carCmpnNameOfGtpCrtfExstTxt;

    @JsonProperty("vehicle_avb_resource")
    private List<VehicleAvbResource> vehicleAvbResource;
}

package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * TrspVehicleTrms（運送車輌条件）Model
 * 
 */
@Data
public class TrspVehicleTrms {
	
	@JsonProperty("car_cls_of_size_cd")
    private String carClsOfSizeCd;

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
}

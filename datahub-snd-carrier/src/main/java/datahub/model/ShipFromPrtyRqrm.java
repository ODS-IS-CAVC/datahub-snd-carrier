package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * ShipFromPrtyRqrm（出荷場所要件）Model
 * 
 */
@Data
public class ShipFromPrtyRqrm {
	
	@JsonProperty("trms_of_car_size_cd")
    private String trmsOfCarSizeCd;

    @JsonProperty("trms_of_car_hght_meas")
    private String trmsOfCarHghtMeas;

    @JsonProperty("trms_of_gtp_cert_txt")
    private String trmsOfGtpCertTxt;

    @JsonProperty("trms_of_cll_txt")
    private String trmsOfCllTxt;

    @JsonProperty("trms_of_gods_hnd_txt")
    private String trmsOfGodsHndTxt;

    @JsonProperty("anc_wrk_of_cll_txt")
    private String ancWrkOfCllTxt;

    @JsonProperty("spcl_wrk_txt")
    private String spclWrkTxt;
}

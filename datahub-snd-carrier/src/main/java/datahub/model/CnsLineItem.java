package datahub.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * CnsLineItem（貨物明細）Model
 * 
 */
@Data
public class CnsLineItem {
	
	@JsonProperty("line_item_num_id")
    private String lineItemNumId;

    @JsonProperty("sev_ord_num_id")
    private String sevOrdNumId;

    @JsonProperty("cnsg_crg_item_num_id")
    private String cnsgCrgItemNumId;

    @JsonProperty("buy_assi_item_cd")
    private String buyAssiItemCd;

    @JsonProperty("sell_assi_item_cd")
    private String sellAssiItemCd;

    @JsonProperty("wrhs_assi_item_cd")
    private String wrhsAssiItemCd;

    @JsonProperty("item_name_txt")
    private String itemNameTxt;

    @JsonProperty("gods_idcs_in_ots_pcke_name_txt")
    private String godsIdcsInOtsPckeNameTxt;

    @JsonProperty("num_of_istd_untl_quan")
    private Integer numOfIstdUntlQuan;

    @JsonProperty("num_of_istd_quan")
    private Integer numOfIstdQuan;

    @JsonProperty("sev_num_unt_cd")
    private String sevNumUntCd;

    @JsonProperty("istd_pcke_weig_meas")
    private Double istdPckeWeigMeas;

    @JsonProperty("sev_weig_unt_cd")
    private String sevWeigUntCd;

    @JsonProperty("istd_pcke_vol_meas")
    private Double istdPckeVolMeas;

    @JsonProperty("sev_vol_unt_cd")
    private String sevVolUntCd;

    @JsonProperty("istd_quan_meas")
    private BigDecimal istdQuanMeas;

    @JsonProperty("cnte_num_unt_cd")
    private String cnteNumUntCd;

    @JsonProperty("dcpv_trpn_pckg_txt")
    private String dcpvTrpnPckgTxt;

    @JsonProperty("pcke_frm_cd")
    private String pckeFrmCd;

    @JsonProperty("pcke_frm_name_cd")
    private String pckeFrmNameCd;

    @JsonProperty("crg_hnd_trms_spcl_isrs_txt")
    private String crgHndTrmsSpclIsrsTxt;

    @JsonProperty("glb_retb_asse_id")
    private String glbRetbAsseId;

    @JsonProperty("totl_rti_quan_quan")
    private Integer totlRtiQuanQuan;

    @JsonProperty("chrg_of_pcke_ctrl_num_unt_amnt")
    private Integer chrgOfPckeCtrlNumUntAmnt;
}

package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * CneePrty（荷受人）Model
 * 
 */
@Data
public class CneePrty {
	
	@JsonProperty("cnee_prty_head_off_id")
    private String cneePrtyHeadOffId;

    @JsonProperty("cnee_prty_brnc_off_id")
    private String cneePrtyBrncOffId;

    @JsonProperty("cnee_prty_name_txt")
    private String cneePrtyNameTxt;

    @JsonProperty("cnee_sct_id")
    private String cneeSctId;

    @JsonProperty("cnee_sct_name_txt")
    private String cneeSctNameTxt;

    @JsonProperty("cnee_prim_cnt_pers_name_txt")
    private String cneePrimCntPersNameTxt;

    @JsonProperty("cnee_tel_cmm_cmp_num_txt")
    private String cneeTelCmmCmpNumTxt;

    @JsonProperty("cnee_pstl_adrs_line_one_txt")
    private String cneePstlAdrsLineOneTxt;

    @JsonProperty("cnee_pstc_cd")
    private String cneePstcCd;
}

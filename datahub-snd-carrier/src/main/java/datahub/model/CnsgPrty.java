package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * CnsgPrty（荷送人）Model
 * 
 */
@Data
public class CnsgPrty {
	
	@JsonProperty("cnsg_prty_head_off_id")
    private String cnsgPrtyHeadOffId;

    @JsonProperty("cnsg_prty_brnc_off_id")
    private String cnsgPrtyBrncOffId;

    @JsonProperty("cnsg_prty_name_txt")
    private String cnsgPrtyNameTxt;

    @JsonProperty("cnsg_sct_sped_org_id")
    private String cnsgSctSpedOrgId;

    @JsonProperty("cnsg_sct_sped_org_name_txt")
    private String cnsgSctSpedOrgNameTxt;

    @JsonProperty("cnsg_tel_cmm_cmp_num_txt")
    private String cnsgTelCmmCmpNumTxt;

    @JsonProperty("cnsg_pstl_adrs_line_one_txt")
    private String cnsgPstlAdrsLineOneTxt;

    @JsonProperty("cnsg_pstc_cd")
    private String cnsgPstcCd;
}

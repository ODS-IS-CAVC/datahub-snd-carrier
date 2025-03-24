package datahub.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * ShipToPrty（荷届先）Model
 * 
 */
@Data
public class ShipToPrty {
	
	@JsonProperty("ship_to_prty_head_off_id")
    private String shipToPrtyHeadOffId;

    @JsonProperty("ship_to_prty_brnc_off_id")
    private String shipToPrtyBrncOffId;

    @JsonProperty("ship_to_prty_name_txt")
    private String shipToPrtyNameTxt;

    @JsonProperty("ship_to_sct_id")
    private String shipToSctId;

    @JsonProperty("ship_to_sct_name_txt")
    private String shipToSctNameTxt;

    @JsonProperty("ship_to_prim_cnt_id")
    private String shipToPrimCntId;

    @JsonProperty("ship_to_prim_cnt_pers_name_txt")
    private String shipToPrimCntPersNameTxt;

    @JsonProperty("ship_to_tel_cmm_cmp_num_txt")
    private String shipToTelCmmCmpNumTxt;

    @JsonProperty("ship_to_pstl_adrs_cty_id")
    private String shipToPstlAdrsCtyId;

    @JsonProperty("ship_to_pstl_adrs_id")
    private String shipToPstlAdrsId;

    @JsonProperty("ship_to_pstl_adrs_line_one_txt")
    private String shipToPstlAdrsLineOneTxt;

    @JsonProperty("ship_to_pstc_cd")
    private String shipToPstcCd;

    @JsonProperty("plc_cd_prty_id")
    private String plcCdPrtyId;

    @JsonProperty("gln_prty_id")
    private String glnPrtyId;

    @JsonProperty("jpn_uplc_cd")
    private String jpnUplcCd;

    @JsonProperty("jpn_van_srvc_cd")
    private String jpnVanSrvcCd;

    @JsonProperty("jpn_van_vans_cd")
    private String jpnVanVansCd;
    
    @JsonProperty("free_time_info")
    private List<FreeTimeInfo> freeTimeInfoList;
    
    @JsonProperty("ship_to_prty_rqrm")
    private ShipToPrtyRqrm shipToPrtyRqrm;
    
}

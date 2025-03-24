package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * FretClimToPrty（運賃請求先）Model
 * 
 */
@Data
public class FretClimToPrty {
	
	@JsonProperty("fret_clim_to_prty_head_off_id")
    private String fretClimToPrtyHeadOffId;

    @JsonProperty("fret_clim_to_prty_brnc_off_id")
    private String fretClimToPrtyBrncOffId;

    @JsonProperty("fret_clim_to_prty_name_txt")
    private String fretClimToPrtyNameTxt;

    @JsonProperty("fret_clim_to_sct_sped_org_id")
    private String fretClimToSctSpedOrgId;

    @JsonProperty("fret_clim_to_sct_sped_org_name_txt")
    private String fretClimToSctSpedOrgNameTxt;
}

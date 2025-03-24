package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * HazardousMaterialInfo（危険物情報）Model
 * 
 */
@Data
public class HazardousMaterialInfo {
	
	@JsonProperty("hazardous_material_item_code")
    private String hazardousMaterialItemCode;

    @JsonProperty("hazardous_material_text_info")
    private String hazardousMaterialTextInfo;
}

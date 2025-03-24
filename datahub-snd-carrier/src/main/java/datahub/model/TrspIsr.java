package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * TrspIsr（運送依頼）Model
 * 
 */
@Data
public class TrspIsr {
	
	@JsonProperty("trsp_instruction_id")
    private String trspInstructionId;

    @JsonProperty("trsp_instruction_date_subm_dttm")
    private String trspInstructionDateSubmDttm;

    @JsonProperty("inv_num_id")
    private String invNumId;

    @JsonProperty("cmn_inv_num_id")
    private String cmnInvNumId;

    @JsonProperty("mix_load_num_id")
    private String mixLoadNumId;
}

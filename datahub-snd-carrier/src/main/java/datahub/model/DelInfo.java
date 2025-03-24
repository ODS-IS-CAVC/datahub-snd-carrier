package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * DelInfo（納品情報）Model
 * 
 */
@Data
public class DelInfo {
	
	@JsonProperty("del_note_id")
    private String delNoteId;

    @JsonProperty("shpm_num_id")
    private String shpmNumId;

    @JsonProperty("rced_ord_num_id")
    private String rcedOrdNumId;
}

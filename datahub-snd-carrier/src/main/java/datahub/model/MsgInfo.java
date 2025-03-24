package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * MsgInfo（メッセージ情報）Model
 * 
 */
@Data
public class MsgInfo {
	
	@JsonProperty("msg_id")
    private Integer msgId;

    @JsonProperty("msg_info_cls_typ_cd")
    private String msgInfoClsTypCd;

    @JsonProperty("msg_date_iss_dttm")
    private String msgDateIssDttm;

    @JsonProperty("msg_time_iss_dttm")
    private Integer msgTimeIssDttm;

    @JsonProperty("msg_fn_stas_cd")
    private String msgFnStasCd;

    @JsonProperty("note_dcpt_txt")
    private String noteDcptTxt;
    
}

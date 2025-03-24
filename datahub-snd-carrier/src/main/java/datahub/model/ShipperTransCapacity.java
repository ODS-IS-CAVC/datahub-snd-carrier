package datahub.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * ShipperTransCapacity（運送能力情報）Model
 * 
 */
@Data
public class ShipperTransCapacity {
	@JsonProperty("msg_info")
    private MsgInfo msgInfo;

    @JsonProperty("trsp_ability_line_item")
    private List<TrspAbilityLineItem> trspAbilityLineItem;
}

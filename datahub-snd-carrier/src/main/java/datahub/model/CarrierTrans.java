package datahub.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * CarrierTrans（運送計画明細）Model
 * 
 */
@Data
public class CarrierTrans {
	
	@JsonProperty("msg_info")
	private MsgInfo msgInfo;
	
	@JsonProperty("trsp_plan")
	private TrspPlan trspPlan;
	
	@JsonProperty("trsp_plan_line_item")
	private List<TrspPlanLineItem> trspPlanLineItem;
}

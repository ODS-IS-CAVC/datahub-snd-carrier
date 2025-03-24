package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * TrspPlan（運送計画）Model
 * 
 */
@Data
public class TrspPlan {
	
	@JsonProperty("trsp_plan_stas_cd")
	private String trspPlanStasCd;
}

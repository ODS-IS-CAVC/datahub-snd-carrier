package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * CutOffInfo（カットオフ情報）Model
 * 
 */
@Data
public class CutOffInfo {
	
	@JsonProperty("cut_off_time")
	private Double cutOffTime;
	
	@JsonProperty("cut_off_fee")
	private Integer cutOffFee;
}

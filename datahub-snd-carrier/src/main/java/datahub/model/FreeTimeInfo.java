package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * FreeTimeInfo（フリータイム情報）Model
 * 
 */
@Data
public class FreeTimeInfo {
	
	@JsonProperty("free_time")
	private Double freeTime;
	
	@JsonProperty("free_time_fee")
	private Integer freeTimeFee;
}

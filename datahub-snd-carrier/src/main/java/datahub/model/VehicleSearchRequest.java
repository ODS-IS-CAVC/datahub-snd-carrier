package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 車輛マスタ参照リクエスト
 *  
 */
@Data
@Builder
@AllArgsConstructor
public class VehicleSearchRequest implements CarrierApiRequest {
	/** 車種 */
	@JsonProperty("vehicle_type")
	private String vehicleType;
}

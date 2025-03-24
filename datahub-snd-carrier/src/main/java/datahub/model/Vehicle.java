package datahub.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Vehicle（車輛マスタ）Model
 * 
 */
@Data
public class Vehicle {
	
	@JsonProperty("vehicle_info")
	private VehicleInfo vehicleInfo;
	
	@JsonProperty("motas_info")
	private MotasInfo motasInfo;
	
	@JsonProperty("vehicle_details")
	private VehicleDetails vehicleDetails;
	
	@JsonProperty("max_carrying_capacity")
	private List<MaxCarryingCapacity> maxCarryingCapacityList;
	
	@JsonProperty("hazardous_material_info")
	private List<HazardousMaterialInfo> hazardousMaterialInfoList;
}

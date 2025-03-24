package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * VehicleDetails（車輌情報詳細）Model
 * 
 */
@Data
public class VehicleDetails {
	@JsonProperty("bed_height")
    private Long bedHeight;

    @JsonProperty("cargo_height")
    private Long cargoHeight;

    @JsonProperty("cargo_width")
    private Long cargoWidth;

    @JsonProperty("cargo_length")
    private Long cargoLength;

    @JsonProperty("max_cargo_capacity")
    private Double maxCargoCapacity;

    @JsonProperty("body_type")
    private String bodyType;

    @JsonProperty("power_gate")
    private String powerGate;

    @JsonProperty("wing_doors")
    private String wingDoors;

    @JsonProperty("refrigeration_unit")
    private String refrigerationUnit;

    @JsonProperty("temperature_range_min")
    private Double temperatureRangeMin;

    @JsonProperty("temperature_range_max")
    private Double temperatureRangeMax;

    @JsonProperty("crane_equipped")
    private String craneEquipped;

    @JsonProperty("vehicle_equipment_notes")
    private String vehicleEquipmentNotes;

    @JsonProperty("master_data_start_date")
    private String masterDataStartDate;

    @JsonProperty("master_data_end_date")
    private String masterDataEndDate;
}

package datahub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * MotasInfo（MOTAS情報）Model
 * 
 */
@Data
public class MotasInfo {
	@JsonProperty("max_payload_1")
    private Integer maxPayload1;

    @JsonProperty("max_payload_2")
    private Integer maxPayload2;

    @JsonProperty("vehicle_weight")
    private Integer vehicleWeight;

    @JsonProperty("vehicle_length")
    private Integer vehicleLength;

    @JsonProperty("vehicle_width")
    private Integer vehicleWidth;

    @JsonProperty("vehicle_height")
    private Integer vehicleHeight;

    @JsonProperty("hazardous_material_volume")
    private Integer hazardousMaterialVolume;

    @JsonProperty("hazardous_material_specific_gravity")
    private Double hazardousMaterialSpecificGravity;

    @JsonProperty("expiry_date")
    private String expiryDate;

    @JsonProperty("deregistration_status")
    private String deregistrationStatus;
}

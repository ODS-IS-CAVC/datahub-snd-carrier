package datahub.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * MaxCarryingCapacity（最大積載能力）Model
 * 
 */
@Data
public class MaxCarryingCapacity {
	
	@JsonProperty("package_code")
    private String packageCode;

    @JsonProperty("package_name_kanji")
    private String packageNameKanji;

    @JsonProperty("width")
    private BigDecimal width;

    @JsonProperty("height")
    private BigDecimal height;

    @JsonProperty("depth")
    private BigDecimal depth;

    @JsonProperty("dimension_unit_code")
    private String dimensionUnitCode;

    @JsonProperty("max_load_quantity")
    private Integer maxLoadQuantity;
}

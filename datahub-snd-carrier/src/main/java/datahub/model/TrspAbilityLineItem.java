package datahub.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * TrspAbilityLineItem（運送能力明細）Model
 * 
 */
@Data
public class TrspAbilityLineItem {
	@JsonProperty("road_carr")
    private RoadCarr roadCarr;

    @JsonProperty("logs_srvc_prv")
    private LogsSrvcPrv logsSrvcPrv;

    @JsonProperty("car_info")
    private List<CarInfo> carInfo;
    
    @JsonProperty("drv_info")
    private List<DrvInfo> drvInfo;
}

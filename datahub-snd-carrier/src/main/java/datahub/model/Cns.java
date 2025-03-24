package datahub.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Cns（委託貨物）Model
 * 
 */
@Data
public class Cns {
	
	@JsonProperty("istd_totl_pcks_quan")
    private Integer istdTotlPcksQuan;

    @JsonProperty("num_unt_cd")
    private String numUntCd;

    @JsonProperty("istd_totl_weig_meas")
    private BigDecimal istdTotlWeigMeas;

    @JsonProperty("weig_unt_cd")
    private String weigUntCd;

    @JsonProperty("istd_totl_vol_meas")
    private BigDecimal istdTotlVolMeas;

    @JsonProperty("vol_unt_cd")
    private String volUntCd;

    @JsonProperty("istd_totl_untl_quan")
    private Integer istdTotlUntlQuan;
    
    @JsonProperty("cns_line_item")
	private List<CnsLineItem> cnsLineItemList;
}

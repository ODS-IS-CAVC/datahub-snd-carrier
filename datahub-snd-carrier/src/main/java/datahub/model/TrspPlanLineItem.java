package datahub.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * TrspPlanLineItem（運送計画明細）Model
 * 
 */
@Data
public class TrspPlanLineItem {
	
	@JsonProperty("trsp_isr")
	private TrspIsr trspIsr;
	
	@JsonProperty("trsp_srvc")
	private TrspSrvc trspSrvc;
	
	@JsonProperty("trsp_vehicle_trms")
	private TrspVehicleTrms trspVehicleTrms;
	
	@JsonProperty("del_info")
	private DelInfo delInfo;
	
	@JsonProperty("cns")
	private Cns cns;
	
	@JsonProperty("cns_line_item")
	private List<CnsLineItem> cnsLineItemList;
	
	@JsonProperty("cnsg_prty")
	private CnsgPrty cnsgPrty;
	
	@JsonProperty("trsp_rqr_prty")
	private TrspRqrPrty trspRqrPrty;
	
	@JsonProperty("cnee_prty")
	private CneePrty cneePrty;
	
	@JsonProperty("logs_srvc_prv")
	private LogsSrvcPrv logsSrvcPrv;
	
	@JsonProperty("road_carr")
	private RoadCarr roadCarr;
	
	@JsonProperty("fret_clim_to_prty")
	private FretClimToPrty fretClimToPrty;
	
	@JsonProperty("ship_from_prty")
	private List<ShipFromPrty> shipFromPrtyList;
	
	@JsonProperty("ship_to_prty")
	private List<ShipToPrty> shipToPrtyList;
}

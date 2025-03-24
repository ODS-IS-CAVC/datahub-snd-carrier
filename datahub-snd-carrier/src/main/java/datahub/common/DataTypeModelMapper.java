package datahub.common;

import java.util.Arrays;

import datahub.model.CarrierTrans;
import datahub.model.ShipperTransCapacity;
import datahub.model.Vehicle;
import lombok.Getter;

/**
 * 情報区分とデータモデルのマッパー
 * 
 */
@Getter
public enum DataTypeModelMapper {
	
	// 情報区分単位に紐づくクラスを指定する
	VEHICLE("4902", Vehicle.class, "車輛マスタ"),
	SHIPPER_TRANS_CAPACISTY("5001", ShipperTransCapacity.class, "荷主向け運行案件"),
	CARRIER_TRANS_REQUEST("30121", CarrierTrans.class, "運送計画情報（明細型）キャリア向け運行依頼案件"),
	CARRIER_TRANS_CAPACITY("30122", CarrierTrans.class,"運送計画情報（明細型）キャリア向け運行案件");

    private final String dataType;
    private final Class<?> clazz;
    private final String dataName;

    /**
     * コンストラクタ
     * 
     */
    DataTypeModelMapper(String dataType, Class<?> clazz, String dataName) {
        this.dataType = dataType;
        this.clazz = clazz;
        this.dataName = dataName;
    }

    /**
     * 存在する情報区分か確認
     * 
     */
    public static DataTypeModelMapper fromCode(String code) {
        return Arrays.stream(DataTypeModelMapper.values())
                     .filter(jsonType -> jsonType.dataType.equals(code))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("存在しない情報区分が選択されています。情報区分: " + code));
    }

}

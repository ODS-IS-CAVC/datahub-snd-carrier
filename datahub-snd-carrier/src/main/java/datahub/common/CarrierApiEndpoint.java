package datahub.common;

import java.util.Arrays;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
* 情報区分とエンドポイントのマッパー
*
*/
@Getter
@Component
public enum CarrierApiEndpoint implements ApplicationContextAware {
    // 情報区分単位に紐づくエンドポイントを指定する
    VEHICLE("4902", "vehicle"),
    SHIPPER_TRANS_CAPACITY("5001", "shipper_trans_capacity"),
    CARRIER_TRANS_REQUEST("30121", "carrier_trans_request"),
    CARRIER_TRANS_CAPACITY("30122", "carrier_trans_capacity");
    
    private final String dataType;
    private String endpoint;
    
    private static ApplicationContext context;
    
    /**
     * コンストラクタ
     *
     */
    CarrierApiEndpoint(String dataType, String endpointKey) {
        this.dataType = dataType;
        this.endpoint = endpointKey; // 一時的にキーを保存
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
        for (CarrierApiEndpoint endpoint : CarrierApiEndpoint.values()) {
            endpoint.endpoint = context.getEnvironment().getProperty(endpoint.endpoint);
        }
    }
    
    /**
     * 存在する情報区分か確認
     *
     */
    public static CarrierApiEndpoint fromCode(String code) {
        return Arrays.stream(CarrierApiEndpoint.values())
                     .filter(jsonType -> jsonType.dataType.equals(code))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("存在しない情報区分が選択されています。情報区分: " + code));
    }
}

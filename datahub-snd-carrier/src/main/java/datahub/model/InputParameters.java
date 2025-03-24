package datahub.model;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datahub.common.DataTypeModelMapper;
import datahub.exception.DataHubException;
import lombok.Data;

/**
 * ユーザー情報
 * 
 */
@Data
public class InputParameters {
	
	private static final Logger logger = LoggerFactory.getLogger(InputParameters.class);
	
	/** 情報区分 */
	private String dataType;
	
	/** 送信元企業ID */
    private String fromId;
    
    /** DataHub ユーザーID */
    private String userId;
    
    /** DataHub パスワード */
    private String password;
    
    /** 車種 */
    private String vehicleType;
    
    /** 便・ダイヤ番号 */
    private String serviceNo;
    
    /** 便・ダイヤ名称 */
    private String serviceName;
    
    /** 最大積載量1 */
    private String carMaxLoadCapacity1Meas;
    
    /** 運行開始地域 */
    private String trspOpStrtAreaLineOneTxt;
    
    /** 運行終了地域 */
    private String trspOpEndAreaLineOneTxt;
    
    /** 運行開始日最大値 */
    private String maxTrspOpDateTrmStrtDate;
    
    /** 運行開始日最小値 */
    private String minTrspOpDateTrmStrtDate;
    
    /** 運行終了日最大値 */
    private String maxTrspOpDateTrmEndDate;
    
    /** 運行終了日最小値 */
    private String minTrspOpDateTrmEndDate;
    
    /** 運行開始希望時刻最大値 */
    private String maxTrspOpPlanDateTrmStrtTime;
    
    /** 運行開始希望時刻最小値 */
    private String minTrspOpPlanDateTrmStrtTime;
    
    /** 運行終了希望時刻最大値 */
    private String maxTrspOpPlanDateTrmEndTime;
    
    /** 運行終了希望時刻最小値 */
    private String minTrspOpPlanDateTrmEndTime;

    // コンストラクタ
    public InputParameters(String... args) throws DataHubException {

    	if(args.length != 18) {
			throw new DataHubException(String.format("必要な引数が不足しています 必要な引数: %d 入力された引数: %d", 18, args.length));
		}
    	
    	logger.debug("args: " + String.join(" ", Arrays.stream(args)
    		    .map(arg -> "\"" + arg + "\"")
    		    .toArray(String[]::new)));
    	
    	// 情報区分
    	this.dataType = args[0];
    	
    	// 送信元企業ID
        this.fromId = args[1];
        
        // DataHubユーザーID
        this.userId = args[2];
        
        // DataHubパスワード
        this.password = args[3];
        		
        // 共通項目のバリデーション
        this.validateDataType();
		this.validateFromId();
		this.validateAuthInfo();
		
		DataTypeModelMapper jsonType = DataTypeModelMapper.fromCode(dataType);
		logger.info("情報区分: " + jsonType.getDataType() + " 機能名: " + jsonType.getDataName() + "[参照]");
        
		switch (jsonType) {
			case VEHICLE:
				this.setArgsForVehicle(args);
				this.validateVehicle();
				this.outputLog();
				break;
			case SHIPPER_TRANS_CAPACISTY:
				this.setArgsForShipperTransCapacity(args);
				this.validateShipperTransCapacity();
				this.outputLog();
				break;
			case CARRIER_TRANS_REQUEST, CARRIER_TRANS_CAPACITY:
				this.setArgsForCarrierTrans(args);
				this.validateCarrierTrans();
				this.outputLog();
				break;
			default:
				throw new DataHubException("存在しない情報区分が選択されています。情報区分: " + dataType);
		}
    }
    
    /**
     * null または空白かどうかをチェック
     */
    private void validateRequired(final String value, final String fieldName, final boolean isRequired) {
    	if (value == null || value.isBlank()) {
            if (isRequired) {
                throw new DataHubException(fieldName + "は必須です。");
            }
        }
    }

    /**
     *  指定された文字数かどうかをチェック
     */
    private void validateLength(final String value, final String fieldName, final int length) {
        if (value.length() != length) {
            throw new DataHubException(fieldName + "は" + length + "桁でなければなりません。");
        }
    }
    
    /**
     * 指定された文字数以下かどうかをチェック
     */
    public void validateLengthLessThan(final String value, final String fieldName, final int length) {
    	if (value.length() > length) {
            throw new DataHubException(fieldName + "は" + length + "桁以下でなければなりません。");
        }
    }

    /**
     * 指定された範囲の文字数かどうかをチェック
     */
    public void validateLengthInRange(final String value, final String fieldName, final int minLength, final int maxLength) {
    	if (value.length() < minLength || value.length() > maxLength) {
            throw new DataHubException(fieldName + "は" + minLength + "~" + maxLength + "桁でなければなりません。");
        }
    }

    /** 
     * 数字かどうかをチェック
     */
    public void validateNumeric(final String value, final String fieldName) {
    	if (!value.chars().allMatch(Character::isDigit)) {
            throw new DataHubException(fieldName + "は数字でなければなりません。");
        }
    }
    
    /**
     * 認証情報に関するチェック
     */
    public void validateAuthInfo() {
    	if (userId == null || password == null || userId.length() > 20 || password.length() > 64) {
            throw new DataHubException("ユーザIDまたはパスワードが不正です。");
        }
    }
    
    /**
     * 車輛マスタ用の引数設定
     */
    private void setArgsForVehicle(final String... args) {
    	this.vehicleType = args[4];
    }
    
    /**
     * 運送能力情報 荷主向け運行案件の引数設定
     */
    private void setArgsForShipperTransCapacity(final String... args) {
    	serviceNo = args[5];
        serviceName = args[6];
        carMaxLoadCapacity1Meas = args[7];
        trspOpStrtAreaLineOneTxt = args[8];
        trspOpEndAreaLineOneTxt = args[9];
        maxTrspOpDateTrmStrtDate = args[10];
        minTrspOpDateTrmStrtDate = args[11];
        maxTrspOpDateTrmEndDate = args[12];
        minTrspOpDateTrmEndDate = args[13];
        maxTrspOpPlanDateTrmStrtTime = args[14];
        minTrspOpPlanDateTrmStrtTime = args[15];
        maxTrspOpPlanDateTrmEndTime = args[16];
        minTrspOpPlanDateTrmEndTime = args[17];
    }
    
    /**
     * 運送計画情報（明細型）の引数設定
     * キャリア向け運行依頼案件およびキャリア向け運行案件
     */
    private void setArgsForCarrierTrans(final String... args) {
    	serviceNo = args[5];
        serviceName = args[6];
        carMaxLoadCapacity1Meas = args[7];
        trspOpStrtAreaLineOneTxt = args[8];
        trspOpEndAreaLineOneTxt = args[9];
        maxTrspOpDateTrmStrtDate = args[10];
        minTrspOpDateTrmStrtDate = args[11];
        maxTrspOpDateTrmEndDate = args[12];
        minTrspOpDateTrmEndDate = args[13];
        maxTrspOpPlanDateTrmStrtTime = args[14];
        minTrspOpPlanDateTrmStrtTime = args[15];
        maxTrspOpPlanDateTrmEndTime = args[16];
        minTrspOpPlanDateTrmEndTime = args[17];
    }
        
	/**
	 * 情報区分のバリデーション
	 */
	private void validateDataType() {
    	this.validateRequired(dataType, "情報区分", true);
		this.validateLengthInRange(dataType, "情報区分", 4, 5);
	}
	
	/**
	 * 送信元企業IDのバリデーション
	 */
	private void validateFromId() {
		this.validateRequired(fromId, "送信元企業ID", true);
		this.validateLength(fromId, "送信元企業ID", 16);
	}
	
	/**
	 * 車輛マスタのバリデーション
	 */
	private void validateVehicle() {
		String fieldName = "車種";
	    this.validateRequired(vehicleType, fieldName, true);
	    if (!vehicleType.isBlank()) {
	    	this.validateLength(vehicleType, fieldName, 1);
	    	this.validateNumeric(vehicleType, fieldName);	    	
	    }
	}
	
	/**
	 * 運送能力情報のバリデーション
	 */
	private void validateShipperTransCapacity() {
		// 便・ダイヤ番号
		String fieldName = "便・ダイヤ番号";
		this.validateRequired(serviceNo, fieldName, false);
	    this.validateLengthLessThan(serviceNo, fieldName, 20);
	    
	    // 便・ダイヤ名称
	    fieldName = "便・ダイヤ名称";
	    this.validateRequired(serviceName, fieldName, false);
	    this.validateLengthLessThan(serviceName, fieldName, 24);
	    
	    // 最大積載量1
	    fieldName = "最大積載量1";
	    this.validateRequired(carMaxLoadCapacity1Meas, fieldName, false);
	    this.validateLengthLessThan(carMaxLoadCapacity1Meas, fieldName, 6);
	    this.validateNumeric(carMaxLoadCapacity1Meas, fieldName);
	    
	    // 運行開始地域
	    fieldName = "運行開始地域";
	    this.validateRequired(trspOpStrtAreaLineOneTxt, fieldName, false);
	    this.validateLengthLessThan(trspOpStrtAreaLineOneTxt, fieldName, 20);
	    
	    // 運行終了地域
	    fieldName = "運行終了地域";
	    this.validateRequired(trspOpEndAreaLineOneTxt, fieldName, false);
	    this.validateLengthLessThan(trspOpEndAreaLineOneTxt, fieldName, 20);
	    
	    // 運行開始日最大値
	    fieldName = "運行開始日最大値";
	    this.validateRequired(maxTrspOpDateTrmStrtDate, fieldName, false);
	    if (!maxTrspOpDateTrmStrtDate.isBlank()) {
	    	this.validateLength(maxTrspOpDateTrmStrtDate, fieldName, 8);
	    	this.validateNumeric(maxTrspOpDateTrmStrtDate, fieldName);
	    }
	    
	    fieldName = "運行開始日最小値";
	    this.validateRequired(minTrspOpDateTrmStrtDate, fieldName, false);
	    if (!minTrspOpDateTrmStrtDate.isBlank()) {
	    	this.validateLength(minTrspOpDateTrmStrtDate, fieldName, 8);
	    	this.validateNumeric(minTrspOpDateTrmStrtDate, fieldName);	    	
	    }
	    
	    // 運行終了日最大値
	    fieldName = "運行終了日最大値";
	    this.validateRequired(maxTrspOpDateTrmEndDate, fieldName, false);
	    if (!maxTrspOpDateTrmEndDate.isBlank()) {
	    	this.validateLength(maxTrspOpDateTrmEndDate, fieldName, 8);
	    	this.validateNumeric(maxTrspOpDateTrmEndDate, fieldName);	    	
	    }
	    
	    // 運行終了日最小値
	    fieldName = "運行終了日最小値";
	    this.validateRequired(minTrspOpDateTrmEndDate, fieldName, false);
	    if (!minTrspOpDateTrmEndDate.isBlank()) {
	    	this.validateLength(minTrspOpDateTrmEndDate, fieldName, 8);
	    	this.validateNumeric(minTrspOpDateTrmEndDate, fieldName);	    	
	    }
	    
	    // 運行開始希望時刻最大値
	    fieldName = "運行開始希望時刻最大値";
	    this.validateRequired(maxTrspOpPlanDateTrmStrtTime, fieldName, false);
	    if (!maxTrspOpPlanDateTrmStrtTime.isBlank()) {
	    	this.validateLength(maxTrspOpPlanDateTrmStrtTime, fieldName, 4);
	    	this.validateNumeric(maxTrspOpPlanDateTrmStrtTime, fieldName);	    	
	    }
	    
	    // 運行開始希望時刻最小値
	    fieldName = "運行開始希望時刻最小値";
	    this.validateRequired(minTrspOpPlanDateTrmStrtTime, fieldName, false);
	    if (!minTrspOpPlanDateTrmStrtTime.isBlank()) {
	    	this.validateLength(minTrspOpPlanDateTrmStrtTime, fieldName, 4);
	    	this.validateNumeric(minTrspOpPlanDateTrmStrtTime, fieldName);	    	
	    }
	    
	    // 運行終了希望時刻最大値
	    fieldName = "運行終了希望時刻最大値";
	    this.validateRequired(maxTrspOpPlanDateTrmEndTime, fieldName, false);
	    if (!maxTrspOpPlanDateTrmEndTime.isBlank()) {
	    	this.validateLength(maxTrspOpPlanDateTrmEndTime, fieldName, 4);
	    	this.validateNumeric(maxTrspOpPlanDateTrmEndTime, fieldName);	    	
	    }
	    
	    // 運行終了希望時刻最小値
	    fieldName = "運行終了希望時刻最小値";
	    this.validateRequired(minTrspOpPlanDateTrmEndTime, fieldName, false);
	    if (!minTrspOpPlanDateTrmEndTime.isBlank()) {
	    	this.validateLength(minTrspOpPlanDateTrmEndTime, fieldName, 4);
	    	this.validateNumeric(minTrspOpPlanDateTrmEndTime, fieldName);	    	
	    }
	}
	
	/**
	 * 運送計画情報（明細型）のバリデーション
	 */
	private void validateCarrierTrans() {
		// 便・ダイヤ番号
		String fieldName = "便・ダイヤ番号";
		this.validateRequired(serviceNo, fieldName, false);
	    this.validateLengthLessThan(serviceNo, fieldName, 20);
	    
	    // 便・ダイヤ名称
	    fieldName = "便・ダイヤ名称";
	    this.validateRequired(serviceName, fieldName, false);
	    this.validateLengthLessThan(serviceName, fieldName, 24);
	    
	    // 最大積載量1
	    fieldName = "最大積載量1";
	    this.validateRequired(carMaxLoadCapacity1Meas, fieldName, false);
	    this.validateLengthLessThan(carMaxLoadCapacity1Meas, fieldName, 6);
	    this.validateNumeric(carMaxLoadCapacity1Meas, fieldName);
	    
	    // 運行開始地域
	    fieldName = "運行開始地域";
	    this.validateRequired(trspOpStrtAreaLineOneTxt, fieldName, false);
	    this.validateLengthLessThan(trspOpStrtAreaLineOneTxt, fieldName, 20);
	    
	    // 運行終了地域
	    fieldName = "運行終了地域";
	    this.validateRequired(trspOpEndAreaLineOneTxt, fieldName, false);
	    this.validateLengthLessThan(trspOpEndAreaLineOneTxt, fieldName, 20);
	    
	    // 運行開始日最大値
	    fieldName = "運行開始日最大値";
	    this.validateRequired(maxTrspOpDateTrmStrtDate, fieldName, false);
	    if (!maxTrspOpDateTrmStrtDate.isBlank()) {
	    	this.validateLength(maxTrspOpDateTrmStrtDate, fieldName, 8);
	    	this.validateNumeric(maxTrspOpDateTrmStrtDate, fieldName);
	    }
	    
	    fieldName = "運行開始日最小値";
	    this.validateRequired(minTrspOpDateTrmStrtDate, fieldName, false);
	    if (!minTrspOpDateTrmStrtDate.isBlank()) {
	    	this.validateLength(minTrspOpDateTrmStrtDate, fieldName, 8);
	    	this.validateNumeric(minTrspOpDateTrmStrtDate, fieldName);	    	
	    }
	    
	    // 運行終了日最大値
	    fieldName = "運行終了日最大値";
	    this.validateRequired(maxTrspOpDateTrmEndDate, fieldName, false);
	    if (!maxTrspOpDateTrmEndDate.isBlank()) {
	    	this.validateLength(maxTrspOpDateTrmEndDate, fieldName, 8);
	    	this.validateNumeric(maxTrspOpDateTrmEndDate, fieldName);	    	
	    }
	    
	    // 運行終了日最小値
	    fieldName = "運行終了日最小値";
	    this.validateRequired(minTrspOpDateTrmEndDate, fieldName, false);
	    if (!minTrspOpDateTrmEndDate.isBlank()) {
	    	this.validateLength(minTrspOpDateTrmEndDate, fieldName, 8);
	    	this.validateNumeric(minTrspOpDateTrmEndDate, fieldName);	    	
	    }
	    
	    // 運行開始希望時刻最大値
	    fieldName = "運行開始希望時刻最大値";
	    this.validateRequired(maxTrspOpPlanDateTrmStrtTime, fieldName, false);
	    if (!maxTrspOpPlanDateTrmStrtTime.isBlank()) {
	    	this.validateLength(maxTrspOpPlanDateTrmStrtTime, fieldName, 4);
	    	this.validateNumeric(maxTrspOpPlanDateTrmStrtTime, fieldName);	    	
	    }
	    
	    // 運行開始希望時刻最小値
	    fieldName = "運行開始希望時刻最小値";
	    this.validateRequired(minTrspOpPlanDateTrmStrtTime, fieldName, false);
	    if (!minTrspOpPlanDateTrmStrtTime.isBlank()) {
	    	this.validateLength(minTrspOpPlanDateTrmStrtTime, fieldName, 4);
	    	this.validateNumeric(minTrspOpPlanDateTrmStrtTime, fieldName);	    	
	    }
	    
	    // 運行終了希望時刻最大値
	    fieldName = "運行終了希望時刻最大値";
	    this.validateRequired(maxTrspOpPlanDateTrmEndTime, fieldName, false);
	    if (!maxTrspOpPlanDateTrmEndTime.isBlank()) {
	    	this.validateLength(maxTrspOpPlanDateTrmEndTime, fieldName, 4);
	    	this.validateNumeric(maxTrspOpPlanDateTrmEndTime, fieldName);	    	
	    }
	    
	    // 運行終了希望時刻最小値
	    fieldName = "運行終了希望時刻最小値";
	    this.validateRequired(minTrspOpPlanDateTrmEndTime, fieldName, false);
	    if (!minTrspOpPlanDateTrmEndTime.isBlank()) {
	    	this.validateLength(minTrspOpPlanDateTrmEndTime, fieldName, 4);
	    	this.validateNumeric(minTrspOpPlanDateTrmEndTime, fieldName);	    	
	    }
	}
	
	/**
	 * ログ出力
	 */
	private void outputLog() {
		logValue("情報区分", dataType);
        logValue("送信元企業ID", fromId);
        logValue("DataHub ユーザーID", userId);
        logValue("DataHub パスワード", password);
        logValue("車種", vehicleType);
        logValue("便・ダイヤ番号", serviceNo);
        logValue("便・ダイヤ名称", serviceName);
        logValue("最大積載量1", carMaxLoadCapacity1Meas);
        logValue("運行開始地域", trspOpStrtAreaLineOneTxt);
        logValue("運行終了地域", trspOpEndAreaLineOneTxt);
        logValue("運行開始日最大値", maxTrspOpDateTrmStrtDate);
        logValue("運行開始日最小値", minTrspOpDateTrmStrtDate);
        logValue("運行終了日最大値", maxTrspOpDateTrmEndDate);
        logValue("運行終了日最小値", minTrspOpDateTrmEndDate);
        logValue("運行開始希望時刻最大値", maxTrspOpPlanDateTrmStrtTime);
        logValue("運行開始希望時刻最小値", minTrspOpPlanDateTrmStrtTime);
        logValue("運行終了希望時刻最大値", maxTrspOpPlanDateTrmEndTime);
        logValue("運行終了希望時刻最小値", minTrspOpPlanDateTrmEndTime);
	}
	
	private void logValue(String fieldName, String value) {
		if (value == null || value.isEmpty()) {
            logger.info("{}: 未設定", fieldName);
        } else {
            logger.info("{}: {}", fieldName, value);
        }
	}
}

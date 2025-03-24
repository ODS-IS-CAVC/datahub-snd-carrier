package datahub.job;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import datahub.config.ApiConfig;
import datahub.model.InputParameters;
import datahub.service.CarrierApiService;
import datahub.service.ConvertService;
import datahub.service.DataHubApiService;
import datahub.util.CommonUtil;
import datahub.util.FileUtil;

/**
 * JSONデータからCSVファイルに変換ジョブ
 *
 */
@Component
public class Job implements CommandLineRunner{
	
	private static final Logger logger = LoggerFactory.getLogger(Job.class);
	
	@Autowired
	private ApiConfig apiConfig;
		
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private CarrierApiService carrierApiService;
	
	@Autowired
	private DataHubApiService dataHubApiService;
	
	@Autowired
	private ConvertService convertSerivce;
	
	@Autowired
	private FileUtil fileUtil;
	
	/**
	 * 実行処理
	 */
	@Override
	public void run(String... args) throws Exception {
		logger.info("JSON->CSVのバッチ処理");
		
		// 引数チェック及び取得
		InputParameters inputParameters = new InputParameters(args);
		
		// 認証API接続
		String accessToken = carrierApiService.login();
		
		// データ変換APIに対してリクエスト、JSONファイルの取得
		Optional<String> jsonData = carrierApiService.fetchJson(inputParameters, accessToken);
		if (jsonData.isEmpty()) {
			logger.warn("JSONデータ: 0Byte");
			logger.info("JSON->CSVのバッチ処理を終了");
			return;
		}
		
		// eventId生成
		String eventId = commonUtil.createEventId();
		logger.info("イベントID: " + eventId);
		
		// 活殺チェック
		dataHubApiService.healthCheck(eventId, inputParameters);
		
		// JSON TO CSV
		List<String> csvRecords =  convertSerivce.writeJsonToCsv(jsonData.get(), inputParameters, eventId);
		if(csvRecords.size() == 0) {
			logger.warn("CSVレコード: 0件");
			logger.info("JSON->CSVのバッチ処理を終了");
			return;
		}
		
		//CSVレコードログ出力用
		if (apiConfig.isCsvOutputEnabled()) {
			logger.debug("CSVレコード");
			logger.debug("-".repeat(30));
			csvRecords.stream().forEach(record -> logger.debug("record: {}", record));
			logger.debug("-".repeat(30));
		}
		
		// CSV出力
		Path csvFilePath = fileUtil.createOutputFilePath(inputParameters, eventId);
		csvFilePath = fileUtil.writeCsvToFile(csvRecords, csvFilePath);
		
		// ZIP化
		Path zipFilePath = fileUtil.zipCsvFile(csvFilePath);
		
		// CSV削除
		if (!apiConfig.isCsvOutputEnabled()) {
			fileUtil.deleteFile(csvFilePath);
		}
		
		// ZIP送信(レスポンスのうち、process_idを取得)
		dataHubApiService.sendFileToDataHub(zipFilePath, eventId, inputParameters);
		
		// ZIP削除
		if (!apiConfig.isCsvOutputEnabled()) {
			fileUtil.deleteFile(zipFilePath);
		}
		
		logger.info("JSON->CSVのバッチ処理を終了");
	}
}

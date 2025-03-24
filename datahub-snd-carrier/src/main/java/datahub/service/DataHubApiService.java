package datahub.service;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datahub.model.HealthCheckRequest;
import datahub.model.InputParameters;
import datahub.model.UploadFileRequest;
import datahub.util.DataHubApiUtil;
import datahub.util.FileUtil;

/**
 * DataHubAPIサービス
 *  
 */
@Service
public class DataHubApiService {
	
	private static final Logger logger = LoggerFactory.getLogger(DataHubApiService.class);
	
	@Autowired
	private DataHubApiUtil dataHubApiUtil;
	
	@Autowired
	private FileUtil fileUtil;
	
	/**
	 * 活殺チェック
	 * 
	 */
	public void healthCheck(final String eventId, final InputParameters userInfo) throws Exception {
		logger.info("DataHubの活殺チェック開始");
		//リクエスト用パラメータの設定
		HealthCheckRequest healthCheckRequest = new HealthCheckRequest();
		healthCheckRequest.setFromId(userInfo.getFromId());
		healthCheckRequest.setEventId(eventId);
		healthCheckRequest.setDhUserId(userInfo.getUserId());
		healthCheckRequest.setDhPassword(userInfo.getPassword());
		
		//リクエストの送信
		dataHubApiUtil.healthcheck(healthCheckRequest);
		logger.info("DataHubの活殺チェック終了: OK");
	}
	
	/**
	 * DataHubにZIPファイル送信
	 * 
	 */
	public String sendFileToDataHub(final Path zipFilePath, final String eventId, final InputParameters userInfo) throws Exception {
		logger.info("DataHubへの送信開始");
		//リクエスト用パラメータの設定
		UploadFileRequest uploadFileRequest = new UploadFileRequest();
		String dataTypeToUse =userInfo.getDataType();
		if (dataTypeToUse.length() == 5) {
			dataTypeToUse = dataTypeToUse.substring(0, 4);
		}
		uploadFileRequest.setFromId(userInfo.getFromId());
		uploadFileRequest.setDataType(dataTypeToUse);
		uploadFileRequest.setEventId(eventId);
		uploadFileRequest.setZipfileContent(fileUtil.encodeBase64(zipFilePath));
		uploadFileRequest.setDhUserId(userInfo.getUserId());
		uploadFileRequest.setDhPassword(userInfo.getPassword());
		
		//リクエストの送信
		String processId = dataHubApiUtil.sendFileToDataHub(uploadFileRequest);
		logger.info("DataHubへの送信完了 プロセスID: " + processId);
		return processId;
	}
}

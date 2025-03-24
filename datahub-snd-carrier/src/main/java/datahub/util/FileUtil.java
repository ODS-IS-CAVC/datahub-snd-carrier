package datahub.util;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import datahub.common.Constants;
import datahub.config.ApiConfig;
import datahub.exception.DataHubException;
import datahub.model.InputParameters;

/**
 * ファイル操作クラス
 * 
 */
@Component
public class FileUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	@Autowired
	private ApiConfig config;
	
	/** 出力先のディレクトリ（受信） */
	private final String RECV_PATH = "recv";
	
	/**
	 * 出力先ファイルパス作成
	 * 
	 */
	public Path createOutputFilePath(final InputParameters userInfo, final String eventId) {
		String fromId = userInfo.getFromId();
		String dataType = userInfo.getDataType();
		Path csvOutputDir = Paths.get(this.config.getBaseCsvOutputPath(),fromId,RECV_PATH,dataType);
		
		// ディレクトリ存在チェック
		this.ensureDirectoryExists(csvOutputDir);
		String csvFileName = dataType + "_" + eventId + ".csv";
		return Paths.get(csvOutputDir.toString(),csvFileName);
	}
	
	/**
	 * CSVファイル出力
	 * 
	 */
	public Path writeCsvToFile(List<String> csvData, Path csvOutputPath) {
		 
		logger.info("CSVファイル作成開始: " + csvOutputPath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvOutputPath.toString(),StandardCharsets.UTF_8))) {
            for (String line : csvData) {
                writer.write(line);
                writer.newLine();
            }
            logger.info("CSVファイル作成完了: " + csvOutputPath);
            return csvOutputPath;
        } catch (IOException e) {
            throw new DataHubException("CSVファイルの書き込み中にエラーが発生しました: ", e);
        }
	}
	
	/**
	 * ZIPファイル出力
	 * 
	 */
	public Path zipCsvFile(final Path csvFilePath) throws Exception {
		logger.info("ZIPファイル作成開始: " + csvFilePath.toString());
		if (csvFilePath == null || !Files.exists(csvFilePath)) {
            throw new IllegalArgumentException("指定されたCSVファイルが存在しません: " + csvFilePath);
        }
		
		// ZIPファイルのパスを設定 (同じディレクトリに同名の.zipファイルとして出力)
        Path zipFilePath = Paths.get(csvFilePath.toString().replaceFirst("[.][^.]+$", "") + ".zip");
        
        // ZIPファイルを作成
        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(csvFilePath.toFile())) {

            // ZIPエントリを追加
            ZipEntry zipEntry = new ZipEntry(csvFilePath.getFileName().toString());
            zos.putNextEntry(zipEntry);

            // CSVファイルの内容を読み込み、ZIPに書き込み
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
            logger.info("ZIPファイル作成完了: " + zipFilePath);

            return zipFilePath;
        } catch (IOException e) {
            throw new DataHubException(Constants.ERR_MSG,e);
        }
	}
	
	/**
	 * ファイル削除
	 * 
	 */
	public boolean deleteFile(final Path filePath) {
		if (filePath == null || !Files.exists(filePath)) {
            throw new IllegalArgumentException("指定されたCSVファイルが存在しません: " + filePath);
        }

        try {
            Files.delete(filePath);
            logger.info("ファイルの削除完了: " + filePath);
            return true;
        } catch (IOException e) {
            throw new DataHubException(Constants.ERR_MSG, e);
        }
	}
	
	/** 
	 * ファイルデータをbase64でencode
	 * 
	 */
	public String encodeBase64(final Path uploadZipFilePath) throws IOException{
		byte[] bytes = Files.readAllBytes(uploadZipFilePath);
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	/** 
	 * ディレクトリ存在チェック
	 * 
	 */
	private Path ensureDirectoryExists(final Path csvOutputPath) {
        if (Files.notExists(csvOutputPath)) {
            try {
            	logger.info("ディレクトリを作成: " + csvOutputPath);
                Files.createDirectories(csvOutputPath);
            } catch (IOException e) {
                throw new DataHubException(Constants.ERR_MSG, e);
            }
        }
        
        return csvOutputPath;
    }
	
	/** 
	 * フィールドがnullの場合に空文字を返す共通メソッド
	 * 
	 */
    public String getSafeValue(final String value) {
        return Optional.ofNullable(value).orElse("");
    }

    /**
     *  double型フィールドがnullまたは0の場合に空文字を返す共通メソッド
     *  
     */
    public String getSafeValue(final Double value) {
        return (value != null && value != 0.0) ? String.valueOf(value) : "";
    }

    /**
     *  CSV行を書き出す共通メソッド
     *  
     */
    public void writeCsvLine(final BufferedWriter writer, final String... values) throws IOException {
        writer.write(String.join(",", values) + "\n");
    }
}

package com.example.quartz.utils;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Van
 * @version 1.0
 */
public class DownloadHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadHelper.class);
	private static int buffer = 8192;
	
	/**
	 * 變更預設串流下載的byte數
	 *
	 * @param bytes
	 *            串流的byte數, int型別
	 */
	public static void setBuffer(int bytes) {
		buffer = bytes;
	}
	
	private DownloadHelper() {
	}

	/**
	 * 下載指定來源網址的檔案, 並以串流輸出與指定路徑
	 *
	 * @see URL
	 * @param urlStr
	 *            來源網址, String型別
	 * @param outputFilePath
	 *            輸出檔案路徑, String型別
	 * @param outputFileName
	 *            輸出檔案名稱, String型別
	 * @return 下載的檔案, File型別
	 * @throws IOException
	 */
	public static File download(String urlStr, String outputFilePath, String outputFileName) throws IOException {
		File outputFile = null;
		try (CloseableHttpClient httpClient = (CloseableHttpClient) HttpUtils.createHttpClient();
             CloseableHttpResponse response = httpClient.execute(HttpUtils.toHttpRequest(urlStr, HttpUtils.customHeaderForPTX()))) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				outputFile = new File(outputFilePath, outputFileName);
				File parentFile = outputFile.getParentFile();
				if (!parentFile.exists()) parentFile.mkdirs();
				try (BufferedInputStream in = new BufferedInputStream(response.getEntity().getContent());
						BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
					byte[] bytes = new byte[buffer];
					int size;
					while ((size = in.read(bytes)) != -1) {
						out.write(bytes, 0, size);
					}
					out.flush();
				}
			} else {
				throw new IOException("The URL is not available so that the connection failed.");
			}
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return outputFile;
	}

	/**
	 * [方法多載]<br>
	 * 支援傳入File型別的參數; 下載指定來源網址的檔案, 並以串流輸出與指定路徑
	 *
	 * @see URL
	 * @param urlStr
	 *            來源網址, String型別
	 * @param outpitFile
	 *            輸出檔案, File型別
	 * @return 下載的檔案, File型別
	 * @throws IOException
	 */
	public static File download(String urlStr, File outpitFile) throws IOException {
		return download(urlStr, outpitFile.getParent(), outpitFile.getName());
	}

	/**
	 * [方法多載]<br>
	 * 下載指定來源網址的檔案, 並以串流輸出與指定路徑<br>
	 * 倘若可以從網址中解析出檔案名稱, 則以此作為檔名; 否則拋出例外
	 *
	 * @param urlStr
	 *            來源網址, String型別
	 * @param destination
	 *            輸出檔案的位置, String型別
	 * @return 下載的檔案, File型別
	 * @throws IOException
	 *             無法解析檔名的例外
	 */
	public static File download(String urlStr, String destination) throws IOException {
		File destDir = new File(destination);
		if (!destDir.exists()) destDir.mkdirs();
		
		// 若可以從url header中解析出檔案名稱, 則自動產生檔案名稱; 反之則拋出例外
		String attachmentName = HttpUtils.getAttachmentName(urlStr);
		if (ClassUtils.isValid(attachmentName)) {
			return download(urlStr, destination, attachmentName);
		} else {
			throw new IOException("There is no file name! Please try the other method!\nEx: download(String urlStr, String destination, String fileName)");
		}
	}

}

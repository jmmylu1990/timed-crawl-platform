package com.example.quartz.componet;

import com.example.quartz.enums.ApiFormatEnum;
import com.example.quartz.utils.DateUtils;
import com.example.quartz.utils.HttpUtils;
import com.example.quartz.utils.StringTools;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class ApiRewriteComponent {

	private static final String DTAE_EXPRESSION_PATTERN_FORMAT = "(?i)(?<=\\$\\{)(\\(?\\s*%s\\s*([\\-\\+]\\s*\\d+\\w\\s*)*\\)?(\\s*:\\s*[\\w\\-\\./]+)?)(?=\\})";
	private static final String OPERATION_PATTERN = "(?i)\\s*[\\+\\-]\\s*\\d+\\w\\s*";
	private static final String RESULT_KEY = "result";
	
	@Value("${api.uri-rewrite.enable}")
	private boolean rewriteEnable;
	@Value("${api.uri-rewrite.proxy}")
	private String apiRewriteProxyDomain;

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public enum ApiProtocol {
		URI("uri://"), FILE("file://"), FTP("ftp://"), HDFS("hdfs://");
		
		private String prefix;
		
		public static String getValidPattern() {
			return Stream.of(ApiProtocol.values()).map(ApiProtocol::getPrefix).collect(Collectors.joining("|", "^(", ")"));
		}
		
		public static ApiProtocol fromValue(String prefix) {
			return Stream.of(ApiProtocol.values())
					.filter(a -> a.prefix.equals(prefix))
					.findAny()
					.orElse(URI);
		}
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public enum DateExpr {
		YESTERDAY, TODAY, MONTH, YEAR;
		
		public static String getValidPattern() {
			return String.format(
					DTAE_EXPRESSION_PATTERN_FORMAT, 
					Stream.of(DateExpr.values()).map(DateExpr::name).collect(Collectors.joining("|", "(", ")"))
			);
		}
		
		public static DateExpr fromName(String expr) {
			return Stream.of(DateExpr.values())
					.filter(d -> d.toString().equalsIgnoreCase(expr))
					.findAny()
					.orElse(TODAY);
		}
	}

	public Map<String, String> anaylysisDateExpr(String str, boolean containResult) {
		List<String> matchList = StringTools.findAllMatchSequences(str, DateExpr.getValidPattern());
		Map<String, String> exprMap = new HashMap<>();
		String result = matchList.stream().reduce(str, (newStr, matchExp) -> {
			String[] segments = matchExp.replaceAll("[\\s\\(\\)]+", "").split(":");
			List<String> operationStrList = StringTools.findAllMatchSequences(segments[0], OPERATION_PATTERN);
			DateExpr expr = DateExpr.fromName(segments[0].replaceAll(OPERATION_PATTERN, ""));
			LocalDate date = expr == DateExpr.YESTERDAY ? LocalDate.now().minusDays(1) : LocalDate.now();

			boolean hasCustomPattern = segments.length > 1;
			String pattern = null;
			// If no assign pattern, then use default pattern
			// Year  => yyyy
			// Month => yyyy-MM
			// Other => yyyy-MM-dd
			if (!hasCustomPattern) {
				if (expr == DateExpr.YEAR) {
					pattern = DateUtils.YEAR_FORMAT;
				} else if (expr == DateExpr.MONTH) {
					pattern = DateUtils.DASHED_YEAR_MONTH_FORMAT;
				} else {
					pattern = DateUtils.DASHED_DATE_FORMAT;
				}
			} else {
				pattern = segments[1];
			}
			date = operationStrList.stream().map(String::trim).reduce(date, (newDate, operationStr) -> {
				int offset = Integer.parseInt(StringTools.slice(operationStr, 0, -1));
				ChronoUnit unit = DateUtils.toTemporalUnit(StringTools.slice(operationStr, -1));
				
				return newDate.plus(offset, unit);
			}, (date1, date2) -> date2);
			String placeholder = String.format("${%s}", matchExp);
			String dateVal = date.format(DateTimeFormatter.ofPattern(pattern));
			exprMap.put(placeholder, dateVal);
			
			return newStr.replace(placeholder, dateVal);
		});
		if (containResult) exprMap.put(RESULT_KEY, result);
		
		return exprMap;
	}
	
	public Map<String, String> anaylysisDateExpr(String str) {
		return anaylysisDateExpr(str, false);
	}

	public String normalize(String originUrl) {
		return this.anaylysisDateExpr(originUrl.replaceAll(ApiProtocol.getValidPattern(), ""), true).get(RESULT_KEY);
	}

	public String rewrite(String originUrl, ApiFormatEnum apiFormat) {
		if (rewriteEnable && HttpUtils.isDomainUri(originUrl)) {
			String encodeUrl = Base64.getUrlEncoder().withoutPadding()
					.encodeToString(originUrl.getBytes(StandardCharsets.UTF_8));
			return String.format("%s/api/%s/fetchApi/%s", apiRewriteProxyDomain, apiFormat.name().toLowerCase(), encodeUrl);
		}

		return originUrl;
	}
	
	public String rewrite(String originUrl) {
		return this.rewrite(originUrl, ApiFormatEnum.JSON);
	}
	
	public String normalinzeAndRewrite(String originUrl, ApiFormatEnum apiFormat) {
		return this.rewrite(this.normalize(originUrl), apiFormat);
	}
	
	public String normalinzeAndRewrite(String originUrl) {
		return this.rewrite(this.normalize(originUrl), ApiFormatEnum.JSON);
	}
	
	public ApiProtocol getApiProtocol(String originUrl) {
		String resourcePrefix = StringTools.findFirstMatchSequence(originUrl, ApiProtocol.getValidPattern());
		return ApiProtocol.fromValue(resourcePrefix);
	}
}

package com.example.batch.service.impl;

import com.example.batch.dto.TdxAuthResponseDto;
import com.example.batch.exception.ResourceException;
import com.example.batch.service.TdxService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Slf4j
@Service
public class TdxServiceImpl implements TdxService {
	@Value("${tdx.api.grant_type}")
	private String grantType;
	@Value("${tdx.api.token-url}")
	private String tokenUrl;
	@Value("${tdx.api.client_id}")
	private String clientID;
	@Value("${tdx.api.client_secret}")
	private String clientSecret;

	private String authToken;

	private long lastAuthTime;

	@SneakyThrows
	@Override
	public String getToken() {
		long ONE_HOURS = 60 * 60 * 1000;
		if (authToken == null || new Date().getTime() - lastAuthTime > ONE_HOURS) {
			getAuth();
			log.info("TDX token 更換：{}", authToken);
		}
		return authToken;
	}

	@SneakyThrows
	private void getAuth() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("grant_type", grantType);
		body.add("client_id", clientID);
		body.add("client_secret", clientSecret);
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();
		TdxAuthResponseDto authResponse = restTemplate.postForObject(tokenUrl, request, TdxAuthResponseDto.class);
		if (authResponse == null) throw new ResourceException("TDX Token 取得失敗");
		authToken = authResponse.getAccessToken();
		lastAuthTime = new Date().getTime();
	}
}

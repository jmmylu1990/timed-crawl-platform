package com.example.batch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TdxAuthResponseDto {

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("expires_in")
	private Integer expiresIn;

	@JsonProperty("refresh_expires_in")
	private Integer refreshExpiresIn;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("not-before-policy")
	private Integer notBeforePolicy;

	private String scope;
}

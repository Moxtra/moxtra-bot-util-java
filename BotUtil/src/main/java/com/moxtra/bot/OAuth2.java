package com.moxtra.bot;

import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;


public class OAuth2 {
	private static final Logger logger = LoggerFactory.getLogger(OAuth2.class);
	private String oauth2_client_id;
	private String oauth2_client_secret;
	private String oauth2_authorizeUrl;
	private String oauth2_tokenUrl;
	private String oauth2_redirectUri;

	
	public void setConfig(Properties config) {
		// set config
		try {
			
			this.oauth2_client_id = config.getProperty("oauth2_client_id"); 
			this.oauth2_client_secret = config.getProperty("oauth2_client_secret");
			this.oauth2_authorizeUrl = config.getProperty("oauth2_authorizeUrl");
			this.oauth2_tokenUrl = config.getProperty("oauth2_tokenUrl");
			this.oauth2_redirectUri = config.getProperty("oauth2_redirectUri");
			
		} catch (Exception e) {
			logger.error("Unable to retrieve OAuth2 configuration!", e);
			
		}		
	}
		
	public OAuth2(Properties config) {
		setConfig(config);
	}
	
	public OAuth2() {
	}

	public void auth(HttpServletRequest request, HttpServletResponse response) {
		try {
			OAuthClientRequest auth_request = OAuthClientRequest
				.authorizationLocation(oauth2_authorizeUrl)
				.setResponseType("code")
				.setClientId(oauth2_client_id)
				.setRedirectURI(oauth2_redirectUri)
				.buildQueryMessage();

			response.sendRedirect(auth_request.getLocationUri());

			logger.info("oauth auth_url: " + auth_request.getLocationUri());

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}


	public void callback(HttpServletRequest request, HttpServletResponse response, MoxtraBot bot) {
		try {

			OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
			String code = oar.getCode();

			OAuthClientRequest auth_request = OAuthClientRequest
				.tokenLocation(oauth2_tokenUrl)
				.setGrantType(GrantType.AUTHORIZATION_CODE)
				.setClientId(oauth2_client_id)
				.setClientSecret(oauth2_client_secret)
				.setRedirectURI(oauth2_redirectUri)
				.setCode(code)
				.buildQueryMessage();

			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

			OAuthJSONAccessTokenResponse jsonAccessToken = oAuthClient.accessToken(auth_request, OAuthJSONAccessTokenResponse.class);
			String access_token = jsonAccessToken.getAccessToken();

			logger.info("oauth access_token: " + access_token);

			bot.handleAccountLinkAccessToken(request, access_token);

			// response the jsonAccessToken
			String jsonObject = new ObjectMapper().writeValueAsString(jsonAccessToken);
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print(jsonObject);
			out.flush();

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}

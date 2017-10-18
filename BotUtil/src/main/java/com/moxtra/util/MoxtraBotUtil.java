package com.moxtra.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxtra.bot.model.MxUser;
import com.moxtra.bot.model.MxUserInput;
import com.moxtra.bot.model.MxUsers;


public class MoxtraBotUtil {
	public static String MOXTRA_API_ENDPOINT = "https://api.moxtra.com/v1";
	public static String PARAM_ACCESS_TOKEN = "access_token";
	public static String PARAM_EXPIRES_IN = "expires_in";
	private static final int SOCKET_TIMEOUT = 30000;
	private static final int MAX_TOTAL_CONNECTION = 100;
	private static final int MAX_CONNECTION_PER_ROUTE = 20;
	private static final int MAX_CONNECTION_MOXTRA = 50;
	private static PoolingHttpClientConnectionManager ccm = null;
	private static String baseUrl = null;
	
	
	
	/**
	 * getHttpClient
	 * 
	 * @return
	 */
	
	public static CloseableHttpClient getHttpClient() {

		  try {

			  if (baseUrl == null) {
				  baseUrl = MOXTRA_API_ENDPOINT;
			  }
			  
			  if (ccm == null) {
				  ccm = new PoolingHttpClientConnectionManager();
				  
				  // Increase max total connection to 100
				  ccm.setMaxTotal(MAX_TOTAL_CONNECTION);
				  // Increase default max connection per route to 20
				  ccm.setDefaultMaxPerRoute(MAX_CONNECTION_PER_ROUTE);
				  // Increase max connections for api.moxtra.com to 50
				  HttpHost moxtrahost = new HttpHost(baseUrl, 443);
				  ccm.setMaxPerRoute(new HttpRoute(moxtrahost), MAX_CONNECTION_MOXTRA);
				  
				  ccm.setSocketConfig(moxtrahost, SocketConfig.custom().setSoTimeout(SOCKET_TIMEOUT).build());
			  }
		   
			  return HttpClients.custom().setConnectionManager(ccm).setConnectionManagerShared(true).build();
		   
		  } catch (Exception e) {
			  return HttpClients.createDefault();
		  }
	 } 
	
	
	public static String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * setBaseUrl
	 * 
	 * @param baseUrl
	 */

	public static void setBaseUrl(String baseUrl) {
		MoxtraBotUtil.baseUrl = baseUrl;
	}

	/**
	 * To get the Access Token via /apps/token with client_id, org_id, timestamp, and signature. The return in the following JSON format
	 *   
	 *   {
	 *   	"access_token": ACCESS_TOKEN,
	 *   	"expires_in": EXPIRES_IN
	 *   }
	 * 
	 * @param baseUrl
	 * @param client_id
	 * @param client_secret
	 * @param org_id
	 * @return HashMap    
	 * @throws MoxtraBotUtilException
	 */
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> getAccessToken(String client_id, String client_secret, String org_id) throws MoxtraBotUtilException {
		
		if (client_id == null || client_secret == null || org_id == null) {
			throw new MoxtraBotUtilException("client_id, client_secret, and org_id are required!"); 
		}
		
		String timestamp = Long.toString(System.currentTimeMillis());
		HashMap<String, String> myMap = new HashMap<String, String>();
		
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		
		try {

			// generate code
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			
			SecretKeySpec secret_key = new SecretKeySpec(client_secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			
			StringBuffer total = new StringBuffer();
			total.append(client_id);
			total.append(org_id);
			total.append(timestamp);		
			
			String signature = encodeUrlSafe(sha256_HMAC.doFinal(total.toString().getBytes()));			

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("client_id", client_id));
			params.add(new BasicNameValuePair("org_id", org_id));
			params.add(new BasicNameValuePair("timestamp", timestamp));
			params.add(new BasicNameValuePair("signature", signature));
			
		    URIBuilder uriBuilder = new URIBuilder(baseUrl + "/apps/token");
		    uriBuilder.addParameters(params);
			
			HttpGet httpGet = new HttpGet(uriBuilder.build());			
			
			httpClient = getHttpClient();		    
		    response = httpClient.execute(httpGet);		    
			HttpEntity responseEntity = response.getEntity(); 
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("unable to get access_token");
			}
			if (responseEntity != null) {
		        // EntityUtils to get the response content
		        String content =  EntityUtils.toString(responseEntity);
                
                // get access token
                ObjectMapper objectMapper = new ObjectMapper();
        		myMap = objectMapper.readValue(content, HashMap.class);
                
            } else {
            	throw new Exception("unable to make request");
            }
            
    		return myMap;		
            
  		} catch (Exception e) {
  			throw new MoxtraBotUtilException(e.getMessage(), e);
  		} finally {
  			if (response != null) {
  				try {
  					response.close();
				} catch (IOException ex) {
					throw new MoxtraBotUtilException(ex.getMessage(), ex);
				}
  			}
  			
  			if (httpClient != null) {
  				try {
  					httpClient.close();
				} catch (IOException ex) {
					throw new MoxtraBotUtilException(ex.getMessage(), ex);
				}
  			}  						
  		}
		
	}
	
	/**
	 * Upload Message
	 * 
	 * @param url
	 * @param uploadFile
	 * @param audioFile
	 * @param payload
	 * @param access_token
	 * @return update status in JSON
	 * @throws MoxtraBotUtilException
	 */
	
	public static String uploadMessage(String binder_id, File uploadFile, File audioFile, String comment, String access_token) throws MoxtraBotUtilException {
		
		if (binder_id == null || (uploadFile == null && audioFile == null) || access_token == null) {
			throw new MoxtraBotUtilException("binder_id, uploadFile or audioFile, and access_token are required!"); 
		}
			
		try {
			String url = baseUrl + "/" + binder_id + "/messages";
			MultipartUtility multipart = new MultipartUtility(url, "UTF-8", access_token);
			
			if (uploadFile != null) {
				multipart.addFilePart("file", uploadFile);
			}
			
			if (audioFile != null) {
				multipart.addFilePart("audio", audioFile);
			}
			
			if (comment != null) {
				multipart.addFormField("payload", comment, "application/json");				
			}
		
			List<String> response = multipart.finish();
			
			//System.out.println("SERVER REPLIED:");
			
			StringBuffer result = new StringBuffer();
			for (String line : response) {
				//System.out.println(line);
				result.append(line);
			}
			
			return result.toString();
			
		} catch (IOException ex) {
			throw new MoxtraBotUtilException("unable to upload message", ex);
		}
	}
		
	/**
	 * invoke API
	 * 
	 * @param path
	 * @param json_input
	 * @param access_token
	 * @return response
	 * @throws MoxtraBotUtilException
	 */
	
	public static String invokePostAPI(String path, String json_input, String access_token) throws MoxtraBotUtilException {
		
		if (path == null || json_input == null || access_token == null) {
			throw new MoxtraBotUtilException("path, json, and access_token are required!"); 
		}
		
		String json_result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		
		try {
/*			
			String requestURL = null;
			if (url.indexOf("?") > 0) {
				requestURL = url + "&access_token=" + access_token;
			} else {
				requestURL = url + "?access_token=" + access_token;
			}
*/			
			httpClient = getHttpClient();
			HttpPost httppost = new HttpPost(baseUrl + path);
			
			httppost.setHeader("Authorization", "Bearer " + access_token);
			httppost.setHeader("Content-type", "application/json");
			
			ContentType contentType = ContentType.create("application/json", Charset.forName("UTF-8"));
			StringEntity entity = new StringEntity(json_input, contentType);
			httppost.setEntity(entity);
			
			response = httpClient.execute(httppost);
			HttpEntity responseEntity = response.getEntity(); 
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("Invoke Post API failed");
			}
			if (responseEntity != null) {
				json_result = EntityUtils.toString(responseEntity);
			}
			
			return json_result;
		
  		} catch (Exception e) {
  			throw new MoxtraBotUtilException(e.getMessage(), e);
  		} finally {
  			if (response != null) {
  				try {
  					response.close();
				} catch (IOException ex) {
					throw new MoxtraBotUtilException(ex.getMessage(), ex);
				}
  			}
  			
  			if (httpClient != null) {
  				try {
  					httpClient.close();
				} catch (IOException ex) {
					throw new MoxtraBotUtilException(ex.getMessage(), ex);
				}
  			}  						
  		}
		
	}
	
	/**
	 * invoke Get API
	 * 
	 * @param url
	 * @param access_token
	 * @return response
	 * @throws MoxtraBotUtilException
	 */
	
	public static String invokeGetAPI(String path, String access_token) throws MoxtraBotUtilException {
		
		if (path == null || access_token == null) {
			throw new MoxtraBotUtilException("path and access_token are required!"); 
		}
		
		String json_result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		
		try {
/*			
			String requestURL = null;
			if (url.indexOf("?") > 0) {
				requestURL = url + "&access_token=" + access_token;
			} else {
				requestURL = url + "?access_token=" + access_token;
			}
*/	
			httpClient = getHttpClient();
			HttpGet httpget = new HttpGet(baseUrl + path);
			
			httpget.setHeader("Authorization", "Bearer " + access_token);
			httpget.setHeader("Content-type", "application/json");				
			
			response = httpClient.execute(httpget);
			HttpEntity responseEntity = response.getEntity(); 
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("Invoke Get API failed");
			}
			if (responseEntity != null) {
				json_result = EntityUtils.toString(responseEntity);
			}
			
			return json_result;
		
  		} catch (Exception e) {
  			throw new MoxtraBotUtilException(e.getMessage(), e);
  		} finally {
  			if (response != null) {
  				try {
  					response.close();
				} catch (IOException ex) {
					throw new MoxtraBotUtilException(ex.getMessage(), ex);
				}
  			}
  			
  			if (httpClient != null) {
  				try {
  					httpClient.close();
				} catch (IOException ex) {
					throw new MoxtraBotUtilException(ex.getMessage(), ex);
				}
  			}  						
  		}
		
	}
	
	/**
	 * invoke Delete API
	 * 
	 * @param path
	 * @param access_token
	 * @return response
	 * @throws MoxtraBotUtilException
	 */
	
	public static String invokeDeleteAPI(String path, String access_token) throws MoxtraBotUtilException {
		
		if (path == null || access_token == null) {
			throw new MoxtraBotUtilException("path and access_token are required!"); 
		}
		
		String json_result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;

		try {
/*			
			String requestURL = null;
			if (url.indexOf("?") > 0) {
				requestURL = url + "&access_token=" + access_token;
			} else {
				requestURL = url + "?access_token=" + access_token;
			}
*/
			
			httpClient = getHttpClient();
			HttpDelete httpdelete = new HttpDelete(baseUrl + path);
			
			httpdelete.setHeader("Authorization", "Bearer " + access_token);
			httpdelete.setHeader("Content-type", "application/json");						
			
			response = httpClient.execute(httpdelete);
			HttpEntity responseEntity = response.getEntity(); 
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("Invoke Delete API failed");
			}
			if (responseEntity != null) {
				json_result = EntityUtils.toString(responseEntity);
			}
			
			return json_result;
		
  		} catch (Exception e) {
  			throw new MoxtraBotUtilException(e.getMessage(), e);
  		} finally {
  			if (response != null) {
  				try {
  					response.close();
				} catch (IOException ex) {
					throw new MoxtraBotUtilException(ex.getMessage(), ex);
				}
  			}
  			
  			if (httpClient != null) {
  				try {
  					httpClient.close();
				} catch (IOException ex) {
					throw new MoxtraBotUtilException(ex.getMessage(), ex);
				}
  			}  						
  		}
		
	}
	
	/**
	 * create a binder with invitee only for the bot user
	 * 
	 * @param binder_name
	 * @param invitee_user_id   
	 * @param access_token
	 * @return binder_id
	 * @throws MoxtraBotUtilException
	 */
	
	public static String createOneOnOneBinder(String binder_name, String invitee_user_id, String access_token) throws MoxtraBotUtilException {
		
		if (binder_name == null || invitee_user_id == null || access_token == null) {
			throw new MoxtraBotUtilException("binder_name, user_id, and access_token are required!"); 
		}
		
		String binder_id = null;
		
		try {
			String requestURL = baseUrl + "/me/binders";
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", binder_name);
			map.put("conversation", Boolean.TRUE);
			String json = new ObjectMapper().writeValueAsString(map);		
			
			String result = invokePostAPI(requestURL, json, access_token);
			// obtain binder_id
			HashMap jsonmap = new ObjectMapper().readValue(result, HashMap.class);
			HashMap data = (HashMap) jsonmap.get("data");
			binder_id = (String) data.get("id");
			
			// add the invitee to the binder
			MxUser user = new MxUser();
			user.setId(invitee_user_id);
			MxUserInput input = new MxUserInput(user);
			MxUsers users = new MxUsers();
			users.add(input);
			
			requestURL = baseUrl + "/ "+ binder_id + "/addorguser";
			result = invokePostAPI(requestURL, users.toJSONString(), access_token);
			
			return binder_id;
		
  		} catch (Exception e) {
  			throw new MoxtraBotUtilException(e.getMessage(), e);
  		}
		
	}
	
	/**
	 * URLSafe Base64 encoding with space padding 
	 * 
	 * @param data
	 * @return
	 */
	public static String encodeUrlSafe(byte[] data) {
		byte[] encode = Base64.encodeBase64(data);
	    for (int i = 0; i < encode.length; i++) {
	        if (encode[i] == '+') {
	            encode[i] = '-';
	        } else if (encode[i] == '/') {
	            encode[i] = '_';
	        } else if (encode[i] == '=') {
	        	encode[i] = ' ';
	        }
	    }
	    return new String(encode).trim();
	}	

}

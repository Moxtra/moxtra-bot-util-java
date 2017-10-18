package com.moxtra;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxtra.bot.Chat;
import com.moxtra.bot.model.AccountLink;
import com.moxtra.bot.model.ChatMessage;
import com.moxtra.bot.model.Comment;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class ChatTest {

	//@Test
	public void testComment() {
		
		try {
			
			ClassLoader classLoader = getClass().getClassLoader();
			InputStream stream = classLoader.getResourceAsStream("text_message.json");
			
		    if (stream == null)
		    	throw new Exception("unable to get resource"); 
	
			ObjectMapper objectMapper = new ObjectMapper();
			ChatMessage chatMessage = objectMapper.readValue(stream, ChatMessage.class);
						
			Chat chat = new Chat(chatMessage);
			
			String username = chat.getUsername();
			String text = chat.getComment().getText();		
			
			StringBuilder richtext = new StringBuilder();
			richtext.append("[table][tr][th][center]BBCode Info[/center][/th][/tr]");
			richtext.append("[tr][td][img=50x25]https://www.bbcode.org/images/lubeck_small.jpg[/img][/td][/tr][tr][td]From: [i]");
			richtext.append(username);
			richtext.append("[/i][/td][/tr][tr][td][color=Red]");
			richtext.append(text + " reply");
			richtext.append("[/color][/td][/tr][/table]");  
			
			Comment comment = new Comment.Builder().richtext(richtext.toString()).build();
			
			String access_token = UploadTest.getAccessToken();
			chat.setAccess_token(access_token);
			
			String result = chat.sendRequest(comment);
			
			System.out.println("result: " + result);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	
	//@Test
	public void testJWT() {
		
		try {
			
			String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRfaWQiOiJyczduX1NHUUFURSIsIm9yZ19pZCI6IlBXc29TUE5oNzBSOHh0Vkd2RVJ2bjU5IiwidXNlcl9pZCI6IlVOWVFEcHlDWGRiOGVJYkh4N0RrZk0wIiwidXNlcm5hbWUiOiJCb3QgQm90IiwiYmluZGVyX2lkIjoiQnJjNmZ5bWxGejZLYWJlZURMQ0lFcjYiLCJpYXQiOjE1MDcwNTE3NDYsImV4cCI6MTUwNzA1NTM0NiwianRpIjoiODAwM2NmOGUtOGQ1ZS00ZGM1LWFiYzItNDJlZmY1M2I4OTRlIn0.1YJWDpU6JdWFOzrkMCFJ73fq4dWQjIQRfEhvTW0-I90";
			String client_secret = "i0PuE043W3c";
			
/*			
			String token = Jwts.builder()
	        .setSubject("test")
	        .setExpiration(new Date(System.currentTimeMillis() + 864000000L))
	        .signWith(SignatureAlgorithm.HS256, client_secret)
	        .compact();
*/
			
            io.jsonwebtoken.Claims body = Jwts.parser()
                    .setSigningKey(client_secret.getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();
           
          
            AccountLink accountLink = new AccountLink();
            accountLink.setUser_id((String) body.get("user_id"));
            accountLink.setUsername((String) body.get("username"));
            accountLink.setBinder_id((String) body.get("binder_id"));
            accountLink.setOrg_id((String) body.get("org_id"));
            accountLink.setClient_id((String) body.get("client_id"));
            
            String result = new ObjectMapper().writeValueAsString(accountLink);
            System.out.println("result: " + result);
                        
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	//@Test
	public void testPayload() {
		
		try {
			String text = "Not Sure? Not Sure?";
			String ntext = text.replace("[^\\x20-\\x7E]+", "").toUpperCase();
            
            System.out.println(ntext);
                        
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
	
	//@Test
	public void testJSON() {
		
		try {
			String username = "Jonathan";
			String text = "hello";
			String binder_id = "BG1e771qT1eCsCc1ZW7ohQ5";
			
			
			HashMap<String, String> fieldsMap = new HashMap();  
			fieldsMap.put("title", "BBCode Info");  
			fieldsMap.put("from", username);  
			fieldsMap.put("info", text);  
			fieldsMap.put("image_url", "https://www.bbcode.org/images/lubeck_small.jpg");
			
			//String message = new ObjectMapper()..writeValueAsString(fieldsMap);
			Comment comment = new Comment.Builder().fields(fieldsMap).build();
			
			Chat chat = new Chat();
			String access_token = UploadTest.getAccessToken();
			chat.setAccess_token(access_token);			

			chat.setBinder_id(binder_id);
			String result = chat.sendRequest(comment); 
            
            System.out.println("result: " + result);
                        
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}		
	
}

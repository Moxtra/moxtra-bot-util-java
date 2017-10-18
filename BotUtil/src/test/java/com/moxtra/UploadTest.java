package com.moxtra;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import com.moxtra.bot.model.Comment;
import com.moxtra.util.MoxtraBotUtil;
import com.moxtra.util.MoxtraBotUtilException;

public class UploadTest {

	public static String getAccessToken() throws MoxtraBotUtilException {
		String client_id = "rs7n_SGQATE";
		String client_secret = "i0PuE043W3c";
		String org_id = "PuE0cDUJRkg5knH0FUwJqi4";
		
		MoxtraBotUtil.setBaseUrl("https://api.grouphour.com/v1");
		HashMap<String, String> value = MoxtraBotUtil.getAccessToken(client_id, client_secret, org_id);			
		String access_token = value.get("access_token");
		String expires_in = value.get("expires_in");
		
		System.out.println("access_token: " + access_token + " expires_in: " + expires_in);

		return access_token;
	}
	
	//@Test
	public void testToken() {
		
		try {
			
			UploadTest.getAccessToken();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void test() {
		String binder_id = "BG1e771qT1eCsCc1ZW7ohQ5";
		File file = new File("c:\\home\\doc\\Moxtra_timelineData.pdf");
		File audio = new File("c:\\temp\\test_comment.3gpp");
		
		try {
			
			StringBuilder richtext = new StringBuilder();
			richtext.append("[table][tr][th][center]BBCode Info[/center][/th][/tr]");
			richtext.append("[tr][td][img=50x25]https://www.bbcode.org/images/lubeck_small.jpg[/img][/td][/tr][tr][td]From: [i]");
			richtext.append("Test");
			richtext.append("[/i][/td][/tr][tr][td][color=Red]");
			richtext.append("UUU");
			richtext.append("[/color][/td][/tr][/table]");              
            
			Comment comment = new Comment.Builder().richtext(richtext.toString()).build();
			
			// get access_token
			String access_token = getAccessToken();
			String result = MoxtraBotUtil.uploadMessage(binder_id, file, audio, comment.toJSONString(), access_token);
			
            System.out.println("result: " + result);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

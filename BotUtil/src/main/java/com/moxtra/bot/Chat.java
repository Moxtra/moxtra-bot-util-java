package com.moxtra.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxtra.bot.model.ChatMessage;
import com.moxtra.bot.model.Comment;
import com.moxtra.bot.model.EventAnnotate;
import com.moxtra.bot.model.EventBot;
import com.moxtra.bot.model.EventComment;
import com.moxtra.bot.model.EventFile;
import com.moxtra.bot.model.EventMeet;
import com.moxtra.bot.model.EventPage;
import com.moxtra.bot.model.EventPostback;
import com.moxtra.bot.model.EventTarget;
import com.moxtra.bot.model.EventTodo;
import com.moxtra.bot.model.EventType;
import com.moxtra.bot.model.EventUser;
import com.moxtra.util.MoxtraBotUtil;
import com.moxtra.util.MoxtraBotUtilException;
import java.io.File;
import java.util.regex.Matcher;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Chat {
	private static final Logger logger = LoggerFactory.getLogger(Chat.class);
	private String user_id;
	private String username;
	private String binder_id;
	private String access_token;
	private String client_id;
	private String org_id;
	private int primatches = 0;
	private Matcher matcher;
	private EventType eventType = EventType.MESSAGE;
	private ChatMessage chatMessage;

	public Chat() {
	}
	
	public Chat(ChatMessage chatMessage) {
		setChatMessage(chatMessage);
	}
	
	public int getPrimatches() {
		return primatches;
	}

	public void setPrimatches(int primatches) {
		this.primatches = primatches;
	}

	public Matcher getMatcher() {
		return matcher;
	}

	public void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}

	@PostConstruct
	public String sendRequest(Comment comment) {
		
		// log sending message
		try {
			logger.info("Send: " + new ObjectMapper().writeValueAsString(comment));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}		
		
        try {
        	        	
        	String path = "/" + this.binder_id + "/messages"; 
        	
        	String result = MoxtraBotUtil.invokePostAPI(path, comment.toJSONString(), this.access_token);
        	
        	return result;
            
        } catch (JsonProcessingException e) {
            logger.error("Invalid message format!", e);
            return null;
        } catch (MoxtraBotUtilException e) {
            logger.error("Error posting message!", e);
            return null;
        }
	
	}
	
	
	@PostConstruct
	public String sendRequest(Comment comment, File file, File audio) {
		
		if (file == null && audio == null) {
			return sendRequest(comment);
		}
	
        try {

        	String commentStr = null;
        	
        	if (comment != null) {
        		commentStr = comment.toJSONString();
        	}
        	
        	String result = MoxtraBotUtil.uploadMessage(this.binder_id, file, audio, commentStr, this.access_token);
        	
        	return result;
            
        } catch (JsonProcessingException e) {
            logger.error("Invalid message format!", e);
            return null;
        } catch (MoxtraBotUtilException e) {
            logger.error("Error posting message!", e);
            return null;
        }
        	
	}
	
	
	public String getBinderInfo() {
		
        try {

            String path = "/" + this.binder_id;
            
            return MoxtraBotUtil.invokeGetAPI(path, this.access_token);
            
        } catch (MoxtraBotUtilException e) {
            logger.error("Error getting binderinfo!", e);
            return null;
        }        
	}	
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBinder_id() {
		return binder_id;
	}

	public void setBinder_id(String binder_id) {
		this.binder_id = binder_id;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	
	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getOrg_id() {
		return org_id;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public ChatMessage getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(ChatMessage chatMessage) {
		
		// log receiving message
		try {
			logger.info("Receive: " + new ObjectMapper().writeValueAsString(chatMessage));
		} catch (Exception e) {}		
		
		this.chatMessage = chatMessage;
		if (chatMessage != null) {
			this.client_id = chatMessage.getClient_id();
			this.org_id = chatMessage.getOrg_id();
			this.binder_id = chatMessage.getBinder_id();
			
			switch(chatMessage.getMessage_type()) {
			case "bot_enabled":
				eventType = EventType.BOT_ENABLED;
				break;
			case "bot_disabled":
				eventType = EventType.BOT_DISABLED;
				break;
			case "bot_installed":
				eventType = EventType.BOT_INSTALLED;
				break;
			case "bot_uninstalled":
				eventType = EventType.BOT_UNINSTALLED;
				break;			
			case "comment_posted":
			case "comment_posted_on_page":
				eventType = EventType.MESSAGE;
				break;			
			case "bot_postback": 	
				eventType = EventType.POSTBACK;
				break;
			case "file_uploaded":
				eventType = EventType.FILE_UPLOADED;
				break;				
			case "page_annotated":
				eventType = EventType.PAGE_ANNOTATED;
				break;				
			case "todo_created":
				eventType = EventType.TODO_CREATED;
				break;				
			case "todo_completed":
				eventType = EventType.TODO_COMPLETED;
				break;
			case "meet_recording_ready": 						
				eventType = EventType.MEET_RECORDING_READY;
				break;
			}
			
			if (chatMessage.getEvent() != null) {
				EventUser user = chatMessage.getEvent().getUser();
		
				if (user != null) {
					this.user_id = user.getId();
					this.username = user.getName();					
				}
			}
		}
	}
	
	public EventBot getBot() {
		if (chatMessage != null && (eventType == EventType.BOT_INSTALLED || eventType == EventType.BOT_UNINSTALLED ||
				eventType == EventType.BOT_ENABLED || eventType == EventType.BOT_DISABLED)) {
			return chatMessage.getEvent().getBot();
		}
		return null;
	}
	
	public EventComment getComment() {
		if (chatMessage != null && eventType == EventType.MESSAGE) {
			return chatMessage.getEvent().getComment();
		}
		return null;
	}
		
	public EventPostback getPostback() {
		if (chatMessage != null && eventType == EventType.POSTBACK) {
			return chatMessage.getEvent().getPostback();
		}
		return null;		
	}
	
	public EventFile getFile() {
		if (chatMessage != null && eventType == EventType.FILE_UPLOADED) {
			return chatMessage.getEvent().getFile();
		}
		return null;		
	}
	
	public EventAnnotate getAnnotate() {
		if (chatMessage != null && eventType == EventType.PAGE_ANNOTATED) {
			return chatMessage.getEvent().getAnnotate();
		}
		return null;		
	}
	
	public EventPage getPage() {
		if (chatMessage != null && eventType == EventType.PAGE_CREATED) {
			return chatMessage.getEvent().getPage();
		}
		return null;
	}
	
	public EventTodo getTodo() {
		if (chatMessage != null && (eventType == EventType.TODO_CREATED || eventType == EventType.TODO_COMPLETED)) {
			return chatMessage.getEvent().getTodo();
		}
		return null;
	}
	
	public EventMeet getMeet() {
		if (chatMessage != null && eventType == EventType.MEET_RECORDING_READY) {
			return chatMessage.getEvent().getMeet();
		}
		return null;
	}
	
	public EventUser getUser() {
		if (chatMessage != null) {
			return chatMessage.getEvent().getUser();
		}
		return null;
	}
	
	public EventTarget getTarget() {
		if (chatMessage != null) {
			return chatMessage.getEvent().getTarget();
		}
		return null;
	}
}

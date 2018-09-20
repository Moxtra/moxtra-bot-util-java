package com.moxtra.bot.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReplyTo implements Serializable {
	private MsgBinderComment binder;
	private MsgPage page;
	private MsgFile file;
	private MsgTransaction transaction;
	private MsgMeet meet;
	
	// Comment interfaces
	
	public void setBinderComment(String comment_id) {
		this.binder = new MsgBinderComment(comment_id);
	}

	public void setPage(String page_id) {
		this.page = new MsgPage(page_id);
	}
	
	public void setPagePostitionComment(String page_id, String position_comment_id) {
		this.page = new MsgPage(page_id, position_comment_id);
	}
	
	public void setFile(String file_id) {
		this.file = new MsgFile(file_id);	
	}
	
	public void setTransaction(String transaction_id) {
		this.transaction = new MsgTransaction(transaction_id);
	}
	
	public void setMeet(String session_key) {
		this.meet = new MsgMeet(session_key);
	}
		
	// setter & getter
	
	public MsgBinderComment getBinder() {
		return binder;
	}

	public void setBinder(MsgBinderComment binder) {
		this.binder = binder;
	}

	public MsgPage getPage() {
		return page;
	}

	public void setPage(MsgPage page) {
		this.page = page;
	}

	public MsgFile getFile() {
		return file;
	}

	public void setFile(MsgFile file) {
		this.file = file;
	}

	public MsgTransaction getTransaction() {
		return transaction;
	}

	public void setTransaction(MsgTransaction transaction) {
		this.transaction = transaction;
	}

	public MsgMeet getMeet() {
		return meet;
	}

	public void setMeet(MsgMeet meet) {
		this.meet = meet;
	}

	public class MsgBinderComment {
		private String comment_id;
		
		public MsgBinderComment(String comment_id) {
			this.comment_id = comment_id;
		}

		public String getComment_id() {
			return comment_id;
		}

		public void setComment_id(String comment_id) {
			this.comment_id = comment_id;
		}
	}

	public class MsgPage {
		private String id;
		private String position_comment_id;

		public MsgPage(String id) {
			this.id = id;
		}
		
		public MsgPage(String id, String position_comment_id) {
			this.id = id;
			this.position_comment_id = position_comment_id;
		}
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getPosition_comment_id() {
			return position_comment_id;
		}
		public void setPosition_comment_id(String position_comment_id) {
			this.position_comment_id = position_comment_id;
		}
	}
	
	public class MsgFile {
		private String id;

		public MsgFile(String id) {
			this.id = id;
		}
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	public class MsgSignature {
		private String id;

		public MsgSignature(String id) {
			this.id = id;
		}
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
	
	public class MsgTransaction {
		private String id;
		
		public MsgTransaction(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
	
	public class MsgMeet {
		private String session_key;

		public MsgMeet(String session_key) {
			this.session_key = session_key;
		}
		
		public String getSession_key() {
			return session_key;
		}

		public void setSession_key(String session_key) {
			this.session_key = session_key;
		}
	}
	
}

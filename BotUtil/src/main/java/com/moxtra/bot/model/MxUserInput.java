package com.moxtra.bot.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MxUserInput implements Serializable {
	private MxUser user;

	
	public MxUserInput() {}
	
	public MxUserInput(MxUser user) {
		this.user = user;
	}
	
	public MxUser getUser() {
		return user;
	}

	public void setUser(MxUser user) {
		this.user = user;
	}
}

package com.moxtra.bot.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MxUsers implements Serializable {
	private List<MxUserInput> users;

	public void add(MxUserInput input) {
		if (users == null) {
			users = new ArrayList<MxUserInput>();
		}
		
		users.add(input);
	}
	
	public List<MxUserInput> getUsers() {
		return users;
	}

	public void setUsers(List<MxUserInput> users) {
		this.users = users;
	}
	
	public String toJSONString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }		
}

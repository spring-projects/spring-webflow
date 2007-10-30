package org.springframework.webflow.config;

import org.springframework.util.Assert;

public class EnableScopesService {
	private EnableScopesUser user;

	public void setUser(EnableScopesUser user) {
		this.user = user;
	}

	public void execute() {
		Assert.isTrue("foo".equals(user.getName()));
	}
}

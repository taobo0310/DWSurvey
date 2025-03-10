package net.diaowen.common.plugs.security;

import net.diaowen.common.base.entity.User;
import net.diaowen.common.base.service.AccountManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class MyDefaultWebSecurityManager extends DefaultWebSecurityManager{
	@Autowired
	private AccountManager accountManager;

	public String getUserId(){
		User user=accountManager.getCurUser();
		if(user!=null){
			return user.getId();
		}
		return null;
	}

}

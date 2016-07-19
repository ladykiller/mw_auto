/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.auth.contract;

/**
 * @author mengfanyuan
 * 2016年6月30日上午10:03:19
 */
public class LoginResp {
	
	/** 用户信息token **/
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}

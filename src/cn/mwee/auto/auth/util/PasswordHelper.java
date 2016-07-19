/** 
 * 上海普景信息科技有限公司
 * 地址：上海市浦东新区祖冲之路899号 	
 * Copyright © 2013-2016 Puscene,Inc.All Rights Reserved.
 */
package cn.mwee.auto.auth.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * @author mengfanyuan
 * 2016年6月30日上午11:20:12
 */
public class PasswordHelper {
	private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
	private String algorithmName = "md5";
	private int hashIterations = 2;
	
	/**
	 * 用户密码加密
	 * @param password
	 */
	public Map<String, String> encryptPassword(String password) {
		Map<String, String> returnMap = new HashMap<String, String>();
		String salt = randomNumberGenerator.nextBytes().toHex();
		returnMap.put("salt", salt);
        String newPassword = new SimpleHash(algorithmName,password,ByteSource.Util.bytes(salt),hashIterations).toHex();
        returnMap.put("password", newPassword);
        return returnMap;
	}
	
	
	public RandomNumberGenerator getRandomNumberGenerator() {
		return randomNumberGenerator;
	}

	public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public int getHashIterations() {
		return hashIterations;
	}

	public void setHashIterations(int hashIterations) {
		this.hashIterations = hashIterations;
	}
	
}

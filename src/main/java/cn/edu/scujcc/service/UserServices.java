package cn.edu.scujcc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.edu.scujcc.dao.UserRepository;
import cn.edu.scujcc.model.User;

@Service
public class UserServices {
	@Autowired
	private UserRepository repo;
	@Autowired
	private CacheManager cacheManager;
	private static final Logger logger = LoggerFactory.getLogger(UserServices.class);
	
	/**
	 * 新建用户
	 */
	public User createUser(User user) {
		logger.debug("用户注册" + user);
		User result =null;
		//TODO  1.保存前把用户密码加密
		//TODO  2.检查用户是否存在，  存在则允许不逊需注册
		result = repo.save(user);
		return result;
	}
	
	/**
	 * 检查用户名与密码是否匹配
	 * @param username  用户登陆名
	 * @param password  用户密码
	 * @return		如果密码正确返回true,错误返回false
	 */
	public boolean checkUser(String username,String password) {
		boolean result = false;
		User u = repo.findOneByUsernameAndPassword(username, password);
		logger.debug("数据库中的用户信息是" + u);
		if(null != u) {
			result = true;
		}
		return result;
	}
	
	/*
	 * 登记注册，并返回一个唯一编号(token)
	 */
	public String checkIn(String username) {
		String temp = username + System.currentTimeMillis();
		String token = DigestUtils.md5DigestAsHex(temp.getBytes());
		Cache cache = cacheManager.getCache(User.CACHE_NAME);
		cache.put(token, username);
		return token;
	}
	
	/**
	 * 根据token查询当前用户是谁
	 * @param token
	 * @return
	 */
	public String currentUser(String token) {
		Cache cache = cacheManager.getCache(User.CACHE_NAME);
		return cache.get(token, String.class);
	}
}

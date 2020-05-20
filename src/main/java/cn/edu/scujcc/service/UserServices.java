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
	 * �½��û�
	 */
	public User createUser(User user) {
		logger.debug("�û�ע��" + user);
		User result =null;
		//TODO  1.����ǰ���û��������
		//TODO  2.����û��Ƿ���ڣ�  ����������ѷ��ע��
		result = repo.save(user);
		return result;
	}
	
	/**
	 * ����û����������Ƿ�ƥ��
	 * @param username  �û���½��
	 * @param password  �û�����
	 * @return		���������ȷ����true,���󷵻�false
	 */
	public boolean checkUser(String username,String password) {
		boolean result = false;
		User u = repo.findOneByUsernameAndPassword(username, password);
		logger.debug("���ݿ��е��û���Ϣ��" + u);
		if(null != u) {
			result = true;
		}
		return result;
	}
	
	/*
	 * �Ǽ�ע�ᣬ������һ��Ψһ���(token)
	 */
	public String checkIn(String username) {
		String temp = username + System.currentTimeMillis();
		String token = DigestUtils.md5DigestAsHex(temp.getBytes());
		Cache cache = cacheManager.getCache(User.CACHE_NAME);
		cache.put(token, username);
		return token;
	}
	
	/**
	 * ����token��ѯ��ǰ�û���˭
	 * @param token
	 * @return
	 */
	public String currentUser(String token) {
		Cache cache = cacheManager.getCache(User.CACHE_NAME);
		return cache.get(token, String.class);
	}
}

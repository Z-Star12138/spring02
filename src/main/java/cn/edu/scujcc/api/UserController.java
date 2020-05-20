package cn.edu.scujcc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.scujcc.model.User;
import cn.edu.scujcc.service.UserServices;

@RestController
@RequestMapping("/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private UserServices service;
	
	@PostMapping("/register")
	public User register(@RequestBody User u) {
		logger.debug("����ע���û����û����ݣ�" + u);
		User saved = service.createUser(u);
		return saved;
	}
	
	@GetMapping("/login/{username}/{password}")
	public int login(@PathVariable String username,
			@PathVariable String password) {
		int result = 0;
		//FIXME ɾ����־
		logger.debug("�û�" + username + "׼����½�������ǣ�" + password);
		boolean status = service.checkUser(username, password);
		if(status) {//��½�ɹ�������1
			result = 1;
		}//
		//���û����뵽������
		Cache cache = cacheManager.getCache(User.CACHE_NAME);
		cache.put("current_user", username);
		return result;
	}
	
}

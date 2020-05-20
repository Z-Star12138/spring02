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

import com.mongodb.operation.UserExistsOperation;

import cn.edu.scujcc.model.Result;
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
	public Result<User> register(@RequestBody User u) {
		Result<User> result = new Result<>();
		logger.debug("即将注册用户，用户数据：" + u);
		try {
			result = result.ok();
			result.setDate(service.createUser(u));
		}catch (UserExistsOperation e){
			logger.error("用户已经存在"， e);
			result = result.error();
			result.setMessage("用户已存在。");
		}
		return result;
	}
	
	@GetMapping("/login/{username}/{password}")
	public int login(@PathVariable String username,
			@PathVariable String password) {
		int result = 0;
		//FIXME 删除日志
		logger.debug("用户" + username + "准备登陆，密码是：" + password);
		boolean status = service.checkUser(username, password);
		if(status) {//登陆成功，返回1
			result = 1;
		}//
		//把用户存入到缓存中
		Cache cache = cacheManager.getCache(User.CACHE_NAME);
		cache.put("current_user", username);
		return result;
	}
	
}

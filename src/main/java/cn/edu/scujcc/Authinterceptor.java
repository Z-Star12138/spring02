package cn.edu.scujcc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import cn.edu.scujcc.model.User;
import cn.edu.scujcc.service.UserServices;

/**
 * ��֤���أ����ڼ���û��Ƿ��¼��
 * @author Star
 *
 */
@Component
public class Authinterceptor implements HandlerInterceptor {
	@Autowired
	private CacheManager cacheManager;
	private static final Logger logger = LoggerFactory.getLogger(UserServices.class);
	/**
	 * ����û��ѵ�¼���򷵻�true,���򷵻�false
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean logged = false;
		String target = request.getRequestURI();
		//�ѵ�½��ע���ų��ڱ���֮��
		if(null != target && target.startsWith("/user")) {
			return true;
		}
		//�ѳ������Ҳ�����ų��ڱ���֮��
		if(response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
			return true;
		}
		
		String token = request.getHeader("token");
		if(token != null) {
			Cache cache = cacheManager.getCache(User.CACHE_NAME);
			String username = cache.get(token, String.class);
			if(username != null) {
				logged = true;
				logger.debug("�û�" + username + "�������" + request.getRequestURI());
			}
		}
		if(!logged){
			logger.warn("�Ƿ����ʣ�" + request.getRequestURI());
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "δ��¼�û�����ֹ����");
		}
		return logged;
	}
	
}

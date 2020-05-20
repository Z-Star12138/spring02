package cn.edu.scujcc.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.scujcc.model.Channel;
import cn.edu.scujcc.model.Comment;
import cn.edu.scujcc.model.User;
import cn.edu.scujcc.service.ChannelService;

@RestController
@RequestMapping("/channel")
public class ChannelController {
	public static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private ChannelService service;
	
	@GetMapping
	/**��ȡ����
	 * 
	 * @return
	 */
	public List<Channel> getAllChannels(){
		logger.info("���ڻ�ȡ����Ƶ����");
		return service.getAllChannels();
	}
	
	@GetMapping("/{id}")
	public Channel getChannel(@PathVariable String id) {
		System.out.println("��ȡƵ����id="+id);
		Channel c = service.getChannel(id);
		if(c != null ) {
			return c;
		}else {
			return null;
		}
	}
	
	/**
	 * ɾ��Ƶ��
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteChannel(@PathVariable String id){
		System.out.println("����ɾ����id="+id);
		boolean result = service.deleteChannel(id);
		if(result) {
			return ResponseEntity.ok().body("ɾ���ɹ�");
		}else {
			return ResponseEntity.ok().body("ɾ��ʧ��");
		}
	}
	
	@PostMapping
	public Channel creatChannel(@RequestBody Channel c) {
		System.out.println("����������Ƶ����Ƶ�����ݣ�" + c);
		Channel saved = service.createChannel(c);
		return saved;
	}
	
	@PutMapping
	public Channel updateChannel(@RequestBody Channel c) {
		System.out.println("�ɹ�����������Ƶ����Ƶ�����ݣ�" + c);
		Channel updated = service.updateChannel(c);
		return updated;
	}
	
	@GetMapping("/q/{quality}")
	public List<Channel>searchByQuality(@PathVariable String quality){
		return service.searchByQuality(quality);
	}
	@GetMapping("/t/{title}")
	public List<Channel>searchByTitle(@PathVariable String title){
		return service.searchByTitle(title);
	}
	
	/**
	 * ����Ƶ���Ľӿ�
	 * @return
	 */
	@GetMapping("/cold")
	public List<Channel> getColdChannels(){
		return service.findColdChannels();
	}
	
	/**
	 * ��ҳ�Ľӿ�
	 * @param page
	 * @return
	 */
	@GetMapping("/p/{page}")
	public List<Channel> getChannelsPage(@PathVariable int page){
		return service.findChannelsPage(page);
	}
	
	@PostMapping("/{channelId}/comment")
	public Channel addComment(@PathVariable String channelId,@RequestBody Comment comment) {
		Channel result = null;
		logger.debug("��������Ƶ��"+channelId+"���۶���"+comment);
		//����û��Ƿ��¼��
		Cache cache = cacheManager.getCache(User.CACHE_NAME);
		ValueWrapper obj = cache.get("current_user");
		if(obj == null) {
			logger.warn("�û�δ��¼�����ۣ��ܾ���");
		} else {
		//�����۱��浽���ݿ�
		String username = (String) obj.get();
		logger.debug("��¼�û�" + username + "��������");
		comment.setAuthor(username);
		result = service.addComment(channelId, comment);
		}
		return result;
	}
	
	@GetMapping("/{channelId}/hotcomments")
	public List<Comment> hotComments(@PathVariable String channelId){
		List<Comment> result = null;
		result = service.hostComments(channelId);
		return result;
	}
}

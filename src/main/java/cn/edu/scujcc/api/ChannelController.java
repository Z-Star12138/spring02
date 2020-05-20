package cn.edu.scujcc.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
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
import cn.edu.scujcc.model.Result;
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
	public Result<List<Channel>> getAllChannels(){
		logger.info("���ڻ�ȡ����Ƶ����");
		Result<List<Channel>> result = new Result<List<Channel>>();
		List<Channel> channels = service.getAllChannels();
		result = result.ok();
		result.setDate(channels);
		return result;
	}
	
	@GetMapping("/{id}")
	public Result<Channel> getChannel(@PathVariable String id) {
		logger.info("��ȡƵ����id="+id);
		Result<Channel> result = new Result<>();
		Channel c = service.getChannel(id);
		if(c != null ) {
			result = result.ok();
			result.setDate(c);
		}else {
			logger.info("�Ҳ���ָ��Ƶ��");
			result = result.error();
			result.setMessage("�Ҳ���ָ��Ƶ��");
		}
		return result;
	}
	
	/**
	 * ɾ��Ƶ��
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	public Result<Channel> deleteChannel(@PathVariable String id){
		logger.info("����ɾ����id="+id);
		Result<Channel> result = new Result<>();
		boolean del = service.deleteChannel(id);
		if(del) {
			result = result.ok();
		}else {
			result = result.error();
			result.setMessage("ɾ��ʧ��");
		}
		return result;
	}
	
	@PostMapping
	public Result<Channel> creatChannel(@RequestBody Channel c) {
		logger.info("����������Ƶ����Ƶ�����ݣ�" + c);
		Result<Channel> result = new Result<>();
		Channel saved = service.createChannel(c);
		result = result.ok();
		result.setDate(saved);
		return result;
	}
	
	@PutMapping
	public Result<Channel> updateChannel(@RequestBody Channel c) {
		logger.info("�ɹ�����������Ƶ����Ƶ�����ݣ�" + c);
		Result<Channel> result = new Result<>();
		Channel updated = service.updateChannel(c);
		result = result.ok();
		result.setDate(updated);
		return result;
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
	public Result<List<Comment>> hotComments(@PathVariable String channelId){
		Result<List<Comment>> result = new Result<List<Comment>>();
		logger.debug("���ڻ�ȡ����Ƶ��");
		result = result.ok();
		result.setDate(service.hostComments(channelId));
		return result;
	}
}

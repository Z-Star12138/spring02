package cn.edu.scujcc.service;

import java.util.List;
import java.util.Optional;

import javax.print.attribute.standard.PageRanges;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cn.edu.scujcc.dao.ChannelRepository;
import cn.edu.scujcc.model.Channel;

@Service
public class ChannelService {
	@Autowired
	private ChannelRepository repo;
//	private List<Channel> channels;

//		public ChannelService() {
		//模拟生成10条数据
//		channels = new ArrayList<>();
//		for (int i = 0; i<10; i++) {
//			Channel c = new Channel();
////			c.setId(i+1);
//			c.setTitle("中央"+(i+1)+"台");
//			c.setUrl("https://www.cctv.com");
//			channels.add(c);
//		}
	/**
	 * 获取所有频道
	 * @return	频道List
	 */
	public List<Channel> getAllChannels(){
		return repo.findAll();
	}
	/**
	 * 获取一个频道的数据
	 * @param channelI  频道编号
	 * @return  频道对象，若未为查找到，返回null
	 */
	// 读数据库
	public Channel getChannel(String channelId) {
		Optional<Channel>result = repo.findById(channelId);
		
		if(result.isPresent()) {
			return result.get();
		}else {
			return null;
		}
	}
//		Channel result = null;
		//循环查找指定的频道
//		for(Channel c: channels) {
//			if (c.getId() == channelId) {
//				result = c;
//				break;
//			}
//		}
//		return result;
	
	/**
	 * 删除指定的频道
	 * @param channelId  待删除的频道编号
	 * @return 若删除成功则返回true 若失败则返回false
	 */
	public boolean deleteChannel(String channelId) {
		boolean result = true;
		repo.deleteById(channelId);
		return result;
	}
		
//		Channel c = getChannel(channelId);
//		if(c != null ) {
//			channels.remove(c);
//			result = true;
//		}
//		return result;
//	}
	
	/**
	 * 保存频道
	 * @param c  待保存的频道的对象(没有id值)
	 * @return	保存后的频道(有id值)
	 */
	public Channel createChannel(Channel c) {
		//找到目前最大ID，然后添加1作为新频道的ID
//		int newId = channels.get(channels.size() - 1).getId() + 1;
//		c.setId(newId);
//		channels.add(c);
//		return c;
		return repo.save(c);
	}
	
	/**
	 * 更新指定的频道信息
	 * @param c   新的频道信息，用于更新已存在的同一频道
	 * @return	  更细后的频道信息
	 */
	public Channel updateChannel(Channel c) {
//		Channel toUpdate = getChannel(c.getId());
//		if(toUpdate != null) {
//			toUpdate.setTitle(c.getTitle());
//			toUpdate.setQuality(c.getQuality());
//			toUpdate.setUrl(c.getUrl());
//		}
//		return toUpdate;
		
		//TODO  仅修改用户指定属性
		Channel saved = getChannel(c.getId());//读出用户原有的数据
		if (c.getTitle()  != null) {
			saved.setTitle(c.getTitle());
		}
		if (c.getQuality()  != null) {
			saved.setQuality(c.getQuality());
		}
		if (c.getUrl()  != null) {
			saved.setUrl(c.getUrl());
		}
		
		if (c.getComments() != null) {
			if (saved.getComments() != null) {//把老评论添加到新评论后面去
				saved.getComments().addAll(c.getComments());
			} else {//用新评论代替老的空评论
			saved.setComments(c.getComments());
			}
		}
		
		if(c.getCover() != null ) {
			saved.setCover(c.getCover());
		}

		return repo.save(c);
	}
	
	
	public List<Channel> searchByQuality(String quality){
		return repo.findByQuality(quality);
	};
	public List<Channel> searchByTitle(String title){
		return repo.findByTitle(title);
	};
	
	/*
	 * 获取冷门的频道。
	 */
	public List<Channel> findColdChannels(){
		return repo.findByCommentsNull();
	}
	
	/**
	 * 翻页
	 * @return
	 */
	public List<Channel> findChannelsPage(int page){
		Page<Channel> p = repo.findAll(PageRequest.of(page, 2));
		return p.toList();
	}
	
}

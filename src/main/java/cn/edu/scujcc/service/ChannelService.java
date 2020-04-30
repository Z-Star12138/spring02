package cn.edu.scujcc.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cn.edu.scujcc.dao.ChannelRepository;
import cn.edu.scujcc.model.Channel;
import cn.edu.scujcc.model.Comment;

@Service
public class ChannelService {
	@Autowired
	private ChannelRepository repo;
	public static final Logger logger = LoggerFactory.getLogger(ChannelService.class);

	/**
	 * 获取所有频道
	 * 
	 * @return 频道List
	 */
	public List<Channel> getAllChannels() {
		return repo.findAll();
	}

	/**
	 * 获取一个频道的数据
	 * 
	 * @param channelI 频道编号
	 * @return 频道对象，若未为查找到，返回null
	 */
	// 读数据库
	public Channel getChannel(String channelId) {
		Optional<Channel> result = repo.findById(channelId);

		if (result.isPresent()) {
			return result.get();
		} else {
			return null;
		}
	}
//		Channel result = null;
	// 循环查找指定的频道
//		for(Channel c: channels) {
//			if (c.getId() == channelId) {
//				result = c;
//				break;
//			}
//		}
//		return result;

	/**
	 * 删除指定的频道
	 * 
	 * @param channelId 待删除的频道编号
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
	 * 
	 * @param c 待保存的频道的对象(没有id值)
	 * @return 保存后的频道(有id值)
	 */
	public Channel createChannel(Channel c) {
		// 找到目前最大ID，然后添加1作为新频道的ID
//		int newId = channels.get(channels.size() - 1).getId() + 1;
//		c.setId(newId);
//		channels.add(c);
//		return c;
		return repo.save(c);
	}

	/**
	 * 更新指定的频道信息
	 * 
	 * @param c 新的频道信息，用于更新已存在的同一频道
	 * @return 更细后的频道信息
	 */
	public Channel updateChannel(Channel c) {
//		Channel toUpdate = getChannel(c.getId());
//		if(toUpdate != null) {
//			toUpdate.setTitle(c.getTitle());
//			toUpdate.setQuality(c.getQuality());
//			toUpdate.setUrl(c.getUrl());
//		}
//		return toUpdate;

		// TODO 仅修改用户指定属性
		Channel saved = getChannel(c.getId());// 读出用户原有的数据
		if (c.getTitle() != null) {
			saved.setTitle(c.getTitle());
		}
		if (c.getQuality() != null) {
			saved.setQuality(c.getQuality());
		}
		if (c.getUrl() != null) {
			saved.setUrl(c.getUrl());
		}

		if (c.getComments() != null) {
			if (saved.getComments() != null) {// 把老评论添加到新评论后面去
				saved.getComments().addAll(c.getComments());
			} else {// 用新评论代替老的空评论
				saved.setComments(c.getComments());
			}
		}

		if (c.getCover() != null) {
			saved.setCover(c.getCover());
		}

		return repo.save(c);
	}

	public List<Channel> searchByQuality(String quality) {
		return repo.findByQuality(quality);
	};

	public List<Channel> searchByTitle(String title) {
		return repo.findByTitle(title);
	};

	/*
	 * 获取冷门的频道。
	 */
	public List<Channel> findColdChannels() {
		return repo.findByCommentsNull();
	}

	/**
	 * 翻页
	 * 
	 * @return
	 */
	public List<Channel> findChannelsPage(int page) {
		Page<Channel> p = repo.findAll(PageRequest.of(page, 2));
		return p.toList();
	}

	/**
	 * 向指定频道添加一条评论
	 * 
	 * @param channelId 目标频道编号
	 * @param comment   即将添加的评论
	 * @return
	 */
	public Channel addComment(String channelId, Comment comment) {
		Channel result = null;
		Channel saved = getChannel(channelId);
		if (null != saved) {// 数据库中有该频道
			saved.addComment(comment);
			result = repo.save(saved);
		}
		return result;
	}

	public List<Comment> hostComments(String channelId) {
		List<Comment> result = null;
		Channel saved = getChannel(channelId);
		if (saved != null) {
			result = saved.getComments();
			result.sort(new Comparator<Comment>() {
				@Override
				public int compare(Comment o1, Comment o2) {
//			    若o1<o2，返回负数
//			    若o1>o2，返回正数
//			    若o1=o2，返回0
					int re = 0;
					if (o1.getStar() < o2.getStar()) {
						re = 1;
					} else if (o1.getStar() > o2.getStar()) {
						re = -1;
					}
					return re;
				}
			});
			if (result.size() > 3) {
				result = result.subList(0, 3);
			}
			logger.debug("热门评论有" + result.size() + "条。");
			logger.debug(result.toString());
		} else {
			logger.warn("指定频道不存在,id=" + channelId);
		}
		return result;
	}
}

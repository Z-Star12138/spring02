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
	 * ��ȡ����Ƶ��
	 * 
	 * @return Ƶ��List
	 */
	public List<Channel> getAllChannels() {
		return repo.findAll();
	}

	/**
	 * ��ȡһ��Ƶ��������
	 * 
	 * @param channelI Ƶ�����
	 * @return Ƶ��������δΪ���ҵ�������null
	 */
	// �����ݿ�
	public Channel getChannel(String channelId) {
		Optional<Channel> result = repo.findById(channelId);

		if (result.isPresent()) {
			return result.get();
		} else {
			return null;
		}
	}
//		Channel result = null;
	// ѭ������ָ����Ƶ��
//		for(Channel c: channels) {
//			if (c.getId() == channelId) {
//				result = c;
//				break;
//			}
//		}
//		return result;

	/**
	 * ɾ��ָ����Ƶ��
	 * 
	 * @param channelId ��ɾ����Ƶ�����
	 * @return ��ɾ���ɹ��򷵻�true ��ʧ���򷵻�false
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
	 * ����Ƶ��
	 * 
	 * @param c �������Ƶ���Ķ���(û��idֵ)
	 * @return ������Ƶ��(��idֵ)
	 */
	public Channel createChannel(Channel c) {
		// �ҵ�Ŀǰ���ID��Ȼ�����1��Ϊ��Ƶ����ID
//		int newId = channels.get(channels.size() - 1).getId() + 1;
//		c.setId(newId);
//		channels.add(c);
//		return c;
		return repo.save(c);
	}

	/**
	 * ����ָ����Ƶ����Ϣ
	 * 
	 * @param c �µ�Ƶ����Ϣ�����ڸ����Ѵ��ڵ�ͬһƵ��
	 * @return ��ϸ���Ƶ����Ϣ
	 */
	public Channel updateChannel(Channel c) {
//		Channel toUpdate = getChannel(c.getId());
//		if(toUpdate != null) {
//			toUpdate.setTitle(c.getTitle());
//			toUpdate.setQuality(c.getQuality());
//			toUpdate.setUrl(c.getUrl());
//		}
//		return toUpdate;

		// TODO ���޸��û�ָ������
		Channel saved = getChannel(c.getId());// �����û�ԭ�е�����
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
			if (saved.getComments() != null) {// ����������ӵ������ۺ���ȥ
				saved.getComments().addAll(c.getComments());
			} else {// �������۴����ϵĿ�����
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
	 * ��ȡ���ŵ�Ƶ����
	 */
	public List<Channel> findColdChannels() {
		return repo.findByCommentsNull();
	}

	/**
	 * ��ҳ
	 * 
	 * @return
	 */
	public List<Channel> findChannelsPage(int page) {
		Page<Channel> p = repo.findAll(PageRequest.of(page, 2));
		return p.toList();
	}

	/**
	 * ��ָ��Ƶ�����һ������
	 * 
	 * @param channelId Ŀ��Ƶ�����
	 * @param comment   ������ӵ�����
	 * @return
	 */
	public Channel addComment(String channelId, Comment comment) {
		Channel result = null;
		Channel saved = getChannel(channelId);
		if (null != saved) {// ���ݿ����и�Ƶ��
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
//			    ��o1<o2�����ظ���
//			    ��o1>o2����������
//			    ��o1=o2������0
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
			logger.debug("����������" + result.size() + "����");
			logger.debug(result.toString());
		} else {
			logger.warn("ָ��Ƶ��������,id=" + channelId);
		}
		return result;
	}
}

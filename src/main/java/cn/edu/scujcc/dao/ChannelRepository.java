package cn.edu.scujcc.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cn.edu.scujcc.model.Channel;

@Repository
public interface ChannelRepository extends MongoRepository<Channel, String>{
	public List<Channel> findByQuality(String quality);
	public List<Channel> findByTitle(String title);
	
	/**
	 * �ҳ�����(������)Ƶ����
	 * @return
	 */
	public List<Channel> findByCommentsNull();
	
	
}

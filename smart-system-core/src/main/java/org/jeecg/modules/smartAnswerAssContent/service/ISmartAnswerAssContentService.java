package org.jeecg.modules.smartAnswerAssContent.service;

import org.jeecg.modules.smartAnswerAssContent.entity.SmartAnswerFile;
import org.jeecg.modules.smartAnswerAssContent.entity.SmartAnswerAssScore;
import org.jeecg.modules.smartAnswerAssContent.entity.SmartAnswerAssContent;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 答题考核节点表
 * @Author: jeecg-boot
 * @Date:   2022-02-21
 * @Version: V1.0
 */
public interface ISmartAnswerAssContentService extends IService<SmartAnswerAssContent> {

	/**
	 * 删除一对多
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);


}

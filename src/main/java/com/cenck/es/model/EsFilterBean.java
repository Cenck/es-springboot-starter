package com.cenck.es.model;

import java.util.Collection;

/**
 * @author zhz
 * @version V1.0
 * @since 2019/2/23 - 18:35
 **/
public interface EsFilterBean {

	/**  过滤	  */
	void filter();


	/**
	 * 获取查询字段
	 * @return
	 */
	FilterNode should();


	/**
	 * 从第几行开始
	 * @return
	 */
	int from();

	/**
	 * 需要获取的数据行数
	 * @return
	 */
	int size();

	/**
	 * 需要搜索的字段
	 * @return
	 */
	Collection<FilterNode> mustFields();

}

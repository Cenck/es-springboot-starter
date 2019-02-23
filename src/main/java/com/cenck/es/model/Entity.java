package com.cenck.es.model;

import java.io.Serializable;

/**
 * @author zhz
 * @version V1.0
 * @since 2019/2/23 - 18:17
 **/
public interface Entity extends Serializable {

	/**
	 * 获取实体主键
	 * @return
	 */
	Long getId();

}

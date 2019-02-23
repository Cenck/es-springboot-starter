package com.cenck.es.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 需要过滤的字段及参数
 *
 * @author zhz
 * @version V1.0
 * @since 2019/2/23 - 18:40
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterNode {

	private String name;
	private String value;

}

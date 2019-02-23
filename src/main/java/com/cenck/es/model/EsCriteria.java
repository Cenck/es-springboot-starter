package com.cenck.es.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author zhz
 * @version V1.0
 * @since 2019/2/23 - 18:56
 **/
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EsCriteria implements EsFilterBean {


	private int size;
	private int from;
	/**  索引   */
	private String index;
	/**  搜索关键字  */
	private FilterNode should;
	/**  搜索限制字段  */
	private Collection<FilterNode> mustFields = new ArrayList<>();


	@Override
	public void filter() {
	}

	@Override
	public String index() {
		return index;
	}

	@Override
	public FilterNode should() {
		return should;
	}


	@Override
	public int from() {
		return from;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Collection<FilterNode> mustFields() {
		return mustFields;
	}

}

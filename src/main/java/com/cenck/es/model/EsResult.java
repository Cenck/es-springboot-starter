package com.cenck.es.model;

import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.*;

import java.util.Collection;

/**
 * @author zhz
 * @version V1.0
 * @since 2019/2/23 - 18:37
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EsResult {

	private Integer                      status;
	private Long                         total;
	private int							 size;
	private String                       message;
	private Boolean                      success;
	private Collection<?> retList;

	public static EsResult success(long total,Collection<?> data){
		int size = -1;
		if (CollectionUtil.isNotEmpty(data)){
			size = data.size();
		}
		return EsResult.builder().message("成功").status(1).size(size).success(true).total(total).retList(data).build();
	}

}

package com.cenck.es.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static com.cenck.es.constant.EsBootConstant.ES_BOOT_YML_TITLE;

/**
 * elasticSearch Prop Bean
 *
 * @author zhz
 * @version V1.0
 * @since 2019/2/23 - 17:34
 **/
@Data
@ConfigurationProperties(prefix = ES_BOOT_YML_TITLE)
public class EsBootProp implements Serializable {

	private String host;

	private int    port;
	/** 集群名称 */
	private String clusterName;

}

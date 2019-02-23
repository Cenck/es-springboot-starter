package com.cenck.es.boot;

import com.cenck.es.bean.EsClient;
import com.cenck.es.bean.EsCrudTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhz
 * @version V1.0
 * @since 2019/2/23 - 17:55
 **/
@Configuration
@EnableConfigurationProperties({EsBootProp.class})
public class EsRootConfiguration {

	@Autowired
	private EsBootProp esBootProp;

	@Bean
	public EsClient esClient(){
		return new EsClient(esBootProp);
	}

	@Bean
	public EsCrudTemplate esCrudTemplate(){
		return new EsCrudTemplate();
	}


}

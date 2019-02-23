package com.cenck.es.bean;

import com.cenck.es.boot.EsBootProp;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zhz
 * @version V1.0
 * @since 2019/2/23 - 18:00
 **/
public class EsClient {

	private static Settings settings;


	private EsBootProp prop;
	public EsClient(EsBootProp prop){
		this.prop = prop;
		settings = Settings.builder().put("cluster.name", prop.getClusterName()).build();
	}
	
	/**  单例模式	  */
	public Client getClient() throws UnknownHostException {
		return TransportClient
				.builder()
				.settings(settings)
				.build()
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(prop.getHost()), prop.getPort()));
	}
}

package com.cenck.es.bean;

import com.cenck.es.boot.EsBootProp;
import com.cenck.es.model.Entity;
import com.cenck.es.model.EsFilterBean;
import com.cenck.es.model.EsResult;
import com.cenck.es.model.FilterNode;
import com.cenck.es.util.JsonMapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.xiaoleilu.hutool.util.StrUtil;
import lombok.extern.java.Log;
import org.codehaus.jackson.map.JsonMappingException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * es 基本模板
 *
 * @author zhz
 * @version V1.0
 * @since 2019/2/23 - 18:00
 **/
@Log
public class EsCrudTemplate {

	@Autowired
	private EsClient   esClient;
	@Autowired
	private EsBootProp esBootProp;

	/**
	 * @param list
	 * @param index 索引
	 * @param type
	 * @throws UnknownHostException
	 * @throws JsonProcessingException
	 */
	public void createIndex(List<? extends Entity> list, String index,String type) throws IOException {
		Client client = esClient.getClient();
		// 如果存在就先删除索引
		if (client.admin()
				.indices()
				.prepareExists(index)
				.get()
				.isExists()) {
			client.admin()
					.indices()
					.prepareDelete(index)
					.get();
		}
		// 创建索引,并设置mapping.
		String mappingStr = "{ \"goods\" : { \"properties\": { \"id\": { \"type\": \"long\" }, \"name\": {\"type\": \"string\", \"analyzer\": \"ik_max_word\"}, \"regionIds\": {\"type\": \"string\",\"index\": \"not_analyzed\"}}}}";

		client.admin()
				.indices()
				.prepareCreate(index)
				.addMapping(type, mappingStr)
				.get();

		// 批量处理request
		BulkRequestBuilder bulkRequest = client.prepareBulk();

		byte[] json;
		for (Entity o : list) {
			json = JsonMapper.getMapper()
					.getObjectMapper()
					.writeValueAsBytes(o);
			bulkRequest.add(new IndexRequest(index, type, o.getId() + "").source(json));
		}

		// 执行批量处理request
		BulkResponse bulkResponse = bulkRequest.get();

		// 处理错误信息
		if (bulkResponse.hasFailures()) {
			log.info("创建索引失败");
			long count = 0L;
			for (BulkItemResponse bulkItemResponse : bulkResponse) {
				log.info("发生错误的 索引id为 : " + bulkItemResponse.getId() + " ，错误信息为：" + bulkItemResponse.getFailureMessage());
				count++;
			}
			log.info("创建索引失败，共有: " + count + " 条失败记录");
		}
		client.close();
	}

	/**
	 * 显示查询列表信息
	 *
	 * @param type
	 * @param filter
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public EsResult search(String type, EsFilterBean filter) throws JsonParseException, JsonMappingException, IOException {
		Client client = esClient.getClient();

		SearchRequestBuilder responsebuilder = client.prepareSearch(filter.index())
				.setTypes(type);
		responsebuilder.setExplain(true);
		String fieldName;
		if (null != filter.should() && StrUtil.isEmpty(filter.should()
				.getName())) {
			fieldName = filter.should()
					.getName();
			QueryBuilder qb = new BoolQueryBuilder().should(QueryBuilders.matchQuery(fieldName, filter.should()
					.getValue()));
			for (FilterNode searchField : filter.mustFields()) {
				((BoolQueryBuilder) qb).must(QueryBuilders.termQuery(searchField.getName(), searchField.getValue()));
			}
			responsebuilder.setQuery(qb);
		} else {
			fieldName = "";
		}

		SearchResponse myresponse = responsebuilder.execute()
				.actionGet();
		SearchHits searchHits = myresponse.getHits();
		// 总命中数
		long total = searchHits.getTotalHits();

		List<Entity> retList = new ArrayList<>();
		for (SearchHit hit : myresponse.getHits()
				.getHits()) {
			Map<String, Object> source = hit.getSource();
			Entity entity = (Entity) source.get(fieldName);
			retList.add(entity);
		}
		client.close();
		return EsResult.success(total, retList);
	}

	/**
	 * 限定查询个数并且关键字设置高亮
	 *
	 * @param type
	 * @param filter
	 * @return
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public EsResult searchDocHighlight(String type, EsFilterBean filter) throws JsonParseException, JsonMappingException, IOException {
		Client client = esClient.getClient();

		SearchRequestBuilder responsebuilder = client.prepareSearch(filter.index())
				.setTypes(type);
		// 跳过前from个文档
		responsebuilder.setFrom(filter.from());
		// 条数
		responsebuilder.setSize(filter.size());
		responsebuilder.setExplain(true);
		String fieldName;
		if (null != filter.should() && StrUtil.isEmpty(filter.should()
				.getName())) {
			fieldName = filter.should()
					.getName();
			QueryBuilder qb = new BoolQueryBuilder().must(QueryBuilders.matchQuery(fieldName, filter.should()
					.getValue()));
			for (FilterNode searchField : filter.mustFields()) {
				((BoolQueryBuilder) qb).must(QueryBuilders.termQuery(searchField.getName(), searchField.getValue()));
			}
			// 查询
			responsebuilder.setQuery(qb);

			// 设置搜索关键字高亮
			responsebuilder.addHighlightedField(fieldName);
			responsebuilder.setHighlighterPreTags("<span style=\"color:red\">")
					.setHighlighterPostTags("</span>");
		} else {
			fieldName = "";
		}

		SearchResponse myresponse = responsebuilder.execute()
				.actionGet();
		SearchHits searchHits = myresponse.getHits();

		// 总命中数
		long total = searchHits.getTotalHits();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (SearchHit hit : myresponse.getHits()
				.getHits()) {
			Map<String, Object> source = hit.getSource();
			HighlightField hField = hit.getHighlightFields()
					.get(fieldName);
			StringBuilder name = new StringBuilder();
			if (hField.fragments() != null) {
				for (Text text : hField.fragments()) {
					name.append(text.toString());
				}
				source.put(fieldName, name.toString());
			}

			list.add(source);
		}
		client.close();
		return EsResult.success(total, list);
	}

	/**
	 * 新增document
	 *
	 * @param index 索引名称
	 * @param type 类型名称
	 * @param entity 数据对象
	 * @throws UnknownHostException
	 * @throws JsonProcessingException
	 */
	public void addDocument(String index, String type, Entity entity) throws IOException {
		Client client = esClient.getClient();

		byte[] json = JsonMapper.getMapper()
				.getObjectMapper()
				.writeValueAsBytes(entity);
		log.info("addDoc对象：" + new String(json));
		IndexResponse response = client.prepareIndex(index, type, entity.getId() + "")
				.setSource(json)
				.get();

		client.close();
	}

	/**
	 * 删除document
	 *
	 * @param index 索引名称
	 * @param type 类型名称
	 * @param goodsId 要删除的数据id
	 * @throws UnknownHostException
	 */
	public void deleteDocument(String index, String type, Long goodsId) throws UnknownHostException {
		Client client = esClient.getClient();

		DeleteIndexResponse dResponse = client.admin()
				.indices()
				.prepareDelete(index)
				.execute()
				.actionGet();
		if (dResponse.isAcknowledged()) {
			log.info("delete index " + index + "  successfully!");
		} else {
			log.warning("Fail to delete index " + index);
		}
		client.close();
	}

	/**
	 * 更新document
	 *
	 * @param index 索引名称
	 * @param type 类型名称
	 * @param entity 数据对象
	 * @throws JsonProcessingException
	 * @throws UnknownHostException
	 */
	public void updateDocument(String index, String type, Entity entity) throws UnknownHostException, JsonProcessingException {
		// 如果新增的时候index存在，就是更新操作
		try {
			addDocument(index, type, entity);
		} catch (IOException e) {
			e.printStackTrace();
			log.warning("es更新doc失败:" + e.getMessage());
			throw new IllegalStateException(e);
		}
	}

}

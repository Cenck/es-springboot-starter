import com.cenck.es.bean.EsCrudTemplate;
import com.cenck.es.model.Entity;
import com.cenck.es.model.EsCriteria;
import com.cenck.es.model.EsResult;
import com.cenck.es.model.FilterNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhz
 * @version V1.0
 * @since 2019/2/23 - 19:03
 **/
public class EsTest {

	@Autowired
	private EsCrudTemplate template;

	public void Test(){
		List<Goods> list = new ArrayList<>(2);
		list.add(new Goods(1,"苹果",new BigDecimal(4.5)));
		list.add(new Goods(2,"手机",new BigDecimal(1699)));
		try {
			template.createIndex(list,"goods","test");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			EsResult esResult = template.search("test", EsCriteria.builder()
					.index("goods")
					.should(new FilterNode("name","苹果")).build());
			System.out.println("本次查询到"+esResult.getSize()+"条数据");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Goods implements Entity {
		private long id;
		private String name;
		private BigDecimal price;
	}

}

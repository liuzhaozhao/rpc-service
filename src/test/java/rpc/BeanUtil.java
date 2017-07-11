package rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.Bean;
import service.DataBean;

public class BeanUtil {
	public static List<DataBean<Bean>> getListBean() {
		List<DataBean<Bean>> dataBeans = new ArrayList<DataBean<Bean>>();
		DataBean<Bean> d1 = new DataBean<Bean>();
		d1.setSuccessData(new Bean());
		dataBeans.add(d1);
		
		DataBean<Bean> d2 = new DataBean<>("测试");
		dataBeans.add(d2);
		return dataBeans;
	}
	
	public static Map<String, DataBean<Bean>> getMapBean() {
		Map<String, DataBean<Bean>> mapBean = new HashMap<>();
		DataBean<Bean> d1 = new DataBean<Bean>();
		d1.setSuccessData(new Bean());
		mapBean.put("map1", d1);
		mapBean.put("map2", new DataBean<>("测试"));
		return mapBean;
	}
}

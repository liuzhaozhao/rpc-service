package service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bean implements Serializable {
	private static final long serialVersionUID = 5592404225181171529L;
	private int i = 0;
	private float flo = 1.5f;
	private double dou = 2.3;
	private long lon = 123;
	private List<Integer> list = Arrays.asList(1,2,3);
	private List<DataBean<Bean2>> dataBeans = new ArrayList<DataBean<Bean2>>();
	private Map<String, DataBean<Bean2>> mapBean = new HashMap<String, DataBean<Bean2>>();
	private String s = "str";
	private Double d = 1.11;
	
	public Bean() {
		DataBean<Bean2> d1 = new DataBean<>();
		d1.setSuccessData(new Bean2());
		dataBeans.add(d1);
		
		DataBean<Bean2> d2 = new DataBean<>("测试");
		dataBeans.add(d2);
		
		mapBean.put("map1", d1);
		mapBean.put("map2", d2);
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public float getFlo() {
		return flo;
	}

	public void setFlo(float flo) {
		this.flo = flo;
	}

	public double getDou() {
		return dou;
	}

	public void setDou(double dou) {
		this.dou = dou;
	}

	public long getLon() {
		return lon;
	}

	public void setLon(long lon) {
		this.lon = lon;
	}

	public List<Integer> getList() {
		return list;
	}

	public void setList(List<Integer> list) {
		this.list = list;
	}

	public List<DataBean<Bean2>> getDataBeans() {
		return dataBeans;
	}

	public void setDataBeans(List<DataBean<Bean2>> dataBeans) {
		this.dataBeans = dataBeans;
	}

	public Map<String, DataBean<Bean2>> getMapBean() {
		return mapBean;
	}

	public void setMapBean(Map<String, DataBean<Bean2>> mapBean) {
		this.mapBean = mapBean;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public Double getD() {
		return d;
	}

	public void setD(Double d) {
		this.d = d;
	}
	
	
}

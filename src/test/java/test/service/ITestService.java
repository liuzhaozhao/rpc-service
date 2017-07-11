package test.service;

import java.util.List;

import service.DataBean;

public interface ITestService {
	public String testM();
	public String testM(String arg);
	public String testM(String msg, int aa);
	public String testM(int aa, String msg);
	public String testM2(int arg1, String arg);
	public DataBean<List<String>> test_1(int arg1, String arg, Integer arg3, DataBean<List<String>> dataBean);
}

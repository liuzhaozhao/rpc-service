package test;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

public class COWListTest {
	private CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<String>();

	@org.junit.Test
	public void test() {
		cowList.add("a");
		cowList.add("b");
		cowList.add("c");
		cowList.add("d");
		
		cowList.removeIf(new Predicate<String>() {
			@Override
			public boolean test(String t) {
				if("a".equals(t) || "b".equals(t)) {
					return true;
				}
				return false;
			}
			
		});
		System.err.println(StringUtils.join(cowList));
	}
}

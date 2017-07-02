package test.urlMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Test2 {
	public static void main(String[] args) {
//		Matcher m = Pattern.compile("1234|123|12").matcher("12345678123888129991234");
//		while(m.find()){
//		    System.out.println(m.group());
//		}
		
			String str = "/api/{name/{aaa-{bbb/test";
			Matcher m = Pattern.compile("(\\{[^\\/\\}]+})").matcher(str);
			List<String> varNames = new ArrayList<String>();
			StringBuffer sb = new StringBuffer();
			while(m.find()){
				String group = m.group();
				varNames.add(group.substring(1, group.length() - 1));
//				System.out.println(m.group());
				m.appendReplacement(sb, "([^\\/]+)");
			}
			m.appendTail(sb);
			System.err.println(sb.toString());
			System.err.println(StringUtils.join(varNames));
			
			String url = "/api/test1/ab-cd/test";
			Pattern pattern = Pattern.compile(sb.toString());
			Matcher mUrl = pattern.matcher(url);
			if (!mUrl.matches()) {
				return;
			}

			for (int i = 0; i < mUrl.groupCount(); i++) {
				System.err.println(mUrl.group(i + 1));
			}
			
//			Map<String, String> keywords = new HashMap<String, String>();
//			keywords.put("(\\{[^\\/\\}]+})", "([^\\/]+)");
//			keywords.put("test111", "111");
//			System.err.println(replceText(str, keywords));
			
			
//			String str = "[{name:\"孙悟空\",sex:\"male\",age:18,job:\"student\"}," +
//					 "{name:\"周星驰\",sex:\"male\",age:18,job:\"student\"}," +
//					 "{name:\"周星星\",sex:\"male\",age:18,job:\"student\"}]";
//					 
//			Matcher m = Pattern.compile("(\\{[^\\}]+})").matcher(str);
//			while(m.find()){
//			    System.out.println(m.group(1));
//			}
	}
	
	
}

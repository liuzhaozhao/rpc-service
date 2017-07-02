package test.urlMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.service.rpc.common.Utils;

public class Test {
	public static final String PARAM_PATTERN = "([^\\/]+)";
	private static final PathParserCharProcessor regularCharPathParserCharProcessor = new PathParserCharProcessor() {

        public void handle(int curChar, PathPatternParser pathPatternParser) {
            if (curChar == '{') {
                pathPatternParser.processor = new CurlyBracesPathParamPathParserCharProcessor();
            } else if (curChar == ':') {
                pathPatternParser.processor = new SimpleColumnBasedPathParamParserCharProcessor();
            } else {
                pathPatternParser.patternBuilder.appendCodePoint(curChar);
                pathPatternParser.stdPathPatternBuilder.appendCodePoint(curChar);
            }
        }

        public void end(PathPatternParser pathPatternParser) {
        }
    };
	
	public static void main(String[] args) {
		String url = "/api/123/456-111/test";
		String path = "/api/{name}/{aaa}-:bbb/test";
		PathPatternParser s = new PathPatternParser(path);
		s.parse();

		System.err.println(s.patternBuilder.toString());
		System.err.println(s.stdPathPatternBuilder.toString());

		Pattern pattern = Pattern.compile(s.patternBuilder.toString());
		Matcher m = pattern.matcher(url);
		if (!m.matches()) {
			return;
		}

		for (int i = 0; i < m.groupCount(); i++) {
			System.err.println(m.group(i + 1));
		}
	}
	
	private static final class PathPatternParser {
        final int length;
        final String pathPattern;
        int offset = 0;
        PathParserCharProcessor processor = regularCharPathParserCharProcessor;
        List<String> pathParamNames = new ArrayList<String>();
        StringBuilder patternBuilder = new StringBuilder();
        StringBuilder stdPathPatternBuilder = new StringBuilder();

        private PathPatternParser(String pathPattern) {
            this.length = pathPattern.length();
            this.pathPattern = pathPattern;
        }

        void parse() {
            while (offset < length) {
                int curChar = pathPattern.codePointAt(offset);

                processor.handle(curChar, this);

                offset += Character.charCount(curChar);
            }
            processor.end(this);
        }
    }
	
	private static interface PathParserCharProcessor {
        void handle(int curChar, PathPatternParser pathPatternParser);

        void end(PathPatternParser pathPatternParser);
    }
	
	
	private static final class CurlyBracesPathParamPathParserCharProcessor implements PathParserCharProcessor {
        private int openBr = 1;
        private boolean inRegexDef;
        private StringBuilder pathParamName = new StringBuilder();
        private StringBuilder pathParamRegex = new StringBuilder();


        public void handle(int curChar, PathPatternParser pathPatternParser) {
            if (curChar == '}') {
                openBr--;
                if (openBr == 0) {
                    // found matching brace, end of path param

                    if (pathParamName.length() == 0) {
                        // it was a mere {}, can't be interpreted as a path param
                        pathPatternParser.processor = regularCharPathParserCharProcessor;
                        pathPatternParser.patternBuilder.append("{}");
                        pathPatternParser.stdPathPatternBuilder.append("{}");
                        return;
                    }

                    // only the opening paren
                    Utils.checkArgument(pathParamRegex.length() != 1, "illegal path parameter definition '%s' at offset %d - custom regex must not be empty",
                            pathPatternParser.pathPattern, pathPatternParser.offset);


                    if (pathParamRegex.length() == 0) {
                        // use default regex
                        pathParamRegex.append(PARAM_PATTERN);
                    } else {
                        // close paren for matching group
                        pathParamRegex.append(")");
                    }

                    pathPatternParser.processor = regularCharPathParserCharProcessor;
                    pathPatternParser.patternBuilder.append(pathParamRegex);
                    pathPatternParser.stdPathPatternBuilder.append("{").append(pathParamName).append("}");
                    pathPatternParser.pathParamNames.add(pathParamName.toString());
                    return;
                }
            } else if (curChar == '{') {
                openBr++;
            }

            if (inRegexDef) {
                pathParamRegex.appendCodePoint(curChar);
            } else {
                if (curChar == ':') {
                    // we were in path name, the column marks the separator with the regex definition, we go in regex mode
                    inRegexDef = true;
                    pathParamRegex.append("(");
                } else {

                    //only letters are authorized in path param name
                	Utils.checkArgument(Character.isLetterOrDigit(curChar), "illegal path parameter definition '%s' at offset %d" +
                                    " - only letters and digits are authorized in path param name",
                            pathPatternParser.pathPattern, pathPatternParser.offset);

                    pathParamName.appendCodePoint(curChar);
                }
            }
        }


        public void end(PathPatternParser pathPatternParser) {
        }
    }

    private static final class SimpleColumnBasedPathParamParserCharProcessor implements PathParserCharProcessor {
        private StringBuilder pathParamName = new StringBuilder();

        public void handle(int curChar, PathPatternParser pathPatternParser) {
//      if (!Character.isLetterOrDigit(curChar) && curChar != '_') {
            if (curChar == '/') {
                pathPatternParser.patternBuilder.append(PARAM_PATTERN);
                pathPatternParser.stdPathPatternBuilder.append("{").append(pathParamName).append("}");
                pathPatternParser.pathParamNames.add(pathParamName.toString());
                pathPatternParser.processor = regularCharPathParserCharProcessor;
                pathPatternParser.processor.handle(curChar, pathPatternParser);
            } else {
                pathParamName.appendCodePoint(curChar);
            }
        }


        public void end(PathPatternParser pathPatternParser) {
            pathPatternParser.patternBuilder.append(PARAM_PATTERN);
            pathPatternParser.stdPathPatternBuilder.append("{").append(pathParamName).append("}");
            pathPatternParser.pathParamNames.add(pathParamName.toString());
        }
    }
    
    
}



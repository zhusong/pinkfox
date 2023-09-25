package co.xiaowangzi.debug.utils;

import org.apache.commons.text.StringEscapeUtils;

public class StringUtils {
    public static boolean isEmpty(String str){
        return str == null || "".equals(str.trim());
    }

    public static String replaceFirstFrom(String str, int from, String regex, String replacement)
    {
        String prefix = str.substring(0, from);
        String rest = str.substring(from);
        rest = rest.replaceFirst(regex, replacement);
        return prefix+rest;
    }

    public static boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }

    public static String unicodeToStr(String unicode) {
        return StringEscapeUtils.unescapeJava(unicode);
    }

    public static void main(String[] args) {
        String html = "com.jd.debug.tools.web.test.Controller;<br /><br />\r\n@RestController()\r\npublic class Controller {\r\n    \r\n    public Controller() {\r\n        super();\r\n    }\r\n    \r\n    @RequestMapping(value = \"debug\")\r\n    public String queryCurrentThreadName() throws URISyntaxException {\r\n        <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|0'></span>MyTest.print(\"good\");</div>\r\n        <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|1'></span>System.out.println(jarName(MyTest.class));</div>\r\n        <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|2'></span>String str = \"\\u6d4b\\u8bd5\";</div>\r\n        <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|3'></span>MyTest myTest = new MyTest();</div>\r\n        <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|4'></span>myTest.instancePrint();</div>\r\n        <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|5'></span>return \"good\";</div>\r\n    }\r\n    \r\n    @RequestMapping(value = \"operator\")\r\n    public String toCacheOperatePage(ModelAndView modelAndView, HttpServletRequest request) {\r\n        <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|6'></span>String id = request.getParameter(\"authorization\");</div>\r\n        if (StringUtils.isEmpty(id)) {\r\n            <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|7'></span>return \"404\";</div>\r\n        }\r\n        if (id.equalsIgnoreCase(\"0\")) {\r\n            <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|8'></span>return \"read\";</div>\r\n        } else if (id.equalsIgnoreCase(\"99\")) {\r\n            <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|9'></span>return \"write\";</div>\r\n        } else if (id.equalsIgnoreCase(\"-1\")) {\r\n            <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|10'></span>return \"readAndWrite\";</div>\r\n        } else {\r\n            <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|11'></span>return \"read\";</div>\r\n        }\r\n    }\r\n    \r\n    public static void main(String[] args) {\r\n        System.out.println(isEmpty(null));\r\n    }\r\n    \r\n    public static boolean isEmpty(String str) {\r\n        <div class='point-line-base point-line-normal '><span class='point-normal point-base' onclick='breakPointClicked(event)' status=0 id='point|com.jd.debug.tools.web.test.Controller|12'></span>return str == null || \"\".equals(str.trim());</div>\r\n    }\r\n}";
        System.out.println(unicodeToStr(html));
    }
}

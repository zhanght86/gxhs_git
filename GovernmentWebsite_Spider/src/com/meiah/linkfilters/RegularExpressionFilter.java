
package com.meiah.linkfilters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegularExpressionFilter implements LinkFilter {

    
    private Pattern pattern;

  
    public RegularExpressionFilter(String regex) {
        pattern = Pattern.compile(regex);
    }

   
    public boolean accept(String link) {
    	if (link == null || link.equals("")) {
			return false;
		}
    	Matcher m = pattern.matcher(link);
        return m.matches();
    }

}

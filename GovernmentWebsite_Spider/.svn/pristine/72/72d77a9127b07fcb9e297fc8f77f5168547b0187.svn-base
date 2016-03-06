package com.meiah.exhtmlparser;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.filters.HasAttributeFilter;

/**
 * htmlparse的标签过滤方法，适用于正则匹配
 * 
 * @author chenc@inetcop.com.cn
 * @date 2009-08-10
 * 
 */
public class LikeAttributeFilter extends HasAttributeFilter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    public LikeAttributeFilter (String attribute, String value)
    {
        mAttribute = attribute.toUpperCase (Locale.ENGLISH);
        mValue = value;
    }


	public boolean accept(Node node) {		
        Tag tag;
        Attribute attribute;
        boolean ret;

        ret = false;
        if (node instanceof Tag)
        {
            tag = (Tag)node;
            attribute = tag.getAttributeEx (mAttribute);
            ret = null != attribute;
            if (ret && (null != mValue)){
            	String s=attribute.getValue()==null?"":attribute.getValue();
            	Matcher m = Pattern.compile(mValue).matcher(s);
            	ret=m.find();
            }
        }
        
        return (ret);
	}

}

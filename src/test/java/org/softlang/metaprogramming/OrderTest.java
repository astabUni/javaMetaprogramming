package org.softlang.metaprogramming;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import org.junit.Ignore;
import org.junit.Test;
import org.softlang.metaprogramming.features.JCParser;
import org.softlang.metaprogramming.features.XSDParser;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class OrderTest {

	@Ignore @Test
	  public void comparisonOrder() {
		  String order = 
					"src/main/java/org/softlang/metaprogramming/dm/order/";
		  Map<String, HashMap<String, String>> orderMap = 
					JCParser.parseJC(JCParser.initJC(order));
		  
		  try {
				Node orderXSD = XSDParser.initXSD("inputs/Order.xsd");
				Map<String, Map<String, String>> orderMapX = 
						XSDParser.parseXSD(orderXSD);
				assertTrue(orderMap.equals(orderMapX));
				orderMapX.get("Item").remove("note");
				assertFalse(orderMap.equals(orderMapX));
				
			} catch (ParserConfigurationException | SAXException | IOException e) {
				
			}
	  }

}

package org.softlang.metaprogramming;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

public class CompanyTest {


	   @Ignore @Test
	  public void comparisonCompany() {
		  String company = 
					"src/main/java/org/softlang/metaprogramming/dm/company/";  
		  Map<String, HashMap<String, String>> companyMap = 
					JCParser.parseJC(JCParser.initJC(company));
		 		  
		  try {
				Node companyXSD = XSDParser.initXSD("inputs/Company.xsd");
				Map<String, Map<String, String>> companyMapX = 
						XSDParser.parseXSD(companyXSD);
				assertTrue(companyMap.equals(companyMapX));
				companyMapX.get("Department").remove("department");
				assertFalse(companyMap.equals(companyMapX));
				
				
			} catch (ParserConfigurationException | SAXException | IOException e) {
				
			}
	  }
	   
	   
	    
	   
	   
	   

}

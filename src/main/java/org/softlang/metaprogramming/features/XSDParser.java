package org.softlang.metaprogramming.features;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XSDParser {
	
	
	private static Map<String, Map<String, String>> parsedXSD2 = new HashMap<>();
	
	private static Map<String, List<Node>> parsedXSD3 = new HashMap<>();

	private static List<Node> refs = new ArrayList<Node>();

	private static String clazzName;
	
	
	
	public static Node initXSD(String file) throws ParserConfigurationException, IOException, SAXException {
		
        Node root = DocumentBuilderFactory.newInstance()
        								  .newDocumentBuilder()
        							      .parse(file).getDocumentElement();
        root.normalize();
        return root;
	}
	
	public static void traverseSubTree(Node subNode){
		
		if (subNode.hasChildNodes()) {
			if (subNode.getParentNode().getNodeName().equals("xs:schema")) {
				refs.add(subNode);
			}
			for (int i = 0; i < subNode.getChildNodes().getLength(); i++) {
				if(subNode.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
					traverseSubTree(subNode.getChildNodes().item(i));
				}
			}
		}else {
			refs.add(subNode);
		}
	}
	
	
	public static Map<String, Map<String, String>> parseXSD (Node root) {
		Map<String, Node> topElements = getTopElements(root);
		topElements.forEach((string, node)-> {
		traverseSubTree(node);	
		});
		Map<String, String> topReferences = getTopReferences(root);
		
		// assign attributes to classes
		refs.forEach(n -> {
			if(n.getParentNode().getNodeName().equals("xs:schema")) {
				String tempUp = n.getAttributes().getNamedItem("name").getNodeValue().toString();
				clazzName = tempUp.substring(0, 1).toUpperCase() + tempUp.substring(1);	
			}else {	
				parsedXSD3.computeIfAbsent(clazzName, k -> new ArrayList<>()).add(n);	
			}
		});
		
		parsedXSD3.forEach((k,v) -> {
			Map<String, String> atts = new HashMap<>();
			v.forEach(n -> {
				if (n.hasAttributes()) {
					if (n.getAttributes().getNamedItem("ref") != null) {
						String typeValue = topReferences.get(n.getAttributes().getNamedItem("ref").getNodeValue());
						if(n.getAttributes().getNamedItem("maxOccurs") != null) {
							atts.put(n.getAttributes().getNamedItem("ref").getNodeValue(), "List<"+typeValue+">");
						}else {
							atts.put(n.getAttributes().getNamedItem("ref").getNodeValue(), typeValue);
						}
					}else if (n.getAttributes().getNamedItem("type") != null) {
						if(n.getAttributes().getNamedItem("maxOccurs") != null) {
							String tempName = n.getAttributes().getNamedItem("type").getNodeValue();
							String tempName2 = "List<" + tempName.substring(0, 1).toUpperCase() + tempName.substring(1) + ">";
							atts.put(n.getAttributes().getNamedItem("name").getNodeValue() , tempName2);
						}else {
							String tempName=n.getAttributes().getNamedItem("type").getNodeValue();
							atts.put(n.getAttributes().getNamedItem("name").getNodeValue(), tempName.substring(0, 1).toUpperCase() + tempName.substring(1));
						}
					}
				}
			});
			parsedXSD2.put(k, atts);
			
		});
		System.out.println();
		System.out.println("-----XSD-----");
		parsedXSD2.forEach((cName, fields) -> {
			System.out.println(cName+": ");
			fields.forEach((decl, type) -> {
				System.out.println(" "+type+" "+decl);
			});
		});
		return parsedXSD2;
	}
	
	

	
	// Returns Map of Elements that get referenced and their type
	public static Map<String, String> getTopReferences(Node root) {
		
		Map<String, String> topReferences = new HashMap<>();
		NodeList children = root.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				NamedNodeMap attributes = children.item(i).getAttributes();
				
				if (children.item(i).hasAttributes()
					&& attributes.getLength() == 1) {
					String tempName= attributes.getNamedItem("name").getNodeValue();
					topReferences.put(attributes.getNamedItem("name").getNodeValue(), tempName.substring(0, 1).toUpperCase() + tempName.substring(1));	
				}else {
					switch (attributes.getNamedItem("type").getNodeValue()) {
					case "xs:string":
						topReferences.put(attributes.getNamedItem("name").getNodeValue(), "String");						
						break;
					case "xs:double":
						topReferences.put(attributes.getNamedItem("name").getNodeValue(), "double");						
						break;
					case "xs:integer":
						topReferences.put(attributes.getNamedItem("name").getNodeValue(), "BigInteger");						
						break;
					case "xs:boolean":
						topReferences.put(attributes.getNamedItem("name").getNodeValue(), "boolean");						
						break;
					default:
						break;
					}
				}
			}			
		}
		return topReferences;
	}
	
	
	
	// Returns Map with Classname and NodeList of children for traversing
	public static Map<String, Node> getTopElements(Node root) {
		
		Map<String, Node> topElements = new HashMap<>();
		NodeList children = root.getChildNodes();
		
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).hasChildNodes()) {
				
					String tempName = children.item(i).getAttributes().item(0).getNodeValue();
					topElements.put(tempName.substring(0, 1).toUpperCase() + tempName.substring(1), children.item(i));
			
			}else if(!(children.item(i).hasChildNodes()) 
					&& children.item(i).getNodeName().equals("xs:complexType")) {
				
					String tempName = children.item(i).getAttributes().item(0).getNodeValue();
					topElements.put(tempName.substring(0, 1).toUpperCase() + tempName.substring(1), children.item(i));
			}
		}
		return topElements;
	}
	
}

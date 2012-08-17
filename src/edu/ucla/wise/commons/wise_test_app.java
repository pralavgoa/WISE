package edu.ucla.wise.commons;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Hashtable;

import org.w3c.dom.Document;

public class wise_test_app {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try
		{
//            String file_loc = "file:///C:\\Documents and Settings\\dbell\\workspace\\WISE_XML\\dme\\Enrollmt.xml";
            String file_loc = "file:///home/manoj/workspace/JBOSS_WISE/lib/Enrollmt.xml";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setCoalescing(true);
            factory.setExpandEntityReferences(false);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            Document xml_doc = factory.newDocumentBuilder().parse(file_loc);
			Survey s = new Survey(xml_doc,null);
			System.out.println(s.toString());

			Hashtable params = new Hashtable();
			String[][] input = {{"PRIOR_CME_1","1"},{"PRIOR_CME_4","2"},{"COMP_ATTITUDES_8","2"},{"NP_SPECIALTY_1","1"}};
			for (int i=0;i<input.length;i++)
			{
				params.put(input[i][0], input[i][1]);
			}
			User testUser = new User(s);
			System.out.println("Got user"+testUser.id);
			System.out.println("Before advancing");
			testUser.readAndAdvancePage(params, false);
			System.out.println("After advancing");
			System.out.println(testUser.get_field_value("subj_type"));

	        String p_output = testUser.currentPage.render_page(testUser);
			System.out.println(p_output);

		}
		catch (Exception e)
		{
			System.out.println("Exception handling");
			System.out.println(e);
		}
	}
}

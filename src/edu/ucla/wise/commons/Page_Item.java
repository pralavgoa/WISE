package edu.ucla.wise.commons;

import java.util.Hashtable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents a single page item
 */

public abstract class Page_Item {
    /** Instance Variables */
    public String name;
    public String item_type;

    public String translation_id = null;
    public Translation_Item question_translated;

    public String html = "";
    public Condition cond = null; // any page item can be conditional on

    // previous responses

    public static boolean IsPageItemNode(Node n) {
	// add try-catch here
	String nname = null;
	boolean answer = false;
	try {
	    nname = n.getNodeName();
	    if (nname != null)
		answer = nname.equalsIgnoreCase("Open_Question")
			|| nname.equalsIgnoreCase("Closed_Question")
			|| nname.equalsIgnoreCase("Question_Block")
			|| nname.equalsIgnoreCase("Directive")
			|| nname.equalsIgnoreCase("Repeating_Item_Set");
	} catch (Exception e) {
	    WISE_Application.log_error("PAGE ITEM test attempt failed for " + n
		    + ": " + e, null);
	}
	return answer;
    }

    /*
     * CLASS HIERARCHY is triaged here - Edit this static function to add new
     * subclasses
     */
    public static Page_Item MakeNewItem(Node n) {
	String nname = null;
	Page_Item item = null;
	try {
	    nname = n.getNodeName();
	    if (nname.equalsIgnoreCase("Open_Question")) {
		NodeList nodelist2 = n.getChildNodes();
		for (int j = 0; j < nodelist2.getLength(); j++) {
		    if (nodelist2.item(j).getNodeName()
			    .equalsIgnoreCase("Numeric_Open_Response"))
			item = (Page_Item) new Numeric_Open_Question(n);
		    else if (nodelist2.item(j).getNodeName()
			    .equalsIgnoreCase("Text_Open_Response"))
			item = (Page_Item) new Text_Open_Question(n);
		}
	    } else if (nname.equalsIgnoreCase("Closed_Question")) {
		item = (Page_Item) new Closed_Question(n);
	    } else if (nname.equalsIgnoreCase("Question_Block")) {
		NodeList nodelist2 = n.getChildNodes();
		for (int j = 0; j < nodelist2.getLength(); j++)
		    if (nodelist2.item(j).getNodeName()
			    .equalsIgnoreCase("Subject_Set_Ref"))
			item = (Page_Item) new Question_Block_forSubjectSet(n);
		item = (Page_Item) new Question_Block(n);
	    } else if (nname.equalsIgnoreCase("Directive")) {
		item = (Page_Item) new Directive(n);
	    } else if (nname.equalsIgnoreCase("Repeating_Item_Set")) {
		item = (Page_Item) new Repeating_Item_Set(n);
	    }
	} catch (Exception e) {
	    WISE_Application.log_error("PAGE ITEM Creation attempt failed for "
		    + nname + ": " + e, null);
	}
	return item;
    }

    /** root constructor for page items - parse values common to all */
    public Page_Item(Node n) {
	try {
	    // name - page item's ID
	    Node node = n.getAttributes().getNamedItem("Name");
	    if (node != null)
		name = node.getNodeValue().toUpperCase();
	    // item_type - page item's type
	    item_type = n.getNodeName();
	    // parse the precondition
	    NodeList nodelist = n.getChildNodes();
	    for (int i = 0; i < nodelist.getLength(); i++) {
		if (nodelist.item(i).getNodeName()
			.equalsIgnoreCase("Precondition")) {
		    // hasPrecondition = true;
		    // create the condition object
		    cond = new Condition(nodelist.item(i));
		}
	    }
	} catch (Exception e) {
	    WISE_Application
		    .log_error("PAGE ITEM ROOT CONSTRUCTOR: " + e, null);
	    return;
	}
    }

    // abstract function for resolving references, creating field list, creating
    // html string
    public void knitRefs(Survey mySurvey) {
	try {
	    throw new Exception("knitRefs called on " + item_type + " " + name);
	} catch (Exception e) {
	    WISE_Application.log_error("Unimplemented Page_item method: " + e,
		    null);
	}
    }

    public String[] listFieldNames() {
	try {
	    throw new Exception("listFieldNames() called on " + item_type + " "
		    + name);
	} catch (Exception e) {
	    WISE_Application.log_error("Unimplemented Page_item method: " + e,
		    null);
	}
	return null;
    }

    // default item stores value as an integer; override necessary only for
    // other value types
    public char getValueType() {
	return Data_Bank.intValueTypeFlag;
    }

    /**
     * returns boolean if field is required but default is always false - will
     * be overwritten by subclass
     */
    public boolean isRequired() {
	return false;
    }

    /** stub function which is overwritten by subclasses */
    // public String render_results(Hashtable data, String whereclause) {
    // return "";
    // }

    // this version is for the admin server
    public String render_results(Page pg, Data_Bank db, String whereclause,
	    Hashtable data) {
	return "";
    }

    /** stub function which is overwritten by subclasses */
    public String print_survey() {
	return "";
    }

    /**
     * returns the stem if the field is required and not filled out - will be
     * overwritten by Question class
     */
    public String get_required_stem() {
	return "";
    }

    /** stub function which is overwritten by subclasses */
    public Hashtable read_form(Hashtable params) {
	try {
	    throw new Exception("read_form called on " + item_type + " " + name);
	} catch (Exception e) {
	    WISE_Application.log_error("Unimplemented Page_item method: " + e,
		    null);
	}
	return null;
    }

    public String render_form() {
	return html;
    }

    // Condition-checking for all item-types initiated here (all same)
    public String render_form(User theUser, int element_number) {
	/*
	 * if (cond != null) { // check if the value of data meets the
	 * precondition boolean write_question = cond.check_condition(theUser);
	 * // if it doesn't meet the precondition, skip writing this question //
	 * by return an empty string if (!write_question) return ""; } return
	 * html;
	 */
	StringBuffer page_item_html = new StringBuffer("");
	if (cond != null) {
	    page_item_html.append("<script>");
	    page_item_html.append("page_function_array[\"q" + element_number
		    + "\"]");
	    page_item_html.append("= function " + "q" + element_number + "(A)");
	    page_item_html.append("{");
	    page_item_html.append("return");

	    page_item_html.append(cond.getJs_expression().toUpperCase());

	    page_item_html.append(";");
	    page_item_html.append("};");
	    page_item_html.append("</script>");
	}
	page_item_html.append("<div ");
	page_item_html.append("id=\"q" + element_number + "\"");
	if (cond != null) {
	    // check if the value of data meets the precondition
	    boolean write_question = cond.check_condition(theUser);
	    // if it doesn't meet the precondition, skip writing this question
	    // by return an empty string
	    if (!write_question)
		page_item_html.append(" style=\"display:none\" ");

	}
	page_item_html.append(">");
	page_item_html.append(html);
	page_item_html.append("</div>");

	return page_item_html.toString();
    }

    /***************************************************************/

    /** stub function which is overwritten by subclasses */
    // public int read_form(Hashtable params, String[] fieldNames, String[]
    // fieldValues, int fieldIndex)
    // {
    // return 0;
    // }
    // /** stub function which is overwritten by subclass - question block */
    // public int read_form(Hashtable params, String[] fieldNames, String[]
    // fieldValues, int fieldIndex, User theUser)
    // {
    // return 0;
    // }

    /** stub function which is overwritten by subclasses */
    public abstract int countFields();

    /** prints out the name of the item */
    public String toString() {
	String s = "Name: " + name + "<br>";
	return s;
    }

}

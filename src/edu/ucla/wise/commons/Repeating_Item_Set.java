package edu.ucla.wise.commons;

import java.util.ArrayList;
import java.util.Collections;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Author: Pralav Dessai
 * Description: This class represents a set of questions
 * 				that can repeat based on user input. For example
 * 				adding answers to the question: "Tell us about
 * 				different schools that you attended". 
 * Created On: 17th May 2012
 */
public class Repeating_Item_Set extends Page_Item {

    /*
     * Instance Variables
     */

    public String id; // Not sure why, but is there in Subject_set
    public String title;
    public ArrayList<Page_Item> item_set = new ArrayList<Page_Item>();
    public Condition pre_condition;

    public ArrayList<String> item_set_as_xml = new ArrayList<String>();

    /*
     * Parsing a Repeating Question set from survey file
     */
    public Repeating_Item_Set(Node i_node) {

	super(i_node);// Avoiding the "Implicit super constructor error"
	try {
	    id = i_node.getAttributes().getNamedItem("ID").getNodeValue();
	    // title =
	    // i_node.getAttributes().getNamedItem("Title").getNodeValue();
	    NodeList node_list = i_node.getChildNodes();

	    for (int i = 0; i < node_list.getLength(); i++) {
		Node child_node = node_list.item(i);

		// //Saving this node as xml
		// String node_as_xml = "Name:"+child_node.getNodeName();
		// if(child_node.getNodeName().equalsIgnoreCase("Closed_Question"));
		// {
		// node_as_xml += get_closed_question_html(child_node);
		// }
		//
		// item_set_as_xml.add(node_as_xml);

		// Check if its a page_item. Else it might be a precondition
		if (Page_Item.IsPageItemNode(child_node)) {
		    Page_Item current_item = Page_Item.MakeNewItem(child_node);
		    if (current_item == null) {
			throw new Exception("Null item parse at " + i);
		    }
		    // All is fine here, so add to the item_set
		    item_set.add(current_item);
		} else {
		    if (child_node.getNodeName().equalsIgnoreCase(
			    "Precondition")) {
			pre_condition = new Condition(child_node);
		    }
		}
	    }
	} catch (Exception e) {
	    WISE_Application.log_error(
		    "WISE - survey parse failure at Repeating Item Set [" + id
			    + "] " + e.toString() + "\n" + this.toString(),
		    null);
	    return;
	}
    }

    public String render_repeating_item_set(User i_user, int item_index) {
	StringBuffer html_content = new StringBuffer("");
	
	if (pre_condition != null) {
	    html_content.append("<script>");
	    html_content.append("page_function_array[\"q" + item_index
		    + "\"]");
	    html_content.append("= function " + "q" + item_index + "(A)");
	    html_content.append("{");
	    html_content.append("return");

	    html_content.append(cond.getJs_expression().toUpperCase());

	    html_content.append(";");
	    html_content.append("};");
	    html_content.append("</script>");
	}

	// html_content.append(get_javascript_html());
	html_content.append("<div id=q" + item_index
		+ " class='repeating_item_set'");
	if (pre_condition != null) {
	    // check if the value of data meets the precondition
	    boolean write_question = cond.check_condition(i_user);
	    // if it doesn't meet the precondition, skip writing this question
	    // by return an empty string
	    if (!write_question)
		html_content.append(" style=\"display:none\" ");

	}
	html_content.append('>');
	
	html_content.append("<div id='repeating_set_with_id_"
		+ get_name_for_repeating_set()
		+ "'>");

	html_content
		.append("<div style='display: block; background-color:#353535;'>");
	html_content
		.append("<input type='text' class='repeat_item_name span3' placeholder='Enter "
			+ get_name_for_repeating_set() + "' />");
	html_content
		.append("<a href='#' class='add_repeat_instance_name_button btn btn-primary btn-medium'>Add</a>");
	html_content.append("</div>");
	html_content
		.append("<div class='add_item_to_repeating_set' style='display:none'>");
	for (int i = 0; i < item_set.size(); i++) {
	    html_content.append(item_set.get(i).render_form(i_user,
		    100 * item_index + i));// 100 is multiplied to get diff div
					   // id, not good
	}
	html_content
		.append("<a href='#' class='add_repeat_item_save_button'><b>Save this item</b></a>");
	html_content.append("</div>");
	html_content.append("<div class = 'repeating_question' Name="
		+ get_name_for_repeating_set() + ">");
	html_content.append("</div>");
	html_content.append("</div>");
	html_content.append("</div>");
	return html_content.toString();
    }

    @Override
    public int countFields() {

	int field_count = 0;

	for (Page_Item repeating_item : item_set) {
	    field_count += repeating_item.countFields();
	}

	return field_count;
    }

    @Override
    public char getValueType() {
	return 'z'; // arbitrary string to satisfy the caller. Do something
		    // about this!!!
    }

    @Override
    public String render_form(User i_user, int item_index) {
	return render_repeating_item_set(i_user, item_index);
    }

    @Override
    public void knitRefs(Survey i_survey) {
	try {
	    for (Page_Item repeating_item : item_set) {
		repeating_item.knitRefs(i_survey);
	    }
	} catch (Exception e) {
	    // DO something with the exception Pralav
	}
    }

    @Override
    public String[] listFieldNames() {
	ArrayList<String> field_names = new ArrayList<String>();

	for (Page_Item repeating_item : item_set) {
	    String[] item_field_names = repeating_item.listFieldNames();
	    Collections.addAll(field_names, item_field_names);
	}

	ArrayList<String> return_field_names = new ArrayList<String>();

	for (String field_name : field_names) {
	    return_field_names.add("repeat_" + field_name);
	}

	String[] field_names_array = new String[return_field_names.size()];

	for (int i = 0; i < field_names_array.length; i++) {
	    field_names_array[i] = return_field_names.get(i);
	}
	return field_names_array;

    }

    public String get_name_for_repeating_set() {
	return id;
    }

    public char[] getValueTypeList() {
	ArrayList<Character> value_list = new ArrayList<Character>();
	for (Page_Item repeating_item : item_set) {
	    String[] item_field_names = repeating_item.listFieldNames();
	    for (int i = 0; i < item_field_names.length; i++) {
		value_list.add(repeating_item.getValueType());
	    }
	}

	char[] value_list_array = new char[value_list.size()];

	for (int i = 0; i < value_list_array.length; i++) {
	    value_list_array[i] = value_list.get(i).charValue();
	}

	return value_list_array;
    }

    // static methods follow
    // ---------------------------------------------------------
    public static Repeating_Item_Set MakeNewItem(Node n) {
	String nname = null;
	Repeating_Item_Set repeating_item = null;
	try {
	    nname = n.getNodeName();
	    if (nname.equalsIgnoreCase("Repeating_Item_Set")) {
		repeating_item = new Repeating_Item_Set(n);
	    }
	} catch (Exception e) {
	    WISE_Application.log_error("PAGE ITEM Creation attempt failed for "
		    + nname + ": " + e, null);
	}
	return repeating_item;
    }

    public static boolean IsRepeatingItemSetNode(Node n) {
	// add try-catch here
	String nname = null;
	boolean answer = false;
	try {
	    nname = n.getNodeName();
	    if (nname != null)
		answer = nname.equalsIgnoreCase("Repeating_Item_Set");
	} catch (Exception e) {
	    WISE_Application.log_error(
		    "Repeating Item Set test attempt failed for " + n + ": "
			    + e, null);
	}
	return answer;
    }

}

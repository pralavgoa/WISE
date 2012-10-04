package edu.ucla.wise.commons;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.ucla.wise.commons.InviteeMetadata.Values;

/**
 * This class is a survey object and contains information about a specific
 * survey
 */

public class Survey {
    /** Instance Variables */

    // public String file_loc;
    public String id;
    public String title;
    public String project_name;
    public String from_string;
    public String from_email;
    public String interrupt_message;
    public String done_message;
    public String review_message;
    public String version;
    // public String user_data_page;
    public boolean allow_goback;
    public int min_completers;
    public String forward_url;
    public String edu_module;
    public String logo_name;
    public String invitee_fields[];

    public Hashtable response_sets;
    public Hashtable subject_sets;
    public Hashtable translation_items;
    public InviteeMetadata inviteeMetadata;
    public Page[] pages;
    public Study_Space study_space;
    public int total_item_count;

    /** constructor - setup a survey by parsing the file */
    public Survey(Document xml_doc, Study_Space ss) {
	try {
	    int numb_pages = 0;
	    total_item_count = 0;
	    study_space = ss;

	    // create a parser and an XML document
	    xml_doc.getDocumentElement().normalize();

	    // parse out the data from survey xml file
	    NodeList nodelist = xml_doc.getElementsByTagName("Survey");
	    for (int i = 0; i < nodelist.getLength(); i++) {
		Node node = nodelist.item(i);
		// parse out the survey attributes
		// survey ID & title
		id = node.getAttributes().getNamedItem("ID").getNodeValue();
		title = node.getAttributes().getNamedItem("Title")
			.getNodeValue();
		// names who send the email
		Node node2 = node.getAttributes().getNamedItem("From_String");
		if (node2 != null)
		    from_string = node2.getNodeValue();
		else
		    from_string = "";
		// FROM email address - fake one
		// the actual FROM email will use the one in SMTP email account
		node2 = node.getAttributes().getNamedItem("From_Email");
		if (node2 != null)
		    from_email = node2.getNodeValue();
		else
		    from_email = "";
		// survey version
		node2 = node.getAttributes().getNamedItem("Version");
		if (node2 != null)
		    version = node2.getNodeValue();
		else
		    version = "";
		// get the name of user-defined data table
		// the default is the invitee table
		// node2 = node.getAttributes().getNamedItem("user_data_page");
		// if (node2 != null)
		// user_data_page = node2.getNodeValue();
		// else
		// user_data_page = "Invitee";
		// messsage ID for survey-interruption message
		node2 = node.getAttributes().getNamedItem("Interrupt_Msg");
		if (node2 != null)
		    interrupt_message = node2.getNodeValue();
		else
		    interrupt_message = "";
		// messsage ID for survey-completion message
		node2 = node.getAttributes().getNamedItem("Done_Msg");
		if (node2 != null)
		    done_message = node2.getNodeValue();
		else
		    done_message = "";
		// messsage ID for survey-review message
		node2 = node.getAttributes().getNamedItem("Review_Msg");
		if (node2 != null)
		    review_message = node2.getNodeValue();
		else
		    review_message = "";
		// allow user to go back to review those survey pages have been
		// past over
		node2 = node.getAttributes().getNamedItem("Allow_Goback");
		if (node2 != null)
		    allow_goback = new Boolean(node2.getNodeValue())
			    .booleanValue();
		else
		    allow_goback = false;
		// user won't allow to review the survey results,
		// until the number of completers reach up to this number.
		node2 = node.getAttributes()
			.getNamedItem("View_Result_After_N");
		if (node2 != null)
		    min_completers = Integer.parseInt(node2.getNodeValue());
		else
		    min_completers = -1;
		// forwarding URL after the survey process
		// normally it is the URL link to the quiz
		node2 = node.getAttributes().getNamedItem("Forward_On");
		if (node2 != null)
		    forward_url = node2.getNodeValue();
		else
		    forward_url = "";
		// the module ID for the quiz process
		node2 = node.getAttributes().getNamedItem("Edu_Module");
		if (node2 != null)
		    edu_module = node2.getNodeValue();
		else
		    edu_module = "";
		// the logo image's name for the survey
		node2 = node.getAttributes().getNamedItem("Logo_Name");
		if (node2 != null)
		    logo_name = node2.getNodeValue();
		else
		    logo_name = "proj_logo.gif";
		node2 = node.getAttributes().getNamedItem("Project_Name");
		if (node2 != null)
		    project_name = node2.getNodeValue();
		else
		    project_name = "UNKOWN";

		// count the number of pages
		NodeList nodelistChildren = node.getChildNodes();
		for (int j = 0; j < nodelistChildren.getLength(); j++) {
		    if (nodelistChildren.item(j).getNodeName()
			    .equalsIgnoreCase("Survey_Page"))
			numb_pages++;
		}
	    }

	    // parse out the response sets
	    response_sets = new Hashtable();
	    nodelist = xml_doc.getElementsByTagName("Response_Set");
	    for (int i = 0; i < nodelist.getLength(); i++) {
		Node node = nodelist.item(i);
		Response_Set r = new Response_Set(node, this);
		response_sets.put(r.id, r);
	    }

	    // parse out the invitee fields
	    nodelist = xml_doc.getElementsByTagName("Invitee_Fields");
	    for (int i = 0; i < nodelist.getLength(); i++) {
		Node node = nodelist.item(i);
		inviteeMetadata = new InviteeMetadata(node, this);
	    }
	    if (inviteeMetadata != null) {
		invitee_fields = new String[inviteeMetadata.fieldMap.size()];
		int cnt = 0;
		for (Map.Entry<String, Values> map : inviteeMetadata.fieldMap
			.entrySet()) {
		    invitee_fields[cnt++] = map.getKey();
		}
		study_space.db.syncInviteeTable(inviteeMetadata);
	    }

	    // parse out the translation sets
	    translation_items = new Hashtable();
	    // nodelist = doc.getElementsByTagName("Translation");
	    nodelist = xml_doc.getElementsByTagName("TranslationType");
	    for (int i = 0; i < nodelist.getLength(); i++) {
		Node node = nodelist.item(i);
		Translation_Item t = new Translation_Item(node, this);
		translation_items.put(t.id, t);
	    }

	    // parse out the subject sets
	    subject_sets = new Hashtable();
	    nodelist = xml_doc.getElementsByTagName("Subject_Set");
	    for (int i = 0; i < nodelist.getLength(); i++) {
		Node n = nodelist.item(i);
		Subject_Set subject_set = new Subject_Set(n, this);
		subject_sets.put(subject_set.id, subject_set);
	    }

	    // create the pages
	    pages = new Page[numb_pages];
	    nodelist = xml_doc.getElementsByTagName("Survey");

	    for (int i = 0; i < nodelist.getLength(); i++) {
		if (nodelist.item(i).getNodeName().equalsIgnoreCase("Survey")) {
		    NodeList nodelist1 = nodelist.item(i).getChildNodes();
		    for (int j = 0, k = 0; j < nodelist1.getLength(); j++) {
			if (nodelist1.item(j).getNodeName()
				.equalsIgnoreCase("Survey_Page")) {
			    pages[k] = new Page(nodelist1.item(j), this);
			    total_item_count += pages[k].get_itemCount();
			    k++;
			}
		    }
		}
	    }

	} catch (Exception e) {
	    WISE_Application.log_error(
		    "WISE - SURVEY parse error: " + e.toString() + "\n" + id
			    + "\n" + this.toString(), null);

	    return;
	}
    }

    /** return a database Connection object */
    // DEPRECATE
    public Connection getDBConnection() throws SQLException {
	return study_space.getDBConnection();
    }

    /** return a the Data_Bank object for the Survey's Study_space */
    public Data_Bank getDB() {
	return study_space.getDB();
    }

    /**
     * display the submenu on the left side of frame to show the survey progress
     * - the submenu won't let the user go back to review completed pages
     */
    public String print_progress(Page currentPage, Hashtable completed_pages) {
	String s = "";
	try {
	    s += "<html><head>";
	    s += "<STYLE>";
	    s += "a:hover {color: #aa0000; text-decoration: underline}";
	    s += "a:link {color: #003366; text-decoration: none}";
	    s += "a:visited {color: #003366; text-decoration: none}";
	    s += "</STYLE>";
	    s += "</head>";
	    s += "<LINK href='" + "styleRender?app=" + study_space.study_name
		    + "&css=style.css" + "' type=text/css rel=stylesheet>";
	    s += "<body>";
	    // s += "<body text='#000000' bgcolor='#FFFFCC' >";
	    s += "<font face='Verdana, Arial, Helvetica, sans-serif'>";
	    s += "<table cellpadding=4 cellspacing=0>";
	    s += "<tr><td align=center valign=top>";
	    s += "<img src='" + "imageRender?app=" + study_space.study_name
		    + "&img=" + logo_name + "' border=0></td></tr>";
	    s += "<tr><td>&nbsp;</td></tr>";
	    // display the interrupt link
	    s += "<tr><td>";
	    s += "<a href=\"javascript:top.mainFrame.form.document.mainform.action.value='interrupt';";
	    s += "top.mainFrame.form.document.mainform.submit();\" target=\"_top\">";
	    s += "<font size=\"-1\"><center>Click here to <B>SAVE</B> your answers ";
	    s += "if you need to pause</center></font></a>";
	    s += "</td></tr>";
	    s += "<tr><td><font size=\"-2\">&nbsp;</font></td></tr>";
	    s += "<tr><td>";
	    // display the page name list
	    s += "<table width=100 height=200 border=0 cellpadding=5 cellspacing=5 bgcolor=#F0F0FF>";
	    s += "<tr><td align=center>";
	    s += "<font size=\"-2\"><u>Page Progress</u></font></td></tr>";

	    for (int i = 0; i < pages.length; i++) {
		s += "<tr>";
		String page_status = (String) completed_pages.get(pages[i].id);

		if (page_status != null
			&& page_status.equalsIgnoreCase("Completed"))
		    // completed pages
		    s += "<td bgcolor='#99CCFF' align=left colspan=1>";
		else if (page_status != null
			&& page_status.equalsIgnoreCase("Current"))
		    // current page - will be highlighted in yellow
		    s += "<td bgcolor='#FFFF00' align=left colspan=1>";
		else
		    s += "<td>";
		s += "<font size=\"-2\">";
		s += "<b>" + pages[i].title + "</b></font>";
		s += "</td></tr>";
	    }
	    s += "</table>";
	    s += "</td></tr></table>";
	} catch (Exception e) {
	    WISE_Application.log_error(
		    "WISE - SURVEY - PRINT PROGRESS WITHOUT LINK: "
			    + e.toString() + " --> " + currentPage.toString()
			    + ", " + completed_pages.toString(), null);
	}
	return s;
    }

    /**
     * display the submenu on the left side of frame to show the survey progress
     * - the submenu will have the links to let the user review the completed
     * pages
     */
    public String print_progress(Page currentPage) {
	String s = "";
	try {
	    s += "<html><head>";
	    s += "<STYLE> ";
	    s += "a:hover {color: #aa0000; text-decoration: underline}";
	    s += "a:link {color: #003366; text-decoration: none}";
	    s += "a:visited {color: #003366; text-decoration: none}";
	    s += "</STYLE>";
	    s += "</head>";
	    s += "<LINK href='" + "styleRender?app=" + study_space.study_name
		    + "&css=style.css" + "' type=text/css rel=stylesheet>";
	    s += "<body>";
	    // s += "<body text='#000000' bgcolor='#FFFFCC' >";
	    s += "<font face='Verdana, Arial, Helvetica, sans-serif'>";
	    s += "<table width=100% cellpadding=0 cellspacing=0>";
	    s += "<tr><td align=center valign=top>";
	    // changing the path to WISE_Application.images_path
	    s += "<img src='" + "imageRender?app=" + study_space.study_name
		    + "&img=" + logo_name + "' border=0></td></tr>";
	    s += "<tr><td>&nbsp;</td></tr>";
	    s += "<tr><td>";
	    s += "<a href=\"javascript:top.mainFrame.form.document.mainform.action.value='interrupt';";
	    s += "top.mainFrame.form.document.mainform.submit();\" target=\"_top\">";
	    s += "<font size=\"-1\"><center>Click here to <B>SAVE</B> your answers ";
	    s += "if you need to pause</center></font></a>";
	    s += "</td></tr>";
	    s += "<tr><td><font size=\"-2\">&nbsp;</font></td></tr>";
	    s += "<tr><td>";

	    s += "<table width=100 height=200 border=0 cellpadding=5 cellspacing=5 bgcolor=#F0F0FF>";
	    s += "<tr><td align=center>";
	    s += "<font size=\"-2\"><u>Survey Pages</u></font></td></tr>";

	    int idx = get_page_index(currentPage.id);
	    for (int i = 0; i < pages.length; i++) {
		s += "<tr>";
		if (i != idx) {
		    s += "<td bgcolor='#99CCFF' align=left colspan=1>";
		    s += "<a href=\"javascript:top.mainFrame.form.document.mainform.action.value='linkpage';";
		    s += "top.mainFrame.form.document.mainform.nextPage.value='"
			    + pages[i].id + "';";
		    s += "top.mainFrame.form.document.mainform.submit();\" target=\"_top\">";
		    s += "<font size=-2><b>" + pages[i].title
			    + "</b></font></a>";
		} else {
		    s += "<td bgcolor='#FFFF00' align=left colspan=1>";
		    s += "<font size=-2><b>" + pages[i].title + "</b></font>";
		}
		s += "</td></tr>";
	    }
	    s += "</table>";
	    s += "</td></tr></table>";
	} catch (Exception e) {
	    WISE_Application.log_error(
		    "WISE - SURVEY - PRINT PROGRESS WITH LINKS: "
			    + e.toString() + " --> " + currentPage.toString(),
		    null);
	}
	return s;
    }

    /** search by ID, return a specific Response_Set */
    public Response_Set get_response_set(String id) {
	Response_Set a = (Response_Set) response_sets.get(id);
	return a;
    }

    /** search by ID, return a specific Subject_Set */
    public Subject_Set get_subject_set(String id) {
	Subject_Set ss = (Subject_Set) subject_sets.get(id);
	return ss;
    }

    /** search by ID, return a specific Translation */
    public Translation_Item get_translation_item(String id) {
	Translation_Item t = (Translation_Item) translation_items.get(id);
	return t;
    }

    /** search by page ID, return the page index in the page array */
    public int get_page_index(String id) {
	for (int i = 0; i < pages.length; i++) {
	    if (pages[i].id.equalsIgnoreCase(id))
		return i;
	}
	return -1;
    }

    /** search by page ID, return the page the page array */
    public Page get_page(String id) {
	int i = get_page_index(id);
	if (i != -1)
	    return pages[i];
	else
	    return null;
    }

    /**
     * search by page index, check if the page is the last page in the page
     * array
     */
    public boolean is_last_page(int idx) {
	int i = pages.length - 1;
	if (idx == i)
	    return true;
	else
	    return false;
    }

    /** search by page ID, check if the page is the last page in the page array */
    public boolean is_last_page(String id) {
	return is_last_page(get_page_index(id));
    }

    /** search by page ID, get the next page from the page array */
    public Page next_page(String id) {
	int idx = get_page_index(id);
	if (idx < 0)
	    return null;
	idx++;
	if (idx >= pages.length)
	    return null;
	else
	    return pages[idx];
    }

    /** search by page index, return the page ID from the page array object */
    /*
     * public String get_page_id(int idx) { if (idx > pages.length) return null;
     * else return pages[idx].id; }
     */

    /** returns the previous page after the page index */
    /*
     * public Page previous_page(int indx) { if(indx <= 0 || indx > pages.length
     * ) return null; else return pages[indx-1]; }
     */
    /** search by page ID, get the previous page from the page array */
    /*
     * public Page previous_page(String id) { return
     * previous_page(get_page_index(id)); }
     */

    /** returns the page for that page index */
    /*
     * public Page get_page(int idx) { if (idx <= pages.length) return
     * pages[idx]; else return null; }
     */

    /**
     * get table creation syntax for the OLD version of data table -- RETIRABLE
     * public String get_table_syntax(Statement stmt) { String create_str="";
     * try { //get the 2nd max internal id in the survey table with the same
     * survey ID //it is the index for locating the old survey data table String
     * sql = "select max(internal_id) from "+
     * "(select * from surveys where id='"+id+"' and internal_id <> "+
     * "(select max(internal_id) from surveys where id='"+id+"')) as a;";
     * boolean dbtype = stmt.execute(sql); ResultSet rs = stmt.getResultSet();
     * //get the value from the table column of create_syntax if(rs.next()) {
     * String sqlc =
     * "select create_syntax from surveys where internal_id="+rs.getString(1);
     * boolean dbtypec = stmt.execute(sqlc); ResultSet rsc =
     * stmt.getResultSet(); if(rsc.next())
     * create_str=rsc.getString("create_syntax"); }
     * 
     * if(create_str==null) create_str=""; } catch (Exception e) {
     * Study_Util.email_alert("SURVEY - GET DATA TABLE SYNTAX: "+e.toString());
     * }
     * 
     * return create_str; }
     */

    /*
     * These functions concatenate together all Field Names and valueTypes in
     * survey -- used by Data_Bank to setup database
     */
    public String[] get_fieldList() {
	String[] mainFields = new String[total_item_count];
	int main_i = 0;
	for (int page_i = 0; page_i < pages.length; page_i++) {
	    String[] pageFields = pages[page_i].get_fieldList();
	    for (int field_i = 0; field_i < pageFields.length; field_i++)
		mainFields[main_i++] = pageFields[field_i];
	}
	return mainFields;
    }

    public char[] get_valueTypeList() {
	char[] mainFieldTypes = new char[total_item_count];
	int main_i = 0;
	for (int page_i = 0; page_i < pages.length; page_i++) {
	    char[] pageTypes = pages[page_i].get_valueTypeList();
	    for (int field_i = 0; field_i < pageTypes.length; field_i++)
		mainFieldTypes[main_i++] = pageTypes[field_i];
	}
	return mainFieldTypes;
    }

    /*
     * Pralav: the following function copy the above two functions for the
     * repeating sets
     */
    public ArrayList<Repeating_Item_Set> get_repeating_item_sets() {
	ArrayList<Repeating_Item_Set> survey_repeating_item_sets = new ArrayList<Repeating_Item_Set>();
	for (int page_i = 0; page_i < pages.length; page_i++) {
	    ArrayList<Repeating_Item_Set> page_repeating_item_sets = pages[page_i]
		    .get_repeating_item_sets();

	    for (Repeating_Item_Set page_repeat_set_instance : page_repeating_item_sets) {
		survey_repeating_item_sets.add(page_repeat_set_instance);
	    }
	}

	return survey_repeating_item_sets;
    }

    /**
     * get table creation syntax for the new data table public String
     * create_table_syntax() throws SQLException { String create_str=""; for
     * (int i = 0; i < pages.length; i++) create_str += pages[i].create_table();
     * return create_str; }
     */

    /** prints brief dump of survey structure */

    public String toString() {
	String s = "<p><b>SURVEY</b><br>";
	s += "ID: " + id + "<br>";
	s += "Title: " + title + "<br>";
	// s += "user_data_page: "+ user_data_page +"<br>";

	for (int i = 0; i < pages.length; i++)
	    s += pages[i].toString();
	if (invitee_fields != null) {
	    s += "Invitee Fields ref'd: ";
	    for (int i = 0; i < invitee_fields.length; i++)
		s += invitee_fields[i] + "; ";
	}
	s += "</p>";
	return s;
    }

    /** prints the overview listing for a survey */
    /*
     * public String print_overview() { String s =
     * "<body text='#000000' bgcolor='#FFFFCC' >"; s +=
     * "<font face='Verdana, Arial, Helvetica, sans-serif'>";
     * 
     * s += "<table cellpadding=5>"; s += "<tr><td>"; //s +=
     * "<a href=\""+Study_Space.servlet_root+"logout\" target=\"_top\">"; s +=
     * "<a href=\""+"logout\" target=\"_top\">"; s +=
     * "<font size=\"-1\"><center>Please <B>LOGOUT</B> when finished</center></font></a>"
     * ; s += "</td></tr>";
     * 
     * s += "<tr><td>"; s +=
     * "<p><font size=\"-2\"><i>Jump to results for any page:</i></font>"; s +=
     * "</td></tr>";
     * 
     * for (int i = 0; i < pages.length; i++) { s += "<tr><td>"; s +=
     * "<font size=\"-2\">"; //s +=
     * "<a href='"+Study_Space.servlet_root+"view_results?page="
     * +pages[i].id+"' target='form'>"; s +=
     * "<a href='view_results?page="+pages[i].id+"' target='form'>"; s +=
     * "<b>"+pages[i].title+"</b></a></font>"; s += "</td></tr>"; } s +=
     * "</table>"; return s; }
     */
}

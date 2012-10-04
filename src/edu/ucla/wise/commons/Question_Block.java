package edu.ucla.wise.commons;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is a subclass of Page_Item and represents a question block on the
 * page
 */

public class Question_Block extends Page_Item {
    public static String Sql_datatype = "int(6)";

    /** Instance Variables */
    public String instructions = "NONE";
    public Response_Set response_set;
    public String responseSet_ID;
    // public Subject_Set subject_set;
    String SubjectSet_name;

    public ArrayList<Stem_Differentiator> stems = new ArrayList<Stem_Differentiator>();
    public ArrayList<String> stem_fieldNames = new ArrayList<String>();
    // P public String[] stems;
    // P public String[] stem_fieldNames;

    // hasPrecondition is a flag to check the precondition attribute of a
    // subject set reference
    public boolean hasPrecondition = false;
    public boolean hasSubjectSetRef = false;
    public Condition cond;

    /** constructor: parse a question block node from the XML DOM node */
    public Question_Block(Node n) {
	// assign the attributes of the page item
	super(n);
	try {
	    NodeList nodelist = n.getChildNodes();
	    // P int num_stems = 0;

	    /*
	     * P // parse subject stem // count the total number of the subject
	     * stems for (int i = 0; i < nodelist.getLength(); i++) { if
	     * (nodelist.item(i).getNodeName().equalsIgnoreCase("Sub_Stem"))
	     * num_stems++; } // declare the string array for stem name & value
	     * based on the // subject stem size stems = new String[num_stems];
	     * stem_fieldNames = new String[num_stems];
	     */

	    // get the sub stem name & value and assign them to the two arrays
	    for (int i = 0, j = 0; i < nodelist.getLength(); i++) {
		if (nodelist.item(i).getNodeName().equalsIgnoreCase("Sub_Stem")
			|| nodelist.item(i).getNodeName()
				.equalsIgnoreCase("Sub_Head")) {
		    Node node = nodelist.item(i);
		    Transformer transformer = TransformerFactory.newInstance()
			    .newTransformer();
		    StringWriter sw = new StringWriter();
		    transformer.transform(new DOMSource(node),
			    new StreamResult(sw));
		    String stem_type = nodelist.item(i).getNodeName()
			    .toUpperCase();
		    // P stems[j] = sw.toString();
		    stems.add(this.new Stem_Differentiator(stem_type, sw
			    .toString()));
		    // each stem name is the question name plus the index number
		    // P stem_fieldNames[j] = name + "_" + (j + 1);
		    stem_fieldNames.add(name + "_" + (j + 1));
		    j++;
		}
	    }
	    // parse other nodes: response set, response set ref, subject set
	    // ref, stem etc.
	    for (int i = 0; i < nodelist.getLength(); i++) {
		// parse the response set
		if (nodelist.item(i).getNodeName()
			.equalsIgnoreCase("Response_Set")) {
		    responseSet_ID = nodelist.item(i).getAttributes()
			    .getNamedItem("ID").getNodeValue();
		}
		// parse the response set reference
		if (nodelist.item(i).getNodeName()
			.equalsIgnoreCase("Response_Set_Ref")) {
		    responseSet_ID = nodelist.item(i).getAttributes()
			    .getNamedItem("Response_Set").getNodeValue();
		}
		// parse the stem
		if (nodelist.item(i).getNodeName().equalsIgnoreCase("Stem")) {
		    Node node = nodelist.item(i);
		    Transformer transformer = TransformerFactory.newInstance()
			    .newTransformer();
		    StringWriter sw = new StringWriter();
		    transformer.transform(new DOMSource(node),
			    new StreamResult(sw));
		    instructions = sw.toString();
		}
		// parse the precondition set for the question block
		// note: this precondition is not the precondition set for child
		// node - subject set reference
		if (nodelist.item(i).getNodeName()
			.equalsIgnoreCase("Precondition")) {
		    // create the condition object
		    cond = new Condition(nodelist.item(i));
		}
	    }
	} catch (Exception e) {
	    WISE_Application.log_error(
		    "WISE - QUESTION BLOCK: " + e.toString(), null);
	    return;
	}
    }

    public void knitRefs(Survey mySurvey) {
	response_set = mySurvey.get_response_set(responseSet_ID);
	html = make_html();
    }

    /** count number of fields/options in the question block */
    public int countFields() {
	// the number of fields is the total number of subject stems
	// P return stems.length;
	return stems.size();
    }

    // TODO (low) enable multiselect question blocks by tacking on response set
    // names to field names
    public String[] listFieldNames() {
	// P return stem_fieldNames;
	return (String[]) stem_fieldNames.toArray(new String[stem_fieldNames
		.size()]);
    }

    /**
     * get the table creation syntax (the series subject set table) for subject
     * set references
     */
    // public void create_subjectset_table()
    // {
    // try
    // {
    // //connect to the database
    // Connection conn = page.survey.getDBConnection();
    // Statement stmt = conn.createStatement();
    // String sql="";
    //
    // //first check if this subject set table already exists
    // boolean found=false;
    // ResultSet rs=stmt.executeQuery("show tables");
    // while(rs.next())
    // {
    // if(rs.getString(1).equalsIgnoreCase(page.survey.id +
    // "_"+SubjectSet_name+"_data"))
    // {
    // found=true;
    // break;
    // }
    // }
    // if(!found)
    // {
    // //if the subject set table doesn't exist, then create the new table
    // sql = "CREATE TABLE "+ page.survey.id + "_"+SubjectSet_name+ "_data (";
    // sql += "invitee int(6) not null,";
    // sql += "subject int(6) not null,";
    // sql += " "+name+" int(6),";
    // sql += "INDEX (invitee),";
    // sql += "FOREIGN KEY (invitee) REFERENCES invitee(id) ON DELETE CASCADE";
    // sql += ") TYPE=INNODB ";
    // boolean dbtype = stmt.execute(sql);
    // }
    // else
    // {
    // //if the table already exists, then check if the column (question block
    // name) exists
    // sql="describe "+ page.survey.id + "_"+SubjectSet_name+ "_data ";
    // boolean result = stmt.execute(sql);
    // rs = stmt.getResultSet();
    // boolean column_exist=false;
    // while(rs.next())
    // {
    // if(name.equalsIgnoreCase(rs.getString(1)))
    // {
    // //find the column
    // column_exist=true;
    // break;
    // }
    // }
    // //if the column doesn't exist
    // //then alter the current table to append a new column representing this
    // question_block
    // if(!column_exist)
    // {
    // sql = "ALTER TABLE "+ page.survey.id + "_"+SubjectSet_name+ "_data ";
    // sql += "ADD "+name+" int(6)";
    // boolean dbtype = stmt.execute(sql);
    // }
    // }
    // stmt.close();
    // conn.close();
    // }
    // catch (Exception e)
    // {
    // Study_Util.email_alert("WISE - QUESTION BLOCK CREATE SUBJECT SET TABLE: "+e.toString());
    // }
    // return;
    // }

    /**
     * Renders the {@link Question_Block} HTML {@link Page_Item}. Renders a
     * static html at the time of loading the survey.
     */
    public String make_html() {
	String s = "";
	int len = response_set.get_size();
	int startV = Integer.parseInt(response_set.startvalue);
	int num = startV;
	String t1, t2;
	int levels = Integer.parseInt(response_set.levels);

	// Print the instruction above the table top

	s += "<p rowspan=2'>";
	if (!instructions.equalsIgnoreCase("NONE"))
	    s += "<br />" + instructions;
	else
	    s += "&nbsp;";
	s += "</p>";

	// cells for question block that doesnt require classified levels
	String no_classified_level_columns = "";
	for (int j = startV, k = 0; j < len + startV; j++, k++)
	    no_classified_level_columns += "<td class=\"header-row\"><center>"
		    + response_set.responses.get(k) + "</center></td>";

	// cells for question block that requires classified levels
	String classified_level_columns = "";
	classified_level_columns += "<td class=\"header-row\" colspan="
		+ levels + " width='60%'>";
	classified_level_columns += "<table cellpadding='3' border='0' width='100%' cellspacing='0'>";
	classified_level_columns += "<tr class='shaded-bg'>";// make one row
							     // 'stead of many
	if (response_set.responses.size() == 2 && levels > 2) {
	    classified_level_columns += "<td class='header-row' align='left'>";
	    classified_level_columns += startV + ". "
		    + response_set.responses.get(0);
	    classified_level_columns += "<td class=\"header-arrows\"align='center' width='10%'>&larr;&rarr;</td>";
	    classified_level_columns += "<td class='header-row' align='right'>";
	    classified_level_columns += (startV + levels - 1) + ". "
		    + response_set.responses.get(1);

	} else {
	    int step = Math.round((levels - 1) / (len - 1));
	    for (int j = 1, k = 0, currentLevel = startV; j <= levels; j++, currentLevel++) {
		int det = (j - 1) % step;
		if (det == 0) {
		    // classified_level_columns += "<tr>";
		    if (j == 1)
			classified_level_columns += "<td align='left'>";
		    else if (j == levels)
			classified_level_columns += "<td align='right'>";
		    else
			classified_level_columns += "<td align='center'>";
		    classified_level_columns += currentLevel + ". "
			    + response_set.responses.get(k);
		    classified_level_columns += "</td>";
		    // classified_level_columns += "</tr>";
		    k++;
		}
	    }
	}
	classified_level_columns += "</tr>";// moved row closing to here
	classified_level_columns += "</table>";
	classified_level_columns += "</td>";
	classified_level_columns += "</tr>";
	classified_level_columns += "<tr class=\"header-row shaded-bg\">";
	classified_level_columns += "<td class=\"header-row\">";
	classified_level_columns += "&nbsp;";
	classified_level_columns += "</td>";
	for (int j = startV; j < levels + startV; j++)
	    classified_level_columns += "<td class=\"header-row\"><center>" + j
		    + "</center></td>";

	int row_background_color_index = 0; // to specify background of a row
	// render row for each stem of the question block
	for (int i = 0; i < stems.size(); i++) {
	    boolean is_Sub_Head = stems.get(i).stem_type
		    .equalsIgnoreCase("Sub_Head");
	    boolean is_Sub_Stem = stems.get(i).stem_type
		    .equalsIgnoreCase("Sub_Stem");
	    if (i == 0) {
		// open the question block table
		s += "<table cellspacing='0' cellpadding='7' width=100%' border='0'>";

		// render header row --
		// if the question block doesn't require classified level
		if (levels == 0) {
		    s += "<tr class=\"shaded-bg\">";
		    s += "<td class=\"header-row sub_head\">";
		    if (is_Sub_Head) {
			s += stems.get(i).stem_value;
		    } else
			s += "&nbsp;";
		    s += "</td>";
		    s += no_classified_level_columns;
		    s += "</tr>";
		    row_background_color_index++;
		    if (is_Sub_Stem) {
			if (row_background_color_index++ % 2 == 0)
			    s += "<tr class=\"shaded-bg\">";
			else
			    s += "<tr class=\"unshaded-bg\">";
			s += "<td>" + stems.get(i).stem_value + "</td>";
			num = startV;

			for (int j = startV, k = 0; j < len + startV; j++, k++) {
			    if (((String) response_set.values.get(k))
				    .equalsIgnoreCase("-1")) {
				s += "<td><center>";
				s += "<input type='radio' name='"
					+ stem_fieldNames.get(i).toUpperCase()
					+ "' value='" + num + "'>";
				s += "</center></td>";
				num = num + 1;
			    } else {
				s += "<td><center>";
				s += "<input type='radio' name='"
					+ stem_fieldNames.get(i).toUpperCase()
					+ "' value='"
					+ response_set.values.get(k) + "'>";
				s += "</center></td>";
				num = num + 1;
			    }
			}
			s += "</tr>";
		    }
		} // if classified level is required for the question block
		else {
		    if (row_background_color_index++ % 2 == 0)
			s += "<tr class=\"shaded-bg\">";
		    else
			s += "<tr class=\"unshaded-bg\">";
		    s += "<td class=\"header-row sub_head\">";
		    if (is_Sub_Head) {
			s += stems.get(i).stem_value;
		    } else
			s += "&nbsp;";
		    s += "</td>";
		    s += classified_level_columns;
		    s += "</tr>";
		    if (is_Sub_Stem) {
			if (row_background_color_index++ % 2 == 0)
			    s += "<tr class=\"shaded-bg\">";
			else
			    s += "<tr class=\"unshaded-bg\">";
			s += "<td>" + stems.get(i).stem_value + "</td>";
			num = startV;

			for (int j = 1; j <= levels; j++) {
			    s += "<td><center>";
			    s += "<input type='radio' name='"
				    + stem_fieldNames.get(i).toUpperCase()
				    + "' value='" + num + "'>";
			    s += "</center></td>";
			    num = num + 1;
			}
			s += "</tr>";
		    }

		}
	    } else {
		if (row_background_color_index++ % 2 == 0)
		    s += "<tr class=\"shaded-bg\">";
		else
		    s += "<tr class=\"unshaded-bg\">";
		if (is_Sub_Head) {
		    s += "<td class=\"sub_head\">" + stems.get(i).stem_value
			    + "</td>";
		    if (levels == 0) {
			s += no_classified_level_columns;
			s += "</tr>";
		    } // if classified level is required for the question block
		    else {
			s += classified_level_columns;
			s += "</tr>";
		    }
		} else {
		    s += "<td>" + stems.get(i).stem_value + "</td>";
		    num = startV;
		    // if the question block doesn't require classified level
		    if (levels == 0) {
			for (int j = startV, k = 0; j < len + startV; j++, k++) {
			    if (((String) response_set.values.get(k))
				    .equalsIgnoreCase("-1")) {
				s += "<td><center>";
				s += "<input type='radio' name='"
					+ stem_fieldNames.get(i).toUpperCase()
					+ "' value='" + num + "'>";
				s += "</center></td>";
				num = num + 1;
			    } else {
				s += "<td><center>";
				s += "<input type='radio' name='"
					+ stem_fieldNames.get(i).toUpperCase()
					+ "' value='"
					+ response_set.values.get(k) + "'>";
				s += "</center></td>";
				num = num + 1;
			    }
			}
		    }
		    // if classified level is required for the question block
		    else {
			for (int j = 1; j <= levels; j++) {
			    s += "<td><center>";
			    s += "<input type='radio' name='"
				    + stem_fieldNames.get(i).toUpperCase()
				    + "' value='" + num + "'>";
			    s += "</center></td>";
			    num = num + 1;
			}
		    }
		}
	    }

	}
	s += "</table>";
	return s;
    }

    /** print survey for a question block - used for admin tool: print survey */
    public String print_survey() {
	String s = "";
	int len = response_set.get_size();
	int startV = Integer.parseInt(response_set.startvalue);
	int num = startV;
	String t1, t2;
	int levels = Integer.parseInt(response_set.levels);

	// render top part of the question block
	if (levels == 0) {
	    s += "<table cellspacing='0' cellpadding='7' width=100%' border='0'>";
	    s += "<tr bgcolor=#FFFFFF><td>";
	    if (!instructions.equalsIgnoreCase("NONE"))
		s += "<b>" + instructions + "</b>";
	    else
		s += "&nbsp;";
	    s += "</td>";
	    for (int j = startV, i = 0; j < len + startV; j++, i++)
		s += "<td align=center>" + response_set.responses.get(i)
			+ "</td>";
	    s += "</tr>";
	} else {
	    s += "<table cellspacing='0' cellpadding='7' width=100%' border='0'>";
	    s += "<tr bgcolor=#FFFFFF>";
	    s += "<td rowspan=2 width='70%'>";
	    if (!instructions.equalsIgnoreCase("NONE"))
		s += "<b>" + instructions + "</b>";
	    else
		s += "&nbsp;";
	    s += "</td>";

	    s += "<td colspan=" + levels + " width='20%'>";
	    s += "<table cellpadding='0' border='0' width='100%'>";
	    int step = Math.round((levels - 1) / (len - 1));
	    int k = 1;
	    for (int j = 1, i = 0, l = startV; j <= levels; j++, l++) {
		int det = (j - 1) % step;
		if (det == 0) {
		    s += "<tr>";
		    if (j == 1)
			s += "<td align='left'>";
		    else if (j == levels)
			s += "<td align='right'>";
		    else
			s += "<td align='center'>";
		    s += l + ". " + response_set.responses.get(i);
		    s += "</td></tr>";
		    i++;
		}
	    }
	    s += "</table>";
	    s += "</td>";
	    s += "</tr>";

	    s += "<tr bgcolor=#FFFFFF>";
	    for (int j = startV; j < levels + startV; j++)
		s += "<td><center>" + j + "</center></td>";
	    s += "</tr>";
	}

	// render each stem of the question block
	for (int i = 0; i < stems.size(); i++) {
	    if (i % 2 == 0)
		s += "<tr bgcolor=#CCCCCC>";
	    else
		s += "<tr bgcolor=#FFFFFF>";
	    s += "<td>" + stems.get(i).stem_value + "</td>";
	    num = startV;
	    if (levels == 0) {
		for (int j = startV, k = 0; j < len + startV; j++, k++) {
		    s += "<td align=center>";
		    s += "<img src='" + WISE_Application.rootURL + "/WISE"
			    + "/" + WiseConstants.SURVEY_APP + "/"
			    + "imageRender?img=checkbox.gif' border='0'></a>";
		    s += "</td>";
		    num = num + 1;
		}
	    } else {
		for (int j = 1; j <= levels; j++) {
		    s += "<td align=center>";
		    s += "<img src='" + WISE_Application.rootURL + "/WISE"
			    + "/" + WiseConstants.SURVEY_APP + "/"
			    + "imageRender?img=checkbox.gif' border='0'></a>";
		    s += "</td>";
		    num = num + 1;
		}
	    }
	}
	s += "</table>";
	return s;
    }

    // public Hashtable read_form(Hashtable params)
    // {
    // Hashtable answers = new Hashtable();
    // for (int i = 0; i < stems.length; i++)
    // {
    // String fieldName = stem_fieldNames[i].toUpperCase();
    // String answerVal = (String) params.get(fieldName);
    // if (answerVal.equalsIgnoreCase(""))
    // answerVal = null;
    // answers.put(fieldName, answerVal);
    // }
    // return answers;
    // }

    /**
     * read out the question field name & value from the hashtable and put them
     * into two arrays respectively
     */

    // Old version
    // public int read_form(Hashtable params, String[] fieldNames, String[]
    // fieldValues, int fieldIndex, User theUser)
    // {
    // //check if the question block has the subject set reference
    // int index_len = 0;
    // //if the question block doesn't have the subject set reference
    // //then read the data from the hashtable param and put into the field name
    // & value arrays
    // if(!hasSubjectSetRef)
    // {
    // for (int i = 0; i < stems.length; i++)
    // {
    // fieldNames[fieldIndex] = stem_fieldNames[i].toUpperCase();
    // fieldValues[fieldIndex] = (String)
    // params.get(stem_fieldNames[i].toUpperCase());
    // fieldIndex++;
    // }
    // index_len=stems.length;
    // }
    // //if the question block has the subject set reference, insert or update
    // the table of subject set
    // else
    // {
    // String sql="";
    //
    // try
    // {
    // //connect to the database
    // Connection conn = page.survey.getDBConnection();
    // Statement stmt = conn.createStatement();
    // //firstly check if the user record exists in the table of page_submit
    // sql =
    // "SELECT * from page_submit where invitee = "+theUser.id+" AND survey = '"+page.survey.id+"'";
    // boolean dbtype = stmt.execute(sql);
    // ResultSet rs = stmt.getResultSet();
    // boolean user_data_exists = rs.next();
    //
    // //then check if a user record exists in table of subject set
    // for (int i = 0; i < stems.length; i++)
    // {
    // sql = "SELECT * from "+page.survey.id+"_"+SubjectSet_name+"_data where ";
    // sql += "invitee = " +theUser.id+" and subject=";
    // sql +=
    // stem_fieldNames[i].substring((stem_fieldNames[i].lastIndexOf("_")+1));
    // dbtype = stmt.execute(sql);
    // rs = stmt.getResultSet();
    // user_data_exists = rs.next();
    //
    // Statement stmt2 = conn.createStatement();
    // //read out the user's new data from the hashtable params
    // String s_new = (String) params.get(stem_fieldNames[i].toUpperCase());
    //
    // //note that s_new could be null - seperate the null value with the 0
    // value
    // s_new = Study_Util.fixquotes(s_new);
    // if (s_new.equalsIgnoreCase(""))
    // s_new = "NULL";
    //
    // //if both tables (page_submit & subject set) have the user's data
    // if (user_data_exists)
    // {
    // String s = rs.getString(name);
    // //compare with the new user data, update the subject set data if the old
    // value has been changed
    // if ((s==null && !s_new.equalsIgnoreCase("NULL")) || (s!=null &&
    // !s.equalsIgnoreCase(s_new)))
    // {
    // //create UPDATE statement
    // sql = "update "+page.survey.id+"_"+SubjectSet_name+"_data set ";
    // sql += name + " = " + s_new;
    // sql += " where invitee = "+theUser.id+" and subject=";
    // sql +=
    // stem_fieldNames[i].substring((stem_fieldNames[i].lastIndexOf("_")+1));
    // dbtype = stmt2.execute(sql);
    //
    // String s1;
    // if (s != null)
    // s1 = Study_Util.fixquotes(s);
    // else
    // s1 = "null";
    // //check if the user's record exists in the table of update_trail, update
    // the data there as well
    // sql =
    // "select * from update_trail where invitee="+theUser.id+" and survey='"+page.survey.id;
    // sql +=
    // "' and page='"+page.id+"' and ColumnName='"+stem_fieldNames[i].toUpperCase()+"'";
    // dbtype = stmt2.execute(sql);
    // ResultSet rs2 = stmt2.getResultSet();
    // if(rs2.next())
    // {
    // //update the records in the update trail
    // if(!s1.equalsIgnoreCase(s_new))
    // {
    // sql = "update update_trail set OldValue='"+s1+"', CurrentValue='"+s_new;
    // sql
    // +="', Modified=now() where invitee="+theUser.id+" and survey='"+page.survey.id;
    // sql
    // +="' and page='"+page.id+"' and ColumnName='"+stem_fieldNames[i].toUpperCase()+"'";
    // }
    // }
    // //insert new record if it doesn't exist in the table of update_trail
    // else
    // {
    // sql =
    // "insert into update_trail (invitee, survey, page, ColumnName, OldValue, CurrentValue)";
    // sql += " values ("+theUser.id+",'"+page.survey.id+"','"+page.id+"','";
    // sql += stem_fieldNames[i].toUpperCase()+"','"+s1+"', '"+s_new+"')";
    // }
    // dbtype = stmt2.execute(sql);
    // }
    // }
    // //if no user's record exists in both tables
    // else
    // {
    // //create a insert statement to insert this record in the table of subject
    // set
    // sql = "insert into "+page.survey.id+"_"+SubjectSet_name+"_data ";
    // sql += "(invitee, subject, "+name+") ";
    // sql += "values ("+theUser.id+",'";
    // sql +=
    // Study_Util.fixquotes(stem_fieldNames[i].substring((stem_fieldNames[i].lastIndexOf("_")+1)));
    // sql += "', "+s_new+")";
    // dbtype = stmt2.execute(sql);
    // //and insert record into the table of update_trail as well
    // sql =
    // "insert into update_trail (invitee, survey, page, ColumnName, OldValue, CurrentValue)";
    // sql += " values ("+theUser.id+",'"+page.survey.id+"','"+page.id+"','";
    // sql += stem_fieldNames[i].toUpperCase()+"','null', '"+s_new+"')";
    // dbtype = stmt2.execute(sql);
    // }
    // stmt2.close();
    // } //end of for loop
    // stmt.close();
    // conn.close();
    // } //end of try
    // catch (Exception e)
    // {
    // Study_Util.email_alert("WISE - QUESTION BLOCK ["+page.id+"] READ FORM ("+sql+"): "+e.toString());
    // }
    // } //end of else
    // return index_len;
    // }

    protected String render_QB_header() {
	String s = "";
	int len = response_set.get_size();
	int startV = Integer.parseInt(response_set.startvalue);
	int levels = Integer.parseInt(response_set.levels);

	// render top part of the question block
	if (levels == 0) {
	    s += "<table cellspacing='0' cellpadding='7' width=100%' border='0'>";
	    s += "<tr>";
	    s += "<td class=\"header-row\">";
	    if (!instructions.equalsIgnoreCase("NONE"))
		s += "<b>" + instructions + "</b>";
	    else
		s += "&nbsp;";
	    s += "</td>";
	    for (int j = startV, i = 0; j < len + startV; j++, i++)
		s += "<td class=\"header-row\"><center>"
			+ response_set.responses.get(i) + "</center></td>";
	    s += "</tr>";
	} else {
	    s += "<table cellspacing='0' cellpadding='7' width=100%' border='0'>";
	    s += "<tr>";
	    s += "<td class=\"header-row\" rowspan=2 width='70%'>";
	    if (!instructions.equalsIgnoreCase("NONE"))
		s += "<b>" + instructions + "</b>";
	    else
		s += "&nbsp;";
	    s += "</td>";

	    s += "<td class=\"header-row\" colspan=" + levels + " width='20%'>";
	    s += "<table cellpadding='0' border='0' width='100%'>";
	    int step = Math.round((levels - 1) / (len - 1));
	    for (int j = 1, i = 0, l = startV; j <= levels; j++, l++) {
		int det = (j - 1) % step;
		if (det == 0) {
		    s += "<tr>";
		    if (j == 1)
			s += "<td align='left'>";
		    else if (j == levels)
			s += "<td align='right'>";
		    else
			s += "<td align='center'>";
		    s += l + ". " + response_set.responses.get(i);
		    s += "</td></tr>";
		    i++;
		}
	    }
	    s += "</table>";
	    s += "</td>";
	    s += "</tr>";

	    s += "<tr class=\"header-row\">";
	    for (int j = startV; j < levels + startV; j++)
		s += "<td><center>" + j + "</center></td>";
	    s += "</tr>";
	}
	return s;
    }

    /** render results for a question block */
    public String render_results(Page pg, Data_Bank db, String whereclause,
	    Hashtable data) {

	int levels = Integer.valueOf(response_set.levels).intValue();
	int startValue = Integer.valueOf(response_set.startvalue).intValue();
	// display the ID of the question
	String s = "<center><table width=100%><tr><td align=right>";
	s += "<span class='itemID'>" + this.name
		+ "</span></td></tr></table><br>";

	// display the question block
	s += "<table cellspacing='0' cellpadding='1' bgcolor=#FFFFF5 width=600 border='1'>";
	s += "<tr><td bgcolor=#BA5D5D rowspan=2 width='60%'>";
	s += "<table><tr><td width='95%'>";
	// display the instruction if it has
	if (!instructions.equalsIgnoreCase("NONE"))
	    s += "<b>" + instructions + "</b>";
	else
	    s += "&nbsp;";

	s += "</td><td width='5%'>&nbsp;</td></tr></table></td>";

	String t1, t2;
	// display the level based on the size of the question block
	if (levels == 0) {
	    s += "<td colspan=" + response_set.responses.size()
		    + " width='40%'>";
	    s += "<table bgcolor=#FFCC99 width=100% cellpadding='1' border='0'>";

	    for (int j = 0; j < response_set.responses.size(); j++) {

		t2 = String.valueOf(j + startValue);
		t1 = (String) response_set.responses.get(j);
		s += "<tr>";

		if (j == 0)
		    s += "<td align=left>";
		else if ((j + 1) == response_set.responses.size())
		    s += "<td align=right>";
		else
		    s += "<td align=center>";
		s += t2 + ". " + t1 + "</td>";
		s += "</tr>";
	    }
	    s += "</table>";
	    s += "</td>";
	    s += "</tr>";
	    int width = 40 / response_set.responses.size();

	    for (int j = 0; j < response_set.responses.size(); j++) {

		t2 = String.valueOf(j + startValue);
		s += "<td bgcolor=#BA5D5D width='" + width + "%'><b><center>"
			+ t2 + "</center></b></td>";
	    }
	}
	// display the classified level
	else {
	    s += "<td colspan=" + levels + " width='40%'>";
	    s += "<table bgcolor=#FFCC99 cellpadding='0' border='0' width='100%'>";
	    // calculate the step between levels
	    int step = Math.round((levels - 1)
		    / (response_set.responses.size() - 1));

	    for (int j = 1, i = 0, l = startValue; j <= levels; j++, l++) {
		int det = (j - 1) % step;
		if (det == 0) {
		    s += "<tr>";
		    if (j == 1)
			s += "<td align='left'>";
		    else if (j == levels)
			s += "<td align='right'>";
		    else
			s += "<td align='center'>";
		    s += l + ". " + response_set.responses.get(i);
		    s += "</td></tr>";
		    i++;
		}
	    }
	    s += "</table>";
	    s += "</td>";
	    s += "</tr>";

	    int width = 40 / levels;
	    for (int j = 0; j < levels; j++) {
		t2 = String.valueOf(j + startValue);
		s += "<td bgcolor=#BA5D5D width='" + width + "%'><b><center>"
			+ t2 + "</center></b></td>";
	    }
	}
	s += "</tr>";

	// display each of the stems on the left side of the block
	for (int i = 0; i < stems.size(); i++) {
	    s += "<tr>";
	    int tnull = 0;
	    int t = 0;
	    float avg = 0;
	    Hashtable h1 = new Hashtable();
	    // get the user's conducted data from the hashtable
	    String subj_ans = (String) h1.get(stem_fieldNames.get(i)
		    .toUpperCase());

	    try {
		// connect to the database
		Connection conn = pg.survey.getDBConnection();
		Statement stmt = conn.createStatement();

		// if the question block doesn't have the subject set ref
		String sql = "";
		if (!hasSubjectSetRef) {
		    // get values from the survey data table
		    // count total number of the users who have the same answer
		    // level
		    sql = "select " + stem_fieldNames.get(i)
			    + ", count(distinct s.invitee) from "
			    + pg.survey.id
			    + "_data as s, page_submit as p where ";
		    sql += "p.invitee=s.invitee and p.survey='" + pg.survey.id
			    + "'";
		    sql += " and p.page='" + pg.id + "'";
		    if (!whereclause.equalsIgnoreCase(""))
			sql += " and s." + whereclause;
		    sql += " group by " + stem_fieldNames.get(i);
		}
		// if the question block has the subject set ref
		else {
		    // get the user's conducted data from the table of subject
		    // set
		    String user_id = (String) data.get("invitee");
		    if (user_id != null && !user_id.equalsIgnoreCase("")) {
			sql = "select "
				+ name
				+ " from "
				+ pg.survey.id
				+ "_"
				+ SubjectSet_name
				+ "_data"
				+ " where subject="
				+ stem_fieldNames.get(i).substring(
					(stem_fieldNames.get(i)
						.lastIndexOf("_") + 1))
				+ " and invitee=" + user_id;
			boolean dbtype = stmt.execute(sql);
			ResultSet rs = stmt.getResultSet();
			if (rs.next()) {
			    subj_ans = rs.getString(1);
			}
		    }
		    // get values from the subject data table
		    // count total number of the users who have the same answer
		    // level
		    sql = "select " + name + ", count(*) from " + pg.survey.id
			    + "_" + SubjectSet_name
			    + "_data as s, page_submit as p";
		    sql += " where s.invitee=p.invitee and p.survey='"
			    + pg.survey.id + "'";
		    sql += " and p.page='" + pg.id + "'";
		    sql += " and s.subject="
			    + stem_fieldNames.get(i)
				    .substring(
					    (stem_fieldNames.get(i)
						    .lastIndexOf("_") + 1));
		    if (!whereclause.equalsIgnoreCase(""))
			sql += " and s." + whereclause;
		    sql += " group by " + name;
		}
		boolean dbtype = stmt.execute(sql);
		ResultSet rs = stmt.getResultSet();
		h1.clear();
		String s1, s2;

		while (rs.next()) {
		    if (rs.getString(1) == null)
			tnull = tnull + rs.getInt(2);
		    else {
			s1 = rs.getString(1);
			s2 = rs.getString(2);
			h1.put(s1, s2);
			t = t + rs.getInt(2);
		    }
		}
		rs.close();

		if (subj_ans == null)
		    subj_ans = "null";

		// if the question block doesn't have the subject set ref
		if (!hasSubjectSetRef) {
		    // get values from the survey data table
		    // calculate the average answer level
		    sql = "select round(avg(" + stem_fieldNames.get(i)
			    + "),1) from " + pg.survey.id
			    + "_data as s, page_submit as p"
			    + " where s.invitee=p.invitee and p.page='" + pg.id
			    + "' and p.survey='" + pg.survey.id + "'";
		    if (!whereclause.equalsIgnoreCase(""))
			sql += " and s." + whereclause;
		}
		// if the question block has the subject set ref
		else {
		    // get values from the subject data table
		    // calculate the average answer level
		    sql = "select round(avg(" + name + "),1) from "
			    + pg.survey.id + "_" + SubjectSet_name
			    + "_data as s, page_submit as p";
		    sql += " where s.invitee=p.invitee and p.survey='"
			    + pg.survey.id + "'";
		    sql += " and p.page='" + pg.id + "'";
		    sql += " and s.subject="
			    + stem_fieldNames.get(i)
				    .substring(
					    (stem_fieldNames.get(i)
						    .lastIndexOf("_") + 1));
		    if (!whereclause.equalsIgnoreCase(""))
			sql += " and s." + whereclause;
		}
		dbtype = stmt.execute(sql);
		rs = stmt.getResultSet();
		if (rs.next())
		    avg = rs.getFloat(1);
		rs.close();

		stmt.close();
		conn.close();
	    } catch (Exception e) {
		WISE_Application
			.log_error(
				"WISE - QUESTION BLOCK RENDER RESULTS: "
					+ e.toString(), e);
		return "";
	    }

	    // display the statistic results
	    String s1;
	    // if classified level is required for the question block
	    if (levels == 0) {
		s += "<td bgcolor=#FFCC99>";
		s += stems.get(i).stem_value + "<p>";
		s += "<div align='right'>";
		s += "mean: </b>" + avg;

		if (tnull > 0) {
		    s += "&nbsp;<b>unanswered:</b>";
		    // if the user's answer is null, highlight the answer
		    // note that if the call came from admin page, this value is
		    // always
		    // highlighted
		    // because the user's data is always to be null
		    if (subj_ans.equalsIgnoreCase("null")) {
			s += "<span style=\"background-color: '#FFFF77'\">"
				+ tnull + "</span>";
		    } else {
			s += tnull;
		    }
		}

		s += "</div>";
		s += "</td>";

		for (int j = 0; j < response_set.responses.size(); j++) {
		    t2 = String.valueOf(j + startValue);
		    if (j < response_set.responses.size())
			t1 = (String) response_set.responses.get(j);
		    int num1 = 0;
		    int p = 0;
		    int p1 = 0;
		    float af = 0;
		    float bf = 0;
		    float cf = 0;
		    String ps, ps1;
		    s1 = (String) h1.get(t2);
		    if (s1 == null) {
			ps = "0";
			ps1 = "0";
		    } else {
			num1 = Integer.parseInt(s1);
			af = (float) num1 / (float) t;
			bf = af * 50;
			cf = af * 100;
			p = Math.round(bf);
			p1 = Math.round(cf);
			ps = String.valueOf(p);
			ps1 = String.valueOf(p1);
		    }
		    // if the user's answer belongs to this answer level,
		    // highlight the image
		    if (subj_ans.equalsIgnoreCase(t2))
			s += "<td bgcolor='#FFFF77'>";
		    else
			s += "<td>";
		    s += "<center>";
		    s += "<img src='" + "imgs/vertical/bar_" + ps + ".gif' ";
		    s += "width='10' height='50'>";
		    s += "<br>" + ps1;
		    s += "</center>";
		    s += "</td>";
		}
	    }
	    // if classified level is required for the question block
	    else {
		s += "<td bgcolor=#FFCC99>";
		s += stems.get(i).stem_value + "<p>";
		s += "<div align='right'>";
		s += "mean: </b>" + avg;

		if (tnull > 0) {
		    s += "&nbsp;<b>unanswered: </b>";
		    // if the user's answer is null, highlight the answer
		    // note that if the call came from admin page, this value is
		    // always
		    // highlighted
		    // because the user's data is always to be null
		    if (subj_ans.equalsIgnoreCase("null")) {
			s += "<span style=\"background-color: '#FFFF77'\">"
				+ tnull + "</span>";
		    } else {
			s += tnull;
		    }
		}

		s += "</div>";
		s += "</td>";
		int step = Math.round((levels - 1)
			/ (response_set.responses.size() - 1));
		for (int j = 0; j < levels; j++) {

		    // t2 = String.valueOf(j);
		    t2 = String.valueOf(j + startValue);
		    if (j < response_set.responses.size())
			t1 = (String) response_set.responses.get(j);
		    int num1 = 0;
		    int p = 0;
		    int p1 = 0;
		    float af = 0;
		    float bf = 0;
		    float cf = 0;
		    String ps, ps1;
		    s1 = (String) h1.get(t2);
		    if (s1 == null) {
			ps = "0";
			ps1 = "0";
		    } else {
			num1 = Integer.parseInt(s1);
			af = (float) num1 / (float) t;
			bf = af * 50;
			cf = af * 100;
			p = Math.round(bf);
			p1 = Math.round(cf);
			ps = String.valueOf(p);
			ps1 = String.valueOf(p1);
		    }
		    // if the User's answer belongs to this answer level,
		    // highlight the image
		    if (subj_ans.equalsIgnoreCase(t2))
			s += "<td bgcolor='#FFFF77'>";
		    else
			s += "<td>";
		    s += "<center>";
		    s += "<img src='" + "imgs/vertical/bar_" + ps + ".gif' ";
		    s += "width='10' height='50'>";
		    s += "<br>" + ps1;
		    s += "</center>";
		    s += "</td>";
		}
	    }
	}

	s += "</table></center>";
	return s;
    }

    protected String render_QB_result_header() {
	String s = "";
	int levels = Integer.valueOf(response_set.levels).intValue();
	int startValue = Integer.valueOf(response_set.startvalue).intValue();
	s += "<span class='itemID'>" + this.name
		+ "</span></td></tr></table><br>";

	// display the question block
	s += "<table cellspacing='0' cellpadding='1' bgcolor=#FFFFF5 width=600 border='1'>";
	s += "<tr><td bgcolor=#BA5D5D rowspan=2 width='60%'>";
	s += "<table><tr><td width='95%'>";
	// display the instruction if it has
	if (!instructions.equalsIgnoreCase("NONE"))
	    s += "<b>" + instructions + "</b>";
	else
	    s += "&nbsp;";

	s += "</td><td width='5%'>&nbsp;</td></tr></table></td>";

	String t1, t2;
	// display the level based on the size of the question block
	if (levels == 0) {
	    s += "<td colspan=" + response_set.responses.size()
		    + " width='40%'>";
	    s += "<table bgcolor=#FFCC99 width=100% cellpadding='1' border='0'>";

	    for (int j = 0; j < response_set.responses.size(); j++) {

		t2 = String.valueOf(j + startValue);
		t1 = (String) response_set.responses.get(j);
		s += "<tr>";

		if (j == 0)
		    s += "<td align=left>";
		else if ((j + 1) == response_set.responses.size())
		    s += "<td align=right>";
		else
		    s += "<td align=center>";
		s += t2 + ". " + t1 + "</td>";
		s += "</tr>";
	    }
	    s += "</table>";
	    s += "</td>";
	    s += "</tr>";
	    int width = 40 / response_set.responses.size();

	    for (int j = 0; j < response_set.responses.size(); j++) {

		t2 = String.valueOf(j + startValue);
		s += "<td bgcolor=#BA5D5D width='" + width + "%'><b><center>"
			+ t2 + "</center></b></td>";
	    }
	}
	// display the classified level
	else {
	    s += "<td colspan=" + levels + " width='40%'>";
	    s += "<table bgcolor=#FFCC99 cellpadding='0' border='0' width='100%'>";
	    // calculate the step between levels
	    int step = Math.round((levels - 1)
		    / (response_set.responses.size() - 1));

	    for (int j = 1, i = 0, l = startValue; j <= levels; j++, l++) {
		int det = (j - 1) % step;
		if (det == 0) {
		    s += "<tr>";
		    if (j == 1)
			s += "<td align='left'>";
		    else if (j == levels)
			s += "<td align='right'>";
		    else
			s += "<td align='center'>";
		    s += l + ". " + response_set.responses.get(i);
		    s += "</td></tr>";
		    i++;
		}
	    }
	    s += "</table>";
	    s += "</td>";
	    s += "</tr>";

	    int width = 40 / levels;
	    for (int j = 0; j < levels; j++) {
		t2 = String.valueOf(j + startValue);
		s += "<td bgcolor=#BA5D5D width='" + width + "%'><b><center>"
			+ t2 + "</center></b></td>";
	    }
	}
	s += "</tr>";
	return s;
    }

    /** returns a comma delimited list of all the fields on a page */
    /*
     * public String list_fields() { String s = ""; for (int i = 0; i <
     * stems.length; i++) s += stem_fieldNames[i]+","; return s; }
     */

    /** prints out the question block information */
    public String toString() {
	String s = "QUESTION BLOCK<br>";
	s += super.toString();

	s += "Instructions: " + instructions + "<br>";
	s += "Response Set: " + response_set.id + "<br>";
	s += "Stems:<br>";

	for (int i = 0; i < stems.size(); i++)
	    s += stem_fieldNames.get(i) + ":" + stems.get(i).stem_value
		    + "<br>";
	if (cond != null)
	    s += cond.toString();
	s += "<p>";
	return s;
    }

    /** private class to store sub_stems and sub_heads together in an ArrayList **/
    public class Stem_Differentiator {
	public String stem_type;
	public String stem_value;

	public Stem_Differentiator(String type, String value) {
	    stem_type = type;
	    stem_value = value;
	}
    }
}

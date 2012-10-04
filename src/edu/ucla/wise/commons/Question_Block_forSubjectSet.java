package edu.ucla.wise.commons;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents a question block that evaluates a subject set
 */

public class Question_Block_forSubjectSet extends Question_Block {

    /** Additional Instance Variables */
    private Subject_Set subject_set;
    private String subjectSet_ID;

    // Condition is a precondition to apply for the subject set
    public Condition ss_cond;

    /**
     * constructor: parse a question block node that has a subject set reference
     */
    // note: subject_set strings copied into inherited "stems" array
    // field names copied into inherieted stem_fieldNames array as
    // QuestionID_SubjectName (with replacemt of spaces and dashes)

    public Question_Block_forSubjectSet(Node n) {
	// assign the parent attributes of the page item
	super(n);
	try {
	    NodeList nodelist = n.getChildNodes();
	    // parse other nodes: response set, response set ref, subject set
	    // ref, stem etc.
	    for (int i = 0; i < nodelist.getLength(); i++) {
		// search out the subject set reference
		if (nodelist.item(i).getNodeName()
			.equalsIgnoreCase("Subject_Set_Ref")) {
		    subjectSet_ID = nodelist.item(i).getAttributes()
			    .getNamedItem("Subject_Set").getNodeValue();
		    // this reference to be resolved in second pass, after full
		    // parse

		    // check for a precondition defined in the subject set
		    // reference node
		    NodeList nodeL = nodelist.item(i).getChildNodes();
		    for (int t = 0; t < nodeL.getLength(); t++) {
			Node NodeC = nodeL.item(t);
			if (NodeC.getNodeName()
				.equalsIgnoreCase("Precondition")) {
			    // parse out the Subject Set condition object
			    ss_cond = new Condition(NodeC);
			}
		    }
		}
	    }
	} catch (Exception e) {
	    WISE_Application.log_error("WISE - QUESTION BLOCK for subjectset: "
		    + e.toString(), null);
	    return;
	}
    }

    public void knitRefs(Survey mySurvey) {
	super.knitRefs(mySurvey);
	try {
	    if (subjectSet_ID != null) {
		subject_set = mySurvey.get_subject_set(subjectSet_ID);
		// declare the stem name & value arrays
		stems = subject_set.subject_labels;
		stem_fieldNames = new String[subject_set.subject_count];
		// construct field names as: QuestionName+suffix, delegated to
		// SS [should be "_ID"]
		for (int k = 0; k < subject_set.subject_count; k++) {
		    stem_fieldNames[k] = name
			    + subject_set.get_fieldName_suffix(k);
		}
	    }
	} catch (Exception e) {
	    WISE_Application.log_error("Failed to resolve subjectset: "
		    + subjectSet_ID + " --> " + e.toString(), null);
	    return;
	}
    }

    /**
     * get the table creation syntax (the survey data table) for a question
     * block
     */
    // public String create_table()
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
    // return "";
    // }

    public Hashtable read_form(Hashtable params) {
	Hashtable answers = super.read_form(params);
	answers.put("__SubjectSet_ID__", subjectSet_ID);
	return answers;
    }

    /** render FORM for a question block */
    public String render_form(User theUser) {
	String s = "";
	// check the question block's own precondition
	if (cond != null) {
	    // check the precondition for the whole question block
	    // if not satisfied, then skip writing the entire question block
	    boolean write_qb = cond.check_condition(theUser);
	    // return an empty string
	    if (!write_qb)
		return s;
	}

	// check the precondition vector for the subject set reference
	boolean[] ss_cond_vector = new boolean[stem_fieldNames.length];
	// if the subject set reference has the precondition defined
	if (ss_cond != null) {
	    boolean any_true = false;
	    // check if the precondition is met by calculating the comparison
	    // result
	    ss_cond_vector = ss_cond.check_condition(subjectSet_ID,
		    stem_fieldNames, theUser);
	    // render each stem of the question block
	    for (int i = 0; i < stems.length; i++) {
		// if the current stem meets the precondition, then it will be
		// displayed
		if (ss_cond_vector[i]) {
		    any_true = true;
		    break;
		}
	    }
	    // if no stem meets the precondition, skip displaying the entire
	    // subject set
	    if (!any_true)
		return s;
	}

	s = render_QB_header();
	// if there is a precondition defined for subject set, test vector for
	// each stem
	if (ss_cond != null) {
	    for (int i = 0; i < stems.length; i++) {
		// if the current stem meets the precondition, then display it
		if (ss_cond_vector[i])
		    s += render_stems(i);
	    }
	}
	// otherwise, display stems without testing
	else {
	    for (int i = 0; i < stems.length; i++)
		s += render_stems(i);
	}
	s += "</table>";
	return s;
    }

    /**
     * render the subject stems which meet the precondition defined in subject
     * set reference
     */
    public String render_stems(int i) {
	String s = "";
	// render each stem of the question block
	int startV = Integer.parseInt(response_set.startvalue);
	int len = response_set.get_size();
	int levels = Integer.parseInt(response_set.levels);
	int num = startV;

	if (i % 2 == 0)
	    s += "<tr class=\"shaded-bg\">";
	else
	    s += "<tr class=\"unshaded-bg\">";
	s += "<td>" + Study_Space.font + stems[i] + "</font></td>";

	if (levels == 0) {
	    for (int j = startV, k = 0; j < len + startV; j++, k++) {
		if (((String) response_set.values.get(k))
			.equalsIgnoreCase("-1")) {
		    s += "<td><center>" + Study_Space.font;
		    s += "<input type='radio' name='"
			    + stem_fieldNames[i].toUpperCase() + "' value='"
			    + num + "'>";
		    s += "</center></font></td>";
		    num = num + 1;
		} else {
		    s += "<td><center>" + Study_Space.font;
		    s += "<input type='radio' name='"
			    + stem_fieldNames[i].toUpperCase() + "' value='"
			    + response_set.values.get(k) + "'>";
		    s += "</center></font></td>";
		    num = num + 1;
		}
	    }
	} else {
	    for (int j = 1; j <= levels; j++) {
		s += "<td><center>" + Study_Space.font;
		s += "<input type='radio' name='"
			+ stem_fieldNames[i].toUpperCase() + "' value='" + num
			+ "'>";
		s += "</center></font></td>";
		num = num + 1;
	    }
	}
	return s;
    }

    /** render RESULTS for a subject set question block */
    public String render_results(Page pg, Data_Bank db, String whereclause,
	    Hashtable data) {

	int levels = Integer.valueOf(response_set.levels).intValue();
	int startValue = Integer.valueOf(response_set.startvalue).intValue();

	String s = render_QB_result_header();

	// display each of the stems on the left side of the block
	for (int i = 0; i < stems.length; i++) {
	    s += "<tr>";
	    int tnull = 0;
	    int t = 0;
	    float avg = 0;
	    Hashtable h1 = new Hashtable();
	    // get the user's conducted data from the hashtable
	    String subj_ans = (String) data.get(stem_fieldNames[i]
		    .toUpperCase());

	    String t1, t2;
	    try {
		// connect to the database
		Connection conn = pg.survey.getDBConnection();
		Statement stmt = conn.createStatement();

		// if the question block doesn't have the subject set ref
		String sql = "";
		// get the user's data from the table of subject set
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
			    + stem_fieldNames[i].substring((stem_fieldNames[i]
				    .lastIndexOf("_") + 1)) + " and invitee="
			    + user_id;
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
			+ stem_fieldNames[i].substring((stem_fieldNames[i]
				.lastIndexOf("_") + 1));
		if (!whereclause.equalsIgnoreCase(""))
		    sql += " and s." + whereclause;
		sql += " group by " + name;
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
		    sql = "select round(avg(" + stem_fieldNames[i]
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
			    + stem_fieldNames[i].substring((stem_fieldNames[i]
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
		s += stems[i] + "<p>";
		s += "<div align='right'>";
		s += "<font size='-2'><b><font color=green>mean: </font></b>"
			+ avg;

		if (tnull > 0) {
		    s += "&nbsp;<b><font color=green>unanswered: </font></b>";
		    // if the user's answer is null, highlight the answer
		    // note that if the call came from admin page, this value is
		    // always highlighted
		    // because the user's data is always to be null
		    if (subj_ans.equalsIgnoreCase("null")) {
			s += "<span style=\"background-color: '#FFFF77'\">"
				+ tnull + "</span>";
		    } else {
			s += tnull;
		    }
		}

		s += "</font></div>";
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
		    s += "<br><font size='-2'>" + ps1 + "</font>";
		    s += "</center>";
		    s += "</td>";
		}
	    }
	    // if classified level is required for the question block
	    else {
		s += "<td bgcolor=#FFCC99>";
		s += stems[i] + "<p>";
		s += "<div align='right'>";
		s += "<font size='-2'><b><font color=green>mean: </font></b>"
			+ avg;

		if (tnull > 0) {
		    s += "&nbsp;<b><font color=green>unanswered: </font></b>";
		    // if the user's answer is null, highlight the answer
		    // note that if the call came from admin page, this value is
		    // always highlighted
		    // because the user's data is always to be null
		    if (subj_ans.equalsIgnoreCase("null")) {
			s += "<span style=\"background-color: '#FFFF77'\">"
				+ tnull + "</span>";
		    } else {
			s += tnull;
		    }
		}

		s += "</font></div>";
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
		    s += "<br><font size='-2'>" + ps1 + "</font>";
		    s += "</center>";
		    s += "</td>";
		}
	    }
	}

	s += "</table></center>";
	return s;
    }

    /** returns a comma delimited list of all the fields on a page */
    /*
     * public String list_fields() { String s = ""; for (int i = 0; i <
     * stems.length; i++) s += stem_fieldNames[i]+","; return s; }
     */

    /** prints out the question block information */
    public String toString() {
	String s = "QUESTION BLOCK for subject set<br>";
	s += super.toString();

	s += "Instructions: " + instructions + "<br>";
	s += "Response Set: " + response_set.id + "<br>";
	s += "Stems:<br>";

	for (int i = 0; i < stems.length; i++)
	    s += stem_fieldNames[i] + ":" + stems[i] + "<br>";
	if (cond != null)
	    s += cond.toString();
	s += "<p>";
	return s;
    }
}

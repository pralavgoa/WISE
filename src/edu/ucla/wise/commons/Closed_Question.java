package edu.ucla.wise.commons;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is a subclass of Question and represents a closed ended question
 * on the page
 */

public class Closed_Question extends Question {
    /** Instance Variables */
    public Response_Set response_set;
    public String responseSet_ID;
    private String type;
    public Skip_List skip_list = null;

    /** constructor: parse a closed question node from XML */
    public Closed_Question(Node n) {
	// parse the quetion properties
	super(n);
	try {
	    // assign the response type - single (default) or multiple selection
	    // type
	    Node nr = n.getAttributes().getNamedItem("RCardinality");
	    Node nt = n.getAttributes().getNamedItem("responseType");
	    if (nr != null)
		type = n.getAttributes().getNamedItem("RCardinality")
			.getNodeValue();
	    else if (nt != null)
		type = n.getAttributes().getNamedItem("responseType")
			.getNodeValue();
	    else
		type = "single";
	    // parse the response set & response set reference, which can be
	    // used for multiple closed questions
	    NodeList nodelist = n.getChildNodes();
	    for (int i = 0; i < nodelist.getLength(); i++) {
		if (nodelist.item(i).getNodeName()
			.equalsIgnoreCase("Response_Set")) {
		    responseSet_ID = nodelist.item(i).getAttributes()
			    .getNamedItem("ID").getNodeValue();
		}
		// parse the response set reference
		else if (nodelist.item(i).getNodeName()
			.equalsIgnoreCase("Response_Set_Ref")) {
		    responseSet_ID = nodelist.item(i).getAttributes()
			    .getNamedItem("Response_Set").getNodeValue();
		}
		// parse the skip list
		if (nodelist.item(i).getNodeName().equalsIgnoreCase("SkipList"))
		    skip_list = new Skip_List(nodelist.item(i), this);
	    }
	} catch (Exception e) {
	    WISE_Application.log_error(
		    "WISE - CLOSED QUESTION: " + e.toString(), null);
	    return;
	}
    }

    public void knitRefs(Survey mySurvey) {
	try {
	    response_set = mySurvey.get_response_set(responseSet_ID);
	    html = make_html();
	} catch (Exception e) {
	    WISE_Application.log_error(
		    "WISE - CLOSED QUESTION knitRefs: " + e.toString(), null);
	}
    }

    /** count number of fields/options in the closed question */
    public int countFields() {
	// single selection has only one option
	if (type.equalsIgnoreCase("Exclusive")
		|| type.equalsIgnoreCase("single"))
	    return 1;
	// multiple selection has an option set
	if (type.equalsIgnoreCase("MultiSelect")
		|| type.equalsIgnoreCase("multiple"))
	    return response_set.responses.size();
	else
	    return 0;
    }

    // TODO: (impr abstr) save field name list; use for making html
    public String[] listFieldNames() {
	int fieldCnt = countFields();
	String[] fieldNames = new String[fieldCnt];
	if (fieldCnt == 1)
	    fieldNames[0] = name;
	else
	    for (int i = 0; i < fieldCnt; i++)
		fieldNames[i] = name + "_" + (i + 1);
	return fieldNames;
    }

    /** renders form for a closed question */
    public String make_html() {
	// begin with outer table to enclose "clear" button
	String s = "\n<table cellspacing='0' cellpadding='0' width=100%' border='0'><tr>\n<td>"
		+ "<table cellspacing='0' cellpadding='0' width=100%' border='0'><tr><td>";
	// display the question stem
	s += super.make_stem_html();
	s += make_responses();
	s += "</table>";
	s += "</td>\n";
	// if it is the single selection type, display the button image to reset
	// the answer
	if (type.equalsIgnoreCase("Exclusive")
		|| type.equalsIgnoreCase("single")) {
	    s += "<td align=right>";
	    s += "<a href=\"javascript:clearButtons(['" + name.toUpperCase()
		    + "']);\">";
	    // WARNING: URL will be null unless this called after
	    // WISE_Application has been inited from a begin servlet
	    // could get independently from Study_Space but no other reason to
	    // pass that info down
	    s += "<img src='" + WISE_Application.rootURL
		    + "/WISE/survey/imageRender?img=clear.gif' border=0></a>";
	    s += "</td>";
	}
	s += "</tr>";
	s += "</table>";
	return s;
    }

    /** generate html for response row(s) +/- clear button */
    public String make_responses() {
	String s = "";
	if (!oneLine)
	    s += "</tr><tr>";
	// start from a new line if it is not requested by one-line layout
	if (!oneLine) {
	    if (type.equalsIgnoreCase("MultiSelect")
		    || type.equalsIgnoreCase("multiple"))
		s += "<td width=10>&nbsp;</td><td colspan='2'><font size='-2'><b>(Select all that apply)</b></font></td>";
	    if (type.equalsIgnoreCase("Exclusive")
		    || type.equalsIgnoreCase("single"))
		s += "<td width=10>&nbsp;</td><td colspan='2'><font size='-2'><b>(Select one)</b></font></td>";
	} else {
	    s += "&nbsp;&nbsp;";
	}
	// start from a new line if it is not requested by one-line layout
	if (!oneLine) {
	    s += "</tr><tr>";
	    s += "<td colspan=3>&nbsp;<br></td>";
	    s += "</tr>";
	}
	// get the number of selections (len), the start value of the 1st level
	// (startV)
	// and the number of levels to classify
	int len = response_set.get_size();
	int startV = Integer.parseInt(response_set.startvalue);
	int num = startV;
	int levels = Integer.parseInt(response_set.levels);
	// display if there is no classified level
	if (levels == 0) {
	    for (int j = startV, i = 0; j < len + startV; j++, i++) {
		if (!oneLine) {
		    s += "\n<tr>";
		    s += "<td width=10>&nbsp;</td>";
		    s += "<td width=20>&nbsp;</td>";
		    s += "<td width=570>" + Study_Space.font;
		} else {
		    s += "&nbsp;";
		}
		// display layout without skip list
		if (skip_list == null) {
		    if (((String) response_set.values.get(i))
			    .equalsIgnoreCase("-1")) {
			if (type.equalsIgnoreCase("Exclusive")
				|| type.equalsIgnoreCase("single"))
			    s += "<input type='radio' name='"
				    + name.toUpperCase() + "' value='" + num
				    + "'>";
			if (type.equalsIgnoreCase("MultiSelect")
				|| type.equalsIgnoreCase("multiple"))
			    s += "<input type='checkbox' name='"
				    + name.toUpperCase() + "_" + num
				    + "' value='1'>";
			// s +=
			// "<input type='checkbox' name='"+name.toUpperCase()+"_"+num+"' value='"+num+"'>";
		    } else {
			if (type.equalsIgnoreCase("Exclusive")
				|| type.equalsIgnoreCase("single"))
			    s += "<input type='radio' name='"
				    + name.toUpperCase() + "' value='"
				    + response_set.values.get(i) + "'>";
			if (type.equalsIgnoreCase("MultiSelect")
				|| type.equalsIgnoreCase("multiple"))
			    // s +=
			    // "<input type='checkbox' name='"+name.toUpperCase()+"_"+num+"' value='"+response_set.values.get(i)+"'>";
			    s += "<input type='checkbox' name='"
				    + name.toUpperCase() + "_" + num
				    + "' value='1'>";
		    }
		}
		// display layout with skip list
		else {
		    if (((String) response_set.values.get(i))
			    .equalsIgnoreCase("-1")) {
			if (type.equalsIgnoreCase("Exclusive")
				|| type.equalsIgnoreCase("single"))
			    s += "<input type='radio' name='"
				    + name.toUpperCase() + "' value='" + num
				    + "' " + skip_list.render_form_element(num)
				    + ">";
			if (type.equalsIgnoreCase("MultiSelect")
				|| type.equalsIgnoreCase("multiple"))
			    // s +=
			    // "<input type='checkbox' name='"+name.toUpperCase()+"_"+num+"' value='"+num+"' "+skip_list.render_form_element(num)+">";
			    s += "<input type='checkbox' name='"
				    + name.toUpperCase() + "_" + num
				    + "' value='1' "
				    + skip_list.render_form_element(num) + ">";
		    } else {
			if (type.equalsIgnoreCase("Exclusive")
				|| type.equalsIgnoreCase("single"))
			    s += "<input type='radio' name='"
				    + name.toUpperCase()
				    + "' value='"
				    + response_set.values.get(i)
				    + "' "
				    + skip_list
					    .render_form_element((String) response_set.values
						    .get(i)) + ">";
			if (type.equalsIgnoreCase("MultiSelect")
				|| type.equalsIgnoreCase("multiple"))
			    s += "<input type='checkbox' name='"
				    + name.toUpperCase()
				    + "_"
				    + num
				    + "' value='1' "
				    + skip_list
					    .render_form_element((String) response_set.values
						    .get(i)) + ">";
			// s +=
			// "<input type='checkbox' name='"+name.toUpperCase()+"_"+num+"' value='"+response_set.values.get(i)+"' "+skip_list.render_form_element((String)response_set.values.get(i))+">";
		    }
		}
		// display the question option
		s += response_set.responses.get(i);
		// display with the skip list
		if (skip_list != null) {
		    if (((String) response_set.values.get(i))
			    .equalsIgnoreCase("-1"))
			s += skip_list.render_identifier(num);
		    else
			s += skip_list
				.render_identifier((String) response_set.values
					.get(i));
		}

		if (!oneLine)
		    s += "</font></td></tr>";
		num = num + 1;
	    }
	}
	// display if the classified level is required
	else {
	    int step = Math.round((levels - 1) / (len - 1));
	    for (int j = startV, i = 0; j < levels + startV; j++) {
		if (!oneLine) {
		    s += "<tr>";
		    s += "<td width=10>&nbsp;</td>";
		    s += "<td width=20>&nbsp;</td>";
		    s += "<td width=570>" + Study_Space.font;
		} else {
		    s += "&nbsp;";
		}
		// display the input field
		if (type.equalsIgnoreCase("Exclusive")
			|| type.equalsIgnoreCase("single"))
		    s += "<input type='radio' name='" + name.toUpperCase()
			    + "' value='" + num + "'>";
		if (type.equalsIgnoreCase("MultiSelect")
			|| type.equalsIgnoreCase("multiple"))
		    s += "<input type='checkbox' name='" + name.toUpperCase()
			    + "_" + num + "' value='" + num + "'>";
		int det = (j - 1) % step;
		if (det == 0) {
		    s += j + ". " + response_set.responses.get(i);
		    i++;
		} else
		    s += j + ". ";

		if (!oneLine)
		    s += "</font></td></tr>";
		num = num + 1;
	    }
	}

	if (oneLine)
	    s += "</td></tr>";
	return s;
    }

    /** print survey for a closed question - used for admin tool: print survey */
    public String print_survey() {
	String s = "<table cellspacing='0' cellpadding='0' width=100%' border='0'>";
	s += "<tr><td>";
	// print out the stem
	s += super.make_stem_html();

	if (!oneLine)
	    s += "</tr><tr>";

	if (!oneLine) {
	    if (type.equalsIgnoreCase("MultiSelect")
		    || type.equalsIgnoreCase("multiple"))
		s += "<td width=10>&nbsp;</td><td colspan='2'><font size='-2'><b>(Select all that apply)</b></font></td>";
	    if (type.equalsIgnoreCase("Exclusive")
		    || type.equalsIgnoreCase("single"))
		s += "<td width=10>&nbsp;</td><td colspan='2'><font size='-2'><b>(Select one)</b></font></td>";
	} else {
	    s += "&nbsp;&nbsp;";
	}

	if (!oneLine) {
	    s += "</tr><tr>";
	    s += "<td colspan=3>&nbsp;<br></td>";
	    s += "</tr>";
	}
	// get the number of selections (len), the start value of the 1st level
	// (startV)
	// and the number of levels to classify
	int len = response_set.get_size();
	int startV = Integer.parseInt(response_set.startvalue);
	int num = startV;
	int levels = Integer.parseInt(response_set.levels);
	// display if there is no classified level
	if (levels == 0) {
	    for (int j = startV, i = 0; j < len + startV; j++, i++) {
		if (!oneLine) {
		    s += "<tr>";
		    s += "<td width=10>&nbsp;</td>";
		    s += "<td width=20>&nbsp;</td>";
		    s += "<td width=570>";
		} else {
		    s += "&nbsp;";
		}

		s += "<img src='" + WISE_Application.rootURL + "/WISE" + "/"
			+ WiseConstants.SURVEY_APP + "/"
			+ "imageRender?img=checkbox.gif' border='0'>&nbsp;";
		s += response_set.responses.get(i);

		if (skip_list != null) {
		    if (((String) response_set.values.get(i))
			    .equalsIgnoreCase("-1"))
			s += skip_list.render_identifier(num);
		    else
			s += skip_list
				.render_identifier((String) response_set.values
					.get(i));
		}

		if (!oneLine)
		    s += "</td></tr>";
		num = num + 1;
	    }
	}
	// display if the classified level is required
	else {
	    int step = Math.round((levels - 1) / (len - 1));
	    for (int j = startV, i = 0; j < levels + startV; j++) {
		if (!oneLine) {
		    s += "<tr>";
		    s += "<td width=10>&nbsp;</td>";
		    s += "<td width=20>&nbsp;</td>";
		    s += "<td width=570>";
		}

		s += "<img src='" + WISE_Application.rootURL + "/WISE" + "/"
			+ WiseConstants.SURVEY_APP + "/"
			+ "imageRender?img=checkbox.gif' border='0'>&nbsp;";

		int det = (j - 1) % step;
		if (det == 0) {
		    s += j + ". " + response_set.responses.get(i);
		    i++;
		} else
		    s += j + ". ";

		if (!oneLine)
		    s += "</td></tr>";
		num = num + 1;
	    }
	}

	if (!oneLine)
	    s += "</table>";
	else
	    s += "</td></tr></table>";

	return s;
    }

    /**
     * read out the question field name & value from the hashtable and put them
     * into two arrays respectively
     */
    // public int read_form(Hashtable params, String[] fieldNames, String[]
    // fieldValues, int fieldIndex)
    // {
    // //single selection has only one option
    // if (type.equalsIgnoreCase("Exclusive") ||
    // type.equalsIgnoreCase("single"))
    // {
    // fieldNames[fieldIndex] = name.toUpperCase();
    // fieldValues[fieldIndex] = (String) params.get(name.toUpperCase());
    // fieldIndex++;
    // return 1;
    // }
    // //multiple selection has an option set
    // else if (type.equalsIgnoreCase("MultiSelect") ||
    // type.equalsIgnoreCase("multiple"))
    // {
    // String m_name;
    // for (int i = 1; i < response_set.responses.size()+1; i++)
    // {
    // m_name = name + "_" + i;
    // fieldNames[fieldIndex] = m_name.toUpperCase();
    // fieldValues[fieldIndex] = (String) params.get(m_name.toUpperCase());
    // fieldIndex++;
    // }
    // return response_set.responses.size();
    // }
    // else
    // return 0;
    // }
    //
    /** render results for a closed question */
    @Override
    public String render_results(Page pg, Data_Bank db, String whereclause,
	    Hashtable data) {
	String s = "<center><table cellspacing='0' cellpadding='2' width=100% border='0'>";
	s += "<tr><td colspan=5 align=right><span class='itemID'>" + this.name
		+ "</span></td></tr>";
	s += "<tr>";
	s += "<td width='2%'>&nbsp;</td>";
	s += "<td colspan='4'><font color=green>" + stem + "</font></td>";
	s += "</tr>";
	// render results for single selection type
	if (type.equalsIgnoreCase("Exclusive")
		|| type.equalsIgnoreCase("single"))
	    s += render_results_exclusive(pg, db, whereclause);
	else if (type.equalsIgnoreCase("MultiSelect"))
	    s += render_results_multiselect(data, pg, db, whereclause);

	// if( (s.contains("#SHAREDFILEURL#") == true) ||
	// (s.contains("#SHAREDIMAGEURL#") == true) )
	// {
	// s.replaceAll("#SHAREDFILEURL#", sa.shared_file_url);
	// s.replaceAll("#SHAREDIMAGEURL#", sa.shared_image_url);
	// }

	// render results for multiple selection type
	// if (type.equalsIgnoreCase("MultiSelect")
	// || type.equalsIgnoreCase("multiple"))
	// s += render_results_multiselect(data, whereclause);

	s += "</table></center>";
	return s;
    }

    // public String render_results(Hashtable data, String whereclause)
    // {
    // String s =
    // "<center><table cellspacing='0' cellpadding='2' width=100% border='0'>";
    // s +=
    // "<tr><td colspan=5 align=right><span class='itemID'>"+this.name+"</span></td></tr>";
    // s += "<tr>";
    // s += "<td width='2%'>&nbsp;</td>";
    // s += "<td colspan='4'><font color=green>"+stem+"</font></td>";
    // s += "</tr>";
    // //render results for single selection type
    // if (type.equalsIgnoreCase("Exclusive") ||
    // type.equalsIgnoreCase("single"))
    // s += render_results_exclusive(data, whereclause);
    // //render results for multiple selection type
    // if (type.equalsIgnoreCase("MultiSelect") ||
    // type.equalsIgnoreCase("multiple"))
    // s += render_results_multiselect(data, whereclause);
    //
    // s += "</table></center>";
    // return s;
    // }

    /** renders results for an exclusive closed question */
    // public String render_results_exclusive(Hashtable data, String
    // whereclause)
    // {
    // String s = "";
    // //get the User's answer
    // String subj_ans = (String) data.get(name.toUpperCase());
    // //if the call came from admin page, the data will be null
    // if (subj_ans == null)
    // subj_ans = "null";
    //
    // Hashtable h1 = new Hashtable();
    // int tnull = 0;
    // int t = 0;
    //
    // try
    // {
    // //connect to the database
    // Connection conn = page.survey.getDBConnection();
    // Statement stmt = conn.createStatement();
    // //count the total number of invitees who has the same level of answer
    // String sql = "select "+name+", count(distinct s.invitee) from "+
    // page.survey.id+"_data as s, page_submit as p where ";
    // sql += "p.invitee=s.invitee and p.survey='"+page.survey.id+"'";
    // sql += " and p.page='"+page.id+"'";
    // if(!whereclause.equalsIgnoreCase(""))
    // sql += " and s."+whereclause;
    // sql +=" group by "+name;
    // boolean dbtype = stmt.execute(sql);
    // ResultSet rs = stmt.getResultSet();
    // //clean up the hashtable
    // h1.clear();
    // String s1, s2;
    // while(rs.next())
    // {
    // //if the answer is null
    // if (rs.getString(1) == null)
    // tnull = tnull + rs.getInt(2);
    // else
    // {
    // s1 = rs.getString(1);
    // s2 = rs.getString(2);
    // //put the level of answer and its invitee number into the hashtable
    // h1.put(s1,s2);
    // t = t + rs.getInt(2);
    // }
    // }
    // rs.close();
    // stmt.close();
    // conn.close();
    // }
    // catch (Exception e)
    // {
    // Study_Util.email_alert("WISE - CLOSED QUESTION RENDER RESULTS EXCLUSIVE: "+e.toString());
    // return "";
    // }
    //
    // //get the average answer of the question
    // float avg = get_avg(whereclause);
    //
    // int num = 0;
    // String t1, t2, t3, t4, ss1;
    // //get the number of question level and its start value
    // int levels = Integer.valueOf(response_set.levels).intValue();
    // int startValue = Integer.valueOf(response_set.startvalue).intValue();
    // //if the question has no classified level
    // if (levels == 0)
    // {
    // for (int j = 0; j < response_set.responses.size(); j++)
    // {
    //
    // s += "<tr><td width='2%'>&nbsp;</td><td width='4%'>&nbsp;</td>";
    // num = j + 1;
    // t2 = String.valueOf(num);
    // t1 = (String) response_set.responses.get(j);
    // //use the predefined value if the question has it
    // String tt = (String) response_set.values.get(j);
    // if(!tt.equalsIgnoreCase("-1"))
    // t2=tt;
    //
    // int num1 = 0;
    // int p = 0;
    // int p1 = 0;
    // float af = 0;
    // float bf = 0;
    // float cf = 0;
    // String ps, ps1;
    // //get the number of invitees with the same level of answer
    // ss1 = (String) h1.get(t2);
    // //if no one has the answer for this level
    // if (ss1 == null)
    // {
    // ps = "0";
    // ps1 = "0";
    // }
    // else
    // {
    // num1 = Integer.parseInt(ss1);
    // af = (float) num1 / (float) t;
    // bf = af * 50;
    // cf = af * 100;
    // p = Math.round(bf);
    // p1 = Math.round(cf);
    // ps = String.valueOf(p);
    // ps1 = String.valueOf(p1);
    // }
    // //if the user's answer belongs to this answer level, highlight the answer
    // if (subj_ans.equalsIgnoreCase(t2))
    // s += "<td bgcolor='#FFFF77' width='3%'>";
    // else
    // s += "<td width='3%'>";
    // s += "<div align='right'><font size='-2'>"+ps1+"%&nbsp;</font></div>";
    // s += "</td>";
    // //if the user's answer belongs to this answer level, highlight the image
    // if (subj_ans.equalsIgnoreCase(t2))
    // s += "<td bgcolor='#FFFF77' width='6%'>";
    // else
    // s += "<td width='6%'>";
    // s += "<img src='"+Study_Space.file_path
    // +"imgs/horizontal/bar_"+ps+".gif' ";
    // s += "width='50' height='10'>";
    // s += "</td><td>&nbsp;&nbsp;"+t1+"</td></tr>";
    // }
    // }
    // //if the question has the level to classify
    // else
    // {
    // //calculate the step between levels
    // int step = Math.round((levels-1)/(response_set.responses.size()-1));
    // int i=0;
    // int j=0;
    // for (j = 1, i = 0; j <= levels; j++)
    // {
    // s += "<tr><td width=2%>&nbsp;</td>";
    // s += "<td width=4%>&nbsp;</td>";
    // num = j;
    // t2 = String.valueOf(num);
    // t1 = (String) response_set.responses.get((j-1)/step);
    // int num1 = 0;
    // int p = 0;
    // int p1 = 0;
    // float af = 0;
    // float bf = 0;
    // float cf = 0;
    // String ps, ps1, ss;
    // ss = (String) h1.get(t2);
    // if (ss == null)
    // {
    // ps = "0";
    // ps1 = "0";
    // }
    // else
    // {
    // num1 = Integer.parseInt(ss);
    // af = (float) num1 / (float) t;
    // bf = af * 50;
    // cf = af * 100;
    // p = Math.round(bf);
    // p1 = Math.round(cf);
    // ps = String.valueOf(p);
    // ps1 = String.valueOf(p1);
    // }
    // //if the user's answer belongs to this answer level, highlight the answer
    // if (subj_ans.equalsIgnoreCase(t2))
    // s += "<td bgcolor='#FFFF77' width='3%'>";
    // else
    // s += "<td align=right width=3%>";
    // s += "<font size='-2'>"+ps1+"% </font></td>";
    // //if the user's answer belongs to this answer level, highlight the image
    // if (subj_ans.equalsIgnoreCase(t2))
    // s += "<td bgcolor='#FFFF77' width=6%>";
    // else
    // s += "<td width=6%>";
    // s += "<img src='"+Study_Space.file_path
    // +"imgs/horizontal/bar_"+ps+".gif' ";
    // s += "width='50' height='10'>";
    // s += "</td>";
    // int det = (j-1) % step;
    // if (det == 0)
    // {
    // s += "<td>&nbsp;&nbsp;"+response_set.responses.get(i)+"</td>";
    // i++;
    // }
    // s += "</tr>";
    // }
    // }
    //
    // //display the average answer of this question
    // s += "<tr><td width='2%'>&nbsp;</td><td colspan=4>";
    // s += "<font size='-2'><b><font color=green>mean: </font></b>"+avg;
    // //if there is null answer, display the number
    // if (tnull > 0)
    // {
    // s += "&nbsp;<b><font color=green>unanswered: </font></b>";
    // //if the User's answer is also null, highlight it
    // //this number will be highlighted in admin page anyway, because the
    // subj_ans is always null
    // if (subj_ans.equalsIgnoreCase("null"))
    // s += "<span style=\"background-color: '#FFFF77'\">"+tnull+"</span>";
    // else
    // s += tnull;
    // }
    //
    // s += "</font></td></tr>";
    // return s;
    // }

    /**
     * renders results for an exclusive closed question -- output is an html row
     */
    public String render_results_exclusive(Page pg, Data_Bank db,
	    String whereclause) {
	String s = "";

	Hashtable h1 = db.getData_forItem(pg.survey.id, pg.id, this.name,
		whereclause);
	Integer tnull = (Integer) h1.remove("null");
	int totalResponses = 0;
	Enumeration en = h1.keys();
	while (en.hasMoreElements())
	    totalResponses += ((Integer) h1.get(en.nextElement())).intValue();
	float avg = get_avg(pg, whereclause);// temporary
	int num = 0;
	String t1, t2;
	Integer cntInt;
	// get the number of question level and its start value
	int levels = Integer.valueOf(response_set.levels).intValue();
	int startValue = Integer.valueOf(response_set.startvalue).intValue();

	if (levels == 0) // question has no predefined number of levels
	{
	    for (int j = 0; j < response_set.responses.size(); j++) {
		s += "<tr><td width='2%'>&nbsp;</td><td width='4%'>&nbsp;</td>";
		num = j + 1;
		t2 = String.valueOf(num);
		t1 = (String) response_set.responses.get(j);
		// use the predefined value if the question has it
		String tt = (String) response_set.values.get(j);
		if (!tt.equalsIgnoreCase("-1"))
		    t2 = tt;

		int num1 = 0;
		int p = 0;
		int p1 = 0;
		float af = 0;
		float bf = 0;
		float cf = 0;
		String ps, ps1;
		// get the number of invitees with the same level of answer
		cntInt = (Integer) h1.get(t2);
		// if no one has the answer for this level
		if (cntInt == null) {
		    ps = "0";
		    ps1 = "0";
		} else {
		    num1 = cntInt.intValue();
		    af = (float) num1 / (float) totalResponses;
		    bf = af * 50;
		    cf = af * 100;
		    p = Math.round(bf);
		    p1 = Math.round(cf);
		    ps = String.valueOf(p);
		    ps1 = String.valueOf(p1);
		}
		s += "<td width='3%'>";
		s += "<div align='right'><font size='-2'>" + ps1
			+ "%&nbsp;</font></div>";
		s += "</td>";
		s += "<td width='6%'>";
		s += "<img src='" + Surveyor_Application.shared_file_url
			+ "imgs/horizontal/bar_" + ps + ".gif' ";
		s += "width='50' height='10'>";
		s += "</td><td>&nbsp;&nbsp;" + t1 + "</td></tr>";
	    }
	}

	else // question does have a set number of levels
	{
	    // calculate the step between levels
	    int step = Math.round((levels - 1)
		    / (response_set.responses.size() - 1));
	    int i = 0;
	    int j = 0;
	    for (j = 1, i = 0; j <= levels; j++) {
		s += "<tr><td width=2%>&nbsp;</td>";
		s += "<td width=4%>&nbsp;</td>";
		num = j;
		t2 = String.valueOf(num);
		t1 = (String) response_set.responses.get((j - 1) / step);
		int num1 = 0;
		int p = 0;
		int p1 = 0;
		float af = 0;
		float bf = 0;
		float cf = 0;
		String ps, ps1;
		cntInt = (Integer) h1.get(t2);
		if (cntInt == null) {
		    ps = "0";
		    ps1 = "0";
		} else {
		    num1 = cntInt.intValue();
		    af = (float) num1 / (float) totalResponses;
		    bf = af * 50;
		    cf = af * 100;
		    p = Math.round(bf);
		    p1 = Math.round(cf);
		    ps = String.valueOf(p);
		    ps1 = String.valueOf(p1);
		}
		s += "<td align=right width=3%>";
		s += "<font size='-2'>" + ps1 + "% </font></td>";
		s += "<td width=6%>";
		s += "<img src='" + Surveyor_Application.shared_file_url
			+ "imgs/horizontal/bar_" + ps + ".gif' ";
		s += "width='50' height='10'>";
		s += "</td>";
		int det = (j - 1) % step;
		if (det == 0) {
		    s += "<td>&nbsp;&nbsp;" + response_set.responses.get(i)
			    + "</td>";
		    i++;
		}
		s += "</tr>";
	    }
	}

	// display the average answer of this question
	s += "<tr><td width='2%'>&nbsp;</td><td colspan=4>";
	s += "<font size='-2'><b><font color=green>mean: </font></b>" + avg;
	// if there is null answer, display the number
	if (tnull != null) {
	    s += "&nbsp;<b><font color=green>unanswered: </font></b>";
	    s += tnull;
	}

	s += "</font></td></tr>";
	return s;
    }

    // TODO: FIXME
    /** renders results for a multiselect closed question */
    public String render_results_multiselect(Hashtable data, Page pg,
	    Data_Bank db, String whereclause) {

	String s = "";
	int num = 0;
	String t2, t3, t4;
	Hashtable h1 = new Hashtable();
	int tnull = 0;

	for (int j = 0; j < response_set.responses.size(); j++) {
	    num = j + 1;
	    t2 = String.valueOf(num);
	    t3 = name + "_" + t2;
	    // get the User's answer
	    String subj_ans = data == null ? null : (String) data.get(t3
		    .toUpperCase());
	    // if the call came from admin page, the data will be null
	    if (subj_ans == null)
		subj_ans = "null";

	    try {
		// connect to the database
		Connection conn = pg.survey.getDBConnection();
		Statement stmt = conn.createStatement();
		// count the total number of invitees who has the same level of
		// answer
		String sql = "select " + t3
			+ ", count(distinct s.invitee) from " + pg.survey.id
			+ "_data as s, page_submit as p where ";
		sql += "p.invitee=s.invitee and p.survey='" + pg.survey.id
			+ "'";
		sql += " and p.page='" + pg.id + "'";
		if (!whereclause.equalsIgnoreCase(""))
		    sql += " and s." + whereclause;
		sql += " group by " + t3;
		boolean dbtype = stmt.execute(sql);
		ResultSet rs = stmt.getResultSet();
		h1.clear();
		String s1, s2;
		while (rs.next()) {
		    if (rs.getString(1) != null) {
			s1 = rs.getString(1);
			s2 = rs.getString(2);
			// put the level of answer and its invitee number into
			// the hashtable
			h1.put(t3, s2);
		    } else {
			tnull = tnull + rs.getInt(2);
		    }
		}
		rs.close();
		stmt.close();
		conn.close();
	    } catch (Exception e) {
		WISE_Application.log_error(
			"WISE - CLOSED QUESTION RENDER RESULTS MULTISELECT: "
				+ e.toString(), e);
		return "";
	    }

	    s += "<tr><td width='2%'>&nbsp;</td><td width='4%'>&nbsp;</td>";

	    t4 = (String) h1.get(t3);

	    float p = 0;
	    float pt = 0;
	    if (t4 != null) {
		Integer it4 = new Integer(t4);
		p = (float) it4.intValue()
			/ (float) pg.get_pagedone_numb(whereclause);
	    }

	    p = p * 50;
	    pt = p * 2;
	    int p1 = Math.round(p);
	    int p1t = Math.round(pt);
	    String ps = String.valueOf(p1);
	    String pst = String.valueOf(p1t);
	    // if the user's answer belongs to this answer level, highlight the
	    // answer
	    if (subj_ans.equalsIgnoreCase(t2))
		s += "<td bgcolor='#FFFF77' width='3%'>";
	    else
		s += "<td width='3%'>";
	    s += "<div align='right'><font size='-2'>" + pst
		    + "% </font></div></td>";
	    // if the user's answer belongs to this answer level, highlight the
	    // image
	    if (subj_ans.equalsIgnoreCase(t2))
		s += "<td bgcolor='#FFFF77' width='6%'>";
	    else
		s += "<td width='6%'>";
	    s += "<img src='" + Surveyor_Application.shared_file_url
		    + "imgs/horizontal/bar_" + ps + ".gif' ";
	    s += "width='50' height='10'></td>";
	    s += "<td>&nbsp;&nbsp;" + response_set.responses.get(j)
		    + "</td></tr>";
	}
	return s;
    }

    /** prints information about a closed question */
    /*
     * public String print() { String s = "CLOSED QUESTION <br>"; s +=
     * super.print(); s += "Type: "+type+"<br>"; s +=
     * "Response Set: "+response_set.id+"<br>"; if (skip_list != null) s +=
     * skip_list.print(); s += "<p>"; return s; }
     */

}

package edu.ucla.wise.commons;

import java.util.HashMap;
import java.util.Hashtable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is a subclass of Open_Question and represents an numeric open
 * ended question on the page
 */

public class Numeric_Open_Question extends Open_Question {
    /** Instance Variables */
    public String maxSize;
    public String width;
    public String minValue;
    public String maxValue;
    public String decimalPlaces;

    // TODO: (med) add methods Survey to track maxSize and decimalPlaces or at
    // least the max() of these

    /** constructor: to create a numeric open question from XML */
    public Numeric_Open_Question(Node n) {
	// get the attributes for open question
	super(n);
	try {
	    NodeList nodelist = n.getChildNodes();
	    for (int i = 0; i < nodelist.getLength(); i++) {
		if (nodelist.item(i).getNodeName()
			.equalsIgnoreCase("Numeric_Open_Response")) {
		    // assign various attributes
		    maxSize = nodelist.item(i).getAttributes()
			    .getNamedItem("MaxSize").getNodeValue();
		    minValue = nodelist.item(i).getAttributes()
			    .getNamedItem("MinValue").getNodeValue();
		    maxValue = nodelist.item(i).getAttributes()
			    .getNamedItem("MaxValue").getNodeValue();

		    Node node = nodelist.item(i).getAttributes()
			    .getNamedItem("Width");
		    if (node != null)
			width = node.getNodeValue();
		    else
			width = maxSize;

		    node = nodelist.item(i).getAttributes()
			    .getNamedItem("DecimalPlaces");
		    if (node != null)
			decimalPlaces = node.getNodeValue();
		    else
			decimalPlaces = "0";
		}
	    }
	} catch (Exception e) {
	    WISE_Application.log_error(
		    "WISE - NUMERIC OPEN QUESTION: " + e.toString(), null);
	    return;
	}
    }

    public char getValueType() {
	return Data_Bank.decimalValueTypeFlag;
    }

    // public Numeric_Open_Question(Node n, Page p)
    // {
    // //get the attributes for open question
    // super(n,p);
    // try
    // {
    // NodeList nodelist = n.getChildNodes();
    // for (int i = 0; i < nodelist.getLength(); i++)
    // {
    // if
    // (nodelist.item(i).getNodeName().equalsIgnoreCase("Numeric_Open_Response"))
    // {
    // //assign various attributes
    // maxSize =
    // nodelist.item(i).getAttributes().getNamedItem("MaxSize").getNodeValue();
    // minValue =
    // nodelist.item(i).getAttributes().getNamedItem("MinValue").getNodeValue();
    // maxValue =
    // nodelist.item(i).getAttributes().getNamedItem("MaxValue").getNodeValue();
    //
    // Node node = nodelist.item(i).getAttributes().getNamedItem("Width");
    // if (node != null)
    // width = node.getNodeValue();
    // else
    // width = maxSize;
    //
    // node = nodelist.item(i).getAttributes().getNamedItem("DecimalPlaces");
    // if (node != null)
    // decimalPlaces = node.getNodeValue();
    // else
    // decimalPlaces = "0";
    // }
    // }
    //
    // }
    // catch (Exception e)
    // {
    // Study_Util.email_alert("WISE - NUMERIC OPEN QUESTION: "+e.toString());
    // return;
    // }
    // }

    /** render form for numeric open question */
    public String form_field_html() {
	String s = "";
	// display the form field
	// start from a new line if it is not requested by one-line layout
	s += "<input type='text' name='" + name.toUpperCase() + "' maxlength='"
		+ maxSize + "' ";
	s += "size='" + width + "' onChange='RangeCheck(this," + minValue + ","
		+ maxValue + ");'>";
	return s;
    }

    /**
     * print survey for numeric open question - used for admin tool: print
     * survey
     */
    public String print_survey() {
	String s = super.make_stem_html();
	s += "";
	// start from a new line if it is not requested by one-line layout
	if (!oneLine)
	    s += "<td width=570>";
	int width_plus = Integer.parseInt(width) + 20;
	s += "<table cellpadding=0 cellspacing=0 border=1><tr>";
	s += "<td width=" + width_plus + " height=15 align=center>";
	s += "</td></tr></table>";

	s += "</td>";
	s += "</tr>";
	s += "</table>";
	return s;
    }

    /** render results for numeric open question */
    @SuppressWarnings("rawtypes")
    public String render_results(Page page, Data_Bank db, String whereclause,
	    Hashtable data) {

	String html = "";
	// min and max values of the question answer
	float field_min = 0, field_max = 0;
	int bin_count = 10;
	int unanswered = 0;
	float min_bin_width = 0, l_mbw = 0, t_l_mbw = 0, bin_base_unit = 0, bin_width_prelim = 0, scale_start = 0, bin_width_final = 0;
	HashMap<String, String> binCountMap = new HashMap<String, String>();

	// get the question value from the hashtable
	String subj_ans = (data == null) ? "null" : (String) data.get(name
		.toUpperCase());

	// convert the value from string type into the float type
	float f_ans = 0;
	if (!subj_ans.equalsIgnoreCase("null"))
	    f_ans = Float.valueOf(subj_ans).floatValue();

	// get average value of the question results within the scope of
	// whereclause
	// TODO: Help!
	float avg = get_avg(page, whereclause);

	// Number of BINS and width
	// get min and max values based on all results within the scope of
	// whereclause
	HashMap<String, Float> minMaxMap = db.getMinMax_forItem(page, name,
		whereclause);
	field_min = minMaxMap.get("min");
	field_max = minMaxMap.get("max");

	min_bin_width = (field_max - field_min) / bin_count;
	if (min_bin_width == 0)
	    min_bin_width = 1;
	l_mbw = (float) Math.log(min_bin_width) * (1 / (float) Math.log(10));
	t_l_mbw = (float) Math.floor(l_mbw);
	bin_base_unit = (float) Math.pow(10, t_l_mbw);
	bin_width_prelim = bin_base_unit
		* ((float) Math.floor(min_bin_width / bin_base_unit) + 1);
	scale_start = bin_width_prelim
		* ((float) Math.floor(field_min / bin_width_prelim));
	bin_width_final = bin_base_unit
		* ((float) Math.floor((field_max - scale_start)
			/ (bin_count * bin_base_unit)) + 1);

	// get bins on that question from database
	binCountMap = db.getHistogram_forItem(page, name, scale_start,
		bin_width_final, whereclause);
	unanswered = binCountMap.get("unanswered") == null ? 0 : Integer
		.parseInt(binCountMap.get("unanswered"));

	html += "<table width=400 border=0>";
	html += "<tr><td align=right><span class='itemID'>" + this.name
		+ "</span></td></tr>";
	html += "<tr><td>";
	html += "<table bgcolor=#FFFFFC cellspacing='0' cellpadding='1' width=400 border='1'>";
	html += "<tr>";
	html += "<td bgcolor=#FFFFE0 rowspan=2 width='30%'>";
	// 3rd table layout
	html += "<table><tr><td width='2%'>&nbsp;</td><td><font color=green>"
		+ stem + "</font>";
	html += "<p><div align='right'><font color=green size='-2'><b>mean:</b>"
		+ avg + "</font></div>";
	String su = "";
	if (unanswered == 1) {
	    su = (String) binCountMap.get("null");
	    html += "<div align='right'>";
	    if (f_ans == 0) {
		html += "<span style=\"background-color: '#FFFF77'\">";
		html += "<font size='-2'>unanswered:&nbsp;&nbsp;" + su
			+ "</font>";
		html += "</span>";
	    } else
		html += "<font size='-2'>unanswered:&nbsp;&nbsp;" + su
			+ "</font>";
	    html += "</div>";
	}
	html += "<p align=left><a href='" + "view_open_results?u=" + su + "&q="
		+ name + "&t=" + page.id + "' >";
	html += "<img src='" + "imageRender?img=go_view.gif' border=0></a>";
	html += "</td></tr></table>";
	// end of 3rd table
	html += "</td>";
	html += "<td width='70%'>";

	int col_span = (bin_count * 2) + 2;
	html += "<table width='100%' border='0'>";
	html += "<tr><td colspan='"
		+ col_span
		+ "' align='center'><font size='-2'>Histogram of values reported</font></td></tr>";
	html += "<tr>";
	html += "<td>&nbsp;</td>";
	String cellCount;
	float f2 = scale_start;
	float f3 = scale_start;
	int totalSum = 0;
	for (int j = 0; j < bin_count; j++) {
	    cellCount = (String) binCountMap.get(Integer.toString(j));
	    totalSum += (cellCount == null) ? 0 : Float.valueOf(cellCount)
		    .floatValue();
	}
	for (int j = 0; j < bin_count; j++) {
	    cellCount = (String) binCountMap.get(Integer.toString(j));

	    if (cellCount == null)
		cellCount = "0";

	    float cellPercent = (Float.valueOf(cellCount).floatValue() / (float) totalSum) * 100;
	    int roundedCellPercent = Math.round(cellPercent);
	    int imageHeight = roundedCellPercent / 2;

	    f3 = f2 + bin_width_final;
	    if ((f_ans > f2) && (f_ans < f3)) // this is the user's answer
		html += "<td colspan='2' bgcolor='#FFFF77' align='center'>";
	    else
		html += "<td colspan='2' align='center'>";
	    // TODO -- Help Sumedh!
	    html += "<font color=green size='-2'>"
		    + Integer.toString(roundedCellPercent)
		    + "%</font><br><img src='"
		    + "imageRender?img=vertical/bar_"
		    + Integer.toString(imageHeight) + ".gif' ";
	    html += "width='10' height='50'>";
	    html += "</td>";

	    f2 = f3;
	}
	html += "<td>&nbsp;</td>";
	html += "</tr>";
	html += "<tr>";
	for (int j = 0; j < bin_count + 1; j++)
	    html += "<td colspan='2' align='center'><font size='-2'>|</font></td>";
	html += "</tr>";

	int p;
	float f1 = scale_start;
	html += "<tr>";
	for (int j = 0; j < bin_count + 1; j++) {
	    p = Math.round(f1);
	    String ps = Integer.toString(p);
	    html += "<td colspan='2' align='center'>";
	    html += "<font color=green size='-2'>" + p + "</font>";
	    html += "</td>";
	    f1 = f1 + bin_width_final;
	}

	html += "</tr></table>";
	html += "</td></tr></table>";
	html += "</td></tr></table>";
	return html;
    }
    // public Hashtable read_form(Hashtable params)
    // {
    // Hashtable answers = new Hashtable();
    // String fieldName = name.toUpperCase();
    // String answerVal = (String) params.get(fieldName);
    // answers.put(fieldName, answerVal);
    // return answers;
    // }
    //
    // /** read out the question field name & value from the hashtable and put
    // them into two arrays respectively */
    // public int read_form(Hashtable params, String[] fieldNames, String[]
    // fieldValues, int fieldIndex)
    // {
    // fieldNames[fieldIndex] = name.toUpperCase();
    // fieldValues[fieldIndex] = (String) params.get(name.toUpperCase());
    // //have to keep the same index for name & value
    // if ( (fieldValues[fieldIndex] == null) ||
    // (fieldValues[fieldIndex].equalsIgnoreCase("")) )
    // fieldValues[fieldIndex] = "null";
    // fieldIndex++;
    // return 1;
    // }

    /** print numeric open question information */
    /*
     * public String print() { String s = "NUMERIC OPEN QUESTION<br>"; s +=
     * super.print(); s += "MaxSize: "+maxSize+"<br>"; s +=
     * "Width: "+width+"<br>"; s += "MinValue: "+minValue+"<br>"; s +=
     * "MaxValue: "+maxValue+"<br>"; s +=
     * "DecimalPlaces: "+decimalPlaces+"<br>"; s += "<p>"; return s; }
     */

}

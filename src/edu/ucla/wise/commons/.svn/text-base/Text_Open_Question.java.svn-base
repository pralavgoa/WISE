package edu.ucla.wise.commons;

import java.util.Hashtable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is a subclass of Open_Question and represents an text open ended
 * question on the page
 */

public class Text_Open_Question extends Open_Question {
	/** Instance Variables */
	public String maxSize;
	public String multiLine;
	public String width;
	public String height;

	/** constructor for creating a text open question from XML */
	public Text_Open_Question(Node n) {
		// get the attributes for open question
		super(n);
		try {
			NodeList nodelist = n.getChildNodes();
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node_TOR = nodelist.item(i);
				if (node_TOR.getNodeName().equalsIgnoreCase(
						"Text_Open_Response")) {
					// assign various attributes
					maxSize = node_TOR.getAttributes().getNamedItem("MaxSize")
							.getNodeValue();
					multiLine = node_TOR.getAttributes()
							.getNamedItem("MultiLine").getNodeValue();
					Node node = node_TOR.getAttributes().getNamedItem("Width");
					if (node != null)
						width = node.getNodeValue();
					else
						width = maxSize;
					node = node_TOR.getAttributes().getNamedItem("Height");
					if (node != null)
						height = node.getNodeValue();
					else
						height = "1";
					NodeList nodeL = node_TOR.getChildNodes();
				}
			}

		} catch (Exception e) {
			WISE_Application.log_error(
					"WISE - TEXT OPEN QUESTION: " + e.toString(), null);
			return;
		}
	}

	public char getValueType() {
		return Data_Bank.textValueTypeFlag;
	}

	/** render text field part of open question */
	public String form_field_html() {
		String s = "";
		// display the form field
		// start from a new line if it is not requested by one-line layout
		if (multiLine.equals("false"))
			s += "<input type='text' name='" + name.toUpperCase()
			+ "' maxlength='" + maxSize + "' size='" + width + "' >";
		else
			s += "<textarea name='" + name.toUpperCase() + "' cols='" + width
			+ "' rows='" + height + "' onchange='SizeCheck(this,"
			+ maxSize + ");'></textarea>";
		return s;
	}

	/** print survey for text open question - used for admin tool: print survey */
	public String print_survey() {
		StringBuffer sb = new StringBuffer("");
		//WISEDEV-8: adding following html to render question block correctly
		sb.append("\n<table cellspacing='0' width='100%' cellpadding='0' border='0'><tr>\n<td>"
				+ "<table cellspacing='0' width='100%' cellpadding='0' border='0'><tr><td>");
		//display the question stem
//		sb.append("<p style=\"margin-left:10em;\">");
		sb.append(super.make_stem_html());
//		sb.append("</p");
		sb.append("</table");
		sb.append("</td>\n");
		sb.append("\n<table cellspacing='0' width='100%' cellpadding='0' border='0'><tr>\n<td>"
				+ "<table cellspacing='0' width='100%' cellpadding='0' border='0'><tr><td>");
		// start from a new line if it is not requested by one-line layout
		if (!oneLine)
			sb.append("<td width=570>");
		if (multiLine.equals("false")) {

			int width_plus = Integer.parseInt(width) + 20;

			sb.append("<table cellpadding=0 cellspacing=0 border=1><tr>");
			sb.append("<td width=" + (width_plus*8*80) + " height=15 align=center>");
			sb.append("</td></tr></table>");
		} else {
			for (int j = 0; j < Integer.parseInt(height); j++) {
				// print the underline field to fill the answer above
				for (int i = 0; i < Integer.parseInt(width)+20; i++)
					sb.append("_");
				sb.append("<br>");
			}

		}
//		sb.append("</p>");
//
		sb.append("</table");
		sb.append("</td>\n");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}

	/** render results for text open question */
	public String render_results(Page page, Data_Bank db, String whereclause,
			Hashtable data) {
		String s = "<table cellspacing='0' cellpadding='0' width=100%' border='0'>";
		s += "<tr>";
		s += "<td colspan=2 align=right>";
		s += "&nbsp;&nbsp;<span class='itemID'><i>" + name + "</i></span>";
		s += "</td></tr><tr>";
		s += "<td width='2%'>&nbsp;</td>";
		s += "<td colspan='4'><font color=green>" + stem
				+ "</font>&nbsp;&nbsp;&nbsp;&nbsp;";
		// add a link to view the answer
		s += "<a href='view_open_results?q=" + name + "' >";
		s += "<img src='" + "imageRender?img=go_view.gif' border=0></a>";
		s += "</td></tr>";
		s += "<tr>";
		s += "<td>&nbsp;</td>";
		s += "</tr>";
		s += "</table>";
		return s;
	}

	/**
	 * read out the question field name & value from the hashtable and put them
	 * into two arrays respectively
	 */
	// public int read_form(Hashtable params, String[] fieldNames, String[]
	// fieldValues, int fieldIndex)
	// {
	// fieldNames[fieldIndex] = name.toUpperCase();
	// String s = (String) params.get(name.toUpperCase());
	// //if the string has a single quote, fix it with double quote
	// s = Study_Util.fixquotes(s);
	// fieldValues[fieldIndex] = "'"+s+"'";
	// if ( (fieldValues[fieldIndex] == null) ||
	// (fieldValues[fieldIndex].equalsIgnoreCase("")) )
	// fieldValues[fieldIndex] = "'null'";
	// fieldIndex++;
	// return 1;
	// }

	/** print information about a text open question */
	/*
	 * public String print() { String s = "TEXT OPEN QUESTION<br>"; s +=
	 * super.print(); s += "MaxSize: "+maxSize+"<br>"; s +=
	 * "MultiLine: "+multiLine+"<br>"; s += "Width: "+width+"<br>"; s +=
	 * "Height: "+height+"<br>"; s += "<p>"; return s; }
	 */
}

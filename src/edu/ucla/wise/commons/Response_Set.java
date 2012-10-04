package edu.ucla.wise.commons;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class contains an answer set called response set and all its possible
 * answers The closed question & question block contain this response set
 */

public class Response_Set {
    /** Instance Variables */
    public String id;
    public String levels;
    public String startvalue;

    public ArrayList responses;
    public ArrayList values;

    public Survey survey;

    /** constructor: parse a response set node from XML */
    public Response_Set(Node n, Survey s) {
	try {
	    survey = s;
	    // assign various attributes
	    id = n.getAttributes().getNamedItem("ID").getNodeValue();
	    // assign the number of levels to classify
	    Node node1 = n.getAttributes().getNamedItem("Levels");
	    if (node1 != null)
		levels = node1.getNodeValue();
	    else
		levels = "0";
	    // assign the start value of the 1st level
	    node1 = n.getAttributes().getNamedItem("StartValue");
	    if (node1 != null)
		startvalue = node1.getNodeValue();
	    else
		startvalue = "1";

	    NodeList nodelist = n.getChildNodes();
	    responses = new ArrayList();
	    values = new ArrayList();
	    // assign answer option & its value
	    for (int i = 0; i < nodelist.getLength(); i++) {
		if (nodelist.item(i).getNodeName()
			.equalsIgnoreCase("Response_Option")) {
		    String str = nodelist.item(i).getFirstChild()
			    .getNodeValue();
		    responses.add(str);
		    Node node2 = nodelist.item(i).getAttributes()
			    .getNamedItem("value");
		    if (node2 != null)
			values.add(node2.getNodeValue());
		    else
			values.add("-1");
		}
	    }
	} catch (Exception e) {
	    WISE_Application.log_error("WISE - RESPONSE SET : ID = " + id
		    + "; Survey = " + s.id + "; Study = " + s.study_space.id
		    + " --> " + e.toString(), null);
	    return;
	}
    }

    /** return the number of responses in the set */
    public int get_size() {
	return responses.size();
    }

    /** prints out a response set - used for admin tool: print survey */
    /*
     * public String print() { String s = "RESPONSE SET<br>"; s +=
     * "ID: "+id+"<br>"; s += "Levels: "+levels+"<br>"; s +=
     * "StartValue: "+startvalue+"<br>"; s += "Responses: <br>"; for (int i = 0;
     * i < responses.size(); i++) s +=
     * values.get(i)+":"+responses.get(i)+"<br>"; s += "<p>"; return s; }
     */
}

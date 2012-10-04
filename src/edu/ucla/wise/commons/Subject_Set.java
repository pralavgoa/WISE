package edu.ucla.wise.commons;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class contains a subject set and all its possible answers
 */

public class Subject_Set {
    /** Instance Variables */
    public String id;
    private int[] subject_IDs;
    public String[] subject_labels;
    public Survey survey;
    public int subject_count;

    /** constructor: parse a subject set node from XML */
    public Subject_Set(Node n, Survey s) {
	try {
	    survey = s;
	    // assign various attributes
	    id = n.getAttributes().getNamedItem("ID").getNodeValue();

	    NodeList nlist = n.getChildNodes();
	    subject_count = nlist.getLength();
	    subject_IDs = new int[subject_count];
	    subject_labels = new String[subject_count];

	    // get each subject name and its value in the subject set
	    for (int j = 0; j < subject_count; j++) {
		int id_num = 1;
		Node subj = nlist.item(j);
		if (subj.getNodeName().equalsIgnoreCase("Subject")) {
		    // get the subject value
		    Node sIDnode = subj.getAttributes().getNamedItem("IDnum");
		    if (sIDnode == null) {
			// ID value is not specified in XML, assign the
			// currentindex as its value
			subject_IDs[j] = id_num++;
		    } else {
			subject_IDs[j] = Integer.parseInt(sIDnode
				.getNodeValue());
			id_num = Math.max(id_num, subject_IDs[j]);
			id_num++;
		    }
		    // record the subject name
		    subject_labels[j] = subj.getFirstChild().getNodeValue();
		}
	    }
	} catch (Exception e) {
	    WISE_Application.log_error("WISE - SUBJECT SET : ID = " + id
		    + "; Survey = " + id + "; Study = " + s.study_space.id
		    + " --> " + e.toString(), null);
	    return;
	}
    }

    public String get_fieldName_suffix(int index) {
	if (index < subject_count)
	    return "_" + Integer.toString(subject_IDs[index]);
	else
	    return ""; // not entirely safe, but should never be out of bounds
    }

    public String toString() {
	String s = "<p><b>SubjectSet</b><br>";
	s += "ID: " + id + "<br>";

	for (int i = 0; i < subject_count; i++)
	    s += "   " + subject_IDs[i] + ": " + subject_labels[i];
	return s;
    }

}

package edu.ucla.wise.commons;

import org.w3c.dom.Node;

/**
 * This class is a subclass of Question and represents an open ended question on
 * the page
 */

public class Open_Question extends Question {
    /** constructor for an open ended question */
    public Open_Question(Node n) {
	// get the attributes for page item & question
	super(n);
    }

    /**
     * count number of field for an open ended question - each has only one
     * field
     */
    public int countFields() {
	return 1;
    }

    // just render html; unlike closed- and question blocks, no shared-element
    // references to knit
    public void knitRefs(Survey mySurvey) {
	html = make_html();
    }

    /** renders a form for an open ended question */
    public String make_html() {
	String s = "\n<table cellspacing='0' cellpadding='0' width=100%' border='0'><tr><td>"
		+ "\n<table cellspacing='0' cellpadding='0' width=100%' border='0'><tr><td>";
	// add/open the question stem row
	s += super.make_stem_html();
	// start new row if it is not requested by one-line layout
	if (!oneLine) {
	    s += "\n<tr>";
	    s += "<td width=10>&nbsp;</td>";
	    s += "<td width=20>&nbsp;</td>";
	    s += "<td width=570>";
	} else {
	    s += "&nbsp;&nbsp;";
	}
	s += this.form_field_html();
	s += "</td></tr></table>\n</td></tr></table>";
	return s;
    }

    // Placeholder; subclasses need to override
    public String form_field_html() {
	return "";
    }

    /** renders results for an open ended question */
    /*
     * public String render_results(Hashtable data, String whereclause) { return
     * super.render_results(data, whereclause); }
     */

    /** returns a comma delimited list of all the fields on a page */
    /*
     * public String list_fields() { return name+","; }
     */
    /** prints information for an open ended question */
    /*
     * public String print() { return super.print(); }
     */

    /**
     * print survey for an open ended question - used for admin tool: print
     * survey
     */
    /*
     * public String print_survey() { String s = super.render_form(); s +=
     * this.common_render_form(); return s; }
     */
}

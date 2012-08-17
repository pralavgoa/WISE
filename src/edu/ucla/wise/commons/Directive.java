package edu.ucla.wise.commons;

import java.io.StringWriter;
import java.util.Hashtable;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
This class is a subclass of Page_Item and represents a directive object on the page
*/

public class Directive extends Page_Item
{
/** Instance Variables */
    public String text;
    public boolean hasPrecondition=false;
    public Condition cond;

/** constructor: parse a directive node from XML */
    public Directive(Node n)
    {
        //parse the page item properties
        super(n);
        try
        {
            //convert to the translated question stem
            if(this.translation_id!=null)
            {
                text = question_translated.text;
            }
            else
            {
                Node node = n;
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                StringWriter sw = new StringWriter();
                transformer.transform(new DOMSource(node), new StreamResult(sw));
                text = sw.toString();
            }
            //parse the precondition
            NodeList nodelist = n.getChildNodes();
            for (int i=0; i < nodelist.getLength();i++)
            {
                if (nodelist.item(i).getNodeName().equalsIgnoreCase("Precondition"))
                {
                    hasPrecondition = true;
                    //create the condition object
                    cond = new Condition(nodelist.item(i)); 
                }
            }
        }
        catch (Exception e)
        {
            WISE_Application.log_error("WISE - DIRECTIVE: "+e.toString(), null);
            return;
        }
    }
    public int countFields()
    {
        return 0;
    }
    public void knitRefs(Survey mySurvey)
    {
    	html = make_html();
    }
    public String[] listFieldNames()
    {
    	return new String[0];
    }

/** render form for directive item */
    public String make_html()
    {
        String s = "";

        s += "<table cellspacing='0' cellpadding='0' width=100%' border='0'>";
        s += "<tr>";
        s += "<td><font face='Verdana, Arial, Helvetica, sans-serif' size='-1'>"+text+"</font></td>";
        s += "</tr>";
        s += "</table>";
        return s;
    }

/** print survey for directive item - used for admin tool: print survey */
    public String print_survey()
    {
        String s = "<table cellspacing='0' cellpadding='0' width=100%' border='0'>";
        s += "<tr>";
        s += "<td>"+text+"</td>";
        s += "</tr>";
        s += "</table>";
        return s;
    }
    
/** render results for directive item */
    public String render_results(Hashtable data, String whereclause)
    {
        String s = "<table cellspacing='0' cellpadding='0' width=100%' border='0'>";
        s += "<tr>";
        s += "<td><i>"+text+"</i></td>";
        s += "</tr>";
        s += "</table>";
        return s;
    }

/** print information about a directive item */
/*
    public String print()
    {
        String s = "DIRECTIVE<br>";
        s += super.print();
        s += "Text: "+text+"<br>";
        s += "<p>";
        return s;
    }
*/
}

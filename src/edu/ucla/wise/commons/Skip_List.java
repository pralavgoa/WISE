package edu.ucla.wise.commons;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
This class contains a skip list set and all its properties
*/

public class Skip_List
{
/** Instance Variables */
    public String[] values;
    public String[] pages;

    public Closed_Question question;

/** constructor: parse a skip list node from the XML */
    public Skip_List(Node n, Closed_Question cq)
    {
        try
        {
            //assign its parent node - the closed question 
            question = cq;
            
            NodeList nodelist = n.getChildNodes();
            values = new String[nodelist.getLength()];
            pages = new String[nodelist.getLength()];
            for (int i = 0; i < nodelist.getLength(); i++)
            {
                NamedNodeMap nnm1 = nodelist.item(i).getAttributes();
                Node n2 = nnm1.getNamedItem("Value");
                values[i] = n2.getNodeValue();
                n2 = nnm1.getNamedItem("Page");
                pages[i] = n2.getNodeValue();
            }
        }
        catch (Exception e)
        {
            WISE_Application.log_error("WISE - SKIP LIST CONSTRUCTOR: "+e.toString(), null);
            return;
        }
    }

/** when render the closed question, directly skip to a target defined in the skip list */
    public String render_form_element(int value)
    {
        //the value is the option index or its value in closed question
        String v = String.valueOf(value);
        String target = "DONE";
        for (int i = 0; i < values.length; i++)
        {
            //if the option value is a value set in the skip list, assign the page ID
            //then after submission, the survey will skip to that page directly by using JavaScript
            if (values[i].equalsIgnoreCase(v))
            {
                target = pages[i];
                break;
            }
        }
        String element = "onClick=\"PageSkip('"+target+"');\"";
        if (target.equalsIgnoreCase("DONE"))
            element = "";
        return element;
    }

/** when render the closed question, directly skip to a target defined in the skip list */
    public String render_form_element(String value)
    {
        String v = value;
        String target = "DONE";
        for (int i = 0; i < values.length; i++)
        {
            if (values[i].equalsIgnoreCase(v))
            {
                target = pages[i];
                break;
            }
        }
        String element = "onClick=\"PageSkip('"+target+"');\"";
        if (target.equalsIgnoreCase("DONE"))
            element = "";
        return element;
    }

/** renders the form element to skip to a target */
    public String render_identifier(int value)
    {
        String v = String.valueOf(value);
        String target = "DONE";
        for (int i = 0; i < values.length; i++)
        {
            if (values[i].equalsIgnoreCase(v))
            {
                target = pages[i];
                break;
            }
        }
        String element = "<FONT FACE='Wingdings'>&egrave;</FONT>";
        if (target.equalsIgnoreCase("DONE"))
            element = "";
        return element;
    }

/** renders the form element to skip to a target */
    public String render_identifier(String value)
    {
        String v = value;
        String target = "DONE";
        for (int i = 0; i < values.length; i++)
        {
            if (values[i].equalsIgnoreCase(v))
            {
                target = pages[i];
                break;
            }
        }
        String element = "<FONT FACE='Wingdings'>&egrave;</FONT>";
        if (target.equalsIgnoreCase("DONE"))
            element = "";
        return element;
    }

/** return the number of targets in a skip list */
    public int get_size()
    {
        return values.length;
    }

/** prints out a skip_list */
/*
    public String print()
    {
        String s = "SKIP LIST<br>";
        s += "Targets: <br>";
        for (int i = 0; i < values.length; i++)
            s += values[i]+":"+pages[i]+"<br>";
        s += "<p>";
        return s;
    }
*/


}

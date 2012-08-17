package edu.ucla.wise.commons;

import java.sql.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import java.net.*;
import java.text.*;
import java.lang.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;

/**
This class contains a thank you page object set in the preface
*/

public class Thankyou_Page
{
/** Instance Variables */
    public String id;
    public String title;
    public String banner;
    public String logo;
    public String survey_id;
    //public String irb_id;
    public String page_contents;
    
    public Preface preface;

/** constructor: parse a response set node from XML */
    public Thankyou_Page(Node n, Preface p)
    {
        try
        {
            preface = p;
            //assign id & survey id (required) no ID
            //id = n.getAttributes().getNamedItem("ID").getNodeValue();
            //survey_id = n.getAttributes().getNamedItem("Survey_ID").getNodeValue();
            
            //assign various attributes
//            Node node_child = n.getAttributes().getNamedItem("Title");
//            if(node_child !=null)
//                title = node_child.getNodeValue();
//            else
//                title = "";
            title = "Thank You";
            Node node_child = n.getAttributes().getNamedItem("BannerFileName");
            if(node_child !=null)
                banner = node_child.getNodeValue();
            else
                banner = "title.gif";
            node_child = n.getAttributes().getNamedItem("LogoFileName");
            if(node_child !=null)
                logo = node_child.getNodeValue();
            else
                logo = "proj_logo.gif";
                        
//            node_child = n.getAttributes().getNamedItem("IRB_ID");
//            if(node_child !=null)
//                irb_id = node_child.getNodeValue();
//            else
//                irb_id = "";
                        
            NodeList node_p = n.getChildNodes();
            page_contents = "";
            for(int j =0; j<node_p.getLength(); j++)
            {
               if(node_p.item(j).getNodeName().equalsIgnoreCase("p"))
                  page_contents += "<p>"+node_p.item(j).getFirstChild().getNodeValue()+"</p>";
               if(node_p.item(j).getNodeName().equalsIgnoreCase("html_content"))
               {
                  NodeList node_n = node_p.item(j).getChildNodes();
                  for(int k =0; k<node_n.getLength(); k++)
                  {
                    if(node_n.item(k).getNodeName().equalsIgnoreCase("#cdata-section"))
                      page_contents += node_n.item(k).getNodeValue();
                  }                          
               }                       
            }
        }
        catch (Exception e)
        {
            //WISE_Application.email_alert("WISE - THANKYOU PAGE : Preface = "+p.project_name+"; Study = "+p.study_space.id+" --> "+e.toString());
        	WISE_Application.log_error("WISE - THANKYOU PAGE : Preface = "+p.project_name+"--> "+e.toString(), e);
            return;
        }
    }
}
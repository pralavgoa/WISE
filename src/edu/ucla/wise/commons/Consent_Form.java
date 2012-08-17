package edu.ucla.wise.commons;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
This class contains a consent form object set in the preface
*/

public class Consent_Form
{
/** Instance Variables */
    public String id;
    public String irb_id;    
    public String title;
    public String sub_title;
    public String survey_id;
    public String consent_header_html="", consent_p="", consent_ul="", consent_s="";
    
    public Preface preface;

/** constructor: parse a response set node from XML */
    public Consent_Form(Node n, Preface p)
    {
        try
        {
            preface = p;
            //assign id, irb id & survey id (required)
            id = n.getAttributes().getNamedItem("ID").getNodeValue();
            irb_id = n.getAttributes().getNamedItem("IRB_ID").getNodeValue();
            survey_id = n.getAttributes().getNamedItem("Survey_ID").getNodeValue();
            
            //assign various attributes
            Node node_child = n.getAttributes().getNamedItem("Title");
            if(node_child !=null)
                title = node_child.getNodeValue();
            else
                title = "";
            node_child = n.getAttributes().getNamedItem("Sub_Title");
            if(node_child !=null)
                sub_title = node_child.getNodeValue();
            else
                sub_title = "";
            
                        
            NodeList node_p = n.getChildNodes();
            for(int j =0; j<node_p.getLength(); j++)
            {
               if (node_p.item(j).getNodeName().equalsIgnoreCase("p"))
                  consent_p += "<p>"+node_p.item(j).getFirstChild().getNodeValue()+"</p>";
               if (node_p.item(j).getNodeName().equalsIgnoreCase("s"))
                  consent_s += "<p>"+node_p.item(j).getFirstChild().getNodeValue()+"</p>";
               if (node_p.item(j).getNodeName().equalsIgnoreCase("html_header"))
               {
                  NodeList node_n = node_p.item(j).getChildNodes();
                  for(int k =0; k<node_n.getLength(); k++)
                  {
                    if(node_n.item(k).getNodeName().equalsIgnoreCase("#cdata-section"))
                      consent_header_html += node_n.item(k).getNodeValue();
                  }                          
               }
               if (node_p.item(j).getNodeName().equalsIgnoreCase("bullets"))
               {
                  consent_ul += "<ul>";
                  NodeList node_b = node_p.item(j).getChildNodes();
                  for(int k =0; k<node_b.getLength(); k++)
                  {
                      if (node_b.item(k).getNodeName().equalsIgnoreCase("bullet_item"))
                      {
                          NodeList node_c = node_b.item(k).getChildNodes();
                          consent_ul += "<li>";
                          for(int t =0; t < node_c.getLength(); t++)
                          {
                              if (node_c.item(t).getNodeName().equalsIgnoreCase("item_subject"))
                                  consent_ul += "<b>"+node_c.item(t).getFirstChild().getNodeValue()+"</b><br>";
                              if (node_c.item(t).getNodeName().equalsIgnoreCase("item_content"))
                              {
                                  consent_ul += node_c.item(t).getFirstChild().getNodeValue();
                                  NodeList node_u = node_c.item(t).getChildNodes();
                                  for(int tt =0; tt<node_u.getLength(); tt++)
                                  {
                                    if(node_u.item(tt).getNodeName().equalsIgnoreCase("#cdata-section"))
                                      consent_ul += node_u.item(tt).getNodeValue();
                                  }
                                  consent_ul += "<br>";
                              }
                          }
                          consent_ul += "<br>";
                      }
                  }
                  consent_ul += "</ul><br>";                          
               }
            }
        }
        catch (Exception e)
        {
            WISE_Application.log_error("WISE - CONSENT FORM : ID = "+id+"; Preface = "+p.project_name+" --> "+e.toString(), null);
            return;
        }
    }
}

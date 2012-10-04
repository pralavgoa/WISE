package edu.ucla.wise.commons;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class contains a message sequence and its properties
 */

public class Message_Sequence {
    /** Instance Variables */
    public Preface myPref;
    public String id, survey_id, irb_id;
    public String from_string = "";
    private String from_email = null;
    private String reply_string = null; // this holds both address and name
					// components, unlike "from"

    // public Hashtable message_hash;
    public Message invite_msg;
    public Message interrupt_msg;
    public Message done_msg;
    public Message review_msg;
    ArrayList start_reminders, completion_reminders, other_msgs;

    // the constructor for Message Sequences
    public Message_Sequence(Node sourceNode, Preface prefaceParam) {
	try {
	    String errTemp = "";
	    start_reminders = new ArrayList();
	    completion_reminders = new ArrayList();
	    other_msgs = new ArrayList();
	    myPref = prefaceParam;
	    // parse out the message sequence attributes: ID, survey ID and IRB
	    // ID if has
	    id = "";
	    survey_id = "";
	    irb_id = "";
	    // ID & survey ID are required -- don't need to check for nulls
	    id = sourceNode.getAttributes().getNamedItem("ID").getNodeValue();
	    survey_id = sourceNode.getAttributes().getNamedItem("Survey_ID")
		    .getNodeValue();
	    // IRB ID is optional
	    Node attrNode = sourceNode.getAttributes().getNamedItem("IRB_ID");
	    if (attrNode != null)
		irb_id = attrNode.getNodeValue();
	    // From String is optional
	    attrNode = sourceNode.getAttributes().getNamedItem("From_String");
	    if (attrNode != null) {
		from_string = attrNode.getNodeValue();
		from_string = from_string.replaceAll(",", "");
	    }
	    attrNode = sourceNode.getAttributes().getNamedItem("From_Email");
	    if (attrNode != null) {
		from_email = attrNode.getNodeValue(); // TODO: validate presence
						      // of @
	    }
	    if (from_email == null)
		from_email = WISE_Application.email_from; // always assign
							  // default email
							  // here
	    attrNode = sourceNode.getAttributes().getNamedItem("Reply_Email");
	    if (attrNode != null) {
		reply_string = attrNode.getNodeValue(); // TODO: validate
							// presence of @
		attrNode = sourceNode.getAttributes().getNamedItem(
			"Reply_String");
		if (attrNode != null) {
		    reply_string = attrNode.getNodeValue().replaceAll(",", "")
			    + " <" + reply_string + ">";
		}
	    }

	    NodeList msg_nodeList = sourceNode.getChildNodes();
	    for (int i = 0; i < msg_nodeList.getLength(); i++) {
		// create the messages for each stage in the message sequence
		Node childNode = msg_nodeList.item(i);
		String nodeName = childNode.getNodeName();
		Message new_msg = null;
		try {
		    if (nodeName.equalsIgnoreCase("Initial_Invitation")) {
			new_msg = new Message(childNode);
			invite_msg = new_msg;
		    } else if (nodeName.equalsIgnoreCase("Interrupt")) {
			new_msg = new Message(childNode);
			interrupt_msg = new_msg;
		    } else if (nodeName.equalsIgnoreCase("Done")) {
			new_msg = new Message(childNode);
			done_msg = new_msg;
		    } else if (nodeName.equalsIgnoreCase("Review")) {
			new_msg = new Message(childNode);
			review_msg = new_msg;
		    } else if (nodeName.equalsIgnoreCase("Start_Reminder")) {
			// create the reminder class
			new_msg = new Reminder(childNode);
			start_reminders.add(new_msg);
		    } else if (nodeName.equalsIgnoreCase("Completion_Reminder")) {
			new_msg = new Reminder(childNode);
			completion_reminders.add(new_msg);
		    } else if (nodeName.equalsIgnoreCase("Message")) {
			new_msg = new Message(childNode);
			other_msgs.add(new_msg);
		    }
		} catch (RuntimeException e) {
		    // TODO Auto-generated catch block
		    WISE_Application.log_error(
			    "Msg SEQ Choke at Parsing message" + nodeName
				    + ". After:" + i + "\n" + errTemp
				    + e.toString(), e);
		}
		// save the message here and in preface's master index
		try {
		    if (new_msg != null)
			myPref.add_message(new_msg, this);
		} catch (RuntimeException e) {
		    WISE_Application.log_error("Msg SEQ Choke at Adding "
			    + nodeName + ". After:\n" + errTemp + e.toString(),
			    e);
		}
	    }
	} catch (Exception e) {
	    WISE_Application.log_error("WISE - MESSAGE SEQUENCE: ID = " + id
		    + "; Survey ID = " + survey_id + " --> " + e.toString(),
		    null);
	    return;
	}
    }

    public String getFromString() // will always return a string
    {
	if (from_email == null)
	    return WISE_Application.email_from; // should actually be
						// initialized to this; just
						// checking
	if (from_string == null)
	    return from_email;
	return from_string + " <" + from_email + ">"; // brackets already added
						      // for now
    }

    public String getReplyString() // returns null if not specified
    {
	return reply_string;
    }

    public String emailID() {
	if (from_email == null)
	    return null;
	int atindx = from_email.indexOf('@');
	if (atindx > 0)
	    return from_email.substring(0, atindx);
	else
	    return from_email;
    }

    /** return the requested message type from the sequence */
    public Message get_type_message(String message_type) // use integer to get
							 // one of the other
							 // messages
    {
	if (message_type.equalsIgnoreCase("invite"))
	    return invite_msg;
	else if (message_type.equalsIgnoreCase("interrupt"))
	    return interrupt_msg;
	else if (message_type.equalsIgnoreCase("done"))
	    return done_msg;
	else if (message_type.equalsIgnoreCase("review"))
	    return review_msg;
	else {
	    int index = Integer.parseInt(message_type);
	    return (Message) other_msgs.get(index);
	}
    }

    public Reminder get_start_reminder(int index) {
	return (Reminder) start_reminders.get(index);
    }

    public Reminder get_completion_reminder(int index) {
	return (Reminder) completion_reminders.get(index);
    }

    public int total_start_reminders() {
	return start_reminders.size();
    }

    public int total_completion_reminders() {
	return completion_reminders.size();
    }

    public int total_other_messages() {
	return other_msgs.size();
    }

    public String toString() {
	String resp = "<b>Message Sequence: " + id + "</b> for survey ID(s): "
		+ survey_id + "<br>Messages<br>";
	resp += invite_msg.toString();
	resp += interrupt_msg.toString();
	resp += done_msg.toString();
	resp += "Start reminders n=" + start_reminders.size()
		+ "; Completion reminders n=" + completion_reminders.size()
		+ "<br>";
	return resp;
    }
}

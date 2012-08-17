package edu.ucla.wise.commons;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is a preface object and contains information about a specific
 * welcome page and consent form
 */

public class Preface {
	/** Instance Variables */
	public String project_name = "";

	public Hashtable welcome_pages = new Hashtable();
	public Hashtable consent_forms = new Hashtable();
	public Hashtable irb_sets = new Hashtable();
	private Hashtable all_message_sequences = new Hashtable();
	private Hashtable all_messages = new Hashtable();
	private Hashtable messageSequences_byMsgID = new Hashtable();
	public Thankyou_Page thankyou_page;

	// public Study_Space study_space;

	/** constructor - create a preface by parsing the xml file */
	public Preface(String preface_file_name) {
		try {
			// Directly read the preface file
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(CommonUtils.loadResource(preface_file_name));
			NodeList root_node = doc.getElementsByTagName("Preface");
			for (int k = 0; k < root_node.getLength(); k++) {
				Node nd = root_node.item(k).getAttributes()
						.getNamedItem("Project_Name");
				if (nd != null)
					project_name = nd.getNodeValue();
			}

			// parse out the welcome pages
			NodeList nodelist = doc.getElementsByTagName("Welcome_Page");
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				// create the welcome page class
				Welcome_Page wp = new Welcome_Page(node, this);
				welcome_pages.put(wp.id, wp);
			}

			// parse out the thankyou pages
			nodelist = doc.getElementsByTagName("ThankYou_Page");
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				// create the thank you page class
				thankyou_page = new Thankyou_Page(node, this);
			}

			// parse out the IRB entities
			nodelist = doc.getElementsByTagName("IRB");
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				// create the welcome page class
				IRB_Set irb = new IRB_Set(node, this);
				irb_sets.put(irb.id, irb);
			}

			// parse out the consent forms
			nodelist = doc.getElementsByTagName("Consent_Form");
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				// create the consent form class
				Consent_Form cf = new Consent_Form(node, this);
				consent_forms.put(cf.id, cf);
			}

			// parse out the message sequence
			nodelist = doc.getElementsByTagName("Message_Sequence");
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				// create the consent form class
				Message_Sequence ms = new Message_Sequence(node, this);
				all_message_sequences.put(ms.id, ms);
			}

			// after reading in & creating all message objects, resolve the
			// references among them
			Enumeration e = all_messages.elements();
			while (e.hasMoreElements()) {
				Message msg = (Message) e.nextElement();
				msg.resolveRef(this);
			}
		} catch (Exception e) {
			WISE_Application.log_error(
					"WISE - PREFACE load error: " + e.toString(), e);
			return;
		}
	}

	// pass down to each message the appropriately localized URLs (eg for
	// images) from StudySpace (add more args here as needed)
	public void setHrefs(String srvltPath, String imgPath) {
		Enumeration e = all_messages.elements();
		while (e.hasMoreElements()) {
			Message msg = (Message) e.nextElement();
			msg.setHrefs(srvltPath, imgPath);
		}
	}

	/** search by the ID and returns a welcome page */
	public Welcome_Page get_welcome_page(String wp_id) {
		Welcome_Page wp = null;
		if (welcome_pages != null)
			wp = (Welcome_Page) welcome_pages.get(wp_id);
		return wp;
	}

	/** return the thank you page, assume a universl thankyou page per study */
	public Thankyou_Page get_thankyou_page() {
		if (thankyou_page != null)
			return thankyou_page;
		else
			return null;
	}

	/** search by the survey ID & irb ID and returns a welcome page */
	// returns null only if no welcome pages defined;
	// otherwise if no surveyID, irbID match, returns last Welcome_Page as
	// default
	public Welcome_Page get_welcome_page_survey_irb(String surveyID,
			String irbID) {
		Welcome_Page wp = null; // returns null
		if (welcome_pages != null) {
			Enumeration e = welcome_pages.elements();
			while (e.hasMoreElements()) {
				wp = (Welcome_Page) e.nextElement();
				if (wp.survey_id.equalsIgnoreCase(surveyID))
					if (wp.irb_id.equalsIgnoreCase(irbID))
						return wp;
			}
		}
		return wp;
	}

	/** search by the ID and returns a IRB set */
	public IRB_Set get_irb_set(String irb_id) {
		IRB_Set irb = null;
		if (irb_sets != null)
			irb = (IRB_Set) irb_sets.get(irb_id);
		return irb;
	}

	/** search by the ID and returns a consent form */
	public Consent_Form get_consent_form(String cf_id) {
		Consent_Form cf = null;
		if (consent_forms != null)
			cf = (Consent_Form) consent_forms.get(cf_id);
		return cf;
	}

	/** search by the survey ID & irb ID and returns a consent form */
	public Consent_Form get_consent_form_survey_irb(String surveyID,
			String irbID) {
		Consent_Form cf = null;
		if (consent_forms != null) {
			Enumeration e = consent_forms.elements();
			while (e.hasMoreElements()) {
				cf = (Consent_Form) e.nextElement();
				if (cf.survey_id.equalsIgnoreCase(surveyID))
					if (cf.irb_id.equalsIgnoreCase(irbID))
						return cf;
			}
		}
		return cf;
	}

	/** return the message sequence with a given ID */
	public Message_Sequence get_message_sequence(String seq_id) {
		if(seq_id == null)
			return null;
		return (Message_Sequence) all_message_sequences.get(seq_id);
	}

	/** return the message sequence for a given Message ID */
	public Message_Sequence get_messageSequence_4msgID(String msg_id) {
		return (Message_Sequence) messageSequences_byMsgID.get(msg_id);
	}

	// extract out the message sequence matching a survey, irb combo --
	// deprecated because may not be just one
	// public Message_Sequence get_message_sequence(String survey_id, String
	// irb_id)
	// {
	// Message_Sequence msg_seq = null;
	// if (irb_id == null)
	// irb_id = "";
	// //get the message sequence from hashtable
	// for (Enumeration e = all_message_sequences.elements();
	// e.hasMoreElements();)
	// {
	// msg_seq = (Message_Sequence) e.nextElement();
	//
	// if((msg_seq.survey_id.indexOf(survey_id) != -1) &&
	// msg_seq.irb_id.equalsIgnoreCase(irb_id)) //irb is "" if unspec
	// break;
	// }
	// return msg_seq;//currently returns last message sequence by default
	// }

	// extract array of all message sequences for a survey */
	public Message_Sequence[] get_message_sequences(String survey_id) {
		Message_Sequence[] msg_seqs = new Message_Sequence[0];// return a
																// 0-length
																// array as
																// default
		ArrayList tempList = new ArrayList();
		// get the message sequence from hashtable
		for (Enumeration e = all_message_sequences.elements(); e
				.hasMoreElements();) {
			Message_Sequence msg_seq = (Message_Sequence) e.nextElement();

			if (msg_seq.survey_id.indexOf(survey_id) != -1)
				tempList.add(msg_seq);
		}
		if (tempList.size() > 0)
			msg_seqs = (Message_Sequence[]) tempList
					.toArray(new Message_Sequence[tempList.size()]);
		return msg_seqs;
	}

	/**
	 * Add message for retrieval by ID and also for retrieval of message
	 * sequence by message ID
	 */
	public void add_message(Message new_msg, Message_Sequence msg_seq) {
		if (new_msg == null)
			return;
		all_messages.put(new_msg.id, new_msg);
		messageSequences_byMsgID.put(new_msg.id, msg_seq);
	}

	/** return the message with a given ID */
	public Message get_message(String msg_id) {
		// the following is safe because all_messages guaranteed initialized to
		// hashtable
		// Bad XML IDref will return null.
		return (Message) all_messages.get(msg_id);
	}

	/** return all initial invitation messages with a given ID */
	public Message[] get_all_initial_messages_forSurveyID(String svy_id) {
		ArrayList foundMsgs = new ArrayList();
		for (Enumeration e = all_message_sequences.elements(); e
				.hasMoreElements();) {
			Message_Sequence msg_sequence = (Message_Sequence) e.nextElement();
			// search by the survey ID
			if (msg_sequence.survey_id.matches(svy_id)) // try using survey ID
														// as a regexp
				foundMsgs.add(msg_sequence.get_type_message("invite"));
		}
		return (Message[]) foundMsgs.toArray(new Message[foundMsgs.size()]);
	}

	/**
	 * return all invitation messages for a given survey ID -- DEPRECATED public
	 * Message[] get_all_messages_forSurveyID(String svy_id) { ArrayList
	 * foundMsgs = new ArrayList(); for (Enumeration e =
	 * all_messages.elements(); e.hasMoreElements();) { Message msg = (Message)
	 * e.nextElement(); //search by the survey ID
	 * if(msg.survey().equalsIgnoreCase(svy_id)) foundMsgs.add(msg); } return
	 * (Message[]) foundMsgs.toArray(new Message[foundMsgs.size()]); }
	 */

	public String toString() {
		String resp = "<b>Preface: </b><br>Message sequences<br>";
		Message_Sequence msgsq;
		Enumeration e1 = all_message_sequences.elements();
		while (e1.hasMoreElements()) {
			msgsq = (Message_Sequence) e1.nextElement();
			resp += msgsq.toString();
		}
		resp += "<B>Total message count: " + all_messages.size() + "</b>";

		return resp;
	}

}

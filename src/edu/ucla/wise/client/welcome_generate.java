package edu.ucla.wise.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ucla.wise.commons.Consent_Form;
import edu.ucla.wise.commons.IRB_Set;
import edu.ucla.wise.commons.Preface;
import edu.ucla.wise.commons.Study_Space;
import edu.ucla.wise.commons.Surveyor_Application;
import edu.ucla.wise.commons.User;
import edu.ucla.wise.commons.WISE_Application;
import edu.ucla.wise.commons.Welcome_Page;

/*
 Generate the welcome page before displaying the consent form 
 */

public class welcome_generate extends HttpServlet {
	static final long serialVersionUID = 1000;

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// prepare for writing
		PrintWriter out;
		res.setContentType("text/html");
		out = res.getWriter();

		HttpSession session = req.getSession(true);
		// if session is new, then show the session expired info
		if (session.isNew()) {
			res.sendRedirect(Surveyor_Application.shared_file_url + "error"
					+ Surveyor_Application.html_ext);
			return;
		}

		// get the user from session
		User theUser = (User) session.getAttribute("USER");
		Study_Space study_space = (Study_Space) session
				.getAttribute("STUDYSPACE");
		if (theUser == null || study_space == null) {
			out.println("<p>Error: Can't find the user & study space.</p>");
			return;
		}

		String error = null;
		Preface pf = study_space.get_preface();
		if (pf != null) {
			if ((pf.irb_sets.size() > 0 && theUser.irb_id == null)
					|| theUser.currentSurvey.id == null) {
				error = "Error: Cannot find your IRB or Survey ID ";
				WISE_Application.log_error("WISE - WELCOME GENERATE: " + error,
						null);
				out.println("<p>" + error + "</p>");
				return;
			}

			Welcome_Page w_page = pf.get_welcome_page_survey_irb(
					theUser.currentSurvey.id, theUser.irb_id);
			if (w_page == null) {
				error = "Error: Can't find a default Welcome Page in the Preface for survey ID="
						+ theUser.currentSurvey.id
						+ " and IRB="
						+ theUser.irb_id;
				WISE_Application.log_error("WISE - WELCOME GENERATE: " + error,
						null);
				out.println("<p>" + error + "</p>");
				return;
			}

			// TODO: get a default logo if the IRB is empty
			String title, banner, logo = "ucla.gif", apr_numb = null, exp_date = null;
			title = w_page.title;
			banner = w_page.banner;
			logo = w_page.logo;

			// check the irb set
			if (!theUser.irb_id.equalsIgnoreCase("")) {
				IRB_Set irb_set = pf.get_irb_set(theUser.irb_id);
				if (irb_set != null) {
					if (!irb_set.irb_logo.equalsIgnoreCase(""))
						logo = irb_set.irb_logo;
					if (!irb_set.approval_number.equalsIgnoreCase(""))
						apr_numb = irb_set.approval_number;
					if (!irb_set.expir_date.equalsIgnoreCase(""))
						exp_date = irb_set.expir_date;
				} else {
					out.println("<p>Can't find the IRB with the number sepecified in welcome page</p>");
					return;
				}
			}

			// print out welcome page
			String welcome_html = "";
			// compose the common header
			welcome_html += "<HTML><HEAD><TITLE>" + title
					+ " - Welcome</TITLE>";
			welcome_html += "<META http-equiv=Content-Type content='text/html; charset=iso-8859-1'>";
			welcome_html += "<LINK href='" + "styleRender?app="
					+ study_space.study_name + "&css=style.css"
					+ "' type=text/css rel=stylesheet>";
			welcome_html += "<META content='MSHTML 6.00.2800.1170' name=GENERATOR></HEAD>";
			// compose the top part of the body
			welcome_html += "<body><center>";
			// welcome_html += "<body bgcolor=#FFFFCC text=#000000><center>";
			welcome_html += "<table width=100% cellspacing=1 cellpadding=9 border=0>";
			welcome_html += "<tr><td width=98 align=center valign=top><img src='"
					+ "imageRender?app="
					+ study_space.study_name
					+ "&img="
					+ logo + "' border=0 align=middle></td>";
			welcome_html += "<td width=695 align=center valign=middle><img src='"
					+ "imageRender?app="
					+ study_space.study_name
					+ "&img="
					+ banner + "' border=0 align=middle></td>";
			welcome_html += "<td rowspan=6 align=center width=280>&nbsp;</td></tr>";
			welcome_html += "<tr><td width=98 rowspan=3>&nbsp;</td>";
			welcome_html += "<td class=head>WELCOME</td></tr>";
			welcome_html += "<tr><td width=695 align=left colspan=1>";
			// get the welcome contents
			welcome_html += w_page.page_contents;
			welcome_html += "</td></tr><tr>";

			// add the bottom part
			// lookup the consent form by user's irb id, otherwise, skip the
			// consent form
			Consent_Form c_form = null;
			if (!theUser.irb_id.equalsIgnoreCase(""))
				c_form = pf.get_consent_form_survey_irb(
						theUser.currentSurvey.id, theUser.irb_id);

			if (c_form != null)
				welcome_html += "<td width=695 align=center colspan=1><a href='"
						+ Surveyor_Application.servlet_url
						+ "consent_generate'><img src='"
						+ "imageRender?img=continue.gif' border=0 align=absmiddle></a></td>";
			else
				welcome_html += "<td width=695 align=center colspan=1><a href='"
						+ Surveyor_Application.servlet_url
						+ "consent_record?answer=no_consent'><img src='"
						+ "imageRender?img=continue.gif' border=0 align=absmiddle></a></td>";

			welcome_html += "</tr>";

			// if there are the expriation date and approval date found in IRB
			if (exp_date != null && apr_numb != null) {
				welcome_html += "<tr><td><p align=left><font size=2><b>IRB Number: "
						+ apr_numb + "<br>";
				welcome_html += "Expiration Date: " + exp_date
						+ "</b></font></p>";
				welcome_html += "</td></tr>";
			}
			welcome_html += "</table></center></body></html>";
			// print out the html form
			out.println(welcome_html);

		} else {
			error = "Error: Can't get the preface";
		}

		if (error != null) {
			WISE_Application.log_error("WISE - WELCOME GENERATE: " + error,
					null);
			out.println("<p>" + error + "</p>");
		}
		out.close();
		theUser.record_welcome_hit();
		return;
	}

}

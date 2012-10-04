package edu.ucla.wise.admin;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import edu.ucla.wise.commons.AdminInfo;

/*
 prints an overview list of the pages in a survey
 */

public class xsltview extends HttpServlet {
    static final long serialVersionUID = 1000;

    public void service(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	response.setContentType("text/html");
	// don't need PrintWriter; instead get a StreamResult inside the
	// try/catch below
	// PrintWriter out = response.getWriter();

	// get information from java session
	String path = request.getContextPath();
	HttpSession session = request.getSession(true);
	if (session.isNew()) {
	    response.sendRedirect(path + "/index.htm");
	    return;
	}

	String survey_name = request.getParameter("FileName");
	// check if the session is still valid
	AdminInfo admin_info = (AdminInfo) session.getAttribute("ADMIN_INFO");

	if (survey_name == null || admin_info == null) {
	    AdminInfo.log_error(
		    "Wise Admin - XSLT View Error: can't get the admin info",
		    null);
	    return;
	}

	// get the file xml and processor xslt

	String f_xml = admin_info.study_xml_path + survey_name;

	String f_xslt = "/CME/WISE_pages/style/survey_all_pg.xslt";

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	try {
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse(f_xml);

	    // use a Transformer for output
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer(new StreamSource(
		    f_xslt));

	    // reserve the DOCTYPE setting in XML file
	    if (document.getDoctype() != null) {
		String systemValue = (new File(document.getDoctype()
			.getSystemId())).getName();
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
			systemValue);
	    }

	    DOMSource source = new DOMSource(document);
	    StreamResult result = new StreamResult(response.getOutputStream());
	    transformer.transform(source, result);

	} catch (Exception e) {
	    System.out.println("  " + e.getMessage());
	    AdminInfo.log_error(
		    "Wise Admin - XSLT View Error: " + e.getMessage(), e);
	}
    }
}

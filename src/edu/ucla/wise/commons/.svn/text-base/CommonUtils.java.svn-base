/**
 * 
 */
package edu.ucla.wise.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

/**
 * @author ssakdeo
 * 
 */
public class CommonUtils {

	static Logger log = Logger.getLogger(CommonUtils.class);

	public static boolean isEmpty(String checkString) {
		return (checkString == null || checkString.isEmpty());
	}

	public static InputStream loadResource(String relPath) {
		ClassLoader c1 = Thread.currentThread().getContextClassLoader();
		URL url = c1.getResource(relPath);
		InputStream fileInputStream = null;
		if (url != null) {
			try {
				fileInputStream = new FileInputStream(URLDecoder.decode(url
						.getFile()));
			} catch (FileNotFoundException e) {
				log.error("File not found", e);
			}
		}
		return fileInputStream;
	}

	public static String getAbsolutePath(String relPath) {

		String absolutePath = null;
		ClassLoader c1 = Thread.currentThread().getContextClassLoader();
		URL url = c1.getResource(relPath);
		if (url != null) {
			absolutePath = URLDecoder.decode(url.getPath());
		}
		return absolutePath;
	}

	public static String base64Encode(String surveyId) {

		if (surveyId == null) {
			return surveyId;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStream b64os = null;
		try {
			b64os = MimeUtility.encode(baos, "base64");
			b64os.write(surveyId.getBytes());

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				b64os.close();
			} catch (IOException e) {
			}
		}
		return new String(baos.toByteArray());
	}

	public static String base64Decode(String encodedSurveyId) {
		if (encodedSurveyId == null) {
			return encodedSurveyId;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(
				encodedSurveyId.getBytes());
		InputStream b64is = null;
		try {
			b64is = MimeUtility.decode(bais, "base64");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] tmp = new byte[encodedSurveyId.getBytes().length];
		int n = 0;
		try {
			n = b64is.read(tmp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] res = new byte[n];
		System.arraycopy(tmp, 0, res, 0, n);
		return new String(res);
	}

	public static void main(String args[]) {

		System.out.println("Encoding string = " + base64Encode("BIP_User"));
		System.out.println("Decoding string = " + base64Decode(base64Encode("BIP_User")));
	}
}

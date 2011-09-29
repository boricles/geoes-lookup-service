package util;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail extends Thread {
	public Mail() {

	}

	String from;

	String to;

	String subject;

	String messageTxt;

	List filename;

	String host;

	String port;

	static String user;

	static String password;

	boolean serverAutentication;

	boolean ssl;

	private static class MiAutenticador extends Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, password);
		}
	}

	public Mail(String host, String port, String from, String to,
			String subject, String messageTxt, boolean serverAutentication,
			String user, String password, boolean ssl) {
		this.host = host;
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.messageTxt = messageTxt;
		this.serverAutentication = serverAutentication;
		this.user = user;
		this.password = password;
		this.port = port;
		this.ssl = ssl;
	}

	public Mail(String host, String port, String from, String to,
			String subject, String messageTxt, List filename,
			boolean serverAutentication, String user, String password,
			boolean ssl) {
		this.host = host;
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.messageTxt = messageTxt;
		this.filename = filename;
		this.serverAutentication = serverAutentication;
		this.user = user;
		this.password = password;
		this.port = port;
		this.ssl = ssl;
	}

	public void run() {
		// ejecutar el envio de correo
		try {
			if (filename == null) {
				sendMail(host, port, from, to, subject, messageTxt,
						serverAutentication, user, password, ssl);
			} else {
				sendMail(host, port, from, to, subject, messageTxt, filename,
						serverAutentication, user, password, ssl);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}

	public static void sendMail(String host, String port, String from,
			String to, String subject, String messageTxt,
			boolean serverAutentication, String user, String password,
			boolean ssl) throws Exception {
		// Para enviar con attachment
		Session session2;
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		// fill props with any information
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.from", from);
		if (serverAutentication) {
			props.put("mail.user", user);
			props.put("mail.password", password);
			props.put("mail.smtp.auth", "true");
			Authenticator auth = new MiAutenticador();
			session2 = Session.getInstance(props, auth);
		} else {
			session2 = Session.getInstance(props);
		}
		Message msg = new MimeMessage(session2);
		msg.setFrom();
		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to, false));
		msg.setSubject(subject);
		msg.setSentDate(new java.util.Date());

		MimeBodyPart mbp = new MimeBodyPart();
		mbp.setText(messageTxt);

		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp);
		msg.setContent(mp);
		// String filename="C:\\temp\\ejemplo.xls";

		// Put parts in message
		msg.setContent(mp);

		// Send the message
		Transport.send(msg);
	}

	public static void sendMail(String host, String port, String from,
			String to, String subject, String messageTxt, List filename,
			boolean serverAutentication, String user, String password,
			boolean ssl) throws Exception {
		// Para enviar con attachment

		Session session2;
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		// fill props with any information

		props.put("mail.transport.protocol", "smtp");
		props.put("mail.from", from);
		if (!port.equals(""))
			props.put("mail.smtp.port", port);
		if (ssl) {
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
		}
		if (serverAutentication) {
			props.put("mail.user", user);
			props.put("mail.password", password);
			props.put("mail.smtp.auth", "true");
			Authenticator auth = new MiAutenticador();
			session2 = Session.getInstance(props, auth);
		} else {
			session2 = Session.getInstance(props);
		}
		Message msg = new MimeMessage(session2);
		msg.setFrom();
		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to, false));
		msg.setSubject(subject);
		msg.setSentDate(new java.util.Date());

		MimeBodyPart mbp = new MimeBodyPart();
		mbp.setText(messageTxt);

		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp);
		msg.setContent(mp);

		Iterator i = filename.iterator();
		while (i.hasNext()) {
			String nombre = (String) i.next();
			mbp = new MimeBodyPart();
			DataSource source = new FileDataSource(nombre);

			mbp.setDataHandler(new DataHandler(source));
			mbp.setFileName(nombre);
			mp.addBodyPart(mbp);

		}
		// Put parts in message
		msg.setContent(mp);

		// Send the message
		Transport.send(msg);

	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
public static void main(String[] args) {
		
		Mail hc = new Mail("mail.fi.upm.es", "587", "vsaquicela@fi.upm.es", "vsaquicela@gmail.com", "appkey:", "nada",true, "vsaquicela", "soloyose", true);
		hc.start();
}
}

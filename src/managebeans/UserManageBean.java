package managebeans;

import java.io.Serializable;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import util.Mail;

import es.upm.fi.dia.oeg.controller.UserJpaController;
import es.upm.fi.dia.oeg.controller.exceptions.PreexistingEntityException;
import es.upm.fi.dia.oeg.entity.User;

@ManagedBean(name = "UserManageBean")
@SessionScoped
public class UserManageBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private User user = new User();

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String save() {
		UserJpaController uj = new UserJpaController();
		try {
			UUID key = UUID.randomUUID();
			user.setKey(key.toString());
			uj.create(user);
		} catch (PreexistingEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Creado con exito");
		sendMail(user);
		user = new User();
		return null;
	}

	public void submit(ActionEvent event) {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Correct", "Correct");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void sendMail(User u) {
		// send by mail the key		
		Mail hc = new Mail("mail.fi.upm.es", "587", "vsaquicela@fi.upm.es", u.getEmail(), "appkey:", u.getKey(),true, "vsaquicela", "soloyose", true);
		hc.start();
	
	}
}
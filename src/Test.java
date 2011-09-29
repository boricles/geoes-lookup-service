import util.Mail;
import es.upm.fi.dia.oeg.controller.UserJpaController;
import es.upm.fi.dia.oeg.controller.exceptions.PreexistingEntityException;
import es.upm.fi.dia.oeg.entity.User;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

		// TODO Auto-generated method stub
		UserJpaController uj= new UserJpaController();
		User u = new User();
		u.setNombre("A");
		u.setApellidos("A");
		try {
			uj.create(u);
		} catch (PreexistingEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		uj.findUserEntities();
		
	}

}

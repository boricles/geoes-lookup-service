package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managebeans.UserManageBean;

import es.upm.fi.dia.oeg.controller.UserJpaController;
import es.upm.fi.dia.oeg.controller.exceptions.NonexistentEntityException;
import es.upm.fi.dia.oeg.entity.History;
import es.upm.fi.dia.oeg.entity.User;
import es.upm.fi.dia.oeg.lookupservice.Indexer;

/**
 * 
 * @author vsaquicela
 */
@WebServlet(name = "luceneSearch", urlPatterns = { "/search" }, initParams = {
		@WebInitParam(name = "appkey", value = "Value"),
		@WebInitParam(name = "term", value = "Value") })
public class LuceneSearch extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String appkey = request.getParameter("appkey");
		String term = request.getParameter("term");
		
		String format;
		
		 format= request.getParameter("format");
		 if (format==null){
			 format="";
		 }
		
				
		if (format.equals("xml")){
			response.setContentType("text/xml;charset=UTF-8");
		}else{
			if (format.equals("html")){
				response.setContentType("text/html;charset=UTF-8");
			}else{
				if (format.equals("plain")){
					response.setContentType("text/plain;charset=UTF-8");
				}else{
					//default
					response.setContentType("text/html;charset=UTF-8");
				}
			}
		}
		PrintWriter out = response.getWriter();
		// Check the appkey into the database
		UserJpaController uj = new UserJpaController();
		User u = uj.findAppkey(appkey);
		if (u == null) {
			try {

				out.println("<html>");
				out.println("<head>");
				out.println("<title>OEG-LuceneSearch</title>");
				out.println("</head>");
				out.println("<body>");
				out.println("<h1>appkey:" + appkey + " not found.</h1>");
				out.println("</body>");
				out.println("</html>");

			} finally {
				out.close();
			}
		} else {
			//History register
			History h = new History();
			h.setFecha(Calendar.getInstance().getTime());
			h.setTermino(term);
			h.setIdUser(u);
			u.getHistoryList().add(h);
			
			try {

				out.println("<html>");
				out.println("<head>");
				out.println("<title>OEG-LuceneSearch</title>");
				out.println("</head>");
				out.println("<body>");
				out.println("<H1> OEG-Results </H1>");
				Indexer i = new Indexer();
				List<String> l = i.search(term);
				String result="Resources:";
				out.println("<TABLE><TR><TH>Resource</TH></TR>");			
				for (String s : l) {						
					out.println("<TR><TD>"+s+"</TD></TR>");
					result=result+s+",";
				}		
				out.println("</TABLE>");
				out.println("</body>");
				out.println("</html>");
				h.setResultado(result);
				try {
					uj.edit(u);
				} catch (NonexistentEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} finally {
				out.close();
			}
		}

	}

	// <editor-fold defaultstate="collapsed"
	// desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 * 
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}

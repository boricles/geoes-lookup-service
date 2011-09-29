/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.controller;

import es.upm.fi.dia.oeg.controller.exceptions.NonexistentEntityException;
import es.upm.fi.dia.oeg.controller.exceptions.PreexistingEntityException;
import es.upm.fi.dia.oeg.entity.User;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import util.EntityManagerUtil;
import es.upm.fi.dia.oeg.entity.History;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vsaquicela
 */
public class UserJpaController implements Serializable {

    public UserJpaController() {
       
    }
    

    public EntityManager getEntityManager() {
        return EntityManagerUtil.get().createEntityManager();
    }

    public void create(User user) throws PreexistingEntityException, Exception {
        if (user.getHistoryList() == null) {
            user.setHistoryList(new ArrayList<History>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<History> attachedHistoryList = new ArrayList<History>();
            for (History historyListHistoryToAttach : user.getHistoryList()) {
                historyListHistoryToAttach = em.getReference(historyListHistoryToAttach.getClass(), historyListHistoryToAttach.getIdHistory());
                attachedHistoryList.add(historyListHistoryToAttach);
            }
            user.setHistoryList(attachedHistoryList);
            em.persist(user);
            for (History historyListHistory : user.getHistoryList()) {
                User oldIdUserOfHistoryListHistory = historyListHistory.getUser();
                historyListHistory.setIdUser(user);
                historyListHistory = em.merge(historyListHistory);
                if (oldIdUserOfHistoryListHistory != null) {
                    oldIdUserOfHistoryListHistory.getHistoryList().remove(historyListHistory);
                    oldIdUserOfHistoryListHistory = em.merge(oldIdUserOfHistoryListHistory);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUser(user.getIdUser()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            user = em.merge(user);
           
            
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = user.getIdUser();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getIdUser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<History> historyList = user.getHistoryList();
            for (History historyListHistory : historyList) {
                historyListHistory.setIdUser(null);
                historyListHistory = em.merge(historyListHistory);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    public List<User> findWhere(String where){
    	EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery(" from User u " + where);
            
            List<User> lp = q.getResultList();
            return lp;
        }catch(Exception e){
        	System.out.println(e.getMessage());
        }
        return null;
    }
    public User findAppkey(String appkey){
    	EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery(" from User u where appkey='" + appkey+"'");            
            List<User> lp = q.getResultList();
            if (lp.size()==1){
            	return lp.get(0);
            }            
        }catch(Exception e){
        	System.out.println(e.getMessage());
        }
        return null;
    }
}

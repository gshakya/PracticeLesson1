package com.example;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.example.model.Person;



public class Application {
	private static final SessionFactory sessionFactory;
	static {
		try {
			Configuration configuration = new Configuration();
			
			// This step will read hibernate.cfg.xml
			sessionFactory = configuration.configure().buildSessionFactory(); 

		} catch (Throwable ex) {
			System.err.println(ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static void main(String[] args) {
		Session session = null;
		Transaction tx = null;
		
		try {
			session = sessionFactory.openSession();

			// Create new instance of Person and set values to it
			Person person = new Person();
			person.setFirstName("George");
			person.setLastName("Washington");

			tx = session.beginTransaction();
			// save the person
			session.persist(person);
			tx.commit();
			output("get ID from detached bean : " + person.getId());

		} catch (HibernateException e) {
			System.err.println(e);
			if (tx != null) tx.rollback();
		} finally {
			if (session != null) session.close();
		}
		
		// ANOTHER SESSION
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			
			// retrieve all
			@SuppressWarnings("unchecked")
			List<Person> persons = 
				session.createQuery("from Person pe order by pe.lastName desc").list();
			System.out.println("-----size-----"+persons.size());
			for (Person p : persons) {
				output("First name=" + p.getFirstName()
						+ ", Last name= " + p.getLastName());
			}

			tx.commit();

		} catch (HibernateException e) {
			System.err.println(e);
			if (tx != null)  tx.rollback();
		} finally {
			if (session != null) session.close();
		}
		
		if (!sessionFactory.isClosed()) {
			sessionFactory.close();
		}
	}

	private static void output(String output) {
		System.out.println("================= OUTPUT =================");
		System.out.println(output);		
		System.out.println("==========================================");
	}
}

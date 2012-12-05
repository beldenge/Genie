package com.ciphertool.genetics.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ciphertool.genetics.entities.ExecutionStatistics;

public class ExecutionStatisticsDao {
	private SessionFactory sessionFactory;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean insert(ExecutionStatistics executionStatistics) {
		Session session = sessionFactory.getCurrentSession();
		session.persist(executionStatistics);
		return true;
	}

	@Required
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}

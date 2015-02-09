/**
 * Copyright 2015 George Belden
 * 
 * This file is part of ZodiacGenetics.
 * 
 * ZodiacGenetics is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * ZodiacGenetics is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ZodiacGenetics. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ciphertool.genetics.dao;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ciphertool.genetics.entities.statistics.ExecutionStatistics;

public class ExecutionStatisticsDao {
	private Logger log = Logger.getLogger(getClass());

	private SessionFactory sessionFactory;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean insert(ExecutionStatistics executionStatistics) {
		if (executionStatistics == null) {
			log.warn("Attempted to insert null ExecutionStatistics.  Returning.");

			return false;
		}

		Session session = sessionFactory.getCurrentSession();
		session.persist(executionStatistics);
		return true;
	}

	@Required
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}

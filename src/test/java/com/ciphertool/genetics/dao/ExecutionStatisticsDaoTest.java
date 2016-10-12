/**
 * Copyright 2015 George Belden
 * 
 * This file is part of Genie.
 * 
 * Genie is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * Genie is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Genie. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ciphertool.genetics.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.entities.statistics.ExecutionStatistics;

public class ExecutionStatisticsDaoTest {
	private static ExecutionStatisticsDao executionStatisticsDao;
	private static SessionFactory sessionFactoryMock;

	@BeforeClass
	public static void setUp() {
		executionStatisticsDao = new ExecutionStatisticsDao();
		sessionFactoryMock = mock(SessionFactory.class);

		executionStatisticsDao.setSessionFactory(sessionFactoryMock);
	}

	@Before
	public void resetMocks() {
		reset(sessionFactoryMock);

		Session sessionMock = mock(Session.class);
		when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
	}

	@Test
	public void testSetSessionFactory() {
		ExecutionStatisticsDao executionStatisticsDao = new ExecutionStatisticsDao();
		executionStatisticsDao.setSessionFactory(sessionFactoryMock);

		Field sessionFactoryField = ReflectionUtils.findField(ExecutionStatisticsDao.class, "sessionFactory");
		ReflectionUtils.makeAccessible(sessionFactoryField);
		SessionFactory sessionFactoryFromObject = (SessionFactory) ReflectionUtils.getField(sessionFactoryField,
				executionStatisticsDao);

		assertSame(sessionFactoryMock, sessionFactoryFromObject);
	}

	@Test
	public void testInsert() {
		ExecutionStatistics executionStatisticsToInsert = new ExecutionStatistics();

		boolean result = executionStatisticsDao.insert(executionStatisticsToInsert);

		verify(sessionFactoryMock, times(1)).getCurrentSession();
		verifyNoMoreInteractions(sessionFactoryMock);
		assertTrue(result);
	}

	@Test
	public void testInsertNull() {
		boolean result = executionStatisticsDao.insert(null);

		verify(sessionFactoryMock, never()).getCurrentSession();
		assertFalse(result);
	}
}

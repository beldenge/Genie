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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mongodb.core.MongoOperations;

import com.ciphertool.genetics.entities.statistics.ExecutionStatistics;

public class ExecutionStatisticsDao {
	private Logger log = Logger.getLogger(getClass());

	private MongoOperations mongoOperations;

	public boolean insert(ExecutionStatistics executionStatistics) {
		if (executionStatistics == null) {
			log.warn("Attempted to insert null ExecutionStatistics.  Returning.");

			return false;
		}

		mongoOperations.insert(executionStatistics);

		return true;
	}

	@Required
	public void setMongoTemplate(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
}

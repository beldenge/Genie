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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mongodb.core.MongoOperations;

import com.ciphertool.genetics.entities.statistics.GenerationStatistics;

public class GenerationStatisticsDao {
	private Logger			log	= LoggerFactory.getLogger(getClass());

	private MongoOperations	mongoOperations;

	public boolean insertBatch(List<GenerationStatistics> statisticsBatch) {
		if (statisticsBatch == null || statisticsBatch.isEmpty()) {
			log.warn("Attempted to insert GenerationStatistics in batch which was found to be null or empty.  Unable to continue, thus returning false.");

			return false;
		}

		mongoOperations.insert(statisticsBatch, GenerationStatistics.class);

		return true;
	}

	@Required
	public void setMongoTemplate(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
}

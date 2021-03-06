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
package com.ciphertool.genetics.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.entities.Sequence;

@Aspect
public class DirtyAspect {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Before("methodsMarkedWithAtDirty()")
	public void beforeMethodsMarkedWithAtDirty(JoinPoint jp) throws Throwable {
		if (log.isDebugEnabled()) {
			log.debug("executing advice for pointcut beforeMethodsMarkedWithAtDirty() at join point "
					+ jp.toShortString());
		}

		Object entity = jp.getTarget();

		if (entity instanceof Chromosome) {
			((Chromosome) entity).setEvaluationNeeded(true);
		} else if (entity instanceof Gene) {
			if (((Gene) entity).getChromosome() == null) {
				log.error("Encountered null Chromosome for JoinPoint of type Gene.  Unable to execute advice for pointcut beforeMethodsMarkedWithAtDirty() at join point "
						+ jp.toShortString());

				return;
			}

			((Gene) entity).getChromosome().setEvaluationNeeded(true);
		} else if (entity instanceof Sequence) {
			if (((Sequence) entity).getGene() == null) {
				log.error("Encountered null Gene for JoinPoint of type Sequence.  Unable to execute advice for pointcut beforeMethodsMarkedWithAtDirty() at join point "
						+ jp.toShortString());

				return;
			}

			if (((Sequence) entity).getGene().getChromosome() == null) {
				log.error("Encountered null Chromosome for JoinPoint of type Sequence.  Unable to execute advice for pointcut beforeMethodsMarkedWithAtDirty() at join point "
						+ jp.toShortString());

				return;
			}

			((Sequence) entity).getGene().getChromosome().setEvaluationNeeded(true);
		}
	}

	@Pointcut("execution(@com.ciphertool.genetics.annotations.Dirty * *(..))")
	public void methodsMarkedWithAtDirty() {
	}
}

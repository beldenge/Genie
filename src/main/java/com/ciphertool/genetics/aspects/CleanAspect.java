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
package com.ciphertool.genetics.aspects;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.ciphertool.genetics.entities.Chromosome;

@Aspect
public class CleanAspect {
	private Logger log = Logger.getLogger(getClass());

	@After("methodsMarkedWithAtClean()")
	public void afterMethodsMarkedWithAtClean(JoinPoint jp) throws Throwable {
		if (log.isDebugEnabled()) {
			log.debug("executing advice for pointcut afterMethodsMarkedWithAtClean() at join point "
					+ jp.toShortString());
		}

		Object chromosome = jp.getTarget();

		if (chromosome instanceof Chromosome) {
			((Chromosome) chromosome).setEvaluationNeeded(false);
		}
	}

	@Pointcut("execution(@com.ciphertool.genetics.annotations.Clean * *(..))")
	public void methodsMarkedWithAtClean() {
	}
}

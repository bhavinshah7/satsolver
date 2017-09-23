package com.sat4j;

import java.io.IOException;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

/**
 * Created by Anindo Saha on 19/9/17.
 */
public class SAT4JSolver {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		System.out.println(solve("f0020-04-u.cnf") ? "SAT" : "UNSAT");
		System.out.println(System.currentTimeMillis() - startTime);
	}

	public static boolean solve(String inputFileName) {
		ISolver solver = SolverFactory.newDefault();
		Reader reader = new DimacsReader(solver);
		try {
			IProblem problem = reader.parseInstance(inputFileName);
			if (problem.isSatisfiable()) {
				return true;
			} else {
				return false;
			}
		} catch (ParseFormatException | IOException | ContradictionException | TimeoutException e) {
			System.out.println("Exception: " + e);
		}
		return false;
	}
}

package com.pedroalmir.testPriorization.flow.correlation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pedroalmir.testPriorization.model.Klass;
import com.pedroalmir.testPriorization.model.RegressionTestingPriorizationProblem;
import com.pedroalmir.testPriorization.model.Requirement;
import com.pedroalmir.testPriorization.util.file.FileAnalyzer;

/**
 * @author Pedro Almir
 * 
 */
public class CorrelationFeaturesAndAssetsThreeLevelXML {
	/** */
	private static final int HIGH_CORRELATION = 9;
	private static final int MEDIUM_CORRELATION = 3;
	private static final int LOW_CORRELATION = 1;
	private static final int NONE_CORRELATION = 0;

	@SuppressWarnings("unchecked")
	public static void fillCorrelation(RegressionTestingPriorizationProblem problem) {
		int globalCount = 1;
		System.out.println("Requirements: " + problem.getRequirements().size());
		System.out.println("Classes: " + problem.getKlasses().size());

		for (Requirement req : problem.getRequirements()) {
			LinkedList<Klass> klasses = (LinkedList<Klass>) problem.getKlasses().clone();
			List<Klass> levelTwo = new LinkedList<Klass>();
			Iterator<Klass> iterator = klasses.iterator();

			while (iterator.hasNext()) {
				Klass klass = iterator.next();
				if (req.getMainClass().getName().equals(klass.getName())) {
					req.addCorrelation(klass, HIGH_CORRELATION);
					iterator.remove();
				} else if (FileAnalyzer.countPattern(req.getMainClass().getContent(), klass.getName().replace(".java", ""), true) > 0) {
					req.addCorrelation(klass, MEDIUM_CORRELATION);
					iterator.remove();
					levelTwo.add(klass);
				}
				System.out.println("Iteration " + globalCount++);
			}
		}
		for (Requirement req : problem.getRequirements()) {
			List<Klass> list = new LinkedList<Klass>();
			Iterator<Klass> iterator = req.getCorrelation().keySet().iterator();
			while(iterator.hasNext()){
				Klass klass = iterator.next();
				if (req.getCorrelation().get(klass).equals(MEDIUM_CORRELATION)) {
					
					Iterator<Klass> iteratorForAllKlasses = problem.getKlasses().iterator();
					while(iteratorForAllKlasses.hasNext()){
						Klass k = iteratorForAllKlasses.next();
						if (!req.getCorrelation().containsKey(k) && FileAnalyzer.countPattern(klass.getContent(), k.getName().replace(".java", ""), true) > 0) {
							list.add(k);
						}
					}
				}
			}
			for(Klass ks : list){
				req.addCorrelation(ks, LOW_CORRELATION);
			}
		}
		
		for (Requirement req : problem.getRequirements()) {
			for (Klass k : problem.getKlasses()) {
				if (!req.getCorrelation().containsKey(k)){
					req.addCorrelation(k, NONE_CORRELATION);
				}
			}
		}

	}
}
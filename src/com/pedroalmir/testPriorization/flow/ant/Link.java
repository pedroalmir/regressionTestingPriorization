package com.pedroalmir.testPriorization.flow.ant;



public interface Link {
	public double transitionProbability(); // Probabilidade de Transsi��o (i, j).
	public double evaporation();           // Evapora��o do link (i, j).
}

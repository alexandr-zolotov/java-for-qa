package com.thomascook.jfq.lambdas;

@FunctionalInterface
public interface PhraseBuilder {

    String biuld(Animal firstAnimal, Animal secondAnimal, Action action);
}

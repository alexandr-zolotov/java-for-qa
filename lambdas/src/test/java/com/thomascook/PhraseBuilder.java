package com.thomascook;

@FunctionalInterface
public interface PhraseBuilder {

    String biuld(Animal firstAnimal, Animal secondAnimal, Action action);
}

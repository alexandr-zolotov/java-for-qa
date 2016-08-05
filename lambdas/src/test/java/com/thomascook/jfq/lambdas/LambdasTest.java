package com.thomascook.jfq.lambdas;


import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class LambdasTest {


    /**
     *  Input list of strings consists of Base64 encoded words.
     *  Your objective is to decode input and leave only those words that don't contain neither 'Z' nor 'z' character
     *  Hints:
     *  1) use java.util.Base64 class for decoding
     *  2) String class contains constructor that accepts array of bytes
     *  3) check org.apache.commons.lang3.StringUtils for methods that may suite your needs
     */
    @Test
    public void testBasicStreamAPI() throws Exception {

        List<String> input = Arrays.asList("QW5k", "elpaeg==", "dGhpcw==", "aXM=", "b2xvbG96eg==","aG93", "d2U=",
                "a25vdw==", "RHpSTkdTZGZn","dGhl", "d29ybGQ=", "dG8=", "YmU=", "YmFuYW5hLXNoYXBl");
//FIXME uncomment stuff below. It is commented out to not cause compilation errors
//        List<String> output = input.stream()
//                .map(null/* put your decoding lambda here*/)
//                .filter(null/* lambda with the filtering condition goes here*/)
//                ./*collect results*/
//
//        assertEquals("Wrong result list size", 11, output.size());
//        assertTrue("Words where decoded incorrectly", output.get(10).startsWith("banana"));
    }

    /**
     * Here you have to write implementation of a custom functional interface in the form of lambda.
     * When the implementation takes more than couple lines of code inlining it can make code hard to read.
     * That is why in this task I suggest to write implementation beforehand.
     * Interface is already written for you - don't change it. Your task is to provide implementation that will
     * cause test to pass.
     * Afterwards you can have some fun using {@link Action#random()}
     */
    @Test
    public void testCustomFunctionalInterface() throws Exception {
        Animal cow = new Animal("cow", "brown");
        Animal cat = new Animal("cat", "red");

        PhraseBuilder phraseBuilder = null;/* write the implementation here as a lambda and remove null */;

        String phrase = getPhrase(cow, cat, phraseBuilder);

        assertEquals("brown cow likes the red cat", phrase);
    }

    private String getPhrase(Animal firstAnimal, Animal secondAnimal, PhraseBuilder phraseBuilder) {
        return phraseBuilder.biuld(firstAnimal, secondAnimal, Action.LIKES);
    }


    /**
     * Often the behavior that you is already available
     * In such cases you can use method reference. Syntactically method reference looks like the reference to the object
     * followed by double colon and the name of the method without parenthesis. For example
     * System.out::println or this::someMethod
     *
     * Assume for this test that animal description always contains it's color.
     * Your task here is to create a map where animals are grouped by their color.
     * Each key in the map represents a color. Corresponding value is a list of Strings - animal names.
     *
     * Hint: check {@link Collectors#mapping} javadoc for example
     */
    @Test
    public void testMethodReference() throws Exception {

        List<Animal> animals = Arrays.asList(
                new Animal("crocodile", "green"),
                new Animal("sheep", "white"),
                new Animal("dog", "brown"),
                new Animal("cow", "brown")
        );

        animals.stream().collect(Collectors.groupingBy(Animal::getDescription));

        Map<String, List<String>> result = animals.stream()
                .collect(null
                                /* use Stream API magic with 'groupingBy' and 'mapping' collector */
                );

        assertEquals(2, result.get("brown").size());
        assertEquals(1, result.get("green").size());
        assertEquals("crocodile", result.get("green").get(0));
    }
}

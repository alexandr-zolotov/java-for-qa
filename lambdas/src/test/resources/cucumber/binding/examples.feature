Feature: BDD/Cucumber QA ninja can cook

  Background:
    #binding to an Integer and a String
    Given I have 10 units of water
    #binding to the object with one-arg constructor
    And a cooking pot in inventory

  Scenario:
    Given I have 5 units of potato
    And I have 2 units of salt
    #binding to single object several properties
    And mom gave me a book with recipe
      | name  | Boiled potato |
      | time  | 1 hour        |
      | level | newbie        |
    #binding list of objects using DataTable
    And the recipe is
      | ingredient | amount | action |
      | potato     | 5      | add    |
      | salt       | 1      | add    |
      | water      | 2      | boil   |
    #binding of a list with custom delimiter
    When I cook 'Boiled potato'
    And I still have at least 2 units of water left
    And I still have at least 1 units of salt left
    And I have something to eat

#  Scenario Outline:

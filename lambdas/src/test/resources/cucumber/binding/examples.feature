Feature: BDD/Cucumber QA ninja can cook
# This scenario is not an example of well written BDD scenario. Its only purpose is to show number of Cucumber
# features.

  Background: I have some water and utensil for cooking
    #binding to an Integer and a String
    Given I have 10 units of water
    #binding to the object with one-arg constructor
    And a cooking pot in inventory

# pay attention which hooks are executed for this scenario and for the next one
# Note: tags (annotations) names are arbitrary. You can write @MyAwesomeAnnotation or whatever you want
  @HooksExamples
  @WithSpecialSetUp
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
    When I cook 'Boiled potato'
    And I end up with 8 units of water
    And I end up with 1 units of salt
    And I have something to eat

  Scenario: I use multiple amounts of ingredient
    #binding of a list with custom delimiter
    When I use 2 and then 5 and then 3 units of water for cooking
    Then I end up with 0 units of water

#  Scenario Outline is a kind of a template of scenario. Step descriptions in Scenario Outline can include placeholders.
#  Placeholders should be written between '<' and '>'. Values for placeholders are taken from mandatory section
#  'Examples'. Each line in Examples section corresponds to a separate scenario execution. So scenario below will be
#  executed twice.
  Scenario Outline: predators don't cook
    Given there are <number before> <prey>(s) in the <biome>
    When the <predator> caught <number caught> <prey>(s)
    Then only <number after> <prey>(s) left in <biome>

    Examples:
      | prey     | predator | biome   | number before | number caught | number after |
      | antelope | lion     | savanna | 1000          | 5             | 995          |
      | mouse    | cat      | straw   | 23            | 7             | 16           |

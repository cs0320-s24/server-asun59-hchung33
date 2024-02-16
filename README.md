# Project Details
**Project Name:** CSV

**Team Members and Contributions:** Alyssa Sun (asun59), Hee Su Chung (hchung33) (20 hrs)

**Link to Repo:** [repo](https://github.com/cs0320-s24/server-asun59-hchung33)
# Design Choices
- **Relationships between Classes/Interfaces:**
    - **handler** is the package that has all the handlers that deal with a URLs end point request. 
      - **BroadbandHandler** deals with receiving and returning the USA Census data.
        - We utilize **dependency injection** by passing in broadbandDatasource into the parameters. 
        This allows us to keep track of the caching, county IDs, and state IDs without having to 
        constantly reinitialize and retrieve already created information.
      - **LoadCSVHandler** deals with loading and parsing a file path requested. 
        - We utilize **dependency injection** again by passing in datasource state, so we can store 
        the file loaded so other handlers will be aware of the valid file loaded. 
      - **viewCSVHandler** deals with returning and displaying the parsed file from loaded
        - We utilize **dependency injection** once more by passing in datasource state, so we
        know the file loaded that can be viewed. 
        - We practiced **defensive programming** by error handling invalid files and printing 
        a informative message instead of throwing an error and terminating the program.
      - **SearchCSVHandler** deals with taking the loaded parsed file from LoadCSVHandler and 
      searches through the file based on various requirements. 
        - Same **dependency injection** as view utilized!
      - We use **moshi** in all the handlers to **serialize** the data and have it be a valid JSON.
    - **datasource** is the package that has all the datasources that deal implemetned the end point request
- **Data Structures:** Mainly dealt with **lists and list of lists** as it was the simplest to
  deal and search with. We used **hashmaps** for things such as countyIDs when we want to efficiently 
  access data. We also used **loadedCache** from google's guava library to easily have a cache. 

# Errors/Bugs
There are no notable bugs in the program.
# Tests
There are two large components to this program.
## Unit tests:
Here we tested new code functionalities. This mainly focused on datasource classes. 
Here I test that the county ID and state ID that we collect is valid. We also 
test that correct errors are thrown accordingly. Forexample,
if an invalid file path is passed in, it should throw a factor failure 
exception. We also test that the parse functions correctly.
This testing file is focusing on testing very specific functionalities 
unlike integration testing. 
## BackendIntegrationTesting
Write JUnit integration tests that exercise the API server's various behaviors 
(e.g., responding to correct requests, responding to ill-formed requests, ...)
Here, we test all the handlers and ensure that the response is 
as expected. We test that we are able to load, view, search,
and retrieve data from the USA census, and that caching stores and deletes 
correctly. We made **mock** tests, so we do not have to consistently
spam and retrieve data from the API. 

# How to...
**Run Tests:** In the SRC folder there is a test folder. Doubleclick the folder, and you will
see two specific packages:
- unitTests
- backendIntegrationTesting
To run tests specific to unitTests, click on unitTests then the class **unitTests**. To test a
specific @test, there is a green play button by each test method. Click on it to run that specific
test. To test all searching related tests, click and play the very first play button on that file
To run tests specific to integration testing, click on backendIntegrationTesting then the class
**TestAPIHandlers**. To test a
specific @test, there is a green play button by each test method. Click on it to run that specific
test. 

**Build and Run Program:** The easiest ways to run the program is to enter **./run** in the terminal
or find the **Main** class in **src/main/java/edu.brown.cs.student/endUser** and click the green
play button at the top right. Then, the terminal will spit out prompts that you can respond to by
typing an answer and clicking enter. If the input is invalid, you will restart the prompts. To exit
the program, get back to the start of the prompts and enter **exit**. 

# Authentication Token Obtain and Replace Extender

## Introduction
In the world of web security testing, automation is key, and Burp Suite is the go to choice. However, to truly harness the power of Burp for automated scanning, you need the right plugins. That's where ATOR comes in. This open-source Burp plugin is designed to revolutionize your scanning capabilities by simplifying token management and replacement for various scenarios, including:
1. Access and Refresh Token Management
2. Token Replacement in XML and JSON Request Bodies
3. Token Replacement in URL, and Cookies

Traditionally, accomplishing these tasks required complex macros, session rules, or custom extenders, especially when dealing with JSON or XML data. Our plugin simplifies this process, offering several key advantages:

Key advantages:
1. In-Memory Token Replacement: Eliminate duplicate login requests by seamlessly replacing tokens in memory, enhancing scan efficiency.
2. User-Friendly UX: Easily extract data from response messages and replace it in requests using regular expressions. This flexibility is invaluable for handling diverse scenarios involving JSON, XML, form data, and more.
3. Enhanced Scan Speed: Achieve faster scan speeds by avoiding unnecessary login requests. The plugin employs a "Trigger Request" mechanism to identify error conditions, such as a response code of 401 with a body containing "Unauthorized request."

The inspiration for the plugin is from ExtendedMacro plugin: https://github.com/FrUh/ExtendedMacro

## Getting Started
 
To use this plugin, follow these steps:

1. Install Java and Maven.
2. Clone this repository.
3. Run the "mvn clean install" command in the cloned repository's directory where pom.xml is located.
4. Retrieve the generated JAR file with dependencies from the "target" folder.

### Prerequisites
Before using the plugin, ensure the following:

1. Make sure java environment is setup in your machine.
2. Confgure the Burp Suite to listen the Proxy traffic
3. Configure the java environment from extender tab of BURP

For usage with test application (Install this testing application (Tiredful application) from https://github.com/payatu/Tiredful-API)


### Steps 

1. Identify the request which provides the error 
2. Identify the Error Pattern (details in section below)
3. Obtain the data from the response using regex (see sample regex values)
4. Replace this data on the request (use same regex as step 3 along with the variable name)


 
### Error Pattern:

Totally there are 4 different ways you can specify the error condition.
1. Status Code: 401, 400
2. Error in Body: give any text from the body content (Example: Access token expired)
3. Error in Header: give any text from header(Example: Unauthorized)
4. Free Form: use this to give multiple condition (st=400 && bd=Access token expired || hd=Unauthorized)
 

### Break down into end to end tests

1. Finding the Invalid request:
    - http://<HOST:PORT>/api/v1/exams/MQ==/ with invalid Bearer token.
2. Identifying Error Pattern:
    - The above request will give you 401, here error condition is Status Code = 401
3. Match regex with request data
    - Authorization: Bearer \w* - this regex will match access token which is passed.
4. Replacement - How to replace 
    - Replace the matched text(step 3 regex) with extracted value (Extraction configuration discussed in below, say varibale name is "token")
    - Authorization: Bearer token -  extracted token will be replaced.

### Usage with test application 

Idea : Record the Tiredful application request in BURP, configure the ATOR extender, check whether token is replaced by ATOR.
1. Open the testing application in browser which you configured with BURP
    - Generate a token from http://<HOST:PORT>/handle-user-token/
    - Send the request http://<HOST:PORT>/api/v1/exams/MQ==/ by passing Authorization Beaer token(get it from above step)
2. Add the ATOR jar file as a extender in BURP
3. Right Click on the request(/handle-user-token) in Proxy history and send it to Authentication Token Optain and Replace Extender
4. Add the new entry in Extraction configuration by selecting the "access_token" value and give name as "token"(it may be any name)
    Note: For this application,one request is enough to generate a token.Token can also get generated after multiple requests
5. TRIGGER CONDITION: 
    - Macro steps will get executed if the condition is matched.
    - After execution of steps, replace the incoming request by taking values from "Pattern" and "Replacement Area" if specified.
    - For our testing, 
        - Error condition is 401(Status Code) 
        - Pattern is "Authorization: Bearer \w*" (Specify the regex Pattern how you want to replace with extraction values)
        - Replacement Area is "Authentication: Bearer <NAME which you gave in STEP 4>"
    - Click on "Add" Button.
6. For this example, one replacement is enough to make the incoming request as valid but you can add mutiple replacement for a single condition.
7. Hit the invalid request from Repeater and check the req/res flows in either FLOW/Logger++
    - Invalid Bearer token(http://<HOST:PORT>/api/v1/exams/MQ==/) from Repeater makes the response as 401.
    - Extender will match this condition and start running the recorded steps, extract the "access_token"
    - Replace the access token(from step ii) in actual response(from Repeater) and makes this invalid request as valid.
    - In the repeater console,  you see 200 OK response.
8. Do the Step7 again and check the flow
    - This time extender will not invoke the steps because existing token is valid and so it uses that.

 
## Built With
 
* [SWING](https://javadoc.scijava.org/Java7/javax/swing/package-summary.html) - Used to add panel
 
## Contributing
 
Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.
 
## Versioning
v2.3.0
 
## Authors
 
* **https://github.com/FrUh/ExtendedMacro ** - *Initial work*

Synopsys


## License
 
This software is released by Synopsys under the MIT license.
 
## Acknowledgments
 
* https://github.com/FrUh/ExtendedMacro
ExtendedMacro was a great start - we have modified the UI to handle more complex scenarios. We have also fixed bugs and improved speed by replacing tokens in memory.


  


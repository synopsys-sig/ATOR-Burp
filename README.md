# Authentication Token Obtain and Replace Extender
## Blogs

1. [Authentication Token Obtain and Replace (ATOR) Burp Plugin - Part1 - Single step login sequence and single token extraction](https://medium.com/@kashwathkumar/authentication-token-obtain-and-replace-ator-burp-plugin-fast-and-reliable-plugin-to-handle-b19e3621c6a7 "ATOR Part 1")
2. [Authentication Token Obtain and Replace (ATOR) Burp Plugin - Part2 - Multi step login sequence and multiple extraction](https://medium.com/@kashwathkumar/authentication-token-obtain-and-replace-ator-burp-plugin-fast-and-reliable-plugin-to-handle-1d9a0b3054e "ATOR Part 2")

## Introduction
The plugin is created to help automated scanning using Burp in the following scenarios:
1. Access/Refresh token
2. Token replacement in XML,JSON body
3. Token replacement in cookies  
The above can be achieved using complex macro, session rules or Custom Extender in some scenarios. The rules become tricky and do not work in scenarios where the replacement text is either JSON, XML. 

Key advantages:
1. We have also achieved in-memory token replacement to avoid duplicate login requests like in  both custom extender, macros/session rules. 
2. Easy UX to help obtain data (from response) and replace data (in requests) using regex. This helps achieve complex scenarios where response body is JSON, XML and the request text is also JSON, XML, form data etc.
3. Scan speed - the scan speed increases considerably because there are no extra login requests. There is something called the "Trigger Request" which is the error condition (also includes regex) when the login requests are triggered. The error condition can include (response code = 401 and body contains "Unauthorized request")

The inspiration for the plugin is from ExtendedMacro plugin: https://github.com/FrUh/ExtendedMacro

## Getting Started
 
1. Install Java and Maven 
2. Clone the repository
3. Run the "mvn clean install" command in cloned repo of where pom.xml is present
4. Take the generated jar with dependencies from the target folder

### Prerequisites
 
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
 
### Regex with samples

1. Use Authorization: Bearer \w* to match Authorization: Bearer AXXFFPPNSUSSUSSNSUSN
2. Use Authorization: Bearer ([\w\+\_\-\.]*) to match Authorization: Bearer AXX-F+FPPNS.USSUSSNSUSN

 
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

## ATOR v3
1. Base 64 encode/decode feature is added. This can be used in extraction layer.
2. OTP validation support
   - mysms.com app needs to be configure for this feature. Register your mobile number with this application. And use the API key, password, sendername, key and phonenumber on ATOR Settings panel.
3. [CONFIG](config/v3)
    - 3 apps are used here to showcase the demo. 
    - All the config files are shared. Take this as a reference and import it to get an understanding of how we can configure ATOR.
    - This gives you an idea about how ATOR-v3 plugin solves the authentication issue which varies from simple to complex scenario's (OAuth based)
4. [V3 Binary](bin/ATOR-v3.0.0.jar)
    - Please take this binary file for ATOR-v3 plugin.
## Built With
 
* [SWING](https://javadoc.scijava.org/Java7/javax/swing/package-summary.html) - Used to add panel
 
## Contributing
 
Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.
 
## Versioning
v1.0 
 
## Authors
 
* **https://github.com/FrUh/ExtendedMacro ** - *Initial work*

Authors from Synopsys - Ashwath Reddy (@ka3hk) and Manikandan Rajappan (@rmanikdn)


## License
 
This software is released by Synopsys under the MIT license.
 
## Acknowledgments
 
* https://github.com/FrUh/ExtendedMacro
ExtendedMacro was a great start - we have modified the UI to handle more complex scenarios. We have also fixed bugs and improved speed by replacing tokens in memory.

## Demo Video

[ATOR Extender Demo Video](https://youtu.be/h1p2rvooTL0 "Demo Video")

### ATOR v2.0.0:

UI Panel was splitted into 4 different configuration. Check out the code from v2 or use the executable from v2/bin.
1. Error Condition - Find the error condition req/res and add trigger condition [Can be statuscode/text in body content/text in header]. Multiple condition can also be added.
2. Obtain Token: Find all the req/res to get the token. It can be single or multiple request (do replacement accordingly)
3. Error Condition Replacement: Mark the trigger condition and also mark the place on request where replacement needs to taken (map the extraction)
4. Preview: Dry run it before configure for scan.

  


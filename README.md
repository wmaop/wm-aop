## wm-aop
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/33aec3b2a6e8474b8663bc5ef1ce45e0)](https://www.codacy.com/app/wmaop/wm-aop?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=wmaop/wm-aop&amp;utm_campaign=Badge_Grade) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This project offers an Aspect Oriented style framework for Software AG webMethods that allows the dynamic creation of mocks and interceptors which can be conditionally invoked when services are executing.   It is also the foundation for [wm-jbehave](https://github.com/wmaop/wm-jbehave/wiki) that offers Behaviour Driven Unit Testing from within Software AG Designer.  When you have these components installed you will be able to:

* Create mocks of IS services
* Apply conditions to mocks so that they only execute when the pipeline contents meet that condition
* Raise an exception based on a condition or in place of a service
* Capture the pipeline to file before or after a service is called
* Modify or insert content into the pipeline
* Have a series of conditions for a mocked service with a default if none of the conditions match
* Create assertions that can apply before or after a service so that its possible to prove a service has been executed.   Assertions can also have conditions to verify that the pipeline had the expected content.
* Return either random or sequenced content from a mock to very its output every time its called
* Use the [wm-jbehave](https://github.com/wmaop/wm-jbehave/wiki) functionality for Behaviour Driven Unit Testing within Designer and execute tests with the in-built JUnit

See [the wiki](https://github.com/wmaop/wm-aop/wiki) for more information on installation and use.

# What it is

Barefoot is a servlet container with no networking. It hosts servlets, listeners and filters within a context and an external agent provides the requests and delivers the responses.

The intended use case is within a cloud function where existing infrastructure can pass HTTP requests to serverless code. This project converts those requests to traditional servlet requests.

The result is that existing code that can be deployed in a traditional Java web server such as Tomcat can be redeployed in a cloud function.

This is not intended to support serving static content, there are better cloud friendly ways of achieving that.

It also does not support JSP pages, just listeners, filters and servlets.

# Philosophy

Cloud functions have trade-offs. While they scale very well, there are both start-up and runtime costs to consider.

The Barefoot solution is to not include code that is not required. To this end it is very modular, so you only package the components you will need.

# What it is not

It does not run WAR files and is not a way of running Springboot applications. It does however act as a servlet container and you can use Spring WebMvc.

# Examples of usage

The included examples include

- Simple servlet
- Glassfish/Metro WSDL service
- Apache CXF WSDL service
- Jersey JAX-RS restful service
- Spring WebMvc RestController

The examples are there both to test the code and demonstrate it.

# Before you start

What do you want to deploy, would it run under Tomcat? If so you must have some form of servlet to handle the requests. That is a good start, you will need that.

# How does it work

The core of Barefoot is the servlet context. This has registered listeners, filters and servlets. An external agent delivers the requests and returns the responses. That is it.
The core servlet context is abstract. There are two concrete implementations, one for javax.servlet API and the second for the new jakarta.servlet API. Most existing code will use the first.

## Programmatic initialisation

Create a context
Add the listeners, filters and servlets.
Create an appropriate dispatcher wrapper.

Feed the incoming cloud native requests to the dispatcher.

This can be done programmatically with

```
context=new BarefootServletContext("");
context.addListener....
context.addFilter...
context.addServlet....
context.onStartup();
dispatcher=new Barefoot*Dispatcher(context);
```

Then call the dispatcher with the external requests.

## Initialisation by configuration

Start with

```
context=new BarefootServletContext("");
context.onStartup();
```

The onStartup method will read all the META-INF/services/javax.servlet.ServletContainerInitializer (or jakarta.servlet.ServletContainerInitializer ) and read the javax.servlet.annotation.HandlesTypes annotations.
It will look up these classes and pass them to the initializers.

Unlike Tomcat, Barefoot does not scan classes looking for annotations or inheritance, it requires configuration in:

- META-INF/services/javax.servlet.ServletContainerInitializer

If you include module net-sf-barefoot-annotation-javax it will read 

- META-INF/services/java.util.EventListener
- META-INF/services/javax.servlet.Filter
- META-INF/services/javax.servlet.Servlet

And process these classes using the javax.servlet.annotation.WebFilter, javax.servlet.annotation.WebListener and javax.servlet.annotation.WebServlet annotations.

If you have Spring-WebMvc included in your project you should find that it will be detected and will then attempt to process

- META-INF/services/org.springframework.web.WebApplicationInitializer

This should contain your Spring application initializers.

## Initialisation with web.xml

If you include module net-sf-barefoot-web-xml-javax it will read WEB-INF/web.xml and configure the current context on startup based on its contents.

## Initialisation with context.xml

This can be achieved with net-sf-barefoot-naming and net-sf-barefoot-context-xml. The first provides a naming context and the second processes the META-INF/context.xml file.

## The mostly codeless solution

Use the net-sf-barefoot-xml-aws, net-sf-barefoot-xml-azure or net-sf-barefoot-xml-google. These will

- configure the naming context
- load context.xml into the naming context
- start the servlet context
- create the specific dispatcher

These can either be used directly as the cloud function entry point or else used for simple delegation.

# Setting up your own project

Follow the guidelines from AWS, Google or Azure for creating a Java cloud function using maven, then include dependencies to just the barefoot modules you need.
In the case of AWS and Google you can use the barefoot entry points directly. The samples from Azure use annotations to define the functions, the implementation can then delegate the call to the barefoot function.
The Lambda packaging mechanism for maven included in these samples is is different to the examples from Amazon. It uses the standard assembly plug in instead of the shade plug in. This results in a package that maintains the integrity of the dependent jars and matches the format built by the Amazon gradle examples.
The implementation code should be separate from the deployment code. These examples allow the same code to be deployed as any kind of cloud function and also as a war file suitable for Tomcat.

## Testing your code

To test a Google function use

```
mvn function:run
```

To test an Azure function use

```
mvn azure-functions:run
```

To test an AWS Lambda function you could use SAM locally. Unlike Google and Azure, to get the full effect you need to run with an API Gateway calling the Lambda.

## Running the examples

The examples do not install by default. To install them locally use
```
mvn -Dbarefoot.install.skip=false install
```

## Java Version

The base version of Java used for this project is now 11. This is now supported in AWS, Google and Azure.

## Deployment Size

The intention for this project is that a single micro-service cloud function would contain just one executable artefact. So it would contain either one WSDL service, one servlet or one RestController. The deployable package would then only contain the dependencies to support that one function. 

## Compatibility reference

The two compatibility goals are compatiblity with Jetty and Tomcat.

## Security and Persistence

This is outside of the scope of Barefoot. The expectation is that either existing cloud security will be used, or alternatively the security will be implemented directly by the solution or as a filter. Persistence is also outside of the scope, similarly the expectation is that persistence will be managed using technologies suitable for the cloud in which the function is deployed. The net-sf-barefoot-example-jdbc demonstrates Spring WebMvc being used to provide both basic authentication and http session persistence. While it is not a recommendation to use JDBC it does demonstrate how http session persistence and authentication can be added using filters.

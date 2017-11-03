import ballerina.net.jms;

function main (string[] args) {
    jmsSender();
}

function jmsSender() {
    // We define the connection properties as a map. 'providerUrl' or 'configFilePath' and the 'initialContextFactory' vary according to the JMS provider you use.
    // In this example we connect to the WSO2 MB server.
    jms:ClientConnector jmsEP;
    jms:ConnectorProperties conProperties = {
                        initialContextFactory:"wso2mbInitialContextFactory",
                        configFilePath:"../jndi.properties",
                        connectionFactoryName: "TopicConnectionFactory",
                        connectionFactoryType : "topic"
                                            };
    // Create the JMS client Connector using the connection properties we defined earlier.
    jmsEP = create jms:ClientConnector(conProperties);
    // Create an empty Ballerina message.
    jms:JMSMessage topicMessage = jms:createTextMessage(jmsEP);
    // Set a string payload to the message.
    topicMessage.setTextMessageContent("Hello from Ballerina!");
    // Send the Ballerina message to the JMS provider.
    jmsEP.send("mytopic", topicMessage);
}
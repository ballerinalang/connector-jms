/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.ballerinalang.net.jms.nativeimpl.endpoint.queue.sender;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.CallableUnitCallback;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.NativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.net.jms.Constants;
import org.ballerinalang.util.exceptions.BallerinaException;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * Get the ID of the connection.
 *
 * @since 0.966
 */
@BallerinaFunction(
        orgName = "ballerina",
        packageName = "jms",
        functionName = "initQueueSender",
        receiver = @Receiver(type = TypeKind.STRUCT,
                             structType = "QueueSender",
                             structPackage = "ballerina.jms"),
        args = {
                @Argument(name = "sessionConnector",
                          type = TypeKind.STRUCT,
                          structType = "SessionConnector")
        },
        isPublic = true
)
public class InitQueueSender implements NativeCallableUnit {

    @Override
    public void execute(Context context, CallableUnitCallback callableUnitCallback) {
        Struct queueSenderEndpoint = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);
        Struct queueSenderConfig = queueSenderEndpoint.getStructField(Constants.QUEUE_SENDER_FIELD_CONFIG);
        BStruct sessionConnector = (BStruct) context.getRefArgument(1);
        Object nativeData = sessionConnector.getNativeData(Constants.JMS_SESSION);
        if (!(nativeData instanceof Session)) {
            throw new BallerinaException("JMS Session  is not properly established.", context);
        }

        Session session = (Session) nativeData;
        try {
            Queue queue
                    = session.createQueue(queueSenderConfig.getStringField(Constants.QUEUE_SENDER_FIELD_QUEUE_NAME));
            MessageProducer producer = session.createProducer(queue);
            Struct queueSenderConnector = queueSenderEndpoint.getStructField(Constants.QUEUE_SENDER_FIELD_CONNECTOR);
            queueSenderConnector.addNativeData(Constants.JMS_QUEUE_SENDER_OBJECT, producer);
        } catch (JMSException e) {
            throw new BallerinaException("Error creating Queue sender", e, context);
        }
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
}
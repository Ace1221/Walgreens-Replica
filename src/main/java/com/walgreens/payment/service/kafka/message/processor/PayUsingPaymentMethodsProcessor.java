package com.walgreens.payment.service.kafka.message.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walgreens.payment.model.CartItem;
import com.walgreens.payment.service.command.PayUsingPaymentMethodsCommand;
import com.walgreens.payment.service.kafka.message.keys.Keys;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PayUsingPaymentMethodsProcessor extends Processor{

    @Override
    public void process() {

        PayUsingPaymentMethodsCommand payUsingPaymentMethodsCommand = (PayUsingPaymentMethodsCommand) getCommand();
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> message = getMessageInfo();


        payUsingPaymentMethodsCommand.setCustomerUuid(UUID.fromString((String) message.get(Keys.customerUuid)));

        payUsingPaymentMethodsCommand.setCartUuid(UUID.fromString((String) message.get(Keys.cartUuid)));

        payUsingPaymentMethodsCommand.setPaymentMethodUuid(UUID.fromString((String) message.get(Keys.paymentMethodUuid)));
        payUsingPaymentMethodsCommand.setAmount(Double.valueOf((String) message.get(Keys.paymentAmount)));

       

    }
}

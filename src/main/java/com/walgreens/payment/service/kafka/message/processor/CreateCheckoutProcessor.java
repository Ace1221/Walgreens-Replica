package com.walgreens.payment.service.kafka.message.processor;

import com.walgreens.payment.service.command.CreateCheckoutCommand;
import com.walgreens.payment.service.kafka.message.keys.Keys;

import java.util.Map;
import java.util.UUID;

public class CreateCheckoutProcessor extends Processor{
    @Override
    public void process() {
        CreateCheckoutCommand createCheckoutCommand = (CreateCheckoutCommand) getCommand();
        Map<String, String> message = getMessageInfo();
        createCheckoutCommand.setCustomerUuid(UUID.fromString(message.get(Keys.customerUuid)));
        createCheckoutCommand.setCouponUuid(UUID.fromString(message.get(Keys.couponUuid)));
    }
}

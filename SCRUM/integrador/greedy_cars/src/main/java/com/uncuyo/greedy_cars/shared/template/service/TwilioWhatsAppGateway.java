package com.uncuyo.greedy_cars.shared.template.service;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TwilioWhatsAppGateway implements WhatsAppGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwilioWhatsAppGateway.class);

    private final TwilioRestClient twilioRestClient;

    public TwilioWhatsAppGateway(
        @Value("${twilio.account.sid}") String accountSid,
        @Value("${twilio.auth.token}") String authToken
    ) {
        this.twilioRestClient = new TwilioRestClient.Builder(accountSid, authToken).build();
        LOGGER.info("Twilio WhatsApp gateway initialized");
    }

    @Override
    public String send(String from, String to, String body) {
        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(from),
                body
        ).create(twilioRestClient);
        return message.getSid();
    }
}

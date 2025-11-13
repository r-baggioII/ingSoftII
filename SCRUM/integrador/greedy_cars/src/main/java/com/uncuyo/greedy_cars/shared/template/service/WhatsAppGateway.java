package com.uncuyo.greedy_cars.shared.template.service;

public interface WhatsAppGateway {

    /**
     * Sends a WhatsApp message and returns the Twilio SID identifier when available.
     *
     * @param from sender WhatsApp number (e.g. whatsapp:+1415...)
     * @param to destination WhatsApp number (e.g. whatsapp:+549...)
     * @param body message body
     * @return the message SID returned by the provider
     */
    String send(String from, String to, String body);
}

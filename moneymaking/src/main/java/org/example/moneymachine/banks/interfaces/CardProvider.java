package org.example.moneymachine.banks.interfaces;

public interface CardProvider {

    /**
     * Checks if card number follows the card number format specified by the bank
     * @param cardNumber - Card number to test (denoted as userId in rest of API)
     * @return true if card number follows bank´s card number format, false otherwise
     * @see- |{@link <a href="https://en.wikipedia.org/wiki/Payment_card_number#Issuer_identification_number_(IIN)"></a> Issuer ID-numbers } |
     */
    boolean cardNumberFollowsFormat(String cardNumber);
}

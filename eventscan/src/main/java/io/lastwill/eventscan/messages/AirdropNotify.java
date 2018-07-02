package io.lastwill.eventscan.messages;

import lombok.Getter;

import java.math.BigInteger;
import java.util.Map;

public class AirdropNotify extends NotifyContract {
    @Getter
    private final Map<String, BigInteger> airdroppedAddresses;

    public AirdropNotify(int contractId, PaymentStatus status, String txHash, Map<String, BigInteger> airdroppedAddresses) {
        super(contractId, status, txHash);
        this.airdroppedAddresses = airdroppedAddresses;
    }

    @Override
    public String getType() {
        return "airdrop";
    }
}
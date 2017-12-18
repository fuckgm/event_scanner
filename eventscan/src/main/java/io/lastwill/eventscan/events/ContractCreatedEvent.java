package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.Product;
import io.mywish.scanner.BaseEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

@Getter
@RequiredArgsConstructor
public class ContractCreatedEvent extends BaseEvent {
    private final Product product;
    private final Transaction transaction;
    private final EthBlock.Block block;
    private final boolean isSuccess;
}

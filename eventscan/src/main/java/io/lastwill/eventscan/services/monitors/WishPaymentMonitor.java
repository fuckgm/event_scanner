package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.UserPaymentEvent;
import io.lastwill.eventscan.events.contract.erc20.TransferEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.UserProfile;
import io.lastwill.eventscan.repositories.UserProfileRepository;
import io.lastwill.eventscan.services.EventParser;
import io.lastwill.eventscan.services.TransactionProvider;
import io.lastwill.eventscan.services.builders.erc20.TransferEventBuilder;
import io.mywish.scanner.WrapperTransaction;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.services.EventPublisher;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class WishPaymentMonitor {
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private TransactionProvider transactionProvider;
    @Autowired
    private EventParser eventParser;
    @Autowired
    private TransferEventBuilder transferEventBuilder;

    @Value("${io.lastwill.eventscan.contract.token-address}")
    private String tokenAddress;

    @PostConstruct
    protected void init() {
        if (tokenAddress != null) {
            tokenAddress = tokenAddress.toLowerCase();
        }
        log.info("Token address: {}.", tokenAddress);
    }

    @EventListener
    public void onNewBlock(final NewBlockEvent newWeb3BlockEvent) {
        // wish only in mainnet works
        if (newWeb3BlockEvent.getNetworkType() != NetworkType.ETHEREUM_MAINNET) {
            return;
        }
        Set<String> addresses = newWeb3BlockEvent.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }

        if (!addresses.contains(tokenAddress)) {
            return;
        }

        List<WrapperTransaction> transactions = newWeb3BlockEvent.getTransactionsByAddress().get(tokenAddress);
        for (final WrapperTransaction transaction : transactions) {
            if (!tokenAddress.equalsIgnoreCase(transaction.getOutputs().get(0).getAddress())) {
                continue;
            }
            transactionProvider.getTransactionReceiptAsync(newWeb3BlockEvent.getNetworkType(), transaction.getHash())
                    .thenAccept(transactionReceipt -> eventParser.parseEvents(transactionReceipt, transferEventBuilder.getEventSignature())
                            .stream()
                            .filter(event -> event instanceof TransferEvent)
                            .map(event -> (TransferEvent) event)
                            .forEach(eventValue -> {
                                String transferTo = eventValue.getTo();
                                BigInteger amount = eventValue.getTokens();

                                    UserProfile userProfile = userProfileRepository.findByInternalAddress(transferTo);
                                    if (userProfile == null) {
                                        return;
                                    }
                                    eventPublisher.publish(new UserPaymentEvent(
                                            newWeb3BlockEvent.getNetworkType(),
                                            transaction,
                                            amount,
                                            CryptoCurrency.WISH,
                                            true,
                                            userProfile));
                                }))
                                .exceptionally(throwable -> {
                                    log.error("Error on getting receipt for handling WISH payment.", throwable);
                                return null;
                            });

        }
    }
}

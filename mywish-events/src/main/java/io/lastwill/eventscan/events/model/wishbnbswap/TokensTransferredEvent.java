package io.lastwill.eventscan.events.model.wishbnbswap;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.WishToBnbSwapEntry;
import lombok.Getter;

@Getter
public class TokensTransferredEvent extends BaseEvent {
    private final String coin;
    private final int decimals;
    private final WishToBnbSwapEntry wishEntry;

    public TokensTransferredEvent(
            String coin,
            int decimals,
            WishToBnbSwapEntry wishEntry
    ) {
        super(NetworkType.BINANCE_MAINNET);
        this.coin = coin;
        this.decimals = decimals;
        this.wishEntry = wishEntry;
    }
}
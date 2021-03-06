package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.ZonedDateTime;

@Getter
@Entity
@Table(name = "contracts_contractdetailsbinancelastwill")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("24")
public class ProductLastWillBinance extends Product implements ProductCheckable {
    @Column(name = "check_interval")
    private Integer checkInterval;
    @Column(name = "active_to")
    private ZonedDateTime activeTo;
    @Column(name = "last_check")
    private ZonedDateTime lastCheck;
    @Column(name = "next_check")
    private ZonedDateTime nextCheck;
    @ManyToOne
    @JoinColumn(name = "btc_key_id")
    private BtcKey btcKey;

    @Override
    public int getContractType() {
        return 24;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.valueOf(200000);
    }
}

package edu.urgu.oopteam.crud.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Objects;

@Entity
@Table(name = "currencytrackrequests")
public class CurrencyTrackRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    // exchange rate for currency on the time of
    @Column(name = "base_rate", nullable = false)
    private BigDecimal baseRate;
    // currency name
    @Column(name = "currency_name", nullable = false)
    private String currencyCode;
    // delta
    @Column(name = "delta", nullable = false)
    private BigDecimal delta;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    /**
     * Needed for Spring to map entities from database !!!!!!!!!!!!!!!!!!!!!!DONOTDELETE
     */
    public CurrencyTrackRequest() {

    }

    public CurrencyTrackRequest(BigDecimal baseRate, String currencyCode, BigDecimal delta, User user) {
        this.baseRate = baseRate;
        this.currencyCode = currencyCode;
        this.delta = delta;
        this.user = user;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }


    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }


    public BigDecimal getDelta() {
        return delta;
    }

    public void setDelta(BigDecimal delta) {
        this.delta = delta;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return MessageFormat.format("CurrencyTrackRequest " +
                        "[id= {0}, userId= {1}, baseRate= {2}, currencyCode= {3}, delta= {4}]",
                id, user.getId(), baseRate, currencyCode, delta);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyTrackRequest that = (CurrencyTrackRequest) o;
        return id == that.id &&
                baseRate.equals(that.baseRate) &&
                currencyCode.equals(that.currencyCode) &&
                delta.equals(that.delta) &&
                user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseRate, currencyCode, delta, user);
    }
}
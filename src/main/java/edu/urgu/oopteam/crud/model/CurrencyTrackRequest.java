package edu.urgu.oopteam.crud.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
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

    // first currency name
    @Column(name = "first_currency", nullable = false)
    private String firstCurrencyCode;

    // second currency name
    @Column(name = "second_currency", nullable = false)
    private String secondCurrencyCode;

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

    public CurrencyTrackRequest(BigDecimal baseRate, String firstCurrencyCode,
                                String secondCurrencyCode, BigDecimal delta, User user) {
        this.baseRate = baseRate;
        this.firstCurrencyCode = firstCurrencyCode;
        this.secondCurrencyCode = secondCurrencyCode;
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


    public String getFirstCurrencyCode() {
        return firstCurrencyCode;
    }

    public void setFirstCurrencyCode(String firstCurrencyCode) {
        this.firstCurrencyCode = firstCurrencyCode;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyTrackRequest that = (CurrencyTrackRequest) o;
        return id == that.id &&
                baseRate.equals(that.baseRate) &&
                firstCurrencyCode.equals(that.firstCurrencyCode) &&
                secondCurrencyCode.equals(that.secondCurrencyCode) &&
                delta.equals(that.delta) &&
                user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseRate, firstCurrencyCode, secondCurrencyCode, delta, user);
    }

    public String getSecondCurrencyCode() {
        return secondCurrencyCode;
    }

    public void setSecondCurrencyCode(String secondCurrencyCode) {
        this.secondCurrencyCode = secondCurrencyCode;
    }

    @Override
    public String toString() {
        return "CurrencyTrackRequest{" +
                "baseRate=" + baseRate +
                ", firstCurrencyCode='" + firstCurrencyCode + '\'' +
                ", secondCurrencyCode='" + secondCurrencyCode + '\'' +
                ", delta=" + delta +
                '}';
    }
}
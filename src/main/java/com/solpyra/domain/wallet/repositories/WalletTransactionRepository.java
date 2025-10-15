package com.solpyra.domain.wallet.repositories;

import com.solpyra.entities.WalletTransaction;
import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, BigInteger> {

}

package org.swp.my_learning_path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.entity.Wallet;
import org.swp.my_learning_path.service.WalletService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Test
    @Transactional
    void testGetWallet() {
        // Kiểm tra lấy ví của người dùng ID 1
        Wallet wallet = walletService.getWalletByUserId(1L);
        assertNotNull(wallet);
        assertNotNull(wallet.getBalance());
    }
}

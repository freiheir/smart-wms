package com.smart.wmserp.config;

import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.master.Partner;
import com.smart.wmserp.repository.ItemRepository;
import com.smart.wmserp.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final ItemRepository itemRepository;
    private final PartnerRepository partnerRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 1. 샘플 거래처 등록
            if (partnerRepository.count() == 0) {
                Partner buyer = Partner.builder()
                        .partnerCode("BUY001")
                        .partnerName("테스트 바이어 (A사)")
                        .partnerType("BUY")
                        .currency("USD")
                        .useYn("Y")
                        .build();
                partnerRepository.save(buyer);

                Partner vendor = Partner.builder()
                        .partnerCode("SELL001")
                        .partnerName("테스트 공급사 (B사)")
                        .partnerType("SELL")
                        .currency("KRW")
                        .useYn("Y")
                        .build();
                partnerRepository.save(vendor);
                System.out.println(">>> Sample Partner data has been saved!");
            }

            // 2. 샘플 상품 등록
            if (itemRepository.count() == 0) {
                Item sampleItem = Item.builder()
                        .partNumber("SKU001")
                        .rawPartNumber("SKU-001")
                        .itemName("테스트 품목")
                        .wholesalePrice(new BigDecimal("100.00"))
                        .multiplier(new BigDecimal("1.2"))
                        .currency("USD")
                        .useYn("Y")
                        .safetyStock(10)
                        .build();

                itemRepository.save(sampleItem);
                System.out.println(">>> Sample data has been successfully saved!");
            }
        };
    }
}

package com.smart.wmserp.config;

import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final ItemRepository itemRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
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

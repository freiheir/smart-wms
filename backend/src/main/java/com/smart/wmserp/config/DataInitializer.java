package com.smart.wmserp.config;

import com.smart.wmserp.domain.Item;
import com.smart.wmserp.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final ItemRepository itemRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (itemRepository.count() == 0) {
                Item sampleItem = Item.builder()
                        .itemCode("ITEM-001")
                        .itemName("테스트 품목")
                        .itemUnit("EA")
                        .description("WMS/ERP 연동 테스트용 품목입니다.")
                        .price(10000)
                        .stockQuantity(100)
                        .useYn("Y")
                        .build();

                itemRepository.save(sampleItem);
                System.out.println(">>> Sample data has been successfully saved!");
            }
        };
    }
}

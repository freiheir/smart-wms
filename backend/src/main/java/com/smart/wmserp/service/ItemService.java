package com.smart.wmserp.service;

import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.master.Partner;
import com.smart.wmserp.dto.ItemRequestDto;
import com.smart.wmserp.dto.ItemResponseDto;
import com.smart.wmserp.repository.ItemRepository;
import com.smart.wmserp.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final PartnerRepository partnerRepository;

    /**
     * 상품 등록
     */
    @Transactional
    public Long createItem(ItemRequestDto requestDto) {
        Partner partner = null;
        if (requestDto.getPartnerId() != null) {
            partner = partnerRepository.findById(requestDto.getPartnerId())
                    .orElse(null);
        }

        Item item = Item.builder()
                .rawPartNumber(requestDto.getRawPartNumber())
                .itemName(requestDto.getItemName())
                .wholesalePrice(requestDto.getWholesalePrice())
                .multiplier(requestDto.getMultiplier())
                .weightKg(requestDto.getWeightKg())
                .cbm(requestDto.getCbm())
                .currency(requestDto.getCurrency())
                .partner(partner)
                .useYn(requestDto.getUseYn() != null ? requestDto.getUseYn() : "Y")
                .safetyStock(requestDto.getSafetyStock())
                .build();
        return itemRepository.save(item).getId();
    }

    /**
     * 상품 수정
     */
    @Transactional
    public void updateItem(Long id, ItemRequestDto requestDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        
        item.setRawPartNumber(requestDto.getRawPartNumber());
        item.setItemName(requestDto.getItemName());
        item.setWholesalePrice(requestDto.getWholesalePrice());
        item.setMultiplier(requestDto.getMultiplier());
        item.setWeightKg(requestDto.getWeightKg());
        item.setCbm(requestDto.getCbm());
        item.setCurrency(requestDto.getCurrency());
        item.setUseYn(requestDto.getUseYn());
        item.setSafetyStock(requestDto.getSafetyStock());
    }

    /**
     * 품번으로 상품 조회
     */
    public ItemResponseDto findByPartNumber(String partNumber) {
        return itemRepository.findByPartNumber(partNumber)
                .map(ItemResponseDto::new)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 상품입니다. (품번: " + partNumber + ")"));
    }

    /**
     * 상품 목록 조회
     */
    public List<ItemResponseDto> findAllItems() {
        return itemRepository.findAll().stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 상품 단건 조회
     */
    public ItemResponseDto findOneItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        return new ItemResponseDto(item);
    }

    /**
     * 상품 삭제 (사용여부 N 처리)
     */
    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        item.setUseYn("N");
    }
}

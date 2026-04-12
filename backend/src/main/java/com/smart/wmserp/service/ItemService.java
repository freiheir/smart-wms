package com.smart.wmserp.service;

import com.smart.wmserp.domain.Item;
import com.smart.wmserp.dto.ItemRequestDto;
import com.smart.wmserp.dto.ItemResponseDto;
import com.smart.wmserp.repository.ItemRepository;
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

    /**
     * 상품 등록
     */
    @Transactional
    public Long createItem(ItemRequestDto requestDto) {
        Item item = Item.builder()
                .itemCode(requestDto.getItemCode())
                .itemName(requestDto.getItemName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .stockQuantity(requestDto.getStockQuantity())
                .itemUnit(requestDto.getItemUnit())
                .barcode(requestDto.getBarcode())
                .useYn(requestDto.getUseYn() != null ? requestDto.getUseYn() : "Y")
                .category(requestDto.getCategory())
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
        
        item.setItemName(requestDto.getItemName());
        item.setDescription(requestDto.getDescription());
        item.setPrice(requestDto.getPrice());
        item.setStockQuantity(requestDto.getStockQuantity());
        item.setItemUnit(requestDto.getItemUnit());
        item.setBarcode(requestDto.getBarcode());
        item.setUseYn(requestDto.getUseYn());
        item.setCategory(requestDto.getCategory());
        // itemCode는 일반적으로 변경하지 않음 (식별자 역할)
    }

    /**
     * 상품코드 또는 바코드로 상품 조회
     */
    public ItemResponseDto findByCodeOrBarcode(String code) {
        return itemRepository.findByItemCode(code)
                .or(() -> itemRepository.findByBarcode(code))
                .map(ItemResponseDto::new)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 상품입니다. (코드: " + code + ")"));
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

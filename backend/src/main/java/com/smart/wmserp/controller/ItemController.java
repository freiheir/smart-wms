package com.smart.wmserp.controller;

import com.smart.wmserp.dto.ItemRequestDto;
import com.smart.wmserp.dto.ItemResponseDto;
import com.smart.wmserp.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getItems() {
        return ResponseEntity.ok(itemService.findAllItems());
    }

    /**
     * 바코드 또는 상품코드로 상품 조회
     */
    @GetMapping("/search")
    public ResponseEntity<ItemResponseDto> searchItem(@RequestParam String code) {
        return ResponseEntity.ok(itemService.findByCodeOrBarcode(code));
    }

    /**
     * 상품 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.findOneItem(id));
    }

    /**
     * 상품 등록
     */
    @PostMapping
    public ResponseEntity<Long> createItem(@RequestBody ItemRequestDto requestDto) {
        return ResponseEntity.ok(itemService.createItem(requestDto));
    }

    /**
     * 상품 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateItem(@PathVariable Long id, @RequestBody ItemRequestDto requestDto) {
        itemService.updateItem(id, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 상품 삭제 (사용여부 변경)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }
}

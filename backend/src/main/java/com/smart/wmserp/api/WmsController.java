package com.smart.wmserp.api;

import com.smart.wmserp.domain.wms.InboundExpected;
import com.smart.wmserp.repository.InboundExpectedRepository;
import com.smart.wmserp.service.WmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms")
@RequiredArgsConstructor
public class WmsController {

    private final InboundExpectedRepository inboundExpectedRepository;
    private final WmsService wmsService;

    /**
     * 입고 예정 목록 조회
     */
    @GetMapping("/inbound/expected")
    public ResponseEntity<List<InboundExpected>> getExpectedInbounds() {
        return ResponseEntity.ok(inboundExpectedRepository.findAll());
    }

    /**
     * 입고 확정 처리
     * @param expectedId 입고예정 ID
     * @param receivedQty 실제 입고 수량
     */
    @PostMapping("/inbound/confirm")
    public ResponseEntity<Void> confirmInbound(@RequestParam Long expectedId, @RequestParam int receivedQty) {
        wmsService.confirmInbound(expectedId, receivedQty);
        return ResponseEntity.ok().build();
    }

    /**
     * 재고 조정 (Adjustment)
     */
    @PostMapping("/stock/adjust")
    public ResponseEntity<Void> adjustStock(@RequestParam Long itemId, @RequestParam int newQuantity, @RequestParam String reason) {
        wmsService.adjustStock(itemId, newQuantity, reason);
        return ResponseEntity.ok().build();
    }
}

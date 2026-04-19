package com.smart.wmserp.api;

import com.smart.wmserp.domain.master.WeightPolicy;
import com.smart.wmserp.service.WeightPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings/weights")
@RequiredArgsConstructor
public class WeightPolicyController {
    private final WeightPolicyService weightPolicyService;

    @GetMapping
    public List<WeightPolicy> getWeights() {
        return weightPolicyService.findAll();
    }

    @PostMapping
    public WeightPolicy saveWeight(@RequestBody WeightPolicy weightPolicy) {
        return weightPolicyService.save(weightPolicy);
    }
}

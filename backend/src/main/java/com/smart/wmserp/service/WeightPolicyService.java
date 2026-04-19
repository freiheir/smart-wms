package com.smart.wmserp.service;

import com.smart.wmserp.domain.master.WeightPolicy;
import com.smart.wmserp.repository.WeightPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WeightPolicyService {
    private final WeightPolicyRepository weightPolicyRepository;

    public List<WeightPolicy> findAll() {
        return weightPolicyRepository.findAll();
    }

    public WeightPolicy save(WeightPolicy weightPolicy) {
        return weightPolicyRepository.save(weightPolicy);
    }
}

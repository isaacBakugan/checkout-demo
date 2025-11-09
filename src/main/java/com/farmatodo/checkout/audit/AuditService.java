package com.farmatodo.checkout.audit;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditEventRepository repo;
    

    public AuditService(AuditEventRepository repo) {
        this.repo = repo;
    }

    public void log(String module, String action, String details) {
        String txId = MDC.get("txId"); // generado en los controladores
        if (txId == null) txId = "no-tx";

        AuditEvent event = AuditEvent.builder()
                .transactionId(txId)
                .module(module)
                .action(action)
                .details(details)
                .build();

        repo.save(event);
    }
}

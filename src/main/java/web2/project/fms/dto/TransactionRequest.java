package web2.project.fms.dto;

import lombok.Data;
import web2.project.fms.model.TransactionType;

import java.util.UUID;

@Data
public class TransactionRequest {
    private double amount;
    private String description;
    private UUID accountId;
    private UUID categoryId;
    private TransactionType type;
}

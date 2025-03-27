package web2.project.fms.dto;
import lombok.Data;

import java.util.UUID;

@Data
public class BudgetRequest {
    private String name;
    private double amount;
    private UUID categoryId;
}
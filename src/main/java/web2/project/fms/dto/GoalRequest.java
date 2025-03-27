package web2.project.fms.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class GoalRequest {
    private String name;
    private String description;
    private double amount;
    private UUID accountId;
    private UUID categoryId;
}

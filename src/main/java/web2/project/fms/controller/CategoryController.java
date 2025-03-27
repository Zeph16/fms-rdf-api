package web2.project.fms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import web2.project.fms.model.Category;
import web2.project.fms.service.AccountService;
import web2.project.fms.service.CategoryService;
import web2.project.fms.security.SecurityUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AccountService accountService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Category> getAllCategories() {
        if (SecurityUtils.isAdmin()) {
            return categoryService.getAllCategories();
        }
        return categoryService.getCategoriesByAccount(SecurityUtils.getAuthenticatedAccountId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID id) {
        Category category = categoryService.getCategoryById(id);
        SecurityUtils.checkOwnershipOrAdmin(category.getAccount().getId());
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        category.setAccount(accountService.getAccountById(SecurityUtils.getAuthenticatedAccountId()));
        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(@PathVariable UUID id, @RequestBody Category category) {
        Category existingCategory = categoryService.getCategoryById(id);
        SecurityUtils.checkOwnershipOrAdmin(existingCategory.getAccount().getId());
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        Category category = categoryService.getCategoryById(id);
        SecurityUtils.checkOwnershipOrAdmin(category.getAccount().getId());
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Category> getCategoriesByName(@PathVariable String name) {
        if (SecurityUtils.isAdmin()) {
            return categoryService.getCategoriesByName(name);
        }
        return categoryService.getCategoriesByName(name).stream()
                .filter(category -> category.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/description/{keyword}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Category> getCategoryByDescriptionKeyword(@PathVariable String keyword) {
        if (SecurityUtils.isAdmin()) {
            return categoryService.getCategoriesByDescriptionKeyword(keyword);
        }
        return categoryService.getCategoriesByDescriptionKeyword(keyword).stream()
                .filter(category -> category.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }
}

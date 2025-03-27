package web2.project.fms.service;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import web2.project.fms.exception.ResourceNotFoundException;
import web2.project.fms.mapper.RDFConverter;
import web2.project.fms.model.Category;
import web2.project.fms.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RepositoryConnection repositoryConnection;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category createCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        updateFuseki(category);
        return savedCategory;
    }

    public Category updateCategory(UUID id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());

        Category savedCategory = categoryRepository.save(category);
        updateFuseki(category);
        return savedCategory;
    }

    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
        removeFromFuseki(id);
    }

    public List<Category> getCategoriesByAccount(UUID accountId) {
        return categoryRepository.findByAccount_Id(accountId);
    }

    public List<Category> getCategoriesByName(String name) {
        return categoryRepository.findByName(name);
    }

    public List<Category> getCategoriesByDescriptionKeyword(String keyword) {
        return categoryRepository.findByDescriptionContaining(keyword);
    }

    public long getTotalCategoriesForAccount(UUID accountId) {
        return categoryRepository.countByAccount_Id(accountId);
    }

    private void updateFuseki(Category category) {
        Model model = RDFConverter.categoryToRDF(category);
        repositoryConnection.add(model);
    }

    private void removeFromFuseki(UUID id) {
        repositoryConnection.remove(RDFConverter.categoryIRI(id), null, null);
    }
}

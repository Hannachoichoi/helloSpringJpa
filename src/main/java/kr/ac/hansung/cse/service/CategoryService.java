package kr.ac.hansung.cse.service;

import kr.ac.hansung.cse.exception.CategoryHasProductsException;
import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.model.CategoryForm;
import kr.ac.hansung.cse.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public void saveCategory(CategoryForm categoryForm) {
        categoryRepository.findByName(categoryForm.getName())
                .ifPresent(category -> {
                    throw new DuplicateCategoryException("이미 존재하는 카테고리입니다.");
                });

        Category category = new Category(categoryForm.getName());
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new CategoryHasProductsException("연결된 상품이 있는 카테고리는 삭제할 수 없습니다.");
        }

        categoryRepository.delete(id);
    }
}

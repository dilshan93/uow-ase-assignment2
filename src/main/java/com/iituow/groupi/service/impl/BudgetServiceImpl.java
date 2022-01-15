package com.iituow.groupi.service.impl;

import com.iituow.groupi.database.model.DaoCategory;
import com.iituow.groupi.database.repository.CategoryRepository;
import com.iituow.groupi.database.repository.TransactionRepository;
import com.iituow.groupi.rest.exception.DatabaseValidationException;
import com.iituow.groupi.rest.request.BudgetRequest;
import com.iituow.groupi.rest.response.BudgetResponse;
import com.iituow.groupi.rest.response.base.BaseResponse;
import com.iituow.groupi.service.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public BudgetServiceImpl(CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public BudgetResponse getBudget(Integer id) {
        Optional<DaoCategory> categoryOpt = this.categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            // Get Total Income
            String income = this.transactionRepository.getTotalIncomeByCategoryId(id);

            // Get Total Expense
            String expense = this.transactionRepository.getTotalExpenseByCategoryId(id);

            BudgetResponse response = new BudgetResponse();
            response.setBudget(categoryOpt.get().getBudget());
            response.setTotalIncome(Double.parseDouble(income));
            response.setTotalExpense(Double.parseDouble("0"));
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Budget details successfully retrieved");

            return null;
        }

        throw new DatabaseValidationException(401, HttpStatus.NOT_FOUND, "Category not found!");
    }

    @Override
    public BaseResponse updateBudget(BudgetRequest payload) {
        Optional<DaoCategory> categoryOpt = this.categoryRepository.findById(payload.getCategoryId());
        if (categoryOpt.isPresent()) {
            DaoCategory category = categoryOpt.get();
            category.setBudget(payload.getBudget());
            this.categoryRepository.save(category);

            BaseResponse response = new BaseResponse();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Budget updated successfully");

            return response;
        }

        throw new DatabaseValidationException(401, HttpStatus.NOT_FOUND, "Category not found!");
    }
}

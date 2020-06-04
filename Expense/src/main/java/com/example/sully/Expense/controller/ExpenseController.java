package com.example.sully.Expense.controller;
import com.example.sully.Expense.model.Expense;
import com.example.sully.Expense.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;



    @GetMapping("/expenses/{user_id}")
    @CrossOrigin(origins = "https://easy-expense-client.herokuapp.com/")
    List<Expense> getAllUserExpenses (@PathVariable UUID user_id) {
        return expenseRepository.findByUserUUID(user_id);
    }

    @GetMapping("/expenses/{id}/{user_id}")
    @CrossOrigin(origins = "https://easy-expense-client.herokuapp.com/")
    ResponseEntity<?> getUserExpense (@PathVariable Long id, @PathVariable UUID user_id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.get().checkUserId(user_id)){
            return          expense.map(
                    response -> ResponseEntity.ok().body(response))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/expenses/category/{user_id}/{category_id}")
    @CrossOrigin(origins = "https://easy-expense-client.herokuapp.com/")
    List<Expense> getUserExpenseWithCategory (@PathVariable UUID user_id, @PathVariable Long category_id) {
        List<Expense> expense = expenseRepository.findUserExpenseByCategoryId(user_id,category_id);
        return expense;
    }



    @DeleteMapping("/expenses/{id}")
    @CrossOrigin(origins = "https://easy-expense-client.herokuapp.com/")
    ResponseEntity<?> deleteExpense(@PathVariable Long id){
        expenseRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    //our way of updating the expense description, etc
    @PutMapping("/expenses/update")
    @CrossOrigin(origins = "https://easy-expense-client.herokuapp.com/")
    ResponseEntity<Expense> updateExpense (@Valid @RequestBody Expense expense){
        Expense result = expenseRepository.save(expense);
        return ResponseEntity.ok().body(result);
    }


    @PostMapping("/expenses")
    @CrossOrigin(origins = "https://easy-expense-client.herokuapp.com/")
    ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) throws URISyntaxException{
        Expense result = expenseRepository.save(expense);
        return ResponseEntity.created(new URI("/api/expenses" + result.getId())).body(result);
    }

}

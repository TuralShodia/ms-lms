package org.example.msbook.controller;

import org.example.msbook.model.BookRequest;
import org.example.msbook.model.BookResponse;
import org.example.msbook.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {
    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/getbooks")
    public List<BookResponse> getBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping("/addbook")
    public BookResponse addBook(@RequestBody BookRequest book) {
        return bookService.addBook(book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable long id) {
        bookService.deleteBook(id);
    }
}

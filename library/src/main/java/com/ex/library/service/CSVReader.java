package com.ex.library.service;

import com.ex.library.entity.Book;
import com.ex.library.mapper.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CSVReader {

    public List<Book> CSVToBook(MultipartFile file) {
        List<Book> books = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVParser csvParser = new CSVParser(br, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            books = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                Book book = new Book();
                book.setName(record.get("name"));
                book.setQuantity(Integer.parseInt(record.get("quantity")));
                book.setAuthor(record.get("author"));
                book.setDescription(record.get("description"));
                book.setPublisher(record.get("publisher"));
                book.setAvailableQuantity(Integer.parseInt(record.get("availableQuantity")));
                book.setCoverImageUrl(record.get("coverImageUrl"));
                book.setGenre(record.get("genre"));
                book.setPublicationDate(record.get("publicationDate"));
                books.add(book);
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Error when read file CSV: " + e.getMessage());
        }

        return books;
    }
}

package com.mvm.transaction.service;

import com.mvm.transaction.dto.ExpenseResponseDTO;
import com.mvm.transaction.dto.IncomeResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

//Service to generate CSV files to export data
@Service
public class ExportService {
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private IncomeService incomeService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String CSV_HEADER = "ID;Date;Type;Description;Category;Amount\n";
    private static final char CSV_SEPARATOR = ';';
    private static final String TYPE_EXPENSE = "EXPENSE";

    public byte[] exportExpensesToCsv(Long userId) {
        List<ExpenseResponseDTO> expenses = expenseService.getAllExpenses(userId);
        return generateExpensesCsv(expenses);
    }

    public byte[] exportFilteredExpensesToCsv(Long userId, String description, String category,
                                              LocalDate startDate, LocalDate endDate,
                                              BigDecimal minAmount, BigDecimal maxAmount) {
        List<ExpenseResponseDTO> expenses = expenseService.filterExpenses(
                userId, description, category, startDate, endDate, minAmount, maxAmount);
        return generateExpensesCsv(expenses);
    }
    public byte[] exportIncomesToCsv(Long userId) {
        List<IncomeResponseDTO> incomes = incomeService.getAllIncomes(userId);
        return generateIncomesCsv(incomes);
    }

    public byte[] exportFilteredIncomesToCsv(Long userId, String description, String category,
                                              LocalDate startDate, LocalDate endDate,
                                              BigDecimal minAmount, BigDecimal maxAmount) {
        List<IncomeResponseDTO> incomes = incomeService.filterIncomes(
                userId, description, category, startDate, endDate, minAmount, maxAmount);
        return generateIncomesCsv(incomes);
    }


    private byte[] generateExpensesCsv(List<ExpenseResponseDTO> expenses) {
        //Storages bytes on memory
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        /*
        The structure try-with-resources grants the resources as OutputStreamWriter close automatically after exit the try
        StandardCharsets.UTF_8 configures the PrintWriter to use UTF-8
        */
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {
            writer.write(CSV_HEADER);
            for (ExpenseResponseDTO expense : expenses) {
                //For each expense we build and write a line
                String csvLine = buildExpenseCsvLine(expense);
                writer.write(csvLine);
            }
            //Make sure all data are write on stream
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV file", e);
        }
        //Convert the stream to an array
        return outputStream.toByteArray();
    }

    private byte[] generateIncomesCsv(List<IncomeResponseDTO> incomes) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {
            writer.write(CSV_HEADER);
            for (IncomeResponseDTO income : incomes) {
                String csvLine = buildIncomeCsvLine(income);
                writer.write(csvLine);
            }
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV file", e);
        }
        return outputStream.toByteArray();
    }

    private String buildExpenseCsvLine(ExpenseResponseDTO expense) {
        //Use StringBuilder to build the line, more efficient than use + to concat
        StringBuilder lineBuilder = new StringBuilder();

        lineBuilder.append(expense.getId()).append(CSV_SEPARATOR); //ID
        lineBuilder.append(expense.getDate().format(DATE_FORMATTER)).append(CSV_SEPARATOR); //Formated date
        lineBuilder.append(TYPE_EXPENSE).append(CSV_SEPARATOR); //Transaction type
        lineBuilder.append(escapeCsv(expense.getDescription())).append(CSV_SEPARATOR);//Escape especial characters if necessary
        lineBuilder.append(escapeCsv(expense.getCategory())).append(CSV_SEPARATOR);
        lineBuilder.append('-').append(expense.getAmount().toString()).append('\n');//Add - due to we want expense look as negative

        return lineBuilder.toString();
    }

    private String buildIncomeCsvLine(IncomeResponseDTO income) {
        StringBuilder lineBuilder = new StringBuilder();
        lineBuilder.append(income.getId()).append(CSV_SEPARATOR);
        lineBuilder.append(income.getDate().format(DATE_FORMATTER)).append(CSV_SEPARATOR);
        lineBuilder.append(TYPE_EXPENSE).append(CSV_SEPARATOR);
        lineBuilder.append(escapeCsv(income.getDescription())).append(CSV_SEPARATOR);
        lineBuilder.append(escapeCsv(income.getCategory())).append(CSV_SEPARATOR);
        lineBuilder.append(income.getAmount().toString()).append('\n');
        return lineBuilder.toString();
    }

    //We need escape especial characters due to CSV use "," to separate the fields
    private String escapeCsv(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        //Verify if the field value need an escape
        boolean needsEscape = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            //This chars broke CSV format
            if (c == CSV_SEPARATOR || c == '"' || c == '\n' || c == '\r') {
                needsEscape = true;
                break;
            }
        }

        if (!needsEscape) {
            return value;
        }

        //If need escape build new string with "
        StringBuilder escaped = new StringBuilder();
        escaped.append('"');//Initial "
        //Process each char
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"') {
                //If is a ", we need duplicate
                escaped.append("\"\"");
            } else {
                escaped.append(c);
            }
        }
        escaped.append('"');//Final "
        return escaped.toString();
    }

}

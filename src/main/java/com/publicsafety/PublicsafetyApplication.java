package com.publicsafety;

import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Додаток для редагування бази даних Publicsafety.
 * 
 * @SpringBootApplication - анотація для позначення головного класу Spring Boot додатку.
 * 
 * Клас реалізує CommandLineRunner для виконання коду після запуску додатку.
 * 
 * Методи:
 * - main(String[] args): запускає додаток Spring Boot.
 * - run(String... args): метод, що виконується після запуску додатку. Виводить меню для користувача.
 * - addPublicsafetyFromCsv(): додає записи Publicsafety з CSV-файлу до бази даних.
 * - viewAllPublicsafety(): виводить всі записи Publicsafety з бази даних.
 * - dropAllPublicsafety(): видаляє всі записи Publicsafety з бази даних.
 * 
 * Поля:
 * - publicsafetyRepository: репозиторій для роботи з документами Publicsafety.
 * 
 * Використовує:
 * - Scanner для зчитування вводу користувача.
 * - CSVReader для зчитування даних з CSV-файлу.
 * - Publicsafety для представлення документу Publicsafety.
 * - PublicsafetyRepository для взаємодії з базою даних.
 */
@SpringBootApplication
public class PublicsafetyApplication implements CommandLineRunner {

    @Autowired
    private PublicsafetyRepository publicsafetyRepository;

    public void setPublicsafetyRepository(PublicsafetyRepository publicsafetyRepository) {
        this.publicsafetyRepository = publicsafetyRepository;
    }

    InputStream getCsvInputStream() {
        return getClass().getClassLoader().getResourceAsStream("publicsafety.csv");
    }

    public static void main(String[] args) {
        SpringApplication.run(PublicsafetyApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Додати записи publicsafety з CSV-файлу");
            System.out.println("2. Подивитись записи publicsafety");
            System.out.println("3. Видалити записи publicsafety");
            System.out.println("4. Вихід");
            System.out.print("Введіть номер команди (1-4): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addPublicsafetyFromCsv();
                    break;
                case 2:
                    viewAllPublicsafety();
                    break;
                case 3:
                    dropAllPublicsafety();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Номер команди некоректний. Спробуй ще.");
            }
        }
    }

    public boolean runCommand(int choice) {
        switch (choice) {
            case 1:
                addPublicsafetyFromCsv();
                return true;
            case 2:
                viewAllPublicsafety();
                return true;
            case 3:
                dropAllPublicsafety();
                return true;
            case 4:
                return false;
            default:
                System.out.println("Номер команди некоректний. Спробуй ще.");
                return true;
        }
    }

    void addPublicsafetyFromCsv() {
        try (InputStream inputStream = getCsvInputStream()) {
            if (inputStream == null) {
                System.out.println("Не знайдено файл publicsafety.csv у classpath.");
                return;
            }

            List<Publicsafety> publicsafetyList = parsePublicsafetyCsv(inputStream);
            publicsafetyRepository.saveAll(publicsafetyList);
            System.out.println(publicsafetyList.size() + " документів publicsafety завантажено з CSV.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Не вдалось завантажити записи publicsafety з CSV.");
        }
    }

    List<Publicsafety> parsePublicsafetyCsv(InputStream inputStream) throws Exception {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<String[]> records = reader.readAll();
            if (!records.isEmpty()) {
                records.remove(0); // Видалити перший рядок з назвами стовпців
            }

            List<Publicsafety> publicsafetyList = new ArrayList<>();
            int rowNumber = 1;
            for (String[] record : records) {
                rowNumber++;
                if (record.length != 10) {
                    System.out.println("Пропускаю рядок " + rowNumber + ", очікувалося 10 стовпців, отримано "
                            + record.length + ".");
                    continue;
                }

                Publicsafety publicsafety = new Publicsafety(
                        record[0], // policeOfficer
                        record[1], // surveillanceOperator
                        record[2], // citizen
                        record[3], // incidentDate
                        record[4], // cameraLocations
                        record[5], // incidentType
                        record[6], // requestStatus
                        parseIntegerField(record[7], rowNumber), // policeExperienceYears
                        record[8], // observationCenterAddress
                        record[9]  // observationCenterPhone
                );

                publicsafetyList.add(publicsafety);
            }

            return publicsafetyList;
        }
    }

    void viewAllPublicsafety() {
        List<Publicsafety> publicsafetyList = publicsafetyRepository.findAll();
        if (publicsafetyList.isEmpty()) {
            System.out.println("Документи publicsafety не знайдено.");
        } else {
            System.out.println("Знайдено " + publicsafetyList.size() + " документів publicsafety:");
            publicsafetyList.forEach(System.out::println);
        }
    }

    void dropAllPublicsafety() {
        publicsafetyRepository.deleteAll();
        System.out.println("Записи publicsafety видалено.");
    }

    int parseIntegerField(String value, int rowNumber) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.out.println("Попередження: не вдалося розпарсити число в колонці policeExperienceYears на рядку "
             + rowNumber + ". Значення: '" + value + "'. Використано 0.");
            return 0;
        }
    }
}
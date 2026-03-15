package utils;

import java.util.regex.Pattern;

public class InputValidator {

    // ФИО: обязательно хотя бы одна буква, могут быть пробелы и дефисы между словами
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^(?=.*[a-zA-Zа-яА-ЯёЁ])[a-zA-Zа-яА-ЯёЁ\\s-]+$"
    );

    // Email: должен содержать @ и . после @
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"
    );

    // Телефон: +7 или 8, затем 10 цифр (итого 11 или 12 символов)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+7|8)[0-9]{10}$"
    );

    // VIN: 17 символов, буквы и цифры (без I, O, Q)
    private static final Pattern VIN_PATTERN = Pattern.compile(
            "^[A-HJ-NPR-Z0-9]{17}$"
    );

    // Госномер: буквы и цифры, формат как в РФ
    private static final Pattern LICENSE_PLATE_PATTERN = Pattern.compile(
            "^[АВЕКМНОРСТУХABEKMHOPCTYX]\\d{3}[АВЕКМНОРСТУХABEKMHOPCTYX]{2}\\d{2,3}$"
    );

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;

        // Убираем все пробелы, дефисы и скобки для проверки
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");

        // Проверяем соответствие паттерну
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    public static boolean isValidVin(String vin) {
        if (vin == null || vin.trim().isEmpty()) return false;
        return VIN_PATTERN.matcher(vin.toUpperCase()).matches();
    }

    public static boolean isValidLicensePlate(String plate) {
        if (plate == null || plate.trim().isEmpty()) return false;
        return LICENSE_PLATE_PATTERN.matcher(plate.toUpperCase()).matches();
    }

    public static String validateName(String name) {
        if (!isValidName(name)) {
            return "ФИО должно содержать хотя бы одну букву. Можно использовать буквы, пробелы и дефисы";
        }

        if (name.replaceAll("[\\s-]", "").isEmpty()) {
            return "ФИО не может состоять только из пробелов и дефисов";
        }

        return null;
    }

    public static String validateEmail(String email) {
        if (!isValidEmail(email)) {
            return "Введите корректный email (пример: user@mail.ru)";
        }
        return null;
    }

    public static String validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Введите номер телефона";
        }

        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");

        if (!isValidPhone(phone)) {
            if (cleanPhone.startsWith("+7") || cleanPhone.startsWith("8")) {
                if (cleanPhone.length() != 12 && cleanPhone.length() != 11) {
                    return "Номер должен содержать 11 цифр (например: +79149097719 или 89149097719)";
                }
                return "Номер должен начинаться с +7 или 8 и содержать 10 цифр после";
            } else {
                return "Номер должен начинаться с +7 или 8";
            }
        }
        return null;
    }
}
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    final static String url = "jdbc:postgresql://localhost/test?user=postgres&password=postgres";
    public static void main(String[] args) {
        try {
            var connection = DriverManager.getConnection(url);
            System.out.println("Connected to PostgreSQL database!");
            var statement = connection.createStatement();
            var getLastStatement = connection.createStatement();
            // Запрашиваем всю таблицу с историями
            var result = statement.executeQuery("SELECT * FROM copy_table_history");
            // Заправшиваем последний элемент таблицы
            var lastResult = getLastStatement.executeQuery("SELECT * FROM copy_table_history ORDER BY id_his DESC LIMIT 1");
            lastResult.next();
            Integer columnCount = null;
            var data = new ArrayList<List<Double>>();
            while (result.next()) {
                if (columnCount == null) {
                    columnCount = result.getMetaData().getColumnCount();
                }
                var record = new ArrayList<Double>(columnCount);
                for (var i = 1; i < columnCount; ++i) {
                    if (!result.wasNull()) {
                        // Для каждой записи читаем все колонки, числа парсим, null и строки сравниваем со значением
                        // в соответствующей колонке у последней записи, если совпадает то 0 иначе 10
                       var value = result.getString(i) == null ? "" : result.getString(i);
                       try {
                           var d = Double.parseDouble(value);
                           record.add(d);
                       } catch (Exception e) {
                           var lastValue = lastResult.getString(i) == null ? "" : lastResult.getString(i);
                           record.add(value.compareTo(lastValue) == 0 ? 0.0 : 10.0);
                       }
                    } else {
                        record.add(null);
                    }
                }
                data.add(record);
            }
            // Подаём считанные данные в алгоритм
            var knn = new KnnAlgorithm(data);
            // Вычисляем близжашую к последней историю
            var nearest = knn.getNearestObject();
            System.out.println();
        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
    }
}

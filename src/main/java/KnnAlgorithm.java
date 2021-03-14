import java.util.ArrayList;
import java.util.List;

public class KnnAlgorithm {
    private List<List<Double>> data;
    private List<Double> mins = new ArrayList<Double>();
    private List<Double> maxs = new ArrayList<Double>();
    private double upBorder = 10;
    private double downBorder = 0;
    private int recordSize;

    public KnnAlgorithm(List<List<Double>> data) {
        this.data = data;
        recordSize = data.get(0).size();
        // Подсчитываем минимальные и максимальные знаечния для каждого столбца
        calculateMinsMaxs();
        for (var record: data) {
            // Пересчитываем по формуле все признаки (приводим к диапазону от 0 до 10)
            record = calculateRangesForRecord(record);
        }
    }

    public List<Double> getNearestObject() {
        List<Double> nearest = data.get(0);
        var min = 9999999999.0;
        var i = 0;
        var last = data.get(data.size() - 1);
        for (var current: data) {
            if (current == last) {
                continue;
            }
            // Считаем расстояние от последней до текущей
            var d = distance(last, current);
            if (d < min) {
                min = d;
                nearest = data.get(i);
            }
            i++;
        }
        return nearest;
    }

    private void calculateMinsMaxs() {
        for (var i = 0; i < recordSize; ++i) {
            mins.add(999999999.0);
            maxs.add(-999999999.0);
            for (var j = 0; j < data.size(); ++j) {
                var value = data.get(j).get(i) == null ? 10 : data.get(j).get(i);
                if (value < mins.get(i)) {
                    mins.set(i, value);
                }
                if (value > maxs.get(i)) {
                    maxs.set(i, value);
                }
            }
        }
    }

    private List<Double> calculateRangesForRecord(List<Double> record) {
        for (var i = 0; i < record.size(); ++i) {
            var value = record.get(i) == null ? 10 : record.get(i);
            var min = mins.get(i);
            var max = maxs.get(i);
            if (max - min < 0.01) {
                record.set(i, 0.0);
                continue;
            }
            record.set(i, downBorder + (value - min) / (max - min) * (upBorder - downBorder));
        }
        return record;
    }

    private double distance(List<Double> first, List<Double> second) { ;
        var result = 0.0;
        for (var i = 0; i < recordSize; ++i) {
            if (first.get(i) == null || second.get(i) == null) {
                result += 100;
                continue;
            }
            result += Math.pow(first.get(i) - second.get(i), 2);
        }
        return Math.sqrt(result);
    }
}

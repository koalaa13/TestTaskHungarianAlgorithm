import java.io.*;
import java.util.*;

/**
 * Идея решения - свести задачу к задаче о назначениях и применить венгерский алгоритм.
 * У нас есть 2 доли, которые связаны между собой попарно с какой-то стоимостью, которую так же
 * необходимо ввести, то есть какая-то метрика похожести слов.
 */
public class Solution {
    /**
     * Для оценки похожести строк нужно использовать какой-либо критерий.
     * В данном случае реализовано расстояние Левенштайна, но можно использовать и какой-либо другой подход, делить
     * строку на отдельные токены и сравнивать их между собой, к примеру. Реализация венгерского алгоритма
     * никак не зависит от выбранной метрики, поэтому можно экспериментировать с ними.
     *
     * @param a первая строка
     * @param b вторая строка
     * @return расстояние Левенштайна между строками
     */
    private int same(String a, String b) {
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        dp[0][0] = 0;
        for (int i = 1; i <= n; ++i) {
            dp[i][0] = i;
        }
        for (int i = 1; i <= m; ++i) {
            dp[0][i] = i;
        }
        for (int i = 1; i <= n; ++i) {
            for (int j = 1; j <= m; ++j) {
                dp[i][j] = Math.min(dp[i - 1][j], dp[i][j - 1]) + 1;
                dp[i][j] = Math.min(dp[i][j], dp[i - 1][j - 1] + (a.charAt(i - 1) != b.charAt(j - 1) ? 1 : 0));
            }
        }
        return dp[n][m];
    }

    private final FastScanner in;
    private final PrintWriter out;

    /**
     * Реализация венгерского алгоритма поиска максимального паросочетания минимального веса.
     *
     * @param a Массив ребер, a[i][j] = похожесть i-ой строки из первого множества на j-тую строку из второго множества
     * @return Возвращает выбранные ребра, то есть пары строк из искомого паросочетания
     */
    private int[] findMinCostMaxMatching(int[][] a) {
        int n = a.length - 1;
        int m = a[0].length - 1;
        int[] u = new int[n + 1], v = new int[m + 1];
        int[] p = new int[m + 1], way = new int[m + 1];
        for (int i = 1; i <= n; ++i) {
            p[0] = i;
            int j0 = 0;
            int[] minv = new int[m + 1];
            Arrays.fill(minv, Integer.MAX_VALUE);
            boolean[] used = new boolean[m + 1];
            do {
                used[j0] = true;
                int i0 = p[j0], delta = Integer.MAX_VALUE, j1 = 0;
                for (int j = 1; j <= m; ++j) {
                    if (!used[j]) {
                        int cur = a[i0][j] - u[i0] - v[j];
                        if (cur < minv[j]) {
                            minv[j] = cur;
                            way[j] = j0;
                        }
                        if (minv[j] < delta) {
                            delta = minv[j];
                            j1 = j;
                        }
                    }
                }
                for (int j = 0; j <= m; ++j) {
                    if (used[j]) {
                        u[p[j]] += delta;
                        v[j] -= delta;
                    } else {
                        minv[j] -= delta;
                    }
                }
                j0 = j1;
            } while (p[j0] != 0);
            do {
                int j1 = way[j0];
                p[j0] = p[j1];
                j0 = j1;
            } while (j0 > 0);
        }
        int[] ans = new int[n + 1];
        for (int j = 1; j <= m; ++j) {
            ans[p[j]] = j;
        }
        return ans;
    }

    /**
     * Возвращает для каждой строки из первого множества максимально похожую строку из второго множества.
     * При этом каждая строка используется только в одной паре.
     * @param as Строки первого множества
     * @param bs Строки второго множества
     * @return Возвращает пары подобранных строк или {строка:?} если для строки не хватило пары
     */
    public List<String> solve(String[] as, String[] bs) {
        int n = as.length;
        int m = bs.length;
        // Данная реализация венгерского алгоритма работает, когда первая доля по размера меньше либо равна размеру второй.
        // В ином случае необходимо поменять их местами.
        boolean swap = false;
        if (n > m) {
            swap = true;
            int temp = m;
            m = n;
            n = temp;
            String[] tmp = as;
            as = bs;
            bs = tmp;
        }
        // массив, на котором будет работать венгерский алгоритм
        int[][] a = new int[n + 1][m + 1];
        for (int i = 1; i <= n; ++i) {
            for (int j = 1; j <= m; ++j) {
                // Так как расстояние Левенштайна показывает насколько строки различны,
                // то мы хотим искать паросочетание МИНИМАЛЬНОГО веса в данном случае.
                a[i][j] = same(as[i - 1], bs[j - 1]);
            }
        }
        int[] ans = findMinCostMaxMatching(a);
        // Так как мы, возможно, поменяли доли местами, а хочется иметь вывод вида "строка из первого множества":"строка из второго множества",
        // то получение ответа становится несколько сложнее.
        boolean[] used = new boolean[m + 1]; // Массив какие строки из второй доли использовали в паросочетании
        List<String> res = new ArrayList<>();
        for (int i = 1; i <= n; ++i) {
            if (!swap) {
                if (ans[i] != 0) {
                    used[ans[i]] = true;
                    res.add(as[i - 1] + ':' + bs[ans[i] - 1]);
                }
            } else {
                if (ans[i] != 0) {
                    used[ans[i]] = true;
                    res.add(bs[ans[i] - 1] + ':' + as[i - 1]);
                }
            }
        }
        for (int i = 1; i <= m; ++i) {
            if (!used[i]) {
                res.add(bs[i - 1] + ":?");
            }
        }
        return res;
    }

    public Solution() {
        in = new FastScanner(System.in);
        out = new PrintWriter(System.out);
    }

    public Solution(String inputFilename, String outputFilename) throws FileNotFoundException {
        in = new FastScanner(new File(inputFilename));
        out = new PrintWriter(outputFilename);
    }

    private void run() {
        int n = in.nextInt();
        String[] as = new String[n];
        for (int i = 0; i < n; ++i) {
            as[i] = in.nextLine();
        }
        int m = in.nextInt();
        String[] bs = new String[m];
        for (int i = 0; i < m; ++i) {
            bs[i] = in.nextLine();
        }
        List<String> res = solve(as, bs);
        for (String s : res) {
            out.println(s);
        }

        out.close();
    }

    public static void main(String[] args) {
        new Solution().run();
    }
}

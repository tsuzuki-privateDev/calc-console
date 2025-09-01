import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcV1 {
    private static final Pattern EXPR = Pattern.compile(
        "\\s*([+-]?\\d+(?:\\.\\d+)?|ans)\\s*([+\\-*/])\\s*([+-]?\\d+(?:\\.\\d+)?|ans)\\s*");

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Double ans = null; // 前回の結果を保持
        System.out.println("電卓V2: '12 + 3' / 'ans * 2' の形式で入力。'exit'で終了。");

        while (true) {
            System.out.print("> ");
            String line = sc.nextLine();
            if (line == null) break;
            if (line.trim().equalsIgnoreCase("exit")) break;

            Matcher m = EXPR.matcher(line);
            if (!m.matches()) {
                System.out.println("形式エラー: 例) 12 + 3 / ans * 2");
                continue;
            }

            try {
                double a = parseValue(m.group(1), ans);
                double b = parseValue(m.group(3), ans);
                String op = m.group(2);

                Double result = calc(a, op, b);
                if (result == null) {
                    System.out.println("エラー: 0で割るな。");
                } else {
                    ans = result; // 結果を保存
                    System.out.println("= " + strip(ans));
                }
            } catch (Exception e) {
                System.out.println("エラー: " + e.getMessage());
            }
        }
        sc.close();
        System.out.println("終了。");
    }

    private static double parseValue(String token, Double ans) {
        if (token.equals("ans")) {
            if (ans == null) throw new IllegalArgumentException("まだansは未定義");
            return ans;
        }
        return Double.parseDouble(token);
    }

    private static Double calc(double a, String op, double b) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return (b == 0.0) ? null : a / b;
            default:  return null;
        }
    }

    private static String strip(double x) {
        if (x == Math.rint(x)) return String.valueOf((long)x);
        return String.valueOf(x);
    }
}

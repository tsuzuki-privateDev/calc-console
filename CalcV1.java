import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcV1 {
    private static final Pattern EXPR = Pattern.compile(
        "\\s*([+-]?\\d+(?:\\.\\d+)?|ans)\\s*([+\\-*/])\\s*([+-]?\\d+(?:\\.\\d+)?|ans)\\s*");

    private static final int HISTORY_LIMIT = 100;
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Deque<String> history = new ArrayDeque<>();
        Double ans = null; // 前回の結果を保持

        System.out.println("Calc v3 (履歴対応): '12 + 3' / 'ans * 2' / 'history' / 'exit'");

        while (true) {
            System.out.print("> ");
            String line = sc.nextLine();
            if (line == null) break;
            String trim = line.trim();
            if (trim.equalsIgnoreCase("exit")) break;
            if (trim.isEmpty()) continue;

            // historyコマンド処理
            if (trim.startsWith("history")) {
                handleHistoryCmd(trim, history);
                continue;
            }

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
                    continue;
                } else {
                    ans = result; // 結果を保存
                    String out = strip(ans);
                    System.out.println("= " + out);

                    // 履歴保存
                    String exprShown = strip(a) + " " + op + " " + strip(b) + " = " + out;
                    pushHistory(history, exprShown);
                }
            } catch (Exception e) {
                System.out.println("エラー: " + e.getMessage());
            }
        }
        sc.close();
        System.out.println("終了。");
    }

    private static void handleHistoryCmd(String cmd, Deque<String> history) {
        String[] parts = cmd.split("\\s+");
        if (parts.length == 1) {
            printHistory(history, 10);
            return;
        }
        if (parts.length == 2 && parts[1].equalsIgnoreCase("clear")) {
            history.clear();
            System.out.println("(履歴を消去しました)");
            return;
        }
        if (parts.length == 2) {
            try {
                int n = Integer.parseInt(parts[1]);
                if (n <= 0) {System.out.println("正の数を指定して。"); return;}
                printHistory(history, n);
            } catch (Exception e) {
                System.out.println("history の使い方： 'history' / history 5' / 'history clear'");
            }
            return;
        }
        System.out.println("history の使い方: 'history' / 'history 5' / 'history clear'");
    }

    private static void printHistory(Deque<String> history, int n) {
        if (history.isEmpty()) {System.out.println("(履歴なし)"); return;}
        int count = 0;
        for (String s : history) {
            System.out.println(s);
            if (++count >= n) break;
        }
    }

    private static void pushHistory(Deque<String> history, String entry) {
        history.addFirst(entry);
        while (history.size() > HISTORY_LIMIT) history.removeLast();
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

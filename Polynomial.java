import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Polynomial {
    private double[] coefficients; 
    private int[] exponents;     

    public Polynomial() {
        this.coefficients = new double[]{0.0};
        this.exponents = new int[]{0};
    }

    public Polynomial(double[] dense) {
        int count = 0;
        for (int i = 0; i < dense.length; i++) {
            if (dense[i] != 0.0) count++;
        }
        if (count == 0) {
            this.coefficients = new double[]{0.0};
            this.exponents = new int[]{0};
            return;
        }
        this.coefficients = new double[count];
        this.exponents = new int[count];
        int idx = 0;
        for (int i = 0; i < dense.length; i++) {
            if (dense[i] != 0.0) {
                this.coefficients[idx] = dense[i];
                this.exponents[idx] = i;
                idx++;
            }
        }
        sortByExponent();
        consolidateInPlace();
    }

    private Polynomial(double[] coeffs, int[] exps, int len) {
        this.coefficients = new double[len];
        this.exponents = new int[len];
        for (int i = 0; i < len; i++) {
            this.coefficients[i] = coeffs[i];
            this.exponents[i] = exps[i];
        }
        sortByExponent();
        consolidateInPlace();
    }

    public Polynomial(File file) throws IOException {
        String line = null;
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            line = br.readLine();
        } finally {
            br.close();
        }
        if (line == null || line.length() == 0) {
            this.coefficients = new double[]{0.0};
            this.exponents = new int[]{0};
            return;
        }
        parseCompact(line.trim());
        sortByExponent();
        consolidateInPlace();
    }

    public Polynomial add(Polynomial other) {
        int n = this.coefficients.length;
        int m = other.coefficients.length;
        double[] c = new double[n + m];
        int[] e = new int[n + m];
        int i = 0, j = 0, k = 0;
        while (i < n && j < m) {
            if (this.exponents[i] == other.exponents[j]) {
                double sum = this.coefficients[i] + other.coefficients[j];
                if (sum != 0.0) {
                    c[k] = sum;
                    e[k] = this.exponents[i];
                    k++;
                }
                i++; j++;
            } else if (this.exponents[i] < other.exponents[j]) {
                c[k] = this.coefficients[i];
                e[k] = this.exponents[i];
                i++; k++;
            } else {
                c[k] = other.coefficients[j];
                e[k] = other.exponents[j];
                j++; k++;
            }
        }
        while (i < n) { c[k] = this.coefficients[i]; e[k] = this.exponents[i]; i++; k++; }
        while (j < m) { c[k] = other.coefficients[j]; e[k] = other.exponents[j]; j++; k++; }
        return new Polynomial(c, e, k);
    }

    public Polynomial multiply(Polynomial other) {
        int n = this.coefficients.length;
        int m = other.coefficients.length;
        double[] tempC = new double[n * m];
        int[] tempE = new int[n * m];
        int t = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                tempC[t] = this.coefficients[i] * other.coefficients[j];
                tempE[t] = this.exponents[i] + other.exponents[j];
                t++;
            }
        }
        for (int a = 0; a < t; a++) {
            int minPos = a;
            for (int b = a + 1; b < t; b++) {
                if (tempE[b] < tempE[minPos]) minPos = b;
            }
            if (minPos != a) {
                int te = tempE[a]; tempE[a] = tempE[minPos]; tempE[minPos] = te;
                double tc = tempC[a]; tempC[a] = tempC[minPos]; tempC[minPos] = tc;
            }
        }
        double[] c = new double[t];
        int[] e = new int[t];
        int k = 0;
        int idx = 0;
        while (idx < t) {
            int exp = tempE[idx];
            double sum = 0.0;
            while (idx < t && tempE[idx] == exp) {
                sum += tempC[idx];
                idx++;
            }
            if (sum != 0.0) {
                c[k] = sum;
                e[k] = exp;
                k++;
            }
        }
        return new Polynomial(c, e, k);
    }

    public double evaluate(double x) {
        double result = 0.0;
        for (int i2 = 0; i2 < coefficients.length; i2++) {
            result += coefficients[i2] * Math.pow(x, exponents[i2]);
        }
        return result;
    }

    public boolean hasRoot(double x) {
        return Math.abs(evaluate(x)) < 1e-10;
    }

    public void saveToFile(String fileName) throws IOException {
        PrintWriter pw = new PrintWriter(fileName);
        try {
            pw.print(toCompactString());
        } finally {
            pw.close();
        }
    }

    private void sortByExponent() {
        for (int i = 1; i < exponents.length; i++) {
            int keyE = exponents[i];
            double keyC = coefficients[i];
            int j = i - 1;
            while (j >= 0 && exponents[j] > keyE) {
                exponents[j + 1] = exponents[j];
                coefficients[j + 1] = coefficients[j];
                j--;
            }
            exponents[j + 1] = keyE;
            coefficients[j + 1] = keyC;
        }
    }

    private void consolidateInPlace() {
        if (coefficients.length == 0) return;
        int n = coefficients.length;
        double[] c = new double[n];
        int[] e = new int[n];
        int k = 0;
        int i = 0;
        while (i < n) {
            int exp = exponents[i];
            double sum = 0.0;
            while (i < n && exponents[i] == exp) {
                sum += coefficients[i];
                i++;
            }
            if (sum != 0.0) {
                c[k] = sum;
                e[k] = exp;
                k++;
            }
        }
        if (k == 0) {
            this.coefficients = new double[]{0.0};
            this.exponents = new int[]{0};
        } else {
            double[] nc = new double[k];
            int[] ne = new int[k];
            for (int t = 0; t < k; t++) { nc[t] = c[t]; ne[t] = e[t]; }
            this.coefficients = nc;
            this.exponents = ne;
        }
    }

    private void parseCompact(String s) {
        int capacity = s.length();
        double[] tmpC = new double[capacity];
        int[] tmpE = new int[capacity];
        int count = 0;
        int i = 0;
        while (i < s.length()) {
            int sign = 1;
            char ch = s.charAt(i);
            if (ch == '+') { sign = 1; i++; }
            else if (ch == '-') { sign = -1; i++; }
            double coeff = 0.0;
            boolean coeffSpecified = false;
            int start = i;
            while (i < s.length()) {
                char c = s.charAt(i);
                if ((c >= '0' && c <= '9') || c == '.') i++; else break;
            }
            if (i > start) {
                coeff = parseDoubleSimple(s.substring(start, i));
                coeffSpecified = true;
            }
            int exp = 0;
            if (i < s.length() && s.charAt(i) == 'x') {
                i++;
                if (!coeffSpecified) coeff = 1.0;
                if (i < s.length() && s.charAt(i) >= '0' && s.charAt(i) <= '9') {
                    int estart = i;
                    while (i < s.length() && s.charAt(i) >= '0' && s.charAt(i) <= '9') i++;
                    exp = parseIntSimple(s.substring(estart, i));
                } else {
                    exp = 1;
                }
            } else {
                if (!coeffSpecified) coeff = 0.0;
                exp = 0;
            }
            tmpC[count] = sign * coeff;
            tmpE[count] = exp;
            count++;
        }
        if (count == 0) {
            this.coefficients = new double[]{0.0};
            this.exponents = new int[]{0};
            return;
        }
        double[] c = new double[count];
        int[] e = new int[count];
        for (int t = 0; t < count; t++) { c[t] = tmpC[t]; e[t] = tmpE[t]; }
        this.coefficients = c;
        this.exponents = e;
    }

    private static int parseIntSimple(String s) {
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            val = val * 10 + (s.charAt(i) - '0');
        }
        return val;
    }

    private static double parseDoubleSimple(String s) {
        double val = 0.0;
        double frac = 0.0;
        double base = 1.0;
        int i = 0;
        while (i < s.length() && s.charAt(i) != '.') {
            val = val * 10 + (s.charAt(i) - '0');
            i++;
        }
        if (i < s.length() && s.charAt(i) == '.') {
            i++;
            while (i < s.length()) {
                base /= 10.0;
                frac += (s.charAt(i) - '0') * base;
                i++;
            }
        }
        return val + frac;
    }

    private String toCompactString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coefficients.length; i++) {
            double c = coefficients[i];
            int e = exponents[i];
            if (c == 0.0) continue;
            String sign = c >= 0 ? "+" : "-";
            double abs = c >= 0 ? c : -c;
            if (e == 0) {
                sb.append(sign).append(abs);
            } else {
                if (abs == 1.0) sb.append(sign).append("x");
                else sb.append(sign).append(abs).append("x");
                if (e != 1) sb.append(e);
            }
        }
        if (sb.length() == 0) return "0";
        return sb.charAt(0) == '+' ? sb.substring(1) : sb.toString();
    }
}
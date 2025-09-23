import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Polynomial {
    private double[] coefficients; 
    private int[] exponents;       

    public Polynomial() {
        this.coefficients = new double[]{0.0};
        this.exponents = new int[]{0};
    }

    public Polynomial(double[] denseCoefficients) {
        List<Double> coeffList = new ArrayList<>();
        List<Integer> expList = new ArrayList<>();
        for (int i = 0; i < denseCoefficients.length; i++) {
            if (denseCoefficients[i] != 0.0) {
                coeffList.add(denseCoefficients[i]);
                expList.add(i);
            }
        }
        if (coeffList.isEmpty()) {
            this.coefficients = new double[]{0.0};
            this.exponents = new int[]{0};
        } else {
            this.coefficients = new double[coeffList.size()];
            this.exponents = new int[expList.size()];
            for (int i = 0; i < coeffList.size(); i++) {
                this.coefficients[i] = coeffList.get(i);
                this.exponents[i] = expList.get(i);
            }
            sortByExponent();
        }
    }

    private Polynomial(double[] coefficients, int[] exponents) {
        // Assume inputs are already consolidated by caller
        this.coefficients = Arrays.copyOf(coefficients, coefficients.length);
        this.exponents = Arrays.copyOf(exponents, exponents.length);
        sortByExponent();
    }

    public Polynomial(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) {
                this.coefficients = new double[]{0.0};
                this.exponents = new int[]{0};
                return;
            }
            parseFromCompactString(line.trim());
        }
    }

    public Polynomial add(Polynomial other) {
        Map<Integer, Double> expToCoeff = new HashMap<>();
        for (int i = 0; i < this.coefficients.length; i++) {
            expToCoeff.merge(this.exponents[i], this.coefficients[i], Double::sum);
        }
        for (int i = 0; i < other.coefficients.length; i++) {
            expToCoeff.merge(other.exponents[i], other.coefficients[i], Double::sum);
        }
        return fromExponentMap(expToCoeff);
    }

    public Polynomial multiply(Polynomial other) {
        Map<Integer, Double> expToCoeff = new HashMap<>();
        for (int i = 0; i < this.coefficients.length; i++) {
            for (int j = 0; j < other.coefficients.length; j++) {
                int exp = this.exponents[i] + other.exponents[j];
                double coeff = this.coefficients[i] * other.coefficients[j];
                expToCoeff.merge(exp, coeff, Double::sum);
            }
        }
        return fromExponentMap(expToCoeff);
    }

    public double evaluate(double x) {
        double result = 0.0;
        for (int i = 0; i < coefficients.length; i++) {
            result += coefficients[i] * Math.pow(x, exponents[i]);
        }
        return result;
    }

    public boolean hasRoot(double x) {
        return Math.abs(evaluate(x)) < 1e-10;
    }

    public void saveToFile(String fileName) throws IOException {
        try (PrintWriter pw = new PrintWriter(fileName)) {
            pw.print(toCompactString());
        }
    }

    private void consolidateLikeTerms() {
        Map<Integer, Double> expToCoeff = new HashMap<>();
        for (int i = 0; i < coefficients.length; i++) {
            if (coefficients[i] != 0.0) {
                expToCoeff.merge(exponents[i], coefficients[i], Double::sum);
            }
        }
        Polynomial normalized = fromExponentMap(expToCoeff);
        this.coefficients = normalized.coefficients;
        this.exponents = normalized.exponents;
    }

    private void sortByExponent() {
        for (int i = 1; i < exponents.length; i++) {
            int keyExp = exponents[i];
            double keyCoeff = coefficients[i];
            int j = i - 1;
            while (j >= 0 && exponents[j] > keyExp) {
                exponents[j + 1] = exponents[j];
                coefficients[j + 1] = coefficients[j];
                j--;
            }
            exponents[j + 1] = keyExp;
            coefficients[j + 1] = keyCoeff;
        }
    }

    private Polynomial fromExponentMap(Map<Integer, Double> expToCoeff) {
        if (expToCoeff.isEmpty()) {
            return new Polynomial();
        }
        List<Integer> exps = new ArrayList<>();
        List<Double> coeffs = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : expToCoeff.entrySet()) {
            double c = e.getValue();
            if (Math.abs(c) >= 1e-12) {
                exps.add(e.getKey());
                coeffs.add(c);
            }
        }
        if (coeffs.isEmpty()) {
            return new Polynomial();
        }
        int n = coeffs.size();
        double[] cArr = new double[n];
        int[] eArr = new int[n];
        for (int i = 0; i < n; i++) {
            cArr[i] = coeffs.get(i);
            eArr[i] = exps.get(i);
        }
        Polynomial p = new Polynomial(cArr, eArr);
        p.sortByExponent();
        return p;
    }

    private void parseFromCompactString(String s) {
        if (s.isEmpty()) {
            this.coefficients = new double[]{0.0};
            this.exponents = new int[]{0};
            return;
        }
        String normalized = s;
        if (normalized.charAt(0) != '-') {
            normalized = "+" + normalized;
        }
        normalized = normalized.replace("-", "+-");
        String[] parts = normalized.split("\\+");
        List<Double> coeffs = new ArrayList<>();
        List<Integer> exps = new ArrayList<>();
        for (String part : parts) {
            if (part == null || part.isEmpty()) continue;
            String token = part;
            double coeff;
            int exp;
            int xIndex = token.indexOf('x');
            if (xIndex == -1) {
                coeff = Double.parseDouble(token);
                exp = 0;
            } else {
                String coeffStr = token.substring(0, xIndex);
                if (coeffStr.isEmpty() || coeffStr.equals("+")) {
                    coeff = 1.0;
                } else if (coeffStr.equals("-")) {
                    coeff = -1.0;
                } else {
                    coeff = Double.parseDouble(coeffStr);
                }
                String expStr = token.substring(xIndex + 1);
                if (expStr.isEmpty()) {
                    exp = 1; 
                } else {
                    exp = Integer.parseInt(expStr);
                }
            }
            coeffs.add(coeff);
            exps.add(exp);
        }
        if (coeffs.isEmpty()) {
            this.coefficients = new double[]{0.0};
            this.exponents = new int[]{0};
        } else {
            this.coefficients = new double[coeffs.size()];
            this.exponents = new int[exps.size()];
            for (int i = 0; i < coeffs.size(); i++) {
                this.coefficients[i] = coeffs.get(i);
                this.exponents[i] = exps.get(i);
            }
            consolidateLikeTerms();
            sortByExponent();
        }
    }

    private String toCompactString() {
        sortByExponent();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coefficients.length; i++) {
            double c = coefficients[i];
            int e = exponents[i];
            if (Math.abs(c) < 1e-12) continue;
            String sign = c >= 0 ? "+" : "-";
            double abs = Math.abs(c);
            if (e == 0) {
                sb.append(sign).append(abs);
            } else {
                if (abs == 1.0) {
                    sb.append(sign).append("x");
                } else {
                    sb.append(sign).append(abs).append("x");
                }
                if (e != 1) {
                    sb.append(e);
                }
            }
        }
        if (sb.length() == 0) return "0";
        if (sb.charAt(0) == '+') {
            return sb.substring(1);
        }
        return sb.toString();
    }
}
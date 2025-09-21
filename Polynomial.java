public class Polynomial {
    private double[] coefficients;
    
    public Polynomial() {
        this.coefficients = new double[]{0};
    }
    
    public Polynomial(double[] coefficients) {
        this.coefficients = new double[coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            this.coefficients[i] = coefficients[i];
        }
    }
    public Polynomial add(Polynomial other) {
        int maxLength = Math.max(this.coefficients.length, other.coefficients.length);
        double[] resultCoeffs = new double[maxLength];
        for (int i = 0; i < this.coefficients.length; i++) {
            resultCoeffs[i] += this.coefficients[i];
        }
        for (int i = 0; i < other.coefficients.length; i++) {
            resultCoeffs[i] += other.coefficients[i];
        }
        
        return new Polynomial(resultCoeffs);
    }
    
    public double evaluate(double x) {
        double result = 0.0;
        for (int i = 0; i < coefficients.length; i++) {
            result += coefficients[i] * Math.pow(x, i);
        }
        return result;
    }
    
    public boolean hasRoot(double x) {
        return Math.abs(evaluate(x)) < 1e-10;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean firstTerm = true;
        
        for (int i = coefficients.length - 1; i >= 0; i--) {
            if (coefficients[i] != 0) {
                if (!firstTerm && coefficients[i] > 0) {
                    sb.append(" + ");
                } else if (!firstTerm && coefficients[i] < 0) {
                    sb.append(" - ");
                } else if (coefficients[i] < 0) {
                    sb.append("-");
                }
                
                double absCoeff = Math.abs(coefficients[i]);
                if (i == 0) {
                    sb.append(absCoeff);
                } else if (i == 1) {
                    if (absCoeff == 1) {
                        sb.append("x");
                    } else {
                        sb.append(absCoeff).append("x");
                    }
                } else {
                    if (absCoeff == 1) {
                        sb.append("x^").append(i);
                    } else {
                        sb.append(absCoeff).append("x^").append(i);
                    }
                }
                firstTerm = false;
            }
        }
        
        if (firstTerm) {
            return "0";
        }
        
        return sb.toString();
    }
}
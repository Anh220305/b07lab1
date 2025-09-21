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
    //using this method learned from class 
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
}
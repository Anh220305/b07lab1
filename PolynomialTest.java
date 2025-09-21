public class PolynomialTest {
    public static void main(String[] args) {
        double[] coeffs1 = {6, -2, 0, 5};
        Polynomial p1 = new Polynomial(coeffs1);
        
        System.out.println("Polynomial 1: " + p1);
        System.out.println("Evaluating at x = -1: " + p1.evaluate(-1));
        System.out.println("Is x = -1 a root? " + p1.hasRoot(-1));
        System.out.println();
        
        Polynomial p2 = new Polynomial();
        System.out.println("Zero polynomial: " + p2);
        System.out.println("Evaluating zero polynomial at x = 5: " + p2.evaluate(5));
        System.out.println("Is x = 5 a root of zero polynomial? " + p2.hasRoot(5));
        System.out.println();
        
        double[] coeffs2 = {1, 3, 2}; 
        Polynomial p3 = new Polynomial(coeffs2);
        System.out.println("Polynomial 2: " + p3);
        
        Polynomial sum = p1.add(p3);
        System.out.println("Sum of polynomials: " + sum);
        System.out.println();
        
        double[] coeffs3 = {-4, 0, 1};
        Polynomial p4 = new Polynomial(coeffs3);
        System.out.println("Polynomial 3: " + p4);
        System.out.println("Evaluating at x = 2: " + p4.evaluate(2));
        System.out.println("Is x = 2 a root? " + p4.hasRoot(2));
        System.out.println("Evaluating at x = -2: " + p4.evaluate(-2));
        System.out.println("Is x = -2 a root? " + p4.hasRoot(-2));
        System.out.println("Evaluating at x = 1: " + p4.evaluate(1));
        System.out.println("Is x = 1 a root? " + p4.hasRoot(1));
    }
}
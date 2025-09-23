import java.io.File;
import java.io.IOException;

public class Driver {
    public static void main(String [] args) {
        Polynomial p = new Polynomial();
        System.out.println(p.evaluate(3));

        double [] c1 = {6,0,0,5};
        Polynomial p1 = new Polynomial(c1);
        double [] c2 = {0,-2,0,0,-9}; 
        Polynomial p2 = new Polynomial(c2);
        Polynomial s = p1.add(p2);
        System.out.println("s(0.1) = " + s.evaluate(0.1));
        if (s.hasRoot(1)) System.out.println("1 is a root of s");
        else System.out.println("1 is not a root of s");

        Polynomial m1 = new Polynomial(new double[]{1,2});
        Polynomial m2 = new Polynomial(new double[]{3,-1});
        Polynomial prod = m1.multiply(m2);
        System.out.println("prod(2) = " + prod.evaluate(2)); 

        try {
            Polynomial fromFile;
            s.saveToFile("poly.txt");
            fromFile = new Polynomial(new File("poly.txt"));
            System.out.println("fromFile(0.1) = " + fromFile.evaluate(0.1));
        } catch (IOException e) {
            System.out.println("File IO error: " + e.getMessage());
        }
    }
}

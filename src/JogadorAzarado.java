import java.util.Random;
import java.util.Scanner;

public class JogadorAzarado extends Jogador {
    public JogadorAzarado(String nome, String cor, Scanner scanner) {
        super(nome, cor, scanner);
    }

    @Override
    public int lancarDados(Random random, boolean debug) {
        int dado1, dado2;
        
        if (debug) {
            System.out.print("[DEBUG] Digite o valor do dado 1 (1-6): ");
            dado1 = scanner.nextInt();
            System.out.print("[DEBUG] Digite o valor do dado 2 (1-6): ");
            dado2 = scanner.nextInt();
            scanner.nextLine();
        } else {
            dado1 = random.nextInt(6) + 1;
            dado2 = random.nextInt(6) + 1;
        }
        
        int total = dado1 + dado2;
        System.out.println(CoresConsole.RED + getCor() + " (Azarado) lançou " + dado1 + " + " + dado2 + " = " + total + CoresConsole.RESET);
        
        setJogarNovamente(dado1 == dado2);

        return total;
    }

    @Override
    public String getTipoJogador() {
        return "Azarado";
    }
}
public class Main {
    public static void main(String[] args) {
        JogoTabuleiro jogo = new JogoTabuleiro();
        try {
            jogo.executarMenu();
        } finally {
            jogo.finalizar();
        }
    }
}
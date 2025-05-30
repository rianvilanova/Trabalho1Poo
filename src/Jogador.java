import java.util.Random;
import java.util.Scanner;

public abstract class Jogador {
    protected Scanner scanner;

    private String nome;
    private String cor;
    private int posicao;
    private int turno;
    private boolean pularProxima;
    private boolean jogarNovamente;
    
    public Jogador(String nome, String cor, Scanner scanner) {
        this.nome = nome;
        this.cor = cor;
        this.scanner = scanner;
        this.posicao = 0;
        this.turno = 0;
        this.pularProxima = false;
        this.jogarNovamente = false;
    }
    
    public abstract int lancarDados(Random random, boolean debug);
    public abstract String getTipoJogador();
    
    public void mover(int casas) {
        posicao += casas;
        if (posicao < 0) posicao = 0;
        if (posicao >= Tabuleiro.TAMANHO_TABULEIRO) {
            posicao = Tabuleiro.TAMANHO_TABULEIRO;
        }
    }
    
    public String getNome() { return nome; }
    public String getCor() { return cor; }
    public int getPosicao() { return posicao; }
    public void setPosicao(int posicao) { this.posicao = posicao; }
    public int getTurno() { return turno; }
    public void setTurno(int turno) { this.turno = turno; }
    public boolean isPularProxima() { return pularProxima; }
    public void setPularProxima(boolean pularProxima) { this.pularProxima = pularProxima; }
    public boolean isJogarNovamente() { return jogarNovamente; }
    public void setJogarNovamente(boolean jogarNovamente) { this.jogarNovamente = jogarNovamente; }
    public int imprimirPosicao() { return posicao; } 
}
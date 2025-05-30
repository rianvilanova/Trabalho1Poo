public class Tabuleiro {
    public static final int TAMANHO_TABULEIRO = 40;
    
    public boolean CasaEspecial(int posicao) {
        return posicao == 10 || posicao == 25 || posicao == 38;
    }
    
    public boolean CasaSurpresa(int posicao) {
        return posicao == 13;
    }
    
    public boolean CasaSorte(int posicao) {
        return posicao == 5 || posicao == 15 || posicao == 30;
    }
    
    public boolean CasaEscolha(int posicao) {
        return posicao == 17 || posicao == 27;
    }
    
    public boolean CasaMagica(int posicao) {
        return posicao == 20 || posicao == 35;
    }
}
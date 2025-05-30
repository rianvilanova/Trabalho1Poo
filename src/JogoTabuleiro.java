import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class JogoTabuleiro {
    private Scanner scanner;
    private List<Jogador> listaJogadores;
    private Random random;
    private Tabuleiro tabuleiro;
    private int turnoAtual;
    private boolean modoDebug;
    private boolean jogoTerminado;

    public JogoTabuleiro() {
        scanner = new Scanner(System.in);
        listaJogadores = new ArrayList<>();
        random = new Random();
        tabuleiro = new Tabuleiro();
        modoDebug = false;
        jogoTerminado = false;
    }

    public void executarMenu() {
        boolean sair = false;

        while (!sair) {
            System.out.println("\n" + CoresConsole.CYAN_BOLD + "=========== MENU ===========" + CoresConsole.RESET);
            System.out.println("1. Instruções do Jogo");
            System.out.println("2. Configurar Jogadores");
            System.out.println("3. Ativar/Desativar Modo Debug");
            System.out.println("4. Iniciar Jogo");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");

            String escolha = scanner.nextLine();

            switch (escolha) {
                case "1":
                    mostrarRegras();
                    break;
                case "2":
                    configurarJogadores();
                    break;
                case "3":
                    modoDebug = !modoDebug;
                    System.out.println("Modo Debug " + (modoDebug ? "ativado" : "desativado"));
                    break;
                case "4":
                    if (listaJogadores.size() >= 2) {
                        iniciarJogo();
                    } else {
                        System.out.println(CoresConsole.RED + "\nAdicione pelo menos 2 jogadores!" + CoresConsole.RESET);
                    }
                    break;
                case "5":
                    System.out.println("Saindo do jogo...");
                    sair = true;
                    break;
                default:
                    System.out.println(CoresConsole.RED + "Opção inválida. Tente novamente." + CoresConsole.RESET);
            }
        }
    }

    private void mostrarRegras() {
        System.out.println("\n" + CoresConsole.YELLOW_BOLD + "=========== INSTRUÇÕES DO JOGO ===========" + CoresConsole.RESET);
        System.out.println("- De 2 a 6 jogadores podem participar.");
        System.out.println("- Cada jogador tem um tipo: Sortudo, Azarado ou Normal.");
        System.out.println("- Jogadores lançam dados e avançam no tabuleiro.");
        System.out.println("- Casas Especiais alteram o jogo:");
        System.out.println("  * Casa de Sorte: avança 3 casas (exceto Azarado).");
        System.out.println("  * Casa de Azar: perde a próxima rodada.");
        System.out.println("  * Casa Mágica: troca de lugar com o último.");
        System.out.println("  * Casa de Escolha: manda outro jogador para o início.");
        System.out.println("  * Casa Surpresa: muda o tipo do jogador.");
        System.out.println("- Vence quem chegar ou ultrapassar a última casa.\n");
    }

    private void configurarJogadores() {
        System.out.print("\nQuantos jogadores irão jogar? (2-6): ");
        int numJogadores = 0;

        while (numJogadores < 2 || numJogadores > 6) {
            try {
                String entrada = scanner.nextLine();
                numJogadores = Integer.parseInt(entrada);

                if (numJogadores < 2 || numJogadores > 6) {
                    System.out.print("Número inválido. Escolha entre 2 e 6 jogadores: " + CoresConsole.RESET);
                }
            } catch (NumberFormatException e) {
                System.out.print(CoresConsole.RED + "Entrada inválida. Escolha entre 2 e 6 jogadores: " + CoresConsole.RESET);
            }
        }

        listaJogadores.clear();
        for (int i = 0; i < numJogadores; i++) {
            adicionarJogador("Jogador " + (i + 1));
        }
    }

    private void adicionarJogador(String nome) {
        if (listaJogadores.size() >= 6) {
            System.out.println("Número máximo de jogadores alcançado.");
            return;
        }

        String cor = perguntarCorJogador(nome);
        Jogador jogador = criarJogadorAleatorio(nome, cor, scanner);
        listaJogadores.add(jogador);
        System.out.println(nome + " adicionado como " + jogador.getTipoJogador() + ".");
    }

    private String perguntarCorJogador(String nomeJogador) {
        String[] coresValidas = {"Azul", "Verde", "Amarelo", "Roxo", "Rosa", "Vermelho"};
        String corEscolhida = null;
        boolean corValida = false;

        while (!corValida) {
            System.out.println("\nEscolha uma cor para " + nomeJogador + ":");
            for (int i = 0; i < coresValidas.length; i++) {
                System.out.println((i + 1) + ". " + coresValidas[i]);
            }
            System.out.print("Digite o número correspondente à cor: ");
            String entrada = scanner.nextLine();

            try {
                int indice = Integer.parseInt(entrada) - 1;
                if (indice >= 0 && indice < coresValidas.length) {
                    corEscolhida = coresValidas[indice];
                    boolean corJaEscolhida = false;
                    for (Jogador jogador : listaJogadores) {
                        if (jogador.getCor().equals(corEscolhida)) {
                            corJaEscolhida = true;
                            break;
                        }
                    }

                    if (!corJaEscolhida) {
                        corValida = true;
                    } else {
                        System.out.println("Essa cor já foi escolhida.");
                    }
                } else {
                    System.out.println("Número inválido.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida.");
            }
        }

        return corEscolhida;
    }

    private Jogador criarJogadorAleatorio(String nome, String cor, Scanner scanner) {
        
        if (listaJogadores.isEmpty()) {
            return criarJogadorPorTipo(new Random().nextInt(3), nome, cor, scanner);
        }
        
        boolean temNormal = false, temAzarado = false, temSortudo = false;

        for (Jogador j : listaJogadores) {
            if (j instanceof JogadorNormal) temNormal = true;
            else if (j instanceof JogadorAzarado) temAzarado = true;
            else if (j instanceof JogadorSortudo) temSortudo = true;
        }

        Random random = new Random();
        int tentativas = 0;

        while (tentativas < 10) { 
            int tipo = random.nextInt(3);
            Jogador novoJogador = criarJogadorPorTipo(tipo, nome, cor, scanner);
            
            if ((tipo == 0 && !temNormal) || 
                (tipo == 1 && !temAzarado) || 
                (tipo == 2 && !temSortudo) ||
                (temNormal && temAzarado && temSortudo)) {
                return novoJogador;
            }
            tentativas++;
        }

        return criarJogadorPorTipo(random.nextInt(3), nome, cor, scanner);
    }

    private Jogador criarJogadorPorTipo(int tipo, String nome, String cor, Scanner scanner) {
        switch (tipo) {
            case 0: return new JogadorNormal(nome, cor, scanner);
            case 1: return new JogadorAzarado(nome, cor, scanner);
            case 2: return new JogadorSortudo(nome, cor, scanner);
            default: return new JogadorNormal(nome, cor, scanner);
        }
    }

    private void iniciarJogo() {
        jogoTerminado = false;
        turnoAtual = 1;
        introducaoAnimada();
        

        while (!jogoTerminado) {
            System.out.println(CoresConsole.CYAN_BOLD + "\n----------- TURNO " + turnoAtual + " -----------" + CoresConsole.RESET);
            mostrarTabuleiro();
            mostrarRanking();

            for (Jogador jogador : listaJogadores) {
                if (jogador.isPularProxima()) {
                    System.out.println(CoresConsole.RED + jogador.getCor() + " está pulando este turno!" + CoresConsole.RESET);
                    jogador.setPularProxima(false);
                    continue;
                }

                boolean jogadorTerminou = processarTurnoJogador(jogador);
                if (jogadorTerminou) {
                    jogoTerminado = true;
                    break;
                }
            }
            
            if (!jogoTerminado && turnoAtual % 5 == 0) {
                eventoGlobal();
            }
            turnoAtual++;
        }
        
        mostrarResultadoFinal();
        mostrarEstatisticas();
    }

    private void introducaoAnimada() {
    
        System.out.println(CoresConsole.BLUE_BOLD + "Preparando o jogo..." + CoresConsole.RESET);
        pausa(1000);
        
        System.out.print("Carregando tabuleiro");
        for (int i = 0; i < 3; i++) {
            System.out.print(".");
            pausa(500);
        }
        System.out.println();
        
        System.out.println(CoresConsole.GREEN_BOLD + "\nJogadores participantes:" + CoresConsole.RESET);
        for (Jogador jogador : listaJogadores) {
            System.out.println("- " + jogador.getCor() + " (" + jogador.getTipoJogador() + ")");
            pausa(300);
        }
        
        System.out.println(CoresConsole.YELLOW_BOLD + "\nQUE OS JOGOS COMECEM!" + CoresConsole.RESET);
        pausa(1000);
        
    }

    private void mostrarTabuleiro() {
        System.out.println("\n" + CoresConsole.PURPLE_BOLD + "=========== TABULEIRO ===========" + CoresConsole.RESET);
        
        System.out.print("       ");
        for (int i = 0; i <= Tabuleiro.TAMANHO_TABULEIRO; i++) {
            System.out.printf("%2d ", i);
        }
        System.out.println();
        
        for (Jogador jogador : listaJogadores) {
            System.out.printf("%-7s", jogador.getCor() + ":");
            for (int i = 0; i <= Tabuleiro.TAMANHO_TABULEIRO; i++) {
                if (i == jogador.getPosicao()) {
                    System.out.print(CoresConsole.BLACK_BOLD + "[" + jogador.getCor().charAt(0) + "]" + CoresConsole.RESET);
                } else if (tabuleiro.CasaEspecial(i)) {
                    System.out.print(CoresConsole.YELLOW + "[*]" + CoresConsole.RESET);
                } else {
                    System.out.print("[ ]");
                }
            }
            System.out.println(" Posição: " + jogador.getPosicao() + " (" + jogador.getTipoJogador() + ")");
        }
    }

    private boolean processarTurnoJogador(Jogador jogador) {
        boolean jogouComDouble = false;
        
        do {
            System.out.println("\n" + CoresConsole.GREEN_BOLD + "VEZ DO " + jogador.getCor().toUpperCase() + " (" + jogador.getTipoJogador().toUpperCase() + ")" + CoresConsole.RESET);
            System.out.println("Posição atual: " + jogador.getPosicao());
            
            System.out.print("Pressione ENTER para lançar os dados...");
            esperarEnter();
            
            int resultadoDado = jogador.lancarDados(random, modoDebug);

            System.out.print("Pressione ENTER para mover...");
            esperarEnter();
            
            jogador.mover(resultadoDado);
            jogador.setTurno(jogador.getTurno() + 1);
            
            System.out.println("Nova posição: " + jogador.getPosicao());

            if (jogador.getPosicao() >= Tabuleiro.TAMANHO_TABULEIRO) {
                System.out.println(CoresConsole.YELLOW_BOLD + "\n🎉 " + jogador.getCor() + " VENCEU O JOGO! 🎉" + CoresConsole.RESET);
                return true;
            }
            
            if (verificarCasasEspeciais(jogador)) {
                if (jogador.isPularProxima()) {
                    jogador.setJogarNovamente(false);
                    return false;
                }
            }

            if (jogador.isJogarNovamente()) {
                if (jogouComDouble) {
                    jogador.setJogarNovamente(false);
                } else {
                    jogouComDouble = true;
                    System.out.println(CoresConsole.YELLOW + "Jogando novamente por ter tirado dados iguais!" + CoresConsole.RESET);
                }
            }
            
        } while (jogador.isJogarNovamente() && !jogoTerminado);
    
        jogador.setJogarNovamente(false);

        return false; 
    }
        
        

    private boolean verificarCasasEspeciais(Jogador jogador) {
        int posicao = jogador.getPosicao();
        
        if (tabuleiro.CasaEspecial(posicao)) {
            System.out.println(CoresConsole.PURPLE + "\n⚡ Você caiu em uma casa especial! ⚡" + CoresConsole.RESET);

            if (posicao == 10 || posicao == 25 || posicao == 38) {
                System.out.println(CoresConsole.RED + "🚫 " + jogador.getCor() + " vai pular a próxima rodada!" + CoresConsole.RESET);
                jogador.setPularProxima(true);
                return true;
            }

            System.out.print("Pressione ENTER para ver o que acontece...");
            esperarEnter();
            
            if (tabuleiro.CasaSurpresa(posicao)) {
                return casaSurpresa(jogador);
            } else if (tabuleiro.CasaSorte(posicao)) {
                return casaSorte(jogador);
            } else if (tabuleiro.CasaEscolha(posicao)) {
                return casaEscolha(jogador);
            } else if (tabuleiro.CasaMagica(posicao)) {
                return casaMagica(jogador);
            }
        }
        return false;
    }

    private boolean casaSurpresa(Jogador jogador) {
        System.out.println(CoresConsole.YELLOW + "\n🎁 CASA SURPRESA! Seu tipo será alterado aleatoriamente!" + CoresConsole.RESET);
        System.out.print("Pressione ENTER para continuar...");
        esperarEnter();
        
        String tipoAntigo = jogador.getTipoJogador();
        trocarTipoJogador(jogador);
        System.out.println(jogador.getCor() + " mudou de " + tipoAntigo + " para " + jogador.getTipoJogador() + "!");

        return false;
    }

    private boolean casaSorte(Jogador jogador) {
        if (jogador instanceof JogadorAzarado) {
            System.out.println(CoresConsole.RED + "😞 Você é Azarado! Nenhum bônus para você!" + CoresConsole.RESET);
            return false;
        }
        
        System.out.println(CoresConsole.GREEN + "🍀 CASA DA SORTE! Você ganha +3 casas!" + CoresConsole.RESET);
        System.out.print("Pressione ENTER para avançar...");
        esperarEnter();
        
        jogador.mover(3);
        System.out.println("Nova posição: " + jogador.getPosicao());
        
        return verificarCasasEspeciais(jogador);
    }

    private boolean casaEscolha(Jogador jogadorAtual) {
        System.out.println(CoresConsole.CYAN_BOLD + "\n💡 CASA DE ESCOLHA! Escolha um jogador para voltar ao início!" + CoresConsole.RESET);
        
        List<Jogador> alvos = new ArrayList<>();
        int index = 1;
        
        for (Jogador j : listaJogadores) {
            if (!j.equals(jogadorAtual)) {
                System.out.println(index + ". " + j.getCor() + " (" + j.getTipoJogador() + ")");
                alvos.add(j);
                index++;
            }
        }
        
        int escolha = -1;
        while (escolha < 1 || escolha >= index) {
            System.out.print("Digite o número do jogador: ");
            try {
                escolha = Integer.parseInt(scanner.nextLine());
                if (escolha < 1 || escolha >= index) {
                    System.out.println(CoresConsole.RED + "Número inválido! Tente novamente." + CoresConsole.RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(CoresConsole.RED + "Digite apenas números!" + CoresConsole.RESET);
            }
        }
        
        Jogador alvo = alvos.get(escolha - 1);
        alvo.setPosicao(0);
        System.out.println(CoresConsole.GREEN + alvo.getCor() + " foi enviado de volta ao início!" + CoresConsole.RESET);
        
        return false;
    }

    private boolean casaMagica(Jogador jogadorAtual) {
        System.out.println(CoresConsole.PURPLE_BOLD + "\n✨ CASA MÁGICA! Você vai trocar de lugar com o último colocado!" + CoresConsole.RESET);
        
        Jogador ultimo = listaJogadores.get(0);
        for (Jogador j : listaJogadores) {
            if (j.getPosicao() < ultimo.getPosicao()) {
                ultimo = j;
            }
        }
        
        if (ultimo.equals(jogadorAtual)) {
            System.out.println(CoresConsole.YELLOW + "Você já é o último! Nada acontece." + CoresConsole.RESET);
            return false;
        }
        
        int posTemp = jogadorAtual.getPosicao();
        jogadorAtual.setPosicao(ultimo.getPosicao());
        ultimo.setPosicao(posTemp);
        
        System.out.println(CoresConsole.GREEN + "Você trocou de lugar com " + ultimo.getCor() + "! Agora você está na posição " + jogadorAtual.getPosicao() + CoresConsole.RESET);
        
        return false;
    }

    private void esperarEnter() {
        try {
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

private void pausa(int millis) {
    try {
        Thread.sleep(millis);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.out.println("Pausa interrompida");
    }
}

    private void trocarTipoJogador(Jogador jogador) {
        int tipo = random.nextInt(3);
        Jogador novoJogador;

        do {
            tipo = random.nextInt(3);
        } while ((tipo == 0 && jogador instanceof JogadorNormal) ||
                (tipo == 1 && jogador instanceof JogadorAzarado) ||
                (tipo == 2 && jogador instanceof JogadorSortudo));
        
        novoJogador = criarJogadorPorTipo(tipo, jogador.getNome(), jogador.getCor(), scanner);
        
        if (novoJogador != null) {
            novoJogador.setPosicao(jogador.getPosicao());
            novoJogador.setTurno(jogador.getTurno());
            novoJogador.setPularProxima(jogador.isPularProxima());
            novoJogador.setJogarNovamente(jogador.isJogarNovamente());
            
            int index = listaJogadores.indexOf(jogador);
            listaJogadores.set(index, novoJogador);
        }
    }

    private void eventoGlobal() {
        System.out.println(CoresConsole.PURPLE_BOLD + "\nEVENTO GLOBAL!" + CoresConsole.RESET);
        int evento = random.nextInt(3);
        
        switch (evento) {
            case 0:
                System.out.println("Terremoto! Todos os jogadores recuam 1 casa.");
                listaJogadores.forEach(j -> j.mover(-1));
                break;
            case 1:
                System.out.println("Vento a favor! Todos os jogadores avançam 1 casa.");
                listaJogadores.forEach(j -> j.mover(1));
                break;
            case 2:
                System.out.println("Turbilhão! As posições serão embaralhadas.");
                Collections.shuffle(listaJogadores);
                break;
        }
    }

    private void mostrarRanking() {
        System.out.println("\n" + CoresConsole.BLUE_BOLD + "=========== RANKING ===========" + CoresConsole.RESET);
        listaJogadores.stream()
            .sorted((j1, j2) -> Integer.compare(j2.getPosicao(), j1.getPosicao()))
            .forEach(j -> System.out.println(j.getCor() + ": " + j.getPosicao() + " casas"));
    }

    private void mostrarResultadoFinal() {
        System.out.println(CoresConsole.YELLOW_BOLD + "\n=========== RESULTADO FINAL ===========" + CoresConsole.RESET);
        
        listaJogadores.stream()
            .sorted((j1, j2) -> Integer.compare(j2.getPosicao(), j1.getPosicao()))
            .forEachOrdered(j -> {
                System.out.print(j.getCor() + ": " + j.getPosicao() + " casas");
                if (j.getPosicao() >= Tabuleiro.TAMANHO_TABULEIRO) {
                    System.out.println(" - VENCEDOR!");
                } else {
                    System.out.println();
                }
            });
        
        System.out.println("\nTotal de turnos: " + (turnoAtual - 1));
    }

    private void mostrarEstatisticas() {
        System.out.println("\n" + CoresConsole.BLUE_BOLD + "=== ESTATÍSTICAS FINAIS ===" + CoresConsole.RESET);
        listaJogadores.stream()
            .sorted((j1, j2) -> Integer.compare(j2.getPosicao(), j1.getPosicao()))
            .forEach(j -> {
                System.out.println(j.getCor() + ":");
                System.out.println("  Posição final: " + j.getPosicao());
                System.out.println("  Total de jogadas: " + j.getTurno());
                System.out.println("  Tipo: " + j.getTipoJogador());
            });
    }  

    public void finalizar() {
        if (scanner != null) {
            scanner.close();
        }
    }

}
package catolica;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class Mochila {
	
	private static final int LIVROS = 15; // Genes
	private static final int POPULACAO = 1000; // Cromossomos
	private static final double MOCHILA = 3.0; // carga maxima da mochila
	private static final int GERACOES = 100; // Condicao de parada por tempo
	
	private static final int qtd_mutacao = 6;
	private static final int qtd_cruzamento = 94;
	
	// semente para o random
	public long semente;
	// objeto para gerar valores aleatorios
	public Random rand;
	// matriz da populacao
	public boolean[][] pop = new boolean[POPULACAO][LIVROS];
	// matriz de herdeiros
	public boolean[][] herdeiros = new boolean[POPULACAO][LIVROS];
	// controle de iteracao para herdeiros
	public int cont_herdeiro = 0;
	// matriz de aptidao (fitness) para uso na avaliacao e roleta
	public float[][] aptidao = new float[POPULACAO][2];
	// matriz com pesos dos livros
	public float[] pesos_livros = new float[LIVROS];
	
	/*
	 * Metodo para geracao de uma populacao inicial.
	 * No problema da mochila cada gene representa a presenca ou nao de
	 * um livro na mochila.
	 */
	public void populacao_inicial()
	{
		semente = (long) System.currentTimeMillis();
		rand = new Random(semente);
		int i, j;
		for(i = 0; i < POPULACAO; i++)
			for(j = 0; j < LIVROS; j++)
				pop[i][j] = rand.nextBoolean();
	}
	
	/*
	 * O operador genetico da mutacao altera o valor de um gene
	 * de um individuo sorteado.
	 */
	public void mutacao()
	{
		int gene, individuo, i;
		gene = rand.nextInt(LIVROS);
		individuo = rand.nextInt(POPULACAO);
		for(i = 0; i < LIVROS; i++)
			herdeiros[cont_herdeiro][i] = pop[individuo][i];
		
		herdeiros[cont_herdeiro][gene] = !herdeiros[cont_herdeiro][gene];
		cont_herdeiro++;
	}
	/*
	 * Operador genetico de cruzamento simples em um ponto fixo.
	 */
	public void cruzamento_simples()
	{
		// sorteio de dois individuos
		int individuo1 = roleta();
		int individuo2 = roleta();
		
		int i;
		while(individuo1 == individuo2)
			individuo2 = roleta();
		// efetua o cruzamento.
		for(i = 0; i < LIVROS; i++)
		{
			if(i < LIVROS/2)
			{
				herdeiros[cont_herdeiro][i] = pop[individuo1][i];
				herdeiros[cont_herdeiro+1][i] = pop[individuo2][i];
			} else
			{
				herdeiros[cont_herdeiro][i] = pop[individuo2][i];
				herdeiros[cont_herdeiro+1][i] = pop[individuo1][i];				
			}
		}
		cont_herdeiro++; cont_herdeiro++;
	}

	/*
	 * Operador genetico de cruzamento uniforme (mascara).
	 */
	public void cruzamento_uniforme()
	{
		// sorteio de dois individuos
		int individuo1 = roleta();
		int individuo2 = roleta();
		
		int i;
		while(individuo1 == individuo2)
			individuo2 = roleta();
		// efetua o cruzamento.
		for(i = 0; i < LIVROS; i++)
		{
			if(rand.nextBoolean())
			{
				herdeiros[cont_herdeiro][i] = pop[individuo1][i];
				herdeiros[cont_herdeiro+1][i] = pop[individuo2][i];
			} else
			{
				herdeiros[cont_herdeiro][i] = pop[individuo2][i];
				herdeiros[cont_herdeiro+1][i] = pop[individuo1][i];				
			}
		}
		cont_herdeiro++; cont_herdeiro++;
	}

	
	/*
	* Metodo de avaliacao do cromossomo. Fitness function.
	*/
	public void avaliacao()
	{
		int melhor = 0;
		float fmelhor = 0;
		
		float peso = 0;
		float peso_total = 0;
		int i, j;
		// Faz o calculo do peso de cada individuo
		for(i = 0; i < POPULACAO; i++)
		{
			for(j = 0; j < LIVROS; j++)
			{
				if(pop[i][j] == true)
					peso += pesos_livros[j];
			}
			aptidao[i][0] = (float) (peso > MOCHILA ? 0 : peso);

			// armazena o id do melhor elemento.
			if(fmelhor < aptidao[i][0])
			{
				fmelhor = aptidao[i][0];
				melhor = i;
			}

			peso_total += peso; 
			peso = 0;
		}
		System.out.print("Melhor combinacao: ");
		System.out.println(fmelhor);
		// Faz o calculo da porcentagem para a roleta de cada individuo
		for(i = 0; i < POPULACAO; i++)
		{
			aptidao[i][1] = (aptidao[i][0] * 100)/peso_total;
		}
	}
	
	/*
	 * Metodo de substituicao da populacao.
	 * Copia os descendentes para a populacao.
	 */
	public void substituicao()
	{
		int i, j;
		for(i = 0; i < POPULACAO; i++)
		{
			for(j = 0; j < LIVROS; j++)
			{
				pop[i][j] = herdeiros[i][j];
			}
		}
		cont_herdeiro = 0;
	}
	
	/*
	 * Metodo de sorteio por roleta.
	 */
	public int roleta()
	{
		int x = rand.nextInt(100);
		int i;
		float soma = 0;
		for(i = 0; i < POPULACAO; i++)
		{
			soma += aptidao[i][1];
			if(soma >= x)
				return i;
		}
		return 0;
	}
	
	/*
	 * Metodo para carregamento dos valores dos pesos dos livros
	 * a partir de um arquivo.
	 */
	public void carrega_pesos()
	{
		try{
			FileInputStream fstream = new FileInputStream("livros300.txt");
			DataInputStream arquivo = new DataInputStream(fstream);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(arquivo));
			String strLine;
			int i = 0;
			while (i < LIVROS)   {
				strLine = buffer.readLine();
				pesos_livros[i] = Float.parseFloat(strLine);
				i++;
			}
			arquivo.close();
		} catch(Exception e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int i = 0;
		int imutacao = 0; // controle da mutacao
	
		int pmutacao = (POPULACAO * qtd_mutacao)/100; // condicao de parada da mutacao
		
		Mochila ag = new Mochila();
		ag.populacao_inicial(); // cria populacao inicial
		ag.carrega_pesos(); // carrega pesos dos livros
		while (i < GERACOES)
		{
			ag.avaliacao(); // avaliacao da populacao
			while(imutacao < pmutacao-1)
			{
				ag.mutacao(); // operador genetico de mutacao
				imutacao++;
			}
			while(ag.cont_herdeiro < POPULACAO-1)
			{
				ag.cruzamento_uniforme();
			}
			ag.substituicao();
			i++;
		}
		
	}

}

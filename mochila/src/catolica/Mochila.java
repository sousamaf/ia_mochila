package catolica;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class Mochila {
	
	
	private static final int LIVROS = 6;
	private static final int POPULACAO = 100;
	private static final double MOCHILA = 2.0;
	
	// semente para o random
	public long semente;
	public Random rand;
	public boolean[][] pop = new boolean[POPULACAO][LIVROS];
	public boolean[][] herdeiros = new boolean[POPULACAO][LIVROS];
	public float[][] aptidao = new float[POPULACAO][2];
	public float[] pesos_livros = new float[LIVROS];
	
	public void populacao_inicial()
	{
		semente = (long) System.currentTimeMillis();
		rand = new Random(semente);
		int i, j;
		for(i = 0; i < POPULACAO; i++)
			for(j = 0; j < LIVROS; j++)
				pop[i][j] = rand.nextBoolean();
	}
	
	public void mutacao()
	{
		int gene, individuo;
		gene = rand.nextInt(LIVROS);
		individuo = rand.nextInt(POPULACAO);
		pop[individuo][gene] = !pop[individuo][gene];
	}
	
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
		// TODO Auto-generated method stub
		Mochila ag = new Mochila();
		ag.populacao_inicial();
		ag.mutacao();
		ag.carrega_pesos();

	}

}

package main;

import java.io.File;

import hash_dinamica.implementacoes.HashDinamicaIntInt;
import hash_dinamica.implementacoes.HashDinamicaStringInt;
import util.IO;

public class Main
{
	public static final String NOME_DO_ARQUIVO_DO_DIRETORIO = "diretorio.dir";
	public static final String NOME_DO_ARQUIVO_DOS_BUCKETS = "buckets.db";
	public static final File ARQUIVO_DO_DIRETORIO = new File(NOME_DO_ARQUIVO_DO_DIRETORIO);
	public static final File ARQUIVO_DOS_BUCKETS = new File(NOME_DO_ARQUIVO_DOS_BUCKETS);
	
	public static void hashStringInt()
	{
		
		HashDinamicaStringInt hashDinamica = null;
		
		try
		{
			hashDinamica = new HashDinamicaStringInt(
				NOME_DO_ARQUIVO_DO_DIRETORIO,
				NOME_DO_ARQUIVO_DOS_BUCKETS
			);
		}
		
		catch (NoSuchMethodException | SecurityException e1)
		{
			e1.printStackTrace();
		}
		
		if (hashDinamica != null)
		{
			hashDinamica.inserir("Axell", 2);
			hashDinamica.inserir("Axell", 3);
			hashDinamica.inserir("Axell", 4);
			hashDinamica.inserir("Maycon", 6);
			hashDinamica.inserir("Maycon", 2);
			hashDinamica.inserir("Maycon", 7);
			hashDinamica.inserir("Maycon", 5);
			hashDinamica.inserir("Melo", 3);
			hashDinamica.inserir("Felipe", 4);
			//hashDinamica.inserir("Felipe", 4);
			
			IO.println( hashDinamica.toString(true) );
			
			hashDinamica.fechar();
		}
	}
	
	public static void hashIntInt()
	{
		
		HashDinamicaIntInt hashDinamica = null;
		
		try
		{
			hashDinamica = new HashDinamicaIntInt(
				NOME_DO_ARQUIVO_DO_DIRETORIO,
				NOME_DO_ARQUIVO_DOS_BUCKETS
			);
		}
		
		catch (NoSuchMethodException | SecurityException e1)
		{
			e1.printStackTrace();
		}
		
		if (hashDinamica != null)
		{
			hashDinamica.inserir(10, 0); // faz várias inserções
			hashDinamica.inserir(14, 0);
			hashDinamica.inserir(20, 0);
			hashDinamica.inserir(22, 0);
			
			hashDinamica.inserir(13, 0);
			hashDinamica.inserir(21, 0);
			
			hashDinamica.inserir(3 , 0);
			hashDinamica.inserir(7 , 0);
			hashDinamica.inserir(15, 0);
			hashDinamica.inserir(23, 0);
			IO.println( hashDinamica.toString(true) ); // mostra a hash
			hashDinamica.excluirRegistrosComAChave(10);
			IO.println( hashDinamica.toString(false) ); // mostra a hash
			hashDinamica.inserir(27, 0); // faz a inserção do 27
			IO.println( hashDinamica.toString(true) ); // mostra a hash
			
			hashDinamica.inserir(0, 0); // faz a inserção do 0
			IO.println( hashDinamica.toString(true) ); // mostra a hash
			
			hashDinamica.fechar();
		}
	}
	
	public static void main(String[] args)
	{
		IO.println("Arq dir deletado = " + ARQUIVO_DO_DIRETORIO.delete());
		IO.println("Arq dir deletado = " + ARQUIVO_DOS_BUCKETS.delete() + "\n");
		
		//hashStringInt();
		hashIntInt();
	}

}

package main;

import java.io.File;
import java.util.Arrays;

import hash_dinamica.implementacoes.HashDinamicaIntInt;
import util.IO;

public class Main
{
	public static final String NOME_DO_ARQUIVO_DO_DIRETORIO = "diretorio.dir";
	public static final String NOME_DO_ARQUIVO_DOS_BUCKETS = "buckets.db";
	public static final File ARQUIVO_DO_DIRETORIO = new File(NOME_DO_ARQUIVO_DO_DIRETORIO);
	public static final File ARQUIVO_DOS_BUCKETS = new File(NOME_DO_ARQUIVO_DOS_BUCKETS);
	
	public static void main(String[] args)
	{
		ARQUIVO_DO_DIRETORIO.delete();
		ARQUIVO_DOS_BUCKETS.delete();
		
		HashDinamicaIntInt hashDinamica = null;
		
		try
		{
			hashDinamica = new HashDinamicaIntInt("intint.dir", "intint.db");
		}
		catch (NoSuchMethodException | SecurityException e1)
		{
			e1.printStackTrace();
		}
		
		hashDinamica.inserir(1, 2);
		hashDinamica.inserir(1, 3);
		hashDinamica.inserir(1, 4);
		hashDinamica.excluir(1, 2);
		hashDinamica.inserir(5, 6);
		hashDinamica.inserir(4, 2);
		hashDinamica.inserir(3, 3);
		hashDinamica.inserir(2, 4);
		
		IO.println( Arrays.toString( hashDinamica.listarDadosComAChave(1) ) );
		//hashDinamica.listarDadosComAChave(1).forEach( (it) -> { IO.println(it); } );
	}

}

package main;

import java.io.File;
import java.util.Arrays;

import hash_dinamica.implementacoes.HashDinamicaStringInt;
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
			hashDinamica.excluir("Maycon", 2);
			hashDinamica.inserir("Melo", 3);
			hashDinamica.inserir("Felipe", 4);
			
			IO.println( Arrays.toString( hashDinamica.listarDadosComAChave("Maycon") ) );
			
			hashDinamica.fechar();
		}
	}

}

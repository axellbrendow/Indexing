package hash.main;

import java.io.File;

import hash.hashs.HashDinamicaIntInt;
import hash.hashs.HashDinamicaStringInt;
import hash.util.IO;

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
				NOME_DO_ARQUIVO_DOS_BUCKETS,
				4 // número de registros por bucket
			);
		}
		
		catch (NoSuchMethodException | SecurityException e1)
		{
			e1.printStackTrace();
		}
		
		if (hashDinamica != null)
		{
			hashDinamica.inserir("A", 2);
			hashDinamica.inserir("A", 3);
			hashDinamica.inserir("A", 4);
			hashDinamica.inserir("B", 6);
			hashDinamica.inserir("B", 2);
			hashDinamica.inserir("B", 7);
			hashDinamica.inserir("B", 5);
			hashDinamica.inserir("C", 3);
			hashDinamica.inserir("D", 4);
			//hashDinamica.inserir("D", 4);
			
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
				NOME_DO_ARQUIVO_DOS_BUCKETS,
				4 // número de registros por bucket
			);
		}
		
		catch (NoSuchMethodException | SecurityException e1)
		{
			e1.printStackTrace();
		}
		
		if (hashDinamica != null)
		{
			IO.println("Inserindo [ 10, 14, 20, 22, 13, 21, 3, 7, 15, 23 ]");
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
			IO.println( hashDinamica ); // mostra a hash incluindo apenas as chaves
			
			IO.println("Inserindo [ 27 ]");
			hashDinamica.inserir(27, 0); // faz a inserção do 27
			IO.println( hashDinamica.toString(true) );

			IO.println("Inserindo [ 8 ]");
			hashDinamica.inserir(8, 0); // faz a inserção do 8
			IO.println( hashDinamica.toString(true) );
			
			hashDinamica.fechar();
		}
	}
	
	public static void main(String[] args)
	{
		ARQUIVO_DO_DIRETORIO.delete();
		ARQUIVO_DOS_BUCKETS.delete();
		
		//hashStringInt();
		hashIntInt();
	}

}

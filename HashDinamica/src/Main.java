import java.io.File;

public class Main
{
	public static final String NOME_DO_ARQUIVO_DO_DIRETORIO = "diretorio.dir";
	public static final String NOME_DO_ARQUIVO_DOS_BUCKETS = "buckets.db";
	public static final File ARQUIVO_DO_DIRETORIO = new File(NOME_DO_ARQUIVO_DO_DIRETORIO);
	public static final File ARQUIVO_DOS_BUCKETS = new File(NOME_DO_ARQUIVO_DOS_BUCKETS);
	
	public static void hashStringInt()
	{
		Hash<String, Integer> hash = new Hash<>(
			NOME_DO_ARQUIVO_DO_DIRETORIO,
			NOME_DO_ARQUIVO_DOS_BUCKETS,
			4, // número de registros por bucket
			String.class,
			Integer.class
		);

		hash.inserir("A", 2);
		hash.inserir("A", 3);
		hash.inserir("A", 4);
		hash.inserir("B", 6);
		hash.inserir("B", 2);
		hash.inserir("B", 7);
		hash.inserir("B", 5);
		hash.inserir("C", 3);
		hash.inserir("D", 4);
		//hash.inserir("D", 4);

		IO.println( hash.toString(true) );

		hash.fechar();
	}
	
	public static void hashIntInt()
	{
		Hash<Integer, Integer> hash = new Hash<>(
			NOME_DO_ARQUIVO_DO_DIRETORIO,
			NOME_DO_ARQUIVO_DOS_BUCKETS,
			4, // número de registros por bucket
			Integer.class,
			Integer.class
		);

		IO.println("Inserindo [ 10, 14, 20, 22, 13, 21, 3, 7, 15, 23 ]");
		hash.inserir(10, 0); // faz várias inserções
		hash.inserir(14, 0);
		hash.inserir(20, 0);
		hash.inserir(22, 0);

		hash.inserir(13, 0);
		hash.inserir(21, 0);

		hash.inserir(3 , 0);
		hash.inserir(7 , 0);
		hash.inserir(15, 0);
		hash.inserir(23, 0);
		IO.println( hash ); // mostra a hash incluindo apenas as chaves

		IO.println("Inserindo [ 27 ]");
		hash.inserir(27, 0); // faz a inserção do 27
		IO.println( hash.toString(true) );

		IO.println("Inserindo [ 8 ]");
		hash.inserir(8, 0); // faz a inserção do 8
		IO.println( hash.toString(true) );

		hash.fechar();
	}
	
	public static void main(String[] args)
	{
		ARQUIVO_DO_DIRETORIO.delete();
		ARQUIVO_DOS_BUCKETS.delete();
		
//		hashStringInt();
		hashIntInt();
	}

}

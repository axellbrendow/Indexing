import java.io.File;
import java.io.IOException;

public class Main
{
	public static final String NOME_DO_ARQUIVO_DO_DIRETORIO = "diretorio.dir";
	public static final String NOME_DO_ARQUIVO_DOS_BUCKETS = "buckets.db";
	public static final File ARQUIVO_DO_DIRETORIO = new File(NOME_DO_ARQUIVO_DO_DIRETORIO);
	public static final File ARQUIVO_DOS_BUCKETS = new File(NOME_DO_ARQUIVO_DOS_BUCKETS);
	
	public static void print(Object msg)
	{
		System.out.print(msg);
	}
	
	public static void println(Object msg)
	{
		print(msg + System.lineSeparator());
	}
	
	public static void main(String[] args)
	{
		ARQUIVO_DO_DIRETORIO.delete();
		ARQUIVO_DOS_BUCKETS.delete();
		
		entity ent1 = new entity(1, "entity 1");
		entity ent2 = new entity(2, "entity 2");
		entity ent3 = new entity(3, "entity 3");
		entity ent4 = new entity(4, "entity 4");
		entity ent5 = new entity(5, "entity 5");
		entity ent6 = new entity(6, "entity 6");
		
		try
		{
			// olhar problema de chaves com hashs iguais num mesmo bucket
			HashDinamica<entity, entity> hash = new HashDinamica<entity, entity>(
				NOME_DO_ARQUIVO_DO_DIRETORIO,
				NOME_DO_ARQUIVO_DOS_BUCKETS,
				3,
				(short) (Integer.BYTES + 300),
				(short) (Integer.BYTES + 300),
				entity.class.getConstructor(),
				entity.class.getConstructor(),
				(ent) -> { return (int) ( Short.MAX_VALUE * ent.id ); }
			);
			
			hash.inserir(ent1, ent2);
			hash.inserir(ent1, ent3);
			hash.inserir(ent1, ent4);
			hash.inserir(ent5, ent6);
			
			hash.listarDadosComAChave(ent5).forEach( (it) -> { println(it); } );
		}
		
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
	}

}
